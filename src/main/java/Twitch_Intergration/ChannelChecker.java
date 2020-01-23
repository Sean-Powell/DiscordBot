package Twitch_Intergration;

import logging.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.*;

public class ChannelChecker {
    private final String clientID;
    private HttpURLConnection connection = null;
    private Logger logger;

    public ChannelChecker(Logger logger, String clientID) {
        this.clientID = clientID;
        this.logger = logger;
    }

    public String GetUserID(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/users?login=" + username);
            String response = openGETConnection(url);
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            List<Object> data = dataArray.toList();
            Object obj = data.get(0);
            HashMap<String, String> map = (HashMap<String, String>) obj;
            return map.get("id");
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

    public ArrayList<String> CheckChannelStatus(String channel_id) { //todo change to a single request format with all channels linked together
        ArrayList<String> info = null;
        try {
            URL url = new URL(("https://api.twitch.tv/helix/streams?user_id=" + channel_id));
            String response = openGETConnection(url);
            System.out.println(response);

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            List<Object> data = jsonArray.toList();
            Object obj = data.get(0);
            HashMap<String, String> map = (HashMap<String, String>) obj;

            info = new ArrayList<>();
            info.add(map.get("title"));
            info.add(map.get("user_name"));
            String time = map.get("started_at");
            info.add("" + parseTime(time));
        } catch (IndexOutOfBoundsException ignored) {
            //just means they are not streaming
            return null;
        } catch (Exception e) {
            logger.createErrorLog(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return info;
    }

    public ArrayList<ArrayList<String>> CheckChannels(ArrayList<TwitchWatchListenerObject> channelIDs){
        ArrayList<ArrayList<String>> channelInfoList = new ArrayList<>();
        StringBuilder ids = new StringBuilder("https://api.twitch.tv/kraken/streams/?channel=" + channelIDs.get(0).getId());
        for(int i = 1; i < channelIDs.size() && i < 100; i++){
            String s = channelIDs.get(i).getId();
            ids.append(",").append(s);
        }
        try{
            URL url = new URL((ids.toString()));
            String response = openGETConnection(url);
            JSONObject jsonObject = new JSONObject(response);
            int numberOfStreams = jsonObject.getInt("_total");
            System.out.println(numberOfStreams);
            JSONArray dataArray = jsonObject.getJSONArray("streams");
            for(int i = 0; i < numberOfStreams; i++){
                JSONObject subObject = dataArray.getJSONObject(i);
                String time = subObject.getString("created_at");
                String stream_id = "" + subObject.getInt("_id");
                JSONObject channelArray = subObject.getJSONObject("channel");
                String status = channelArray.getString("status");
                String display_name = channelArray.getString("display_name");
                String url_data = channelArray.getString("url");

                ArrayList<String> info = new ArrayList<>();
                info.add(status);
                info.add(display_name);
                info.add("" + parseTime(time));
                info.add(url_data);
                info.add(stream_id);
                channelInfoList.add(info);
            }
            return channelInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String openGETConnection(URL url) throws Exception {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
        connection.addRequestProperty("Client-ID", clientID);

        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
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

    //2020-01-22T21:14:21Z
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
