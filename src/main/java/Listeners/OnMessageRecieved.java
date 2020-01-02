package Listeners;

import Commands.*;
import Logging.Logger;
import YoutubeIntergration.PlayLink;
import main.Member;

import main.RacismDetection;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Objects;

public class OnMessageRecieved extends ListenerAdapter {
    private ArrayList<Command> commands = new ArrayList<>();
    private Logger logger;
    private ArrayList<String> admins;
    private ArrayList<Member> members;
    private Member bot;

    private ArrayList<String> bannedFromDF = new ArrayList<>();
    private File bannedFromDFFile = new File("TextFiles/Commands/bannedFromDeepFrying.txt");

    private OnGuildVoiceEvents onGuildVoiceEvents;
    private BanPhrase bannedPhrases;
    private CleanseChannel cleanseChannel;
    private PlayLink playLink;
    private RacismDetection racismDetection;

    public OnMessageRecieved(Logger logger, ArrayList<Member> members, ArrayList<String> admins, PlayLink link, OnGuildVoiceEvents onGuildVoiceEvents) throws Exception{
        this.playLink = link;
        this.logger = logger;
        this.admins = admins;
        this.members = members;
        this.racismDetection = new RacismDetection(logger);
        this.onGuildVoiceEvents = onGuildVoiceEvents;

        bot = getMember("bot");
        if(bot == null){
            logger.createErrorLog("bots id could not be found in the members list");
            throw new Exception();
        }
        readBannedFromDF();
        addCommands();
    }

    private void readBannedFromDF(){
        try{
            FileReader fr = new FileReader(bannedFromDFFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                bannedFromDF.add(line);
            }
        }catch (IOException e){
            logger.createErrorLog("reading list of people banned from deep frying " + e.getMessage());
        }
    }

    private void addCommands(){
        bannedPhrases = new BanPhrase(logger, "ban", " 'phrase' - bans a phrase from being used", true);
        cleanseChannel = new CleanseChannel(logger, "cleanse", " 'text' - clears all occurrences of the text in the channel in the last 'limit' messages", true);

        commands.add(bannedPhrases);
        commands.add(cleanseChannel);
        commands.add(new UnbanPhrase(logger, "unban", " 'phrase' - unbans a phrase from being used", true));
        commands.add(new SetLimit(logger, "limit", cleanseChannel, " 'limit' - sets the limit for the cleanse command", true));
        commands.add(new BanFromDeepFrying(logger, "dfban", " @user - bans the user from using deep fryer", true));
        commands.add(new UnbanFromDeepFrying(logger, "dfunban", " @user - unbans the user from using deep fryer", true));
        commands.add(new AddMember(logger, "member", " @user name - makes a new member entry for the user", true));
        commands.add(new MakeAdmin(logger, "admin", " @user - makes a user into an admin", true));
        commands.add(new Help(logger, "help", commands, " - displays the help page", false));
        commands.add(new AddYoutubeKeyword(logger, "ytadd", " 'name' 'link' - creates a new keyword that when typed will play that youtube video in the users channel", true));
        commands.add(new RemoveYoutubeKeyword(logger, "ytremove", " 'name' - removes the keyword from the list", true));
        commands.add(new ListYTKeywords(logger, "ytlist", " - lists all the YouTube keywords", false));
        commands.add(new Clear(logger, "ytclear", " - clears all queued up youtube links", playLink, false));
        commands.add(new Skip(logger, "skip", " - skips the currently playing track", playLink, false));
        commands.add(new Volume(logger, "volume", " 'volume' - sets the volume to the number provided, range 0-100", playLink, false));
        commands.add(new BanFromNameChanges(logger, "nameban", " @user - makes it so that all the users name changes are tracked and if a duplicate is found they are kicked", true));
        commands.add(new UnbanFromNameChanges(logger, "nameunban", " @user - removes the name restrictions on the user", true));
        commands.add(new ListRacism(logger, "nranks", " - Lists a counter of how many times each user has said the n word", false));
        commands.add(new SetRateLimit(logger, "ratelimit", "- Sets the limit on the number of commands that can be used in a minute, default is 3", true));
    }

