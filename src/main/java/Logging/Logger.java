package Logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.dv8tion.jda.api.entities.TextChannel;

import org.jetbrains.annotations.NotNull;

public class Logger {
    private File logFile;
    private TextChannel logChannel = null;

    public Logger(String fileLocation){
        logFile = new File(fileLocation);
    }

    public void setLogChannel(TextChannel channel){this.logChannel = channel;}

    public void createLog(String message){
        try{
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(getTimeStamp() + message + "\n");
            fw.close();
            notifyLogChannel(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void createErrorLog(String message){
        try{
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(getTimeStamp() + "ERROR - " + message + "\n");
            fw.close();
            notifyLogChannel("<@156120841891872768> ERROR: " + message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void notifyLogChannel(String message){
        if(logChannel != null){
            logChannel.sendMessage(message).queue();
        }else{
            System.out.println("CRITICAL: log channel not set");
        }
    }

    @NotNull
    private String getTimeStamp(){
        return new SimpleDateFormat("[dd:MM:yyyy | HH:mm:ss]: ").format(Calendar.getInstance().getTime());
    }
}
