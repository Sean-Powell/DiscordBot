package Twitch_Intergration;

public class TwitchWatchListenerObject {
    private String id;
    private String msg;

    TwitchWatchListenerObject(String id, String msg){
        this.id = id;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    String getMsg() {
        return msg;
    }
}
