package main;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class RacismDetection {
    private Logger logger;
    private final int msgHistoryToKeep = 20;

    public RacismDetection(Logger logger){
        this.logger = logger;
    }

    public void checkForNWord(Message message) {
        String rawMessage = message.getContentRaw();
        String id = message.getAuthor().getId();
        String filePath = "Commands/RacistMsgHistory/" + id + ".txt";

        if(containsWord(rawMessage)){
            RestAction action = message.delete();
            message.getChannel().sendMessage("Hey <@" + id + "> you can't say that").queue();
            logger.createLog("Deleting message sent by " + message.getAuthor().getName() + " containing n word");
            action.complete();
            return;
        }

        try {
            FileReader fr = new FileReader("Commands/racist.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(line.contains(message.getAuthor().getId())){
                    FileWriter fw = new FileWriter(filePath, true);
                    fw.write(message.getContentRaw() + "\n");
                    fw.close();

                    fr = new FileReader(filePath);
                    br = new BufferedReader(fr);
                    Queue<String> queue = new LinkedList<>();
                    while((line = br.readLine()) != null){
                        queue.add(line);
                    }
                    String full = "";
                    fw = new FileWriter(filePath, false);
                    for(int i = 0; i < msgHistoryToKeep; i++){
                        if(queue.size() > 0) {
                            line = queue.remove();
                            full = full + line;
                            fw.write(line + "\n");
                        }
                    }

                    fw.close();

                    if(containsWord(full)){
                        message.getChannel().sendMessage("Hey <@" + id + "> you can't say that, not even vertical").queue();
                        logger.createLog("Deleting message sent by " + message.getAuthor().getName() + " containing n word");

                        List<Message> messages = message.getTextChannel().getIterableHistory().stream().limit(6).filter(m -> m.getAuthor().getId().equals(id)).collect(Collectors.toList());
                        for(Message m: messages){
                            RestAction action = m.delete();
                            action.complete();
                        }

                        fw = new FileWriter("Commands/RacistMsgHistory/" + id + ".txt", false);
                        fw.write("");
                        fw.close();
                    }
                }
            }
            br.close();
            fr.close();
        }catch (IOException e){
            logger.createErrorLog("some racist broke it " + e.getMessage());
        }
    }

    private boolean containsWord(String message){
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = message.replaceAll("[^A-za-z1]", "").toLowerCase();
        return message.contains("nigg") || message.contains("n1g") || message.contains("nlg");
    }
}
