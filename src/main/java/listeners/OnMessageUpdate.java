package listeners;

import logging.Logger;
import main.RacismDetection;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class OnMessageUpdate extends ListenerAdapter {
    private RacismDetection racismDetection;
    public OnMessageUpdate(Logger logger){
        racismDetection = new RacismDetection(logger);
    }

    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        racismDetection.checkForNWord(event.getMessage());
    }
}
