package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;

public class BanFromDeepFrying extends Command {
    private File file = new File("Commands/bannedFromDeepFrying.txt");
    public BanFromDeepFrying(Logger logger, String keyword, String description){
        super(logger, keyword, description);
    }

    @Override
    public void function(Message message) { //,banDF <@!156120841891872768>
        String toBan;
        boolean contained;
        try {
            String rawMessage = message.getContentRaw();
            String[] split = rawMessage.split(getKeyword());
            toBan = split[1].substring(1);
            toBan = toBan.substring(3, toBan.length() - 1);
            contained = false;
        }catch (Exception e){
            String toSend = "invalid parameters on command try ,help for help";
            message.getTextChannel().sendMessage(toSend).queue();
            return;
        }

        try{
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line = bufferedReader.readLine()) != null){
                if(line.equals(toBan)){
                    contained = true;
                }
            }
            bufferedReader.close();
            fileReader.close();
            if(!contained){
                FileWriter fileWriter = new FileWriter(file, true);
                fileWriter.write(toBan + "\n");
                getLogger().createLog("added " + message.getGuild().getMemberById(toBan).getNickname() + " to the ban list");
            }else{
                String toSend = "<@" + message.getAuthor().getId() + "> that user is already banned from deep frying.";
                message.getTextChannel().sendMessage(toSend).queue();
                getLogger().createLog("user was already banned from deep frying");
            }
        }catch(IOException e){
            getLogger().createErrorLog("unable to add user to the ban list of deep frying");
        }
    }
}
