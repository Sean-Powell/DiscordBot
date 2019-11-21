package SpeachRecognition;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
public class AudioRecievingManager implements AudioReceiveHandler {
    private SpeachRecognition speachRecognition = new SpeachRecognition();
    private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();

    @Override
    public boolean canReceiveCombined(){
        return true;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio){ //todo figure out a way to increase the length of the audio clip passes to the speach recognition
                                                                  // pos solution queue of byte arrays that are then combined back into one lone byte array
        if(combinedAudio.getUsers().isEmpty())
            return;

        byte[] data = combinedAudio.getAudioData(1.0f);
        queue.add(data);

        if(queue.size() > 100){
            speachRecognition.StartRecognition(combineQueue());
        }
    }

    private byte[] combineQueue(){
        byte[] combined = queue.poll();
        byte[] toAdd;
        while((toAdd = queue.poll()) != null){
            byte[] joinedArray = Arrays.copyOf(combined, combined.length + toAdd.length);
            System.arraycopy(toAdd, 0, joinedArray, combined.length, toAdd.length);
            combined = joinedArray;
        }

        return combined;
    }
}
