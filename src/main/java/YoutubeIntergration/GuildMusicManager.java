package YoutubeIntergration;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
    final AudioPlayer player;
    final TrackScheduler scheduler;

    GuildMusicManager(AudioPlayerManager manager){
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    AudioPlayerSendHandler getSendHandler(){
        return new AudioPlayerSendHandler(player);
    }
}