    public void onMessageReceived(MessageReceivedEvent event){
        User author = event.getAuthor();
        Message message = event.getMessage();
        String rawMessage = message.getContentRaw();
        if(checkIfBotSentMsg(author)){
            return;
        }

        try{
            String[] messageSplit = rawMessage.split(" ");
            checkCommands(messageSplit[0], message);
        }catch (Exception e){
            e.printStackTrace();
            logger.createErrorLog("encountered in the commands check " + e.getMessage());
        }

        addMessageContainmentChecks(message, event);
    }

    private boolean checkIfBotSentMsg(User author){
        return author.getId().equals(bot.getId());
    }


    private void checkCommands(String keyword, Message message){
        for(Command command: commands){
            if(command.getKeyword().equals(keyword) && (!command.getAdminProtected() || admins.contains(message.getAuthor().getId()))){
                if(checkCmdHistory(message) || admins.contains(message.getAuthor().getId())) {
                    command.function(message);
                }else{
                    String toSend = "You have issued too many commands recently please wait...";
                    RestAction action = message.getTextChannel().sendMessage(toSend);
                    action.complete();
                }
            }
        }
    }

    private void addMessageContainmentChecks(Message message, MessageReceivedEvent event){
        racismDetection.checkForNWord(message);
        checkForBannedPhrase(message);
        checkForDeepFry(message, event);
        checkForAlexGif(message);
        checkForYTKeyword(message);
        checkForThanks(message);
        checkForReddit(message);
    }

    private void checkForBannedPhrase(Message message){
        if(bannedPhrases.checkString(message)){
            RestAction action = message.delete();
            message.getChannel().sendMessage("Hey <@" + message.getAuthor().getId() + "> that message contained a banned phrase sorry").queue();
            logger.createLog("Deleted message for containing banned phrase " + message);
            action.complete();
        }
    }


    private void checkForDeepFry(Message message, MessageReceivedEvent event){
        String rawMessage = message.getContentRaw();
        String authorID = message.getAuthor().getId();
        if(bannedFromDF.contains(authorID)){
            String rawMessagesSpacesRemoved = rawMessage.replaceAll("\\s+", "");
            String rawMessageSymbolsRemoved = rawMessagesSpacesRemoved.
                    replaceAll("[-_+^()<{}&%$¦£\\[\\]€\"!>:;,\\\\/*~#|@]", "");

            if (rawMessagesSpacesRemoved.contains(".df")){
                AuditableRestAction result = event.getGuild().kick(authorID);
                String messageToSend = "<@" + authorID + "> you are banned from deep frying";
                logger.createLog("kicking " + message.getAuthor().getName() + " for deep frying while banned");
                message.getTextChannel().sendMessage(messageToSend).queue();
                result.submit();
            }else if(rawMessageSymbolsRemoved.contains(".df")){
                AuditableRestAction result = event.getGuild().kick(authorID);
                String messageToSend = "<@ " + authorID + "> nice attempt but you are still banned from deep frying";
                logger.createLog("kicking " + message.getAuthor().getName() + " for deep frying, attempted to get around the ban");
                message.getTextChannel().sendMessage(messageToSend).queue();
                result.submit();
            }
        }
    }

    private void checkForAlexGif(Message message){
        if(message.getContentRaw().contains("https://tenor.com/view/giant-giantess-tiny-small-vore-gif-13251272")) {
            Member gary = getMember("gary");
            Member alex = getMember("alex");
            if (alex == null) {
                String messageToSend = "alex has not been added to the member list";
                message.getTextChannel().sendMessage(messageToSend).queue();
            }

            if (gary == null) {
                String messageToSend = "gary has not been added to the member list";
                message.getTextChannel().sendMessage(messageToSend).queue();
            }

            if (gary != null && alex != null) {
                String messageToSend = "<@" + gary.getId() + ">, <@" + alex.getId() + "> is the kid";
                message.getTextChannel().sendMessage(messageToSend).queue();
            }
            logger.createLog("alex's gif was found");
        }
    }

