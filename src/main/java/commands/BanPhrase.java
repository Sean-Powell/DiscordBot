package commands;

import java.io.*;

import logging.Logger;
import net.dv8tion.jda.api.entities.Message;

public class BanPhrase extends Command{
    private File bannedPhrases = new File("TextFiles/commands/bannedPhrases.txt");
    private Logger logger;

    public BanPhrase(Logger logger, String keyword, String description, Boolean adminProtected) {
        super(logger, keyword, description, adminProtected);
        this.logger = logger;
    }

    public void function(Message message){
        try {
            FileWriter fileWriter = new FileWriter(bannedPhrases);
            String rawMessage = message.getContentRaw();
            String[] toBan = rawMessage.split(getKeyword());
            String banned = toBan[1].substring(1);

            fileWriter.write(banned + "\n");
            logger.createLog("added new banned phrase " + banned + " to the ban list");
            fileWriter.close();
        }catch (IOException e){
            String toSend = "There was an error adding that word to the ban list";
            message.getTextChannel().sendMessage(toSend).queue();
            logger.createErrorLog("adding new banned word to the banned word list " + e.getMessage());
        }
    }

    public boolean checkString(Message message){
        try{
            FileReader fr = new FileReader(bannedPhrases);
            BufferedReader br = new BufferedReader(fr);
            String messageRaw = message.getContentRaw();
            String line;

            while((line = br.readLine()) != null){
                if(messageRaw.contains(line)){
                    br.close();
                    fr.close();
                    logger.createLog("message contained " + line);
                    return true;
                }
            }
            br.close();
            fr.close();
        }catch (IOException e){
            logger.createErrorLog("opening file to read banned phrases " + e.getMessage());
        }
        return false;
    }
}
