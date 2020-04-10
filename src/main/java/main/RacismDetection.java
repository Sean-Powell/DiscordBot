package main;

import logging.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class RacismDetection {
    private Logger logger;
    private final int msgHistoryToKeep = 20;

    public RacismDetection(Logger logger) {
        this.logger = logger;
    }

    public boolean checkForNWord(Message message, String text) {
        String id = message.getAuthor().getId();
        String filePath = "TextFiles/commands/RacistMsgHistory/" + id + ".txt";

        if (containsWord(text)) {
            if(message.getMember().getUser().getId().equals("252832922564820992")){
                RestAction action = message.delete();
                action.complete();
            }else {
                RestAction action = message.delete();
                message.getChannel().sendMessage("Hey <@" + id + "> you can't say that").queue();
                if(text.length() >= 2000){
                    logger.createLog("Deleting message sent by " + message.getAuthor().getName() + " was over 2000 chars long so can't post text");
                }else {
                    logger.createLog("Deleting message sent by " + message.getAuthor().getName() + " containing n word msg was " + text);
                }
                action.complete();
                increaseCount(message.getMember(), message.getGuild());
            }
            return true;
        }

        try {
            FileReader fr = new FileReader("TextFiles/commands/racist.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(message.getAuthor().getId())) {
                    FileWriter fw = new FileWriter(filePath, true);
                    fw.write(message.getContentRaw() + "\n");
                    fw.close();

                    fr = new FileReader(filePath);
                    br = new BufferedReader(fr);
                    Queue<String> queue = new LinkedList<>();
                    while ((line = br.readLine()) != null) {
                        queue.add(line);
                    }
                    StringBuilder full = new StringBuilder();
                    fw = new FileWriter(filePath, false);

                    if(queue.size() > msgHistoryToKeep){
                        queue.remove();
                    }

                    for (int i = 0; i < msgHistoryToKeep; i++) {
                        if (queue.size() > 0) {
                            line = queue.remove();
                            full.append(line);
                            fw.write(line + "\n");
                        }
                    }

                    fw.close();

                    if (containsWord(full.toString())) {
                        message.getChannel().sendMessage("Hey <@" + id + "> you can't say that, not even vertical").queue();
                        logger.createLog("Deleting message sent by " + message.getAuthor().getName() + " containing n word");

                        List<Message> messages = message.getTextChannel().getIterableHistory().stream().limit(6).filter(m -> m.getAuthor().getId().equals(id)).collect(Collectors.toList());
                        for (Message m : messages) {
                            RestAction action = m.delete();
                            action.complete();
                        }

                        fw = new FileWriter("TextFiles/commands/RacistMsgHistory/" + id + ".txt", false);
                        fw.write("");
                        fw.close();
                        increaseCount(message.getMember(), message.getGuild());
                        br.close();
                        fr.close();
                        return true;
                    }
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            logger.createErrorLog("some racist broke it " + e.getMessage());
        }
        return false;
    }

    public boolean containsWord(String message) { //todo improve the detection method
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = message.replaceAll("[^A-za-z1]", "").toLowerCase();
        return message.contains("nigg") || message.contains("nlgg") || message.contains("n1gg") ||
                message.contains("n|gg") || message.contains("n/gger") || message.contains("n\\gger");
    }


    public void increaseCount(Member member, Guild guild) {
        if(member == null){
            logger.createErrorLog("Member could not be found");
            return;
        }
        User user = member.getUser();
        String userID = user.getId();
        File file = new File("TextFiles/commands/NWordCount.txt");
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String roleID = "";
            int count = -1;
            ArrayList<String> otherEntries = new ArrayList<>();
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                String id = split[0];
                if(id.equals(userID)){
                    count = Integer.parseInt(split[2]);
                    roleID = split[1];
                }else{
                    otherEntries.add(line);
                }
            }

            br.close();
            fr.close();

            if(count == -1){
                count = 0;
                RoleAction role = guild.createRole();
                role = role.setName("N Word Count: " + 0);
                Role newRole = role.complete();
                roleID = newRole.getId();

                RestAction action = guild.addRoleToMember(member, newRole);
                action.complete();
            }
            count++;

            Role role = guild.getRoleById(roleID);
            if(role == null){
                logger.createErrorLog("The role does not exist " + roleID);
                return;
            }

            RoleManager roleManager = role.getManager();
            RestAction action = roleManager.setName("N Word Count: " + count);
            action.submit();

            String newLine = userID + "," + roleID + "," + count;
            otherEntries.add(newLine);
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for(String entry: otherEntries){
                bufferedWriter.write(entry + "\n");
            }

            bufferedWriter.close();
            fileWriter.close();
            logger.createLog("Updated the role on the user");
        }catch (IOException e){
            logger.createErrorLog("Error incrementing count " + e.getMessage());
        }
    }
}
