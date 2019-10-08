package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

public class Command {
    private final String symbol = ",";
    private String keyword;
    private Logger logger;
    private String description;
    private Boolean adminProtected;

    Command(Logger logger, String keyword, String description, Boolean adminProtected){
        this.keyword = symbol + keyword;
        this.logger = logger;
        this.description = keyword + description;
        this.adminProtected = adminProtected;
    }

    public void function(Message message){

    }

    public String getKeyword() {
        return keyword;
    }

    public Logger getLogger() { return logger; }

    public String getDescription(){ return description; }

    public Boolean getAdminProtected() { return adminProtected; }
}
