package Twitch_Intergration;

import logging.Logger;

import java.io.*;
import java.net.URL;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.*;

import javax.net.ssl.HttpsURLConnection;

public class ChannelChecker {
    private final String clientID;
    private  HttpsURLConnection connection = null;
    private Logger logger;

    public ChannelChecker(Logger logger, String clientID) {
        this.clientID = clientID;
        this.logger = logger;
    }

    public String GetUserID(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/users?login=" + username);
            String response = openGETConnection(url);
            //String response = openConnection(url);
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            return "" + dataArray.getJSONObject(0).getInt("id");
        } catch (Exception e) {
            //logger.createErrorLog(e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    ArrayList<ArrayList<String>> CheckChannels(ArrayList<TwitchWatchListenerObject> channelIDs){
        ArrayList<ArrayList<String>> channelInfoList = new ArrayList<>();
        StringBuilder ids = new StringBuilder("https://api.twitch.tv/kraken/streams/?channel=" + channelIDs.get(0).getId());
        for(int i = 1; i < channelIDs.size() && i < 100; i++){
            String s = channelIDs.get(i).getId();
            s = s.replaceAll("//r|//n", "");
            ids.append(",").append(s);
        }
        try{
            URL url = new URL((ids.toString()));
            String response = openGETConnection(url);
            //String response = openConnection(url);
           // String response = runCommand(url);
            JSONObject jsonObject = new JSONObject(response);
            int numberOfStreams = jsonObject.getInt("_total");
            JSONArray dataArray = jsonObject.getJSONArray("streams");
            for(int i = 0; i < numberOfStreams; i++){
                JSONObject subObject = dataArray.getJSONObject(i);
                String time = subObject.getString("created_at");
                String stream_id = "" + subObject.getInt("_id");
                JSONObject channelArray = subObject.getJSONObject("channel");
                String status = channelArray.getString("status");
                String display_name = channelArray.getString("display_name");
                String url_data = channelArray.getString("url");
                String channel_id = "" + channelArray.getInt("_id");

                ArrayList<String> info = new ArrayList<>();
                info.add(status);
                info.add(display_name);
                info.add("" + parseTime(time));
                info.add(url_data);
                info.add(stream_id);
                info.add(channel_id);
                channelInfoList.add(info);
            }
            return channelInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String openGETConnection(URL url) throws Exception {
        connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
        connection.addRequestProperty("Client-ID", clientID);
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux armv7l) AppleWebKit/537.36 (KHTML, like Gecko) Raspbian Chromium/65.0.3325.181 Chrome/65.0.3325.181 Safari/537.36");


        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(true);

        InputStream inputStream = connection.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = br.readLine()) != null) {
            response.append(line);
            response.append("\r");
        }
        br.close();
        return response.toString();
    }

    private long parseTime(String time){
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(time, timeFormatter);
            Date date = Date.from(Instant.from(offsetDateTime));
            return date.getTime();
        }catch (Exception e){
            logger.createErrorLog(e.getMessage());
            return 0;
        }
    }
}
