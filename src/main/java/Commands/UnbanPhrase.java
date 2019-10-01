package Commands;

import Logging.Logger;
import net.dv8tion.jda.core.entities.Message;

import java.io.*;
import java.util.ArrayList;

public class UnbanPhrase extends Command {

    private File bannedPhrases = new File("Commands/bannedPhrases.txt");

    public UnbanPhrase(Logger logger, String keyword, String description) {
        super(logger, keyword, description);
    }

    public void function(Message message) {
        String rawMessage = message.getContentRaw();
        String unban;

        try {
            String[] split = rawMessage.split(getKeyword());
            unban = split[1].substring(1);
        }catch (Exception e){
            String toSend = "invalid parameters on command try ,help for help";
            message.getTextChannel().sendMessage(toSend).queue();
            return;
        }

        ArrayList<String> keptPhrases = new ArrayList<>();
        try{
            FileReader fr = new FileReader(bannedPhrases);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(!line.equals(unban)){
                    keptPhrases.add(line);
                }
            }
            br.close();
            fr.close();

            FileWriter fw = new FileWriter(bannedPhrases, false);
            for(String phrase: keptPhrases){
                fw.write(phrase + "\n");
            }
            fw.close();
            getLogger().createLog("removed " + unban + " from the ban list");
        }catch (IOException e){
            getLogger().createErrorLog("in removing a phrase " + unban + " from the ban list " + e.getMessage());
        }
    }
}
