package Commands;

import Logging.Logger;
import YoutubeIntergration.PlayLink;
import net.dv8tion.jda.api.entities.Message;

public class Skip extends Command{
    private PlayLink playLink;
    public Skip(Logger logger, String keyword, String description, PlayLink playLink, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
        this.playLink = playLink;
    }

    @Override
    public void function(Message message) {
        playLink.skip(message.getTextChannel());
    }
}
