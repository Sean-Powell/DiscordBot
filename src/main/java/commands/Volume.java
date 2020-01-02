package commands;

import logging.Logger;
import youtube_intergration.PlayLink;
import net.dv8tion.jda.api.entities.Message;

import java.io.File;
import java.io.FileWriter;


public class Volume extends Command {
    private  PlayLink playLink;
    private File file = new File("TextFiles/commands/volume.txt");

    public Volume(Logger logger, String keyword, String description, PlayLink playLink, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
        this.playLink = playLink;
    }

    @Override
    public void function(Message message) {
        String rawMessage= message.getContentRaw();
        try{
            String[] split = rawMessage.split(" ");
            int volume = Integer.parseInt(split[1]);
            if (volume < 0 || volume > 100){
                String toSend = "volume has to be in a range from 0 to 100";
                message.getTextChannel().sendMessage(toSend).queue();
                getLogger().createLog("volume was outside of the acceptable range " + volume);
                return;
            }
            playLink.volume(message.getTextChannel(), volume);
            FileWriter fw = new FileWriter(file, false);
            fw.write("" + volume);
            fw.close();
            getLogger().createLog("set the volume to " + volume);
        }catch (Exception e){
            getLogger().createErrorLog("setting the volume " + e.getMessage());
        }
    }
}
