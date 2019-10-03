package Commands;

import Logging.Logger;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;

public class Help extends Command{
    private ArrayList<Command> commands;
    public Help(Logger logger, String keyword,  ArrayList<Command> commands, String description, Boolean adminProtected){
        super(logger, keyword, description, adminProtected);
        this.commands = commands;
    }

    @Override
    public void function(Message message) {
        StringBuilder helpMessage = new StringBuilder();
        for(Command command: commands){
            helpMessage.append(command.getDescription()).append("\n");
        }
        message.getTextChannel().sendMessage(helpMessage.toString()).queue();
    }
}
