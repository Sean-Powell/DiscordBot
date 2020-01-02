package commands;

import logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.util.ArrayList;

public class UnbanPhrase extends Command {

    private File bannedPhrases = new File("TextFiles/commands/bannedPhrases.txt");

    public UnbanPhrase(Logger logger, String keyword, String description, Boolean adminProtected) {
        super(logger, keyword, description, adminProtected);
    }

    public void function(Message message) {
        String rawMessage = message.getContentRaw();
        String unban;

        try {
            String[] split = rawMessage.split(getKeyword());
            unban = split[1].substring(1);
        }catch (Exception e){
            String toSend = "invalid parameters on command try ,help for help";
            message.getTextChannel().sendMessage(toSend).queue();
            return;
        }

        ArrayList<String> keptPhrases = new ArrayList<>();
        try{
            FileReader fr = new FileReader(bannedPhrases);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(!line.equals(unban)){
                    keptPhrases.add(line);
                }
            }
            br.close();
            fr.close();

            FileWriter fw = new FileWriter(bannedPhrases, false);
            for(String phrase: keptPhrases){
                fw.write(phrase + "\n");
            }
            fw.close();
            getLogger().createLog("removed " + unban + " from the ban list");
        }catch (IOException e){
            getLogger().createErrorLog("in removing a phrase " + unban + " from the ban list " + e.getMessage());
        }
    }
}
