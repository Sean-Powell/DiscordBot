package commands;

import Twitch_Intergration.ChannelChecker;
import logging.Logger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class TwitchWatchListRemove extends Command{

    private ChannelChecker channelChecker;
    public TwitchWatchListRemove(Logger logger, String keyword, String description, ChannelChecker channelChecker, Boolean adminProtected) {
        super(logger, keyword, description, adminProtected);
        this.channelChecker = channelChecker;
    }

    public void function(Message message){
        String[] split = message.getContentRaw().split(" ");
        String id = channelChecker.GetUserID(split[1]);
        File file = new File("TextFiles/commands/TwitchWatchList.txt");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            ArrayList<String> strings = new ArrayList<>();
            String line;
            while((line = br.readLine()) != null){
                if(!line.contains(id)){
                    strings.add(line);
                }
            }

            br.close();
            fr.close();

            FileWriter fileWriter = new FileWriter(file, false);
            for(String s: strings){
                fileWriter.write(s + "\n");
            }
            fileWriter.close();
            RestAction action = message.getTextChannel().sendMessage("That channel has been removed");
            action.complete();
        }catch (Exception e){
            getLogger().createErrorLog(e.getMessage());
            RestAction action = message.getTextChannel().sendMessage("An error occurred removing that channel from the list");
            action.complete();
        }
    }
}
