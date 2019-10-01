package FileManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileFunctions {
    public static boolean checkIfFileContains(File file, String phrase) throws IOException{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(phrase)) {
                    br.close();
                    fr.close();
                    return true;
                }
            }
            br.close();
            fr.close();

        return false;
    }
}
