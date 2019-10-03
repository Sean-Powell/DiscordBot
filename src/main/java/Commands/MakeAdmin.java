package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.util.ArrayList;

public class MakeAdmin extends Command {
    private File file = new File("members.txt");
    public MakeAdmin(Logger logger, String keyword, String description, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
    }

    @Override
    public void function(Message message) {
        String id;
        ArrayList<String> newFile = new ArrayList<>();
        boolean found = false;

        try {
            String rawMessage = message.getContentRaw();
            String[] split = rawMessage.split(" ");
            id = split[1].substring(3, split[1].length() - 1);
        }catch (Exception e){
            String toSend = "invalid parameters on command try ,help for help";
            message.getTextChannel().sendMessage(toSend).queue();
            return;
        }

        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(line.contains(id)){
                    String[] lineSplit = line.split(",");
                    String newLine = lineSplit[0] + "," + lineSplit[1] + ",true";
                    newFile.add(newLine);
                    found = true;
                }else{
                    newFile.add(line);
                }
            }
            br.close();
            fr.close();

            FileWriter fw = new FileWriter(file, false);
            for(String l: newFile){
                fw.write(l + "\n");
            }
            fw.close();

            String toSend;
            if(found){
               getLogger().createLog("upgraded " + message.getGuild().getMemberById(id) + " to admin");
               toSend = "upgraded <@" + id + "> to admin";
            }else{
                toSend = "<@" + id + "> is not a member add them as a member first";
            }
            message.getTextChannel().sendMessage(toSend).queue();
        }catch (IOException e){
            getLogger().createErrorLog("error upgrading person to admin " + e.getMessage());
        }
    }
}
