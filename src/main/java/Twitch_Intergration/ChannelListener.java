package Twitch_Intergration;
import logging.Logger;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.*;
import java.util.ArrayList;

public class ChannelListener implements Runnable {
    private ChannelChecker channelChecker;
    private Logger logger;
    private TextChannel notificationChannel;

    //talking id 311227229772316672

    private ArrayList<TimeoutObject> timeoutObjects = new ArrayList<>();

    public ChannelListener(ChannelChecker channelChecker, TextChannel channel,  Logger logger){
        this.logger = logger;
        this.channelChecker = channelChecker;
        this.notificationChannel = channel;
        this.run();
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while(true){
           ArrayList<TwitchWatchListenerObject> twitchWatchListObjects =  ReadWatchList();
           if(twitchWatchListObjects != null) {
               ArrayList<ArrayList<String>> result = channelChecker.CheckChannels(twitchWatchListObjects);
               for(int i = 0; i < result.size(); i++){
                   ArrayList<String> list = result.get(i);
                   if(checkStreamID(list.get(4))){
                       TwitchWatchListenerObject obj = new TwitchWatchListenerObject("", "");
                       for(TwitchWatchListenerObject twlo : twitchWatchListObjects){
                           if(twlo.getId().equals(list.get(5))){
                               obj = twlo;
                           }
                       }
                       String message = "@here " + list.get(1) + " has started streaming " + list.get(0) +  " at " + list.get(3) + " " + obj.getMsg();
                       RestAction action = notificationChannel.sendMessage(message);
                       action.complete();

                       timeoutObjects.add(new TimeoutObject(twitchWatchListObjects.get(i).getId()));
                   }
               }
           }

           try {
               Thread.sleep(15000);
           }catch (InterruptedException e){
               logger.createErrorLog(e.getMessage());
           }
        }
    }

    private ArrayList<TwitchWatchListenerObject> ReadWatchList(){
        try{
            ArrayList<TwitchWatchListenerObject> twitchWatchListObjects = new ArrayList<>();
            File file = new File("TextFiles/commands/TwitchWatchList.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                String[] split = line.split(",");
                twitchWatchListObjects.add(new TwitchWatchListenerObject(split[0], split[1]));
            }
            br.close();
            fr.close();
            return twitchWatchListObjects;
        }catch (Exception e) {
            logger.createErrorLog(e.getMessage());
        }
        return null;
    }

    private boolean checkStreamID(String id){
        File file = new File("TextFiles/commands/TwitchStreamHistory.txt");
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(line.equals(id)){
                    br.close();
                    fr.close();
                    return false;
                }
            }

            br.close();
            fr.close();
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(id + "\n");
            fileWriter.close();
        }catch (IOException e){
            logger.createErrorLog(e.getMessage());
        }

        return true;
    }
}

class TimeoutObject{
    private String id;

    TimeoutObject(String id){
        this.id = id;
    }

    String getId() {
        return id;
    }
}
