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
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    ToggleButton tb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

       /* tv = (TextView) findViewById(R.id.mylocation);
        tb = (ToggleButton) findViewById(R.id.palceButton);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(tb.isChecked()){
                        tv.setText("수신중..");
                        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                    }else{
                        tv.setText("위치정보 미수신중");
                        lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
                    }
                }catch(SecurityException ex){
                }
            }
        });*/

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
       /* arData task = new arData();
        String s = null;


        try {
            s = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String[][] parsedata = new String[0][5];

        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][5];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);
                parsedata[i][0] = jobject.getString("test_code");
                parsedata[i][1] = jobject.getString("title");
                parsedata[i][2] = jobject.getString("contnet");
                parsedata[i][3] = jobject.getString("longtitude");
                parsedata[i][4] = jobject.getString("latitude");

                Log.v("json",s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
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
        Intent intent = new Intent(this, AlbumSelectActivity.class);
        startActivity(intent);
    }

}
