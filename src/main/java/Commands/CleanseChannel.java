package Commands;

import Logging.Logger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.List;
import java.util.stream.Collectors;

public class CleanseChannel extends Command{
    private int messageLimit;

    public CleanseChannel(Logger logger, String keyword, String description) {
        super(logger, keyword, description);
    }

    public void function(Message message) {
        final String toCleanse;
        TextChannel channel;
        try {
            channel = message.getTextChannel();
            String rawMessage = message.getContentRaw();
            String[] messageSplit = rawMessage.split(getKeyword());
             toCleanse = messageSplit[1].substring(1);
        }catch (Exception e){
        String toSend = "invalid parameters on command try ,help for help";
        message.getTextChannel().sendMessage(toSend).queue();
        return;
    }

        if(toCleanse.length() > 0) {
            List<Message> messages = channel.getIterableHistory().stream().limit(messageLimit).filter(m -> m.getContentRaw().contains(toCleanse)).collect(Collectors.toList());
            messages.remove(0); //removes the ,cleanse message from the list
            for (Message toDelete : messages) {
                RestAction action = toDelete.delete();
                action.complete();
            }

            getLogger().createLog("deleted " + messages.size() + " containing " + toCleanse);
        }else{
            String messageToSend = "<@" + message.getAuthor().getId() + "> there was no phrase inputted to cleanse please try again.";
            getLogger().createLog("no cleanse phrase inputted with the command.");
            message.getTextChannel().sendMessage(messageToSend).queue();
        }

    }

    void setMessageLimit(int messageLimit){
        this.messageLimit = messageLimit;
    }
}
