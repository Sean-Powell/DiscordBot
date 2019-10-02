package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

public class Command {
    private static String symbol = ",";
    private String keyword;
    private Logger logger;
    private String description = "";

    Command(Logger logger, String keyword, String description){
        this.keyword = symbol + keyword;
        this.logger = logger;
        this.description = description;
    }

    public void function(Message message){

    }

    public String getKeyword() {
        return keyword;
    }

    public Logger getLogger() { return logger; }

    public String getDescription(){ return description; }
}
