package commands;

import logging.Logger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.RestAction;

public class TwitchNotificationAdd extends Command {
    public TwitchNotificationAdd(Logger logger, String keyword, String description, Boolean adminProtected) {
        super(logger, keyword, description, adminProtected);
    }

    public void function(Message message){
        Role role = message.getGuild().getRoleById("671472280362024970");
        if(role == null){
            getLogger().createErrorLog("The notification role could not be found");
            return;
        }

        Member member = message.getMember();
        if(member == null){
            getLogger().createErrorLog("The message memeber is null");
            return;
        }
        RestAction action =  member.getGuild().addRoleToMember(member, role);
        action.complete();


        action = message.getTextChannel().sendMessage("I added the role to you :)");
        action.complete();
    }
}
