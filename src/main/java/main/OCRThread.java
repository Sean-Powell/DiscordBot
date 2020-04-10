package main;

import logging.Logger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.text.Normalizer;

public class OCRThread implements Runnable {
    private File file;
    private Message message;
    private Logger logger;

    public OCRThread(File file, Message message, Logger logger){
        this.file = file;
        this.message = message;
        this.logger = logger;
    }

    public void run(){
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        try{
            Thread.sleep(500);
            long start = System.currentTimeMillis();
            String result = tesseract.doOCR(file);
            logger.createLog("Found " + result + " in image");
            if(containsWord(result)){
                RestAction action = message.delete();
                message.getChannel().sendMessage("Hey <@" + message.getAuthor().getId() + "> you can't say that").queue();
                action.complete();
            }
            long end = System.currentTimeMillis();
            logger.createLog("Finished OCR took " + (end - start) + " milliseconds");
        }catch(TesseractException te){
            logger.createErrorLog(te.getMessage());
            te.printStackTrace();
        }catch(InterruptedException ie) {
            logger.createErrorLog("Could not pause thread");
        } finally
        {
            if(file.delete()){
                logger.createLog("Temp File Deleted");
            }else{
                logger.createErrorLog("Could not delete file");
            }

            tesseract = null;
        }
    }

    private boolean containsWord(String message) { //todo improve the detection method
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = message.replaceAll("[^A-za-z1]", "").toLowerCase();
        return message.contains("nigg") || message.contains("nlgg") || message.contains("n1gg") ||
                message.contains("n|gg") || message.contains("n/gger") || message.contains("n\\gger");
    }
}
