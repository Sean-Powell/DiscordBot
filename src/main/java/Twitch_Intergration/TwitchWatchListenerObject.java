package Twitch_Intergration;

public class TwitchWatchListenerObject {
    private String id;
    private String msg;

    public TwitchWatchListenerObject(String id, String msg){
        this.id = id;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }
}
