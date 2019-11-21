package SpeachRecognition;

import com.sedmelluq.discord.lavaplayer.tools.io.ByteBufferInputStream;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class SpeachRecognition {

    void StartRecognition(byte[] data){
        try {
            Configuration config = new Configuration();

            config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
            config.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            config.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");


            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(config);
            InputStream stream = new ByteArrayInputStream(data);
            SpeechResult  result;
            recognizer.startRecognition(stream);
            StringBuilder output = new StringBuilder();
            while((result = recognizer.getResult()) != null){
                List<WordResult> words =result.getWords();
                for(WordResult word: words){
                    output.append(word.getWord().getSpelling());
                }
            }
            recognizer.stopRecognition();
            System.out.println(output);
        }catch (IOException e){
            //todo handle exception
        }

    }
}
