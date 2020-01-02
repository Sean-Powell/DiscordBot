package commands;

import logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;

public class BanFromNameChanges extends Command{
    private File file = new File("TextFiles/OnUserNameUpdate/usernameChangesTargets.txt");
    public BanFromNameChanges(Logger logger, String keyword, String decription, Boolean adminProtected){
        super(logger, keyword, decription, adminProtected);
    }

    @Override
    public void function(Message message) {
        String rawMessage = message.getContentRaw();
        try{
            String[] split = rawMessage.split(" ");
            String id = split[1].substring(3, split[1].length() - 1);

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null){
                if(id.equals(line)){
                    String toSend = "That user is already on the list";
                    getLogger().createLog("The user was already in the name change ban list");
                    message.getTextChannel().sendMessage(toSend).queue();
                    br.close();
                    fr.close();
                    return;
                }
            }

            FileWriter fw = new FileWriter(file, true);
            fw.write(id + "\n");
            fw.close();
            getLogger().createLog("added user to the banned name change list");
        }catch (IOException ioe){
            getLogger().createErrorLog("in adding a user to the banned from name change list " + ioe.getMessage());
        }
    }
}
