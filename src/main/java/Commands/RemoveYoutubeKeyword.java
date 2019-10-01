package Commands;

import Logging.Logger;
import net.dv8tion.jda.core.entities.Message;

import java.io.*;
import java.util.ArrayList;

public class RemoveYoutubeKeyword extends Command{
    File file = new File("Commands/youtubeLinks.txt");
    public RemoveYoutubeKeyword(Logger logger, String keyword, String description){
        super(logger, keyword, description);
    }

    @Override
    public void function(Message message) {
        String name;
        try{
        String rawMessage = message.getContentRaw();
        String[] split = rawMessage.split(" ");
        name = split[1];
    }catch (Exception e){
        String toSend = "invalid parameters on command try ,help for help";
        message.getTextChannel().sendMessage(toSend).queue();
        return;
    }

        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            boolean found = false;
            ArrayList<String> newFile = new ArrayList<>();

            while((line = br.readLine()) != null){
                if(line.contains(name)){
                    found = true;
                }else{
                    newFile.add(line);
                }
            }
            br.close();
            fr.close();

            if(!found){
                String toSend = "there is no youtube link with the name " + name;
                message.getTextChannel().sendMessage(toSend).queue();
                return;
            }

            FileWriter fw = new FileWriter(file, false);
            for(String newLine: newFile){
                fw.write(newLine + "\n");
            }
            fw.close();
            String toSend = "removed youtube link with the name " + name;
            getLogger().createLog(toSend);
            message.getTextChannel().sendMessage(toSend).queue();
        }catch (IOException e){
            getLogger().createErrorLog("removing youtube link with the name " + name + " " + e.getMessage());
        }
    }
}
