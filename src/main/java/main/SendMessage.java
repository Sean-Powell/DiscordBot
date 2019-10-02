package main;

import net.dv8tion.jda.api.entities.TextChannel;
import org.w3c.dom.Text;

public class SendMessage {
    private TextChannel channel;
    SendMessage(TextChannel channel){
        this.channel = channel;
    }

    public void sendMsg(String message){
        channel.sendMessage(message).queue();
    }
}
