package turn.zio.zara.travel_log;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity   {

    TextView user_place;
    TextView user_main_id;
    LocationManager lm;

    private ListViewDialog mDialog;

    SharedPreferences login;
    SharedPreferences.Editor editor;

    LinearLayout mainPage;
    LinearLayout searchPage;
    LinearLayout likeFollowPage;
    LinearLayout myPage;
    LinearLayout serch_view;

    TextView search_Text_view;
    EditText search_Text;

    private BackPressCloseHandler backPressCloseHandler;
    private String hashTagText;

    private boolean[] menu;
    Bitmap[] images;

    String imageURL = "http://211.211.213.218:8084/android/resources/upload/";

    private String[][] parsedata;
    MyAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = new boolean[4];

        backPressCloseHandler = new BackPressCloseHandler(this);

        user_place = (TextView) findViewById(R.id.user_place_info);
        user_main_id = (TextView) findViewById(R.id.main_user_id);

        mainPage = (LinearLayout) findViewById(R.id.main_page);
        searchPage = (LinearLayout) findViewById(R.id.search_page);
        likeFollowPage = (LinearLayout) findViewById(R.id.like_follow);
        myPage = (LinearLayout) findViewById(R.id.my_page);
        serch_view = (LinearLayout) findViewById(R.id.serch_view);

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();

        search_Text = (EditText)findViewById(R.id.search_Text);
        search_Text_view = (TextView) findViewById(R.id.search_Text_view);

        GridView gv = (GridView) findViewById(R.id.list);

        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        String user_id = user.getString("user_id", "0");

        user_main_id.setText(user_id);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;// 가로
        int height = displayMetrics.heightPixels;

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*위치정보*/
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);

        /*검색시 해시태그 View 클릭시*/
        search_Text.setOnFocusChangeListener(new View.OnFocusChangeListener() { // 포커스를 얻으면
            @Override
            public void onFocusChange(View v, boolean hasFocus) { // 포커스가 한뷰에서 다른뷰로 바뀔때
                if(hasFocus == false)
                {
                    String hashhint = search_Text.getHint().toString();
                    String hashtest = search_Text.getText().toString();
                    serch_view.setVisibility(View.GONE);
                    search_Text_view.setVisibility(View.VISIBLE);
                    if(!hashtest.equals("")) {
                        search_Text_view.setText(hashtest);
                    }else{
                        search_Text_view.setText("검색");
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
        /*해시태그 검색버튼 클릭시 엔터버튼을 검색버튼으로*/
        search_Text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        hashTagText = search_Text.getText().toString();
                        try {
                            listHashAll taskSearch = new listHashAll();
                            String result = taskSearch.execute().get();
                            jsonParse(result);
                            serpic setimage = new serpic();
                            setimage.execute();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        search_Text.clearFocus();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "기본", Toast.LENGTH_LONG).show();
                        return false;
                }
                return true;
            }
        });

        /*list에 뿌려진 로그 클릭시*/
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity2.class);
                intent.putExtra("board_Code",parsedata[position][0]);
                intent.putExtra("board_Title",parsedata[position][1]);
                intent.putExtra("board_Content",parsedata[position][2]);
                intent.putExtra("log_longtitude",parsedata[position][3]);
                intent.putExtra("log_latitude",parsedata[position][4]);
                intent.putExtra("board_Date",parsedata[position][5]);
                intent.putExtra("user_id",parsedata[position][6]);
                intent.putExtra("file_Type",parsedata[position][7]);
                intent.putExtra("file_Content", parsedata[position][8]);

                startActivity(intent);
            }
        });


    }

    /*result JSon Parese*/
    public void jsonParse(String s){

        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][9];
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
    }

    public void Apeter(Bitmap[] s){
        String[] text = new String[parsedata.length];
        String[] file_type = new String[parsedata.length];
        for(int i=0; i < parsedata.length;i++){
            if(parsedata[i][7].equals("0")){
                text[i]=parsedata[i][1];
            }else{
                text[i]=parsedata[i][8];
            }
            file_type[i] = parsedata[i][7];
        }
        adapter = new MyAdapter (
                MainActivity.this,
                R.layout.pop_view_list,       // GridView 항목의 레이아웃 row.xml
                text, file_type);
        adapter.image(s);
        GridView gv = (GridView)findViewById(R.id.list);
        gv.setAdapter(adapter);

    }



    /*gridView 웹서버 이미지 뿌리기*/
    class serpic extends AsyncTask<String, Void, Bitmap[]> {
        ProgressDialog loading;
        @Override
        protected Bitmap[] doInBackground(String... params) {
            images = new Bitmap[parsedata.length];
            try{
                for(int i=0; i < parsedata.length; i++) {

                    if(parsedata[i][7].equals("1")){
                        String url = imageURL + parsedata[i][8];
                        InputStream is = (InputStream) new URL(url).getContent();

                        Bitmap bmImg = BitmapFactory.decodeStream(is);
                        int width = bmImg.getWidth();
                        int height = bmImg.getHeight();
                        //화면에 표시할 데이터
                        Matrix matrix = new Matrix();
                        Bitmap resizedBitmap = Bitmap.createBitmap(bmImg, 0, 0, width, height, matrix, true);
                        images[i] = resizedBitmap;
                    }
                }
                return images;

                // Read Server Response

            }
            catch(Exception e){
                images = null;
                return images;
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MainActivity.this);
            loading.setProgressStyle(R.style.MyDialog);
            loading.show();
        }

        @Override
        protected void onPostExecute(Bitmap[] s) {
            super.onPostExecute(s);
            Apeter(s);
            this.cancel(true);
            loading.dismiss();
        }
    }

    public void backView(View view){
        search_Text.clearFocus();
    }
    /*뷰버튼을 editView로*/
    public void modeWrite(View view){
        String hashtest = (String) search_Text_view.getText();
        search_Text_view.setVisibility(view.GONE);
        serch_view.setVisibility(view.VISIBLE);
        Log.d("?",hashtest);
        if(hashtest.equals("검색")){
            Log.d("?1",hashtest);
            search_Text.setHint("해시태그 검색");
        }else {
            search_Text.setText(hashtest);
        }
        search_Text.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }
    /*메뉴버튼을 눌러 뷰바꿀시*/
    public void viewPageChange(View v){
        switch (v.getId()){
            case R.id.view_home_icon:
                if(menu[0]==false) {
                    mainPage.setVisibility(v.VISIBLE);
                    searchPage.setVisibility(v.INVISIBLE);
                    likeFollowPage.setVisibility(v.INVISIBLE);
                    myPage.setVisibility(v.INVISIBLE);
                    menu[0] = true;
                    menu[1] = false;
                    menu[2] = false;
                    menu[3] = false;
                }
                break;
            case R.id.view_search_icon:
                if(menu[1]==false) {
                    searchPage.setVisibility(v.VISIBLE);
                    mainPage.setVisibility(v.INVISIBLE);
                    likeFollowPage.setVisibility(v.INVISIBLE);
                    myPage.setVisibility(v.INVISIBLE);
                    menu[1] = true;
                    menu[0] = false;
                    menu[2] = false;
                    menu[3] = false;
                }

                    try {
                        listAll task = new listAll();
                        String result = task.execute().get();
                        jsonParse(result);
                        serpic setimage = new serpic();
                        setimage.execute();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                break;
            case R.id.view_heart_icon:
                if(menu[2]==false) {
                    likeFollowPage.setVisibility(v.VISIBLE);
                    searchPage.setVisibility(v.INVISIBLE);
                    mainPage.setVisibility(v.INVISIBLE);
                    myPage.setVisibility(v.INVISIBLE);
                    menu[2] =true;
                    menu[1] = false;
                    menu[0] = false;
                    menu[3] = false;
                }
                break;
            case R.id.view_mypage_icon:
                if(menu[3]==false) {
                    myPage.setVisibility(v.VISIBLE);
                    likeFollowPage.setVisibility(v.INVISIBLE);
                    searchPage.setVisibility(v.INVISIBLE);
                    mainPage.setVisibility(v.INVISIBLE);
                    menu[3]=true;
                    menu[1] = false;
                    menu[2] = false;
                    menu[0] = false;
                }
                break;
        }
    }
    /*검색 클릭시 db시*/
    class listAll extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        @Override
        protected String doInBackground(String... params) {
            try{

                String link="http://211.211.213.218:8084/android/all_list_View"; //92.168.25.25
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
            loading = new ProgressDialog(MainActivity.this);
            loading.setProgressStyle(R.style.MyDialog);
            loading.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.cancel(true);
            loading.dismiss();
        }
    }
    /*검색 DB 해시태그 검색시*/
    class listHashAll extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {

                Map<String, String> seldata = new HashMap<String, String>();
                seldata.put("hash_tag", hashTagText);

                String link = "http://211.211.213.218:8084/android/search_View"; //92.168.25.25
                HttpClient.Builder http = new HttpClient.Builder("GET", link);

                http.addAllParameters(seldata);

                // HTTP 요청 전송
                HttpClient post = http.create();
                post.request();
                // 응답 상태코드 가져오기
                int statusCode = post.getHttpStatusCode();
                // 응답 본문 가져오기
                String body = post.getBody();
                return body;

                // Read Server Response

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MainActivity.this);
            loading.setProgressStyle(R.style.MyDialog);
            loading.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            loading.dismiss();
        }
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
    // 메인 -> 트레벌 스토리 이동
    public void travel_Story(View view){
        Intent intent = new Intent(this, TravelStoryActivity.class);

        startActivity(intent);
    }
    public void log_Write(View view){
        Intent intent = new Intent(this, Life_LogActivity.class);
        startActivity(intent);
    }
    public void PictureSel(View v){
        switch(v.getId()){
            case R.id.Camera_sel_pop:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }
    private void showListDialog(){

        String[] item = getResources().getStringArray(R.array.list_dialog_main_item);

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
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(mainPage.getVisibility() == View.VISIBLE){
            backPressCloseHandler.onBackPressed();
        }else {
            mainPage.setVisibility(View.VISIBLE);
            searchPage.setVisibility(View.INVISIBLE);
            likeFollowPage.setVisibility(View.INVISIBLE);
            myPage.setVisibility(View.INVISIBLE);
        }
    }

    public void bakcMain(View view){
        mainPage.setVisibility(View.VISIBLE);
        searchPage.setVisibility(View.INVISIBLE);
        likeFollowPage.setVisibility(View.INVISIBLE);
        myPage.setVisibility(View.INVISIBLE);
    }

    public void profile_change(View view){
        Intent intent = new Intent(getApplicationContext(), profileEditActivity.class);
        startActivityForResult(intent, 1);
    }

    public void user_logout(View view){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
        alert_confirm.setMessage("TravelLog에서 로그아웃 하시겠습니까?").setCancelable(false).setPositiveButton("로그아웃",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES'
                        editor.clear();
                        editor.commit();
                        finish();
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    public void passChangeView(View view){
        Intent intent = new Intent(getApplicationContext(), passWordChangeActivity.class);
        startActivityForResult(intent, 2);
    }

    public void push_alram_setting(View view){
        Intent intent = new Intent(getApplicationContext(), pushAlramSettingActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) // 액티비티가 정상적으로 종료되었을 경우
        {
            if (requestCode == 1 || requestCode ==2) // requestCode==1 로 호출한 경우에만 처리.
            {
                editor.clear();
                editor.commit();
                finish();
            }
        }
    }
}
