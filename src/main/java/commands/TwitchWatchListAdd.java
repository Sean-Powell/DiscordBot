package commands;

import Twitch_Intergration.ChannelChecker;
import logging.Logger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TwitchWatchListAdd extends Command {
    private ChannelChecker channelChecker;
    public TwitchWatchListAdd(Logger logger, String keyword, String description, ChannelChecker channelChecker, Boolean adminProtected) {
        super(logger, keyword, description, adminProtected);
        this.channelChecker = channelChecker;
    }

    //twitch id, custom message
    public void function(Message message){
        File file = new File("TextFiles/commands/TwitchWatchList.txt");
        String[] split = message.getContentRaw().split(" ");
        String id = channelChecker.GetUserID(split[1]);
        if(id == null){
            RestAction action = message.getTextChannel().sendMessage("That username could not be found on twitch, sorry.");
            action.submit();
            return;
        }

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(line.contains(id)){
                    RestAction action = message.getTextChannel().sendMessage("That username is already being tracked.");
                    action.submit();
                    br.close();
                    fr.close();
                    return;
                }
            }

            br.close();
            fr.close();

            FileWriter fileWriter = new FileWriter(file, true);
            StringBuilder msg = new StringBuilder(split[2]);
            for(int i = 3; i < split.length; i++){
                msg.append(" ").append(split[i]);
            }
            fileWriter.write(id + "," + msg.toString() + "\n");
            fileWriter.close();
            RestAction action = message.getTextChannel().sendMessage("That username is now being tracked.");
            action.complete();
        }catch (Exception e){
            getLogger().createErrorLog(e.getMessage());
            RestAction action = message.getTextChannel().sendMessage("Sorry an error occurred while trying to track that channel.");
            action.complete();
        }
    }
}
