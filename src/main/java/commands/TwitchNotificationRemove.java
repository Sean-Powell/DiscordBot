package commands;

import logging.Logger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.RestAction;

public class TwitchNotificationRemove extends Command{
    public TwitchNotificationRemove(Logger logger, String keyword, String description, Boolean adminProtected) {
        super(logger, keyword, description, adminProtected);
    }

    public void function(Message message){
        Member member = message.getMember();
        if(member == null){
            getLogger().createErrorLog("The message memeber is null");
            return;
        }

        Role role = message.getGuild().getRoleById("671472280362024970");
        if(role == null){
            getLogger().createErrorLog("The notification role could not be found");
            return;
        }

        RestAction action = member.getGuild().removeRoleFromMember(member, role);
        action.complete();

        action = message.getTextChannel().sendMessage("I removed the role from you");
        action.complete();
    }
}