    private void checkForYTKeyword(Message message){
        String rawMessage = message.getContentRaw();
        File file = new File("TextFiles/Commands/youtubeLinks.txt");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(",");
                if(rawMessage.contains(lineSplit[0])){
                    if(onGuildVoiceEvents.checkFile(message.getAuthor().getId())){
                        logger.createLog("user has been in channel for required time");
                        playLink.loadAndPlay(message.getTextChannel(), message.getAuthor(), lineSplit[1]);
                    }else{
                        logger.createLog("user has not been in the channel long enough");
                        String toSend = "Sorry,  you have not been in that channel long enough\n";
                        message.getTextChannel().sendMessage(toSend).queue();
                    }
                }
            }
            br.close();
            fr.close();
        }catch (IOException e){
            logger.createErrorLog("error in checking youtube keywords " + e.getMessage());
        }
    }

    private void checkForThanks(Message message){
        String botAt = "<@485897239521132564>";
        String rawMessage = message.getContentRaw();
        if(rawMessage.contains(botAt) && (rawMessage.toLowerCase().contains("thanks") || rawMessage.toLowerCase().contains("thank"))){
            RestAction action = message.getTextChannel().sendMessage("You're welcome");
            action.complete();
        }
    }

    private void checkForReddit(Message message){
        String rawMessage = message.getContentRaw();
        Normalizer.normalize(rawMessage, Normalizer.Form.NFD);
        rawMessage = rawMessage.toLowerCase();
        String[] split = rawMessage.split(" ");
        for(String s: split){
            if(s.equals("reddit")){
                String toSend = "Instagram normie am i right fellow redditors? Please validate my circlejerking. Also please don’t use emojis around me please. I raided Area 51 with Keanu Reeves," +
                                " Ricardo milos and bob ross. And danny devito. r/subsifellfor Elon musk is my boyfriend. Also sub to pewdiepie. Whaaaaaaaaaaat? You haven’t played Minecraft?" +
                                " Ew fortnite virgin. R/unexpectedthanos r/foundthemobileuser r/foundthelightmodeuser. I’m so sorry I had to downvote to 69, kind stranger. God damn it," +
                                " take my damn updoot. r/angryupdoot. Karen took the kids! My? You mean OUR? r/unexpectedcommunism r/twentycharacterlimit r/thirdsub r/fuckthirdsub." +
                                " r/expected for balance r/unexpectedthanos";
                RestAction action = message.getChannel().sendMessage(toSend);
                action.complete();
            }
        }
    }

    private Member getMember(String name){
        for(Member member: members){
            if(member.getName().equals(name)){
                return member;
            }
        }
        return null;
    }

    private boolean checkCmdHistory(Message message){
        long currentTime = System.currentTimeMillis();
        boolean valid = false;
        boolean found = false;
        int rateLimit;
        try{
            File file = new File("TextFiles/Commands/ratelimit.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            rateLimit = Integer.parseInt(line);
            br.close();
            fr.close();
        }catch (Exception e){
            rateLimit = 3;
            logger.createErrorLog("reading the rate limit, defaulting to 3");
        }

        try{
            File file = new File("TextFiles/Commands/commandHistory.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            ArrayList<String> lines = new ArrayList<>();
            String line;
            StringBuilder newLine = new StringBuilder();
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                if(split[0].equals(message.getAuthor().getId())){
                    found = true;
                    newLine.append(split[0]);
                    for(int i = 1; i < rateLimit; i++){
                        long commandTime = Long.parseLong(split[i]);
                        if(currentTime - commandTime > 60000){
                            newLine.append(",");
                            newLine.append(System.currentTimeMillis());
                            for(int j = i; j < rateLimit - i + 1; j++){
                                newLine.append(",");
                                newLine.append(split[j]);
                            }
                            i = line.length();
                            valid = true;
                        }else{
                            newLine.append(",");
                            newLine.append(split[i]);
                        }
                    }
                    lines.add(newLine.toString());
                }else{
                    lines.add(line);
                }
            }

            if(!found){
                StringBuilder newUser = new StringBuilder(Objects.requireNonNull(message.getMember()).getId() + "," + System.currentTimeMillis());
                for(int i = 0; i < rateLimit - 1; i++){
                    newUser.append(",0");
                }
                lines.add(newUser.toString());
                valid = true;
            }
            br.close();
            fr.close();

            FileWriter fw = new FileWriter(file, false);
            for(String s: lines){
                fw.write(s + "\n");
            }
            fw.close();

        }catch(IOException e){
            logger.createErrorLog("Checking the command history of a user " + e.getMessage());
            valid = true;
        }

         return valid;
    }
}
