package listeners;

import logging.Logger;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;

public class GuildMemberJoin extends ListenerAdapter {
    private Logger logger;
    public GuildMemberJoin(Logger logger){
        this.logger = logger;
    }

    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        Member member =  event.getMember();
        String id = member.getId();
        try {
            File file = new File("TextFiles/GuildMemberLeave/guildMemberLeaveRoles.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            ArrayList<String> lines = new ArrayList<>();

            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                if(id.equals(split[0])){
                    Guild guild = member.getGuild();
                    ArrayList<Role> roleList = new ArrayList<>();
                    for(int i = 1; i < split.length; i++){
                        roleList.add(guild.getRoleById(split[i]));
                    }

                    try {
                        AuditableRestAction action = member.getGuild().modifyMemberRoles(member, roleList, null);
                        action.complete();
                    }catch (HierarchyException e){
                        logger.createErrorLog("Could not assign the role to the user " + e.getMessage());
                    }
                }else{
                    lines.add(line);
                }
            }
            br.close();
            fr.close();

            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String l: lines){
                bw.write(l + "\n");
            }
            bw.close();
            fw.close();
            logger.createLog("added roles back to user");
        }catch (IOException e){
            logger.createErrorLog("Error adding roles back to user " + e.getMessage());
        }
    }
}