package YoutubeIntergration;

import Logging.Logger;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayLink {

    private File file = new File("TextFiles/Commands/volume.txt");

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private Logger logger;
    private int volume = 50;

    public PlayLink(Logger logger){
        this.logger = logger;
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();

        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            this.volume = Integer.parseInt(line);
            br.close();
            fr.close();
        }catch (IOException e){
            logger.createErrorLog("setting the volume at " + e.getMessage());
        }

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild){
        long guildID = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildID);

        if(musicManager == null){
            musicManager = new GuildMusicManager(playerManager);
            musicManager.player.setVolume(volume);
            musicManagers.put(guildID, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public void loadAndPlay(final TextChannel channel, final User author, final String trackURL){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                channel.sendMessage("Added to queue " + audioTrack.getInfo().title).queue();
                play(channel.getGuild(), musicManager, audioTrack, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if(firstTrack == null){
                    firstTrack = audioPlaylist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (First track in playlist " + audioPlaylist.getName() + ")").queue();
                play(channel.getGuild(), musicManager, firstTrack, author);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackURL).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                channel.sendMessage("Could not play the track due to an error").queue();
                logger.createErrorLog("loading the track " + e.getMessage());
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, User author){
        connectToAuthorsVoiceChannel(guild.getAudioManager(), author, guild);
        musicManager.scheduler.queue(track);
    }

    public void skip(TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();
        channel.sendMessage("Skipped to next track.").queue();
    }

    public void clear(TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        int queueSize = musicManager.scheduler.numberInQueue();
        if(musicManager.scheduler.isPlaying()){
            queueSize++;
        }
        for(int i = 0; i < queueSize; i++) {
            musicManager.scheduler.nextTrack();
        }
        channel.sendMessage("Queue cleared").queue();
    }

    public void volume(TextChannel channel, int volume){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.player.setVolume(volume);
    }

    private boolean checkIfInChannel(User author, VoiceChannel channel){
        if(channel == null){
            return false;
        }

        List<Member> members = channel.getMembers();
        for(Member member: members){
            if(member.getUser().getId().equals(author.getId())){
                return true;
            }
        }
        return false;
    }

    private void connectToAuthorsVoiceChannel(AudioManager audioManager, User author, Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);

        System.out.println(musicManager.scheduler.numberInQueue());
        if(!audioManager.isConnected() && !audioManager.isAttemptingToConnect() || (!musicManager.scheduler.isPlaying() && !checkIfInChannel(author, audioManager.getConnectedChannel()))){
            for(VoiceChannel channel: audioManager.getGuild().getVoiceChannels()){
                if(checkIfInChannel(author, channel)){
//                    audioManager.setReceivingHandler(new AudioRecievingManager());
                    audioManager.openAudioConnection(channel);
                }
            }
        }
    }
}
