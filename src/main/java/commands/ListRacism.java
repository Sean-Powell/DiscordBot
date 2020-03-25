package commands;

import logging.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ListRacism extends Command {
    File file = new File("TextFiles/commands/NWordCount.txt");
    public ListRacism(Logger logger, String keyword, String description, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
    }

    public void function(Message message){
        StringBuilder toSend = new StringBuilder();
        Guild guild = message.getGuild();
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                Member member = guild.getMemberById(split[0]);
                if(member == null){
                    getLogger().createErrorLog("The member could not be found in listing the n word count");
                    return;
                }
                toSend.append(member.getUser().getName()).append(": ").append(split[2]).append("\n");
            }
        }catch (IOException e){
            getLogger().createErrorLog("Error when listing all n word history " + e.getMessage());
        }

        RestAction action = message.getTextChannel().sendMessage(toSend);
        action.complete();
    }
}

class Randle{

}
