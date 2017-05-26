package turn.zio.zara.travel_log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



    }
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            tv.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    public void Log_Write(View view){

    }
    class arData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try{


                String link="http://211.211.213.218:8084/android/ardata"; //92.168.25.25
                HttpClient.Builder http = new HttpClient.Builder("GET", link);

                // HTTP 요청 전송
                HttpClient post = http.create();
                post.request();
                // 응답 상태코드 가져오기
                int statusCode = post.getHttpStatusCode();
                // 응답 본문 가져오기
                String body = post.getBody();
                return body;

                // Read Server Response

            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("result", s);

        }
    }

    public  void googleMap(View view){
        Intent intent = new Intent(this, Map_TestActivity.class);
        startActivity(intent);
    }

    public void Log_out(View view){
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.clear();
        editor.commit();

        finish();
    }
    public void preview(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void travle_Camera(View view){
        Intent intent = new Intent(getApplicationContext(), TravelCameraActivity.class);
        intent.putExtra("action", "1");
        startActivity(intent);
    }

    public void logmove(View view){
        Intent intent = new Intent(this, Life_LogActivity.class);
        startActivity(intent);
    }
}
