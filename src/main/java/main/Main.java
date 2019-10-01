package main;

import Listeners.OnMessageRecieved;
import Listeners.OnUsernameUpdate;
import Logging.Logger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//todo add command to add a new member
//todo add command to raise a user to an admin
//todo add youtube keyword to play a video

public class Main extends ListenerAdapter {
    private static File configFile = new File("config.txt");
    private static BufferedReader configBR;
    private static File membersFile = new File("members.txt");
    private static Logger logger = new Logger("Log.txt");
    private static SendMessage sm;

    private static JDA jda;
    private static String token;

    private static ArrayList<Member> members = new ArrayList<>();
    private static ArrayList<String> admins = new ArrayList<>();

    public static void main(String[] args) {
        logger.createLog("Starting up...");
        setupConfig();
        token = getToken();
        getMembers();
        getAdmins();

        setupJDA();
        setupSendMessage();
        setupLogger();
        addListeners();

    }

    private static void setupConfig(){
        try{
            FileReader configFR = new FileReader(configFile);
            configBR = new BufferedReader(configFR);
        }catch (IOException e){
            logger.createErrorLog("error setting up config file");
        }
    }

    private static String getNextConfigLine(){ //todo add check for end of file, then close the config file
        try {
            return configBR.readLine();
        }catch (IOException e){
            return "";
        }
    }

    private static String getToken(){
        return getNextConfigLine();
    }

    private static void getMembers(){
        try{
            FileReader fr = new FileReader(membersFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                members.add(createMember(line));
            }
            fr.close();
            br.close();
        }catch (IOException e){
            logger.createErrorLog("error reading members list");
        }
    }

    private static Member createMember(String line){
        String[] splitLine = line.split(",");
        boolean isAdmin = splitLine[2].contains("true");
        return new Member(splitLine[0],splitLine[1],isAdmin);
    }

    private static void getAdmins(){
        for(Member member : members){
            if(member.isAdmin()){
                admins.add(member.getId());
            }
        }
    }

    private static void setupJDA(){
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(token).build().awaitReady();
        }catch (InsufficientPermissionException e){
            logger.createErrorLog("Insufficient permission to create jda instance: " + e.getMessage());
        }catch (LoginException e){
            logger.createErrorLog("Login exception encountered: " + e.getMessage());
        }catch (InterruptedException e){
            logger.createErrorLog("Process was interrupted: " + e.getMessage());
        }
    }


    private static void setupLogger(){
        logger.setLogChannel(jda.getTextChannelById(getNextConfigLine()));
    }

    private static void setupSendMessage(){
        sm = new SendMessage(jda.getTextChannelById(getNextConfigLine()));
    }

    private static void addListeners(){
        try {
            jda.addEventListener(new OnUsernameUpdate(logger, sm, members));
            jda.addEventListener(new OnMessageRecieved(logger, members, admins));
        }catch (Exception e){
            logger.createErrorLog("unknown exception " + e.getMessage());
        }
    }



}
