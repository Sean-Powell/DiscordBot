package listeners;

import logging.Logger;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;

public class OnGuildVoiceEvents extends ListenerAdapter{
    private Logger logger;
    private File file = new File("TextFiles/OnGuildVoiceEvents/ChannelJoin.txt");
    private long TIME_REQUIRED_IN_CHANNEL = 5000;

    public OnGuildVoiceEvents(Logger logger){
        this.logger = logger;
    }

    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        super.onGuildVoiceJoin(event);
        updateFile(event.getMember().getId());
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        super.onGuildVoiceMove(event);
        updateFile(event.getMember().getId());
    }

    private void updateFile(String id){
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            ArrayList<String> lines = new ArrayList<>();
            String record = id + "," + System.currentTimeMillis();
            boolean found = false;
            String line;
            while((line = br.readLine()) != null){
                if(line.contains(id)){
                    lines.add(record);
                    found = true;
                }else{
                    lines.add(line);
                }
            }
            br.close();
            fr.close();

            if(!found){
                lines.add(record);
            }

            FileWriter fw = new FileWriter(file, false);
            for(String l: lines){
                fw.write(l + "\n");
            }
            fw.flush();
            fw.close();
        }catch (IOException e){
            logger.createErrorLog("Error updating file join times");
        }
    }

    boolean checkFile(String id){
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(line.contains(id)){
                    String[] split = line.split(",");
                    long currentTime = System.currentTimeMillis();
                    br.close();
                    fr.close();
                    return currentTime - Long.parseLong(split[1]) > TIME_REQUIRED_IN_CHANNEL;
                }
            }

            logger.createErrorLog("user has never joined a channel before");
            br.close();
            fr.close();
        }catch (IOException e){
            logger.createErrorLog("error checking channel join time");
        }
        return false;
    }
}
