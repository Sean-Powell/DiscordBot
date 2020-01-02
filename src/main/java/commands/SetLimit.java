package commands;

import logging.Logger;
import net.dv8tion.jda.api.entities.Message;

public class SetLimit extends Command {
    private CleanseChannel cleanseChannel;

    public SetLimit(Logger logger, String keyword, CleanseChannel cleanseChannel, String description, Boolean adminProtected) {
        super(logger, keyword, description, adminProtected);
        this.cleanseChannel = cleanseChannel;
    }

    @Override
    public void function(Message message) {
        String rawMessage = message.getContentRaw();
        String newLimit;

        try {
            String[] split = rawMessage.split(getKeyword());
            newLimit = split[1].substring(1);
        }catch (Exception e){
            String toSend = "invalid parameters on command try ,help for help";
            message.getTextChannel().sendMessage(toSend).queue();
            return;
        }
        try {
            cleanseChannel.setMessageLimit(Integer.parseInt(newLimit));
            getLogger().createLog("new limit set to " + newLimit);
        } catch (NumberFormatException e) {
            getLogger().createErrorLog("in converting " + newLimit + " to an integer");
        }
    }
}
