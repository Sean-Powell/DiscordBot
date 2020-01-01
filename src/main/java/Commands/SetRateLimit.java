package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.util.ArrayList;

public class SetRateLimit extends Command {
    private File file = new File("TextFiles/Commands/ratelimit.txt");
    private File cmdFile = new File("TextFiles/Commands/commandHistory.txt");
    public SetRateLimit(Logger logger, String keyword, String description, Boolean adminProtected) {
        super(logger, keyword, description, adminProtected);
    }

    public void function(Message message){
        String rawMessage = message.getContentRaw();
        int rateLimit;
        try{
            String[] split = rawMessage.split(" ");
            rateLimit = Integer.parseInt(split[1]);
        }catch (Exception e){
            String toSend = "Invalid parameters, please enter a numerical rate limit";
            message.getTextChannel().sendMessage(toSend).queue();
            return;
        }

        try{
            FileWriter fw = new FileWriter(file, false);
            fw.write(rateLimit + "\n");
            fw.close();
            getLogger().createLog("Set the rate limit to " + rateLimit);
        }catch(IOException e){
            getLogger().createErrorLog("setting the new rate limit " + e.getMessage());
        }

        try{
            FileReader fr = new FileReader(cmdFile);
            BufferedReader br = new BufferedReader(fr);
            ArrayList<String> newLines = new ArrayList<>();
            String line;
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                if(split.length + 1 > rateLimit){
                    StringBuilder newLine = new StringBuilder();
                    newLine.append(split[0]);
                    for(int i = 1; i < rateLimit + 1; i++){
                        newLine.append(",").append(split[i]);
                    }
                    newLines.add(newLine.toString());
                }else if(split.length + 1 < rateLimit){
                    StringBuilder newLine = new StringBuilder();
                    newLine.append(line);
                    for(int i = split.length; i < rateLimit + 1; i++){
                        newLine.append(",0");
                    }
                    newLines.add(newLine.toString());
                }else{
                    newLines.add(line);
                }
            }
            br.close();
            fr.close();

            FileWriter fw = new FileWriter(cmdFile, false);
            for(String s: newLines){
                fw.write(s + "\n");
            }
            fw.close();
        }catch(Exception e){
            getLogger().createErrorLog("in updating the command history file");
        }
    }
}
