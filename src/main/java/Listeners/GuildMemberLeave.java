package Listeners;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.List;

public class GuildMemberLeave extends ListenerAdapter {
    private Logger logger;
    public GuildMemberLeave(Logger logger){
        this.logger = logger;
    }

    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        List<Role> roles = event.getMember().getRoles();
        StringBuilder toWrite = new StringBuilder(event.getMember().getId());
        for(Role role: roles){
            toWrite.append(",").append(role.getId());
        }

        try{
            File file = new File("TextFiles/GuildMemberLeave/guildMemberLeaveRoles.txt");
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(toWrite.toString() + "\n");
            bw.close();
            fw.close();
            logger.createLog("Users roles save to file");
        }catch (IOException e){
            logger.createErrorLog("An error occurred during saving the users roles " + e.getMessage());
        }
    }

}
