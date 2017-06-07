package turn.zio.zara.travel_log;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView user_place;
    TextView user_main_id;
    LocationManager lm;

    private ListViewDialog mDialog;

    SharedPreferences login;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_place = (TextView) findViewById(R.id.user_place_info);
        user_main_id = (TextView) findViewById(R.id.main_user_id);

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();


        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        String user_id = user.getString("user_id", "0");

        user_main_id.setText(user_id);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            user_place.setText(getAddress(latitude, longitude));
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

    public String getAddress(double lat, double lng){
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;
        try{
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch(Exception e){
            e.printStackTrace();
        }
        if(list == null){
            Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }
        if(list.size() > 0){
            Address addr = list.get(0);
            address = addr.getAdminArea() + " "
                    + addr.getLocality() + " "
                    + addr.getThoroughfare() + " ";
        }
        return address;
    }
    public void Log_Write(View view){

    }
    public void addFile(View v){
        switch(v.getId()){
            case R.id.addFile:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }
    private void showListDialog(){

        String[] item = getResources().getStringArray(R.array.list_dialog_list_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String> (listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener(){


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub

                if (position == 0){
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intent);
                } else if (position == 1){
                    Intent intent = new Intent(getApplicationContext(), TravelCameraActivity.class);
                    startActivity(intent);
                } else if(position == 2){
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    class unknown extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try{



                return "s";

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


}
