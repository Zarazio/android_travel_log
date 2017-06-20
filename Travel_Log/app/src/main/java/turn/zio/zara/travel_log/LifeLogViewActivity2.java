package turn.zio.zara.travel_log;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class LifeLogViewActivity2 extends AppCompatActivity  implements OnMapReadyCallback {

    private TextView log_title;
    private TextView log_Content;
    private TextView log_Place;
    private TextView log_date;
    private TextView profile_user_id;

    private LinearLayout picutre_Linear;
    private LinearLayout text;
    private LinearLayout goomap;

    private ImageView image;
    private ImageView bakcMain_icon;
    private Drawable drawable;

    String step_log_code;
    String file_Content;
    MediaPlayer player;
    ArrayList<String> location = new ArrayList<String>();

    String imageURL = "http://211.211.213.218:8084/android/resources/upload/";

    LinearLayout MapContainer;
    MapFragment mMapFragment;
    private LinearLayout mLayout;
    private GoogleMap mMap;
    private int board_code;
    public static boolean oneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_life_log_view);

        log_title = (TextView) findViewById(R.id.log_title) ;
        log_Content = (TextView) findViewById(R.id.log_cotennt) ;
        log_Place = (TextView) findViewById(R.id.log_place) ;
        log_date = (TextView) findViewById(R.id.log_date) ;
        profile_user_id = (TextView) findViewById(R.id.user_id) ;

        image = (ImageView) findViewById(R.id.log_picture);
        bakcMain_icon = (ImageView) findViewById(R.id.bakcMain_icon);
        picutre_Linear= (LinearLayout) findViewById(R.id.log_picture_Linear);
        text = (LinearLayout) findViewById(R.id.text);
        goomap = (LinearLayout) findViewById(R.id.MapContainer);

        Intent intent = getIntent();
        board_code = Integer.parseInt(intent.getExtras().getString("board_Code"));
        String boder_Title = intent.getExtras().getString("board_Title");
        String board_Content = intent.getExtras().getString("board_Content");
        String user_id = intent.getExtras().getString("user_id");
        String String_Date = intent.getExtras().getString("board_Date");
        String file_Type = intent.getExtras().getString("file_Type");
        file_Content = intent.getExtras().getString("file_Content");
        Double log_longtitude= null;
        Double log_latitude = null;
        if(!file_Type.equals("3")) {
            log_longtitude = Double.parseDouble(intent.getExtras().getString("log_longtitude"));
            log_latitude = Double.parseDouble(intent.getExtras().getString("log_latitude"));
        }
        if(file_Type.equals("3")){
            step_log_code = intent.getExtras().getString("step_log_code");
            Log.d("dd",step_log_code);
        }
        String address = "0";
        if(file_Type.equals("1")) {
            Log.d("이미지","이미지");
            picutre_Linear.setVisibility(View.VISIBLE);
            address = getAddress(log_latitude, log_longtitude);
            serpic webserver = new serpic();
            webserver.execute();
        }else if(file_Type.equals("2")){
                Log.d("뷰",file_Content);
                picutre_Linear.setVisibility(View.VISIBLE);
                address = getAddress(log_latitude, log_longtitude);
                final String url = "http://211.211.213.218:8084/android/resources/upload/" + file_Content;
                drawable = getResources().getDrawable(R.drawable.voice);
                image.setImageDrawable(drawable);
                image.setOnClickListener(new View.OnClickListener(){
                    public  void onClick(View v){
                        if(v.getId() == R.id.log_picture){
                            try {
                                player = new MediaPlayer();
                                player.setDataSource(url);
                                player.prepare();
                                player.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        }else if(file_Type.equals("3")){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
           mMapFragment = MapFragment.newInstance();
            fragmentTransaction.add(R.id.MapContainer, mMapFragment);
            fragmentTransaction.commit();
            mMapFragment.getMapAsync(this);
        }

        log_title.setText(boder_Title);
        if(!file_Type.equals("3")) {
            log_Content.setText(board_Content);
            log_Place.setText(address);
        }else{
            goomap.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
        }
        profile_user_id.setText(user_id);
        log_date.setText(String_Date);
    }

    /** 위도와 경도 기반으로 주소를 리턴하는 메서드*/
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

    public void backAR(View view){
        CameraOverlayView.DBselect = true;
        popListView.touch= true;
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
        finish();
    }
    @Override
    public void onBackPressed(){
        CameraOverlayView.DBselect = true;
        popListView.touch= true;
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
        finish();
    }
    class serpic extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog loading;
        Bitmap resizedBitmap;
        @Override
        protected Bitmap doInBackground(String... params) {
            try{

                        String url = imageURL + file_Content;
                        Log.d("url",url);
                        InputStream is = (InputStream) new URL(url).getContent();

                        Bitmap bmImg = BitmapFactory.decodeStream(is);
                        int width = bmImg.getWidth();
                        int height = bmImg.getHeight();
                        //화면에 표시할 데이터
                        Matrix matrix = new Matrix();
                        resizedBitmap = Bitmap.createBitmap(bmImg, 0, 0, width, height, matrix, true);


                return resizedBitmap;

                // Read Server Response

            }
            catch(Exception e){
                resizedBitmap = null;
                return resizedBitmap;
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(LifeLogViewActivity2.this);
            loading.setProgressStyle(R.style.MyDialog);
            loading.show();
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            this.cancel(true);
            image.setImageBitmap(resizedBitmap);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            loading.dismiss();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        selFile();
    }

    private void selFile(){

        class loginData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            private InputStream is =null;
            KmlLayer layer  = null;
            private String[][] parsedata;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("result",s);
                JSONArray json = null;
                try {
                    json = new JSONArray(s);
                    parsedata = new String[json.length()][10];
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jobject = json.getJSONObject(i);

                        parsedata[i][0] = jobject.getString("board_Code");
                        parsedata[i][1] = jobject.getString("board_Title");
                        parsedata[i][2] = jobject.getString("board_Content");
                        parsedata[i][3] = jobject.getString("log_longtitude");
                        parsedata[i][4] = jobject.getString("log_latitude");
                        parsedata[i][5] = jobject.getString("board_Date");
                        parsedata[i][6] = jobject.getString("user_id");
                        if(json.getJSONObject(i).isNull("file_Content") == false){
                            parsedata[i][7] = jobject.getString("file_Type");
                            parsedata[i][8] = jobject.getString("file_Content");
                        }else{
                            parsedata[i][7] = "0";
                            parsedata[i][8] = "1";
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(mMap != null) {


                    String[] coo = location.get(((location.size()-1)/2)).toString().split(",");
                    Log.d("size", location.get(((location.size()-1)/2)).toString().trim());
                    LatLng startPoint = new LatLng(Double.parseDouble(coo[1]), Double.parseDouble(coo[0]));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));

                    PolylineOptions option = new PolylineOptions();
                    option.width(4);
                    option.color(Color.BLACK);
                    for(int i=0; i< location.size(); i++) {
                        String[] coos = location.get(i).toString().split(",");
                        Log.d("draw",location.get(i).toString());
                        LatLng point = new LatLng(Double.parseDouble(coos[1]), Double.parseDouble(coos[0]));
                        option.add(point);
                    }
                    mMap.addPolyline(option);

                    MarkerOptions markerOption = new MarkerOptions();
                    for(int i =0; i< parsedata.length; i++){
                        markerOption.position(new LatLng(Double.parseDouble(parsedata[i][4]),Double.parseDouble(parsedata[i][3])));
                        markerOption.title(parsedata[i][1]);
                        if(parsedata[i][7].equals("1")){
                            markerOption.snippet("사진");
                        }else if(parsedata.equals("2")){
                            markerOption.snippet("음성");
                        }else {
                            markerOption.snippet("글");
                        }
                        mMap.addMarker(markerOption);

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                for(int i = 0; i< parsedata.length; i++){

                                    if(marker.getPosition().latitude == Double.parseDouble(parsedata[i][4])
                                            && marker.getPosition().longitude == Double.parseDouble(parsedata[i][3])
                                            && oneView){
                                        oneView = false;
                                        Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity.class);
                                        intent.putExtra("board_Code",parsedata[i][0]);
                                        intent.putExtra("board_Title",parsedata[i][1]);
                                        intent.putExtra("board_Content",parsedata[i][2]);
                                        intent.putExtra("log_longtitude",parsedata[i][3]);
                                        intent.putExtra("log_latitude",parsedata[i][4]);
                                        intent.putExtra("user_id",parsedata[i][6]);
                                        intent.putExtra("board_Date",parsedata[i][5]);
                                        startActivity(intent);
                                    }
                                }


                                return false;
                            }
                        });
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    Map<String, String> loginParam = new HashMap<String,String>() ;

                    loginParam.put("step_log_code",board_code+"");


                    String link="http://211.211.213.218:8084/android/step_log_select"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(loginParam);

                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();


                    String urltext = imageURL + "step_Log/" + file_Content;
                    Log.d("url", urltext);
                    URL url = new URL(urltext);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    is = urlConnection.getInputStream();

                    InputStreamReader inputReader = new InputStreamReader(is);

                    String column = null;
                    BufferedReader br = new BufferedReader(inputReader);
                    boolean flag = false;
                        while ((column = br.readLine()) != null) {
                            int coordin = column.indexOf("<coordinates>");

                            if (coordin != -1 || flag) {
                                int i= 0;
                                flag = true;
                                String tmpCoordin = column;
                                tmpCoordin = tmpCoordin.replaceAll("<coordinates>", "");
                                tmpCoordin = tmpCoordin.replaceAll("</coordinates>", "");
                                Log.d("tomCoordib",tmpCoordin.trim());
                                if(tmpCoordin.trim().equals("</LineString>")){
                                    break;
                                }
                                location.add(tmpCoordin);
                            }

                        }

                    return body;
                }  catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "failed";
            }
        }

        loginData task = new loginData();
        task.execute();
    }
}
