package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;

public class UnbanFromDeepFrying extends Command {
    private File file = new File("TextFiles/Commands/bannedFromDeepFrying.txt");
    public UnbanFromDeepFrying(Logger logger, String keyword, String description, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
    }

    @Override
    public void function(Message message) {
        String toUnban;
        ArrayList<String> stillBanned;
        try {
            String rawMessage = message.getContentRaw();
            String[] split = rawMessage.split(getKeyword());
            toUnban = split[1].substring(1);
            toUnban = toUnban.substring(3, toUnban.length() - 1);
            stillBanned = new ArrayList<>();
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
                if(!line.equals(toUnban)){
                    stillBanned.add(line);
                }
            }

            br.close();
            fr.close();

            FileWriter fw = new FileWriter(file, false);
            for(String id: stillBanned){
                fw.write(id + "\n");
            }

            String toSend = message.getGuild().getMemberById(toUnban) + " has been removed from the deep fry ban list";
            message.getTextChannel().sendMessage(toSend).queue();
            getLogger().createLog("user " + toUnban + " removed from deep fry ban list");
        }catch (IOException e){
            getLogger().createErrorLog("unable to delete user " + toUnban + " from the deep fry banned list " + e.getMessage());
        }
    }
}
