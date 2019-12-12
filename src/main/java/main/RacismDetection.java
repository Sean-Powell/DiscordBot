package main;

import Logging.Logger;
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

    public void checkForNWord(Message message) {
        String rawMessage = message.getContentRaw();
        String id = message.getAuthor().getId();
        String filePath = "TextFiles/Commands/RacistMsgHistory/" + id + ".txt";

        if (containsWord(rawMessage)) {
            if(message.getMember().getUser().getId().equals("252832922564820992")){
                RestAction action = message.delete();
                action.complete();
            }else {
                RestAction action = message.delete();
                message.getChannel().sendMessage("Hey <@" + id + "> you can't say that").queue();
                logger.createLog("Deleting message sent by " + message.getAuthor().getName() + " containing n word");
                action.complete();
                increaseCount(message);
            }
            return;
        }

        try {
            FileReader fr = new FileReader("TextFiles/Commands/racist.txt");
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

                        fw = new FileWriter("TextFiles/Commands/RacistMsgHistory/" + id + ".txt", false);
                        fw.write("");
                        fw.close();
                        increaseCount(message);
                    }
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            logger.createErrorLog("some racist broke it " + e.getMessage());
        }
    }

    private boolean containsWord(String message) { //todo improve the detection method
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = message.replaceAll("[^A-za-z1]", "").toLowerCase();
        return message.contains("nigg") || message.contains("nlgg") || message.contains("n1gg");
    }


    private void increaseCount(Message message) {
        User user = message.getAuthor();
        String userID = user.getId();
        File file = new File("TextFiles/Commands/NWordCount.txt");
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
                RoleAction role =  message.getGuild().createRole();
                role = role.setName("N Word Count: " + 0);
                Role newRole = role.complete();
                roleID = newRole.getId();
                Member member = message.getGuild().getMember(message.getAuthor());
                if(member == null){
                    logger.createErrorLog("Member could not be found " + message.getAuthor());
                    return;
                }
                RestAction action = message.getGuild().addRoleToMember(member, newRole);
                action.complete();
            }
            count++;

            Role role = message.getGuild().getRoleById(roleID);
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
