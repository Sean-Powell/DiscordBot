package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.util.ArrayList;

public class UnbanFromNameChanges extends Command{
    private File file = new File("TextFiles/OnUserNameUpdate/usernameChangesTargets.txt");
    public UnbanFromNameChanges(Logger logger, String keyword, String description, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
    }

    @Override
    public void function(Message message) {
        String rawMessage = message.getContentRaw();
        try{
            String[] split = rawMessage.split(" ");
            String id = split[1].substring(3, split[1].length() - 1);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            ArrayList<String> kept = new ArrayList<>();
            while ((line = br.readLine()) != null){
                if(!line.equals(id)){
                    kept.add(line);
                }
            }

            br.close();
            fr.close();

            FileWriter fw = new FileWriter(file, false);
            for(String userID: kept){
                fw.write(userID + "\n");
            }
            fw.close();
            getLogger().createLog("removed user from the name ban list");
        }catch (IOException e){
            getLogger().createErrorLog("removing a user from the name ban list caused an error " + e.getMessage());
        }
    }
}
