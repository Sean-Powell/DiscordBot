package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;

public class AddYoutubeKeyword extends Command {
    private File file = new File("TextFiles/Commands/youtubeLinks.txt");

    public AddYoutubeKeyword(Logger logger, String keyword, String description, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
    }

    @Override
    public void function(Message message) {
        String name;
        String link;
        try {
            String rawMessage = message.getContentRaw();
            String[] split = rawMessage.split(" ");
            name = split[1].toLowerCase();
            link = split[2];
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
                if(line.contains(name)){
                    String toSend = "there is already a youtube link with that keyword sorry";
                    message.getTextChannel().sendMessage(toSend).queue();
                    br.close();
                    fr.close();
                    getLogger().createLog("already a link with the name " + name);
                    return;
                }
            }

            br.close();
            fr.close();
            FileWriter fw = new FileWriter(file, true);
            fw.write(name + "," + link + "\n");
            fw.close();
            String toSend = "Created a youtube link with the keyword " + name;
            getLogger().createLog(toSend + " to " + link);
            message.getTextChannel().sendMessage(toSend).queue();
        }catch (IOException e){
            getLogger().createErrorLog("creating a new youtube link keyword " + e.getMessage());
        }
    }
}
