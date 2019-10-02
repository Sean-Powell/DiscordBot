package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;

public class Help extends Command{
    private ArrayList<Command> commands;
    public Help(Logger logger, String keyword,  ArrayList<Command> commands, String description){
        super(logger, keyword, description);
        this.commands = commands;
    }

    @Override
    public void function(Message message) {
        for(Command command: commands){
            message.getTextChannel().sendMessage(command.getDescription()).queue();
        }
    }
}
