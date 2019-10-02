package Commands;

import Logging.Logger;
import YoutubeIntergration.PlayLink;
import net.dv8tion.jda.api.entities.Message;

public class Volume extends Command {
    private  PlayLink playLink;
    public Volume(Logger logger, String keyword, String description, PlayLink playLink){
        super(logger, keyword, description);
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
            getLogger().createLog("set the volume to " + volume);
        }catch (Exception e){
            getLogger().createErrorLog("setting the volume " + e.getMessage());
        }
    }
}
