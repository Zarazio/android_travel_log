package turn.zio.zara.travel_log;

/**
 * Created by 하루마다 on 2017-07-12.
 */

public class DataBaseUrl {
    /*
    * 서버 http://211.202.32.52:8084
    * 노트북 http://172.20.1.219:8087 192.168.43.109
    */
    private String tumnailUrl = "http://192.168.43.109:8087/turn/resources/upload/logs/s_";
    private String serverUrl = "http://192.168.43.109:8087/android/";
    private String dataUrl = "http://192.168.43.109:8087/turn/resources/upload/logs/";
    private String stepUrl = "http://192.168.43.109:8087/turn/resources/upload/step_Log/";

    public String getTumnailUrl() {
        return tumnailUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getStepUrl() {

        return stepUrl;
    }
}
