package commands;


import logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;

public class AddMember extends Command{
    private File file = new File("members.txt");
    public AddMember(Logger logger, String keyword, String description, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
    }

    @Override
    public void function(Message message) {
        String rawMessage = message.getContentRaw();
        String id;
        String name;
        try {
            String[] split = rawMessage.split(" ");
            id = split[1].substring(3, split[1].length() - 1);
            name = split[2];
        }catch (Exception e){
            String toSend = "invalid parameters on command try ,help for help";
            message.getTextChannel().sendMessage(toSend).queue();
            return;
        }

        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null){
                if(line.contains(id)){
                    String[] lineSplit = line.split(",");
                    String toSend = "<@" + id + "> is already a member with the name " + lineSplit[1];
                    message.getTextChannel().sendMessage(toSend).queue();
                    br.close();
                    fr.close();
                    return;
                }
            }
            br.close();
            fr.close();
            FileWriter fw = new FileWriter(file, true);
            fw.write(id + "," + name + ",false\n");
            fw.close();
            getLogger().createLog("Added new member " + id + " with name " + name);
        }catch (IOException e){
            getLogger().createErrorLog("creating a new member " + e.getMessage());
        }

    }
}
