package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ListYTKeywords extends Command {
    File file = new File("TextFiles/Commands/youtubeLinks.txt");
    public ListYTKeywords(Logger logger, String keyword, String description, boolean adminProtected){
        super(logger, keyword, description, adminProtected);
    }

    @Override
    public void function(Message message) {
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            StringBuilder toSend = new StringBuilder("The YouTube keywords are:\n");
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                toSend.append(split[0]).append("\n");
            }
            message.getTextChannel().sendMessage(toSend.toString()).queue();
            getLogger().createLog("listed all YT keywords");
        }catch (IOException e){
            getLogger().createErrorLog("listing the YT keywords " + e.getMessage());
        }
    }
}
