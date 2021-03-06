import Twitch_Intergration.ChannelListener;
import listeners.*;
import logging.Logger;

import main.RacismDetection;
import youtube_intergration.PlayLink;
import Twitch_Intergration.ChannelChecker;

import main.Member;
import main.SendMessage;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;


public class DiscordBot extends ListenerAdapter {
    private static File configFile = new File("TextFiles/config.txt");
    private static BufferedReader configBR;
    private static File membersFile = new File("TextFiles/members.txt");
    private static Logger logger = new Logger("TextFiles/Log.txt");

    private static PlayLink link;
    private static ChannelChecker channelChecker;
    private static ChannelListener channelListener;

    private static SendMessage sm;
    private static RacismDetection rm;

    private static JDA jda;
    private static String token;

    private static OnGuildVoiceEvents onGuildVoiceEvents;

    private static ArrayList<Member> members = new ArrayList<>();
    private static ArrayList<String> admins = new ArrayList<>();



    public static void main(String[] args) {
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("https.proxyHost","");
        System.setProperty("https.proxyPort","");

        setupConfig();
        token = getToken();
        getMembers();
        getAdmins();

        setupJDA();
        setupSendMessage();
        setupLogger();
        setupRacismDetection();
        setupYoutubeIntergration();
        setupTwitchIntergration();
        addListeners();
        logger.createLog("Starting up...");
    }

    private static void setupYoutubeIntergration() {
         link = new PlayLink(logger);
    }

    private static void setupTwitchIntergration() {
        channelChecker = new ChannelChecker(logger, getClientID());
    }

    private static void setupConfig(){
        try{
            FileReader configFR = new FileReader(configFile);
            configBR = new BufferedReader(configFR);
        }catch (IOException e){
            logger.createErrorLog("error setting up config file");
        }
    }

    private static String getNextConfigLine(){
        try {
            return configBR.readLine();
        }catch (IOException e){
            return "";
        }
    }

    private static String getToken(){
        return getNextConfigLine();
    }

    private static String getClientID() { return getNextConfigLine(); }

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

    private static void setupRacismDetection(){rm = new RacismDetection(logger);}

    private static void addListeners(){
        try {
            onGuildVoiceEvents = new OnGuildVoiceEvents(logger);
            jda.addEventListener(new OnUsernameUpdate(logger, sm, members, rm));
            jda.addEventListener(new OnMessageRecieved(logger, members, admins, link, onGuildVoiceEvents, channelChecker, rm));
            jda.addEventListener(new OnMessageUpdate(logger));
            jda.addEventListener(new GuildMemberLeave(logger));
            jda.addEventListener(new GuildMemberJoin(logger));
            jda.addEventListener(onGuildVoiceEvents);
            channelListener = new ChannelListener(channelChecker, jda.getTextChannelById(getToken()), logger);
        }catch (Exception e){
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTraceString = sw.toString();
            logger.createErrorLog("unknown exception " + e.getMessage() + ", " + stackTraceString);
        }
    }
}
