package Listeners;

import Commands.*;
import Logging.Logger;
import YoutubeIntergration.PlayLink;
import main.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class OnMessageRecieved extends ListenerAdapter {
    private ArrayList<Command> commands = new ArrayList<>();
    private Logger logger;
    private ArrayList<String> admins;
    private ArrayList<Member> members;
    private Member bot;

    private ArrayList<String> bannedFromDF = new ArrayList<>();
    private File bannedFromDFFile = new File("Commands/bannedFromDeepFrying.txt");

    private BanPhrase bannedPhrases;
    private CleanseChannel cleanseChannel;
    private PlayLink playLink = new PlayLink();

    public OnMessageRecieved(Logger logger, ArrayList<Member> members, ArrayList<String> admins) throws Exception{
        this.logger = logger;
        this.admins = admins;
        this.members = members;
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

    private void addCommands(){ //todo add the rest of the commands
        bannedPhrases = new BanPhrase(logger, "ban", ",ban 'phrase' - bans a phrase from being used");
        cleanseChannel = new CleanseChannel(logger, "cleanse", ",cleanse text - clears all occurances of the text in the channel in the last 'limit' messages");

        commands.add(bannedPhrases);
        commands.add(cleanseChannel);
        commands.add(new UnbanPhrase(logger, "unban", ",unban 'phrase' - unbans a phrase from being used"));
        commands.add(new SetLimit(logger, "limit", cleanseChannel, ",limit 'limit' - sets the limit for the cleanse command"));
        commands.add(new BanFromDeepFrying(logger, "dfban", ",dfban @user - bans the user from using deep fryer"));
        commands.add(new UnbanFromDeepFrying(logger, "dfunban", ",dfunban @user - unbans the user from using deep fryer"));
        commands.add(new AddMember(logger, "member", ",member @user name - makes a new memember for the user"));
        commands.add(new MakeAdmin(logger, "admin", ",adming @user - makes a user into an admin"));
        commands.add(new Help(logger, "help", commands, ",help - displays the help page"));
        commands.add(new AddYoutubeKeyword(logger, "ytadd", ",ytadd 'name' 'link' - creates a new keyword that when typed will play that youtube video in the users channel"));
        commands.add(new RemoveYoutubeKeyword(logger, "ytremove", ",ytremove 'name' - removes the keyword from the list"));
    }

    public void onMessageReceived(MessageReceivedEvent event){
        User author = event.getAuthor();
        Message message = event.getMessage();
        String rawMessage = message.getContentRaw();
        if(checkIfBotSentMsg(author)){
            return;
        }

        if(admins.contains(author.getId())){
            String[] messageSplit = rawMessage.split(" ");
            String keyword = messageSplit[0];
            checkCommands(keyword, message);
        }

        addMessageContainmentChecks(message, event);
    }

    private void addMessageContainmentChecks(Message message, MessageReceivedEvent event){
        checkForNigger(message);
        checkForBannedPhrase(message);
        checkForDeepFry(message, event);
        checkForAlexGif(message);
        checkForYTKeyword(message);
    }

    private void checkForDeepFry(Message message, MessageReceivedEvent event){ //todo add method to remove and add people from being blocked from deep frying
        String rawMessage = message.getContentRaw();
        String authorID = message.getAuthor().getId();
        if(bannedFromDF.contains(authorID)){
            String rawMessagesSpacesRemoved = rawMessage.replaceAll("\\s+", "");
            String rawMessageSymbolsRemoved = rawMessagesSpacesRemoved.
                    replaceAll("[-_+^()<{}&%$¦£\\[\\]€\"!>:;,\\\\/*~#|@]", "");

            if (rawMessagesSpacesRemoved.contains(".df")){
                AuditableRestAction result = event.getGuild().getController().kick(authorID);
                String messageToSend = "<@" + authorID + "> you are banned from deep frying";
                logger.createLog("kicking " + message.getAuthor().getName() + " for deep frying while banned");
                message.getTextChannel().sendMessage(messageToSend).queue();
                result.submit();
            }else if(rawMessageSymbolsRemoved.contains(".df")){
                AuditableRestAction result = event.getGuild().getController().kick(authorID);
                String messageToSend = "<@ " + authorID + "> nice attempt but you are still banned from deep frying";
                logger.createLog("kicking " + message.getAuthor().getName() + " for deep frying, attempted to get around the ban");
                message.getTextChannel().sendMessage(messageToSend).queue();
                result.submit();
            }
        }
    }

    private void checkForYTKeyword(Message message){
        String rawMessage = message.getContentRaw();
        File file = new File("Commands/youtubeLinks.txt");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(",");
                if(rawMessage.contains(lineSplit[0])){
                    playLink.play(message, lineSplit[1]);
                }
            }
        }catch (IOException e){
            logger.createErrorLog("error in checking youtube keywords " + e.getMessage());
        }
    }

    private void checkForBannedPhrase(Message message){
        if(bannedPhrases.checkString(message)){
            RestAction action = message.delete();
            message.getChannel().sendMessage("Hey <@ " + message.getAuthor().getId() + "> that message contained a banned phrase sorry").queue();
            logger.createLog("Deleted message for containing banned phrase " + message);
            action.complete();
        }
    }

    private void checkCommands(String keyword, Message message){
        for(Command command: commands){
            if(command.getKeyword().equals(keyword)){
                command.function(message);
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

    private boolean checkIfBotSentMsg(User author){
        return author.getId().equals(bot.getId());
    }

    private void checkForNigger(Message message) {
        if(message.getContentRaw().toLowerCase().matches("n+\\s*[i1]+\\s*g+\\s*g+\\s*[ea3]+\\s*r*+\\s*")){
            RestAction action = message.delete();
            message.getChannel().sendMessage("Hey <@" + message.getAuthor().getId() + "> you can't say that").queue();
            logger.createLog("Deleting message sent by " + message.getAuthor().getName() + " containing nigger");
            action.complete();
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
}
