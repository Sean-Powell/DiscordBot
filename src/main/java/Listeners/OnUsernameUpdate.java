package Listeners;

import FileManagement.FileFunctions;
import Logging.Logger;
import main.SendMessage;
import main.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.io.*;
import java.util.ArrayList;

public class OnUsernameUpdate extends ListenerAdapter {
    private String directory = "TextFiles/OnUserNameUpdate/";
    private File targetsFile = new File(directory + "usernameChangesTargets.txt");

    private ArrayList<Member> members;
    private ArrayList<String> targets = new ArrayList<>();
    private Logger logger;
    private SendMessage sm;


    public OnUsernameUpdate(Logger logger, SendMessage sendMessage, ArrayList<Member> members) {
        this.logger = logger;
        this.sm = sendMessage;
        this.members = members;
        setupTargets();
    }

    private void setupTargets(){
        try{
            FileReader fr = new FileReader(targetsFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                targets.add(line);
            }
        }catch(IOException e){
            logger.createErrorLog("reading targets file in Listeners.OnUsernameUpdate " + e.getMessage());
        }
    }


    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event){
        String newName = event.getNewNickname();
        String oldName = event.getOldNickname();
        User user = event.getUser();
        String id = user.getId();
        String message;

        if(isTarget(id)){
            if(checkNames(id, newName)){
                Member gary = getMember("gary");
                message = "<@" + id + "> ";
                if(gary != null){
                    message += "<@" +  gary.getId() + "> " ;
                }
                message += "that name has been used before.";
                logger.createLog("kicking user");
                AuditableRestAction result = event.getGuild().kick(id);
                result.submit();
            }else{
                addName(id, newName);
                logger.createLog("adding new name " + newName);
                message =  "" + user.getName() +  "'s new name is " + newName;
            }
            logger.createLog("sending message " + message);
            sm.sendMsg(message);
        }else{
            logger.createLog("None target " + user.getName() + " changed name from " + oldName + " to " + newName);
        }
    }

    private boolean checkNames(String id, String newName){
        File targetFile = new File(directory + id + ".txt");
        try {
            if(FileFunctions.checkIfFileContains(targetFile, newName)){
                return true;
            }
        }catch (IOException e){
            logger.createErrorLog("reading past names " + e.getMessage());
        }
        return false;
    }

    private void addName(String id, String name){
        File targetFile = new File(directory + id + ".txt");
        try{
            FileWriter fw = new FileWriter(targetFile, true);
            fw.write(name + "\n");
            fw.flush();
            fw.close();
        }catch (IOException e){
            logger.createErrorLog("writing new name to name file " + e.getMessage());
        }
    }

    private boolean isTarget(String id){
        for(String target: targets){
            if(target.equals(id)){
                return true;
            }
        }
        return false;
    }

    private Member getMember(String name){
        for(Member member: members){
            if(member.getName().equals(name)){
                return member;
            }
        }
        return null;
    }
}
