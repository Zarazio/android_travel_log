package turn.zio.zara.travel_log;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static turn.zio.zara.travel_log.TravelListActivity.joinCode;

public class MainActivity extends AppCompatActivity {

    TextView user_place;
    TextView user_main_id;
    LocationManager lm;

    private ListViewDialog mDialog;

    SharedPreferences login;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor2;
    SharedPreferences.Editor editor3;
    SharedPreferences.Editor editor4;
    private SharedPreferences alram;
    SharedPreferences travelStory;
    SharedPreferences smartCost;
    ListViewAdapter Tadapter;

    SharedPreferences stepkeep;

    LinearLayout mainPage;
    LinearLayout searchPage;
    LinearLayout likeFollowPage;
    LinearLayout myPage;
    LinearLayout serch_view;
    LinearLayout mylogvisible;
    LinearLayout travelvisible;

    TextView search_Text_view;
    TextView friendsCount;
    TextView logCount;
    EditText search_Text;
    String sc_Division;
    public static String select_group_Code = "";

    ArrayList<String> location = new ArrayList<String>();

    private ImageView step_log_pic;
    private ImageView profile;
    ListView listview;

    private BackPressCloseHandler backPressCloseHandler;
    private String hashTagText;

    private boolean[] menu;

    private ArrayList<LocationInfo> steparr;
    int placeTime;

    String steplogkeep;
    int mode;
    private String[][] parsedata;
    MyAdapter adapter;
    MainAdapter mainapter;
    String user_id;

    DataBaseUrl dataurl = new DataBaseUrl();
    private String prifile_pict;

    Bitmap[] pImage;
    private ImageView my_page_profile_picture;
    Notification.Builder builder;
    PendingIntent pendingNotificationIntent;
    NotificationManager notificationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*처음 DB 실행*/
        notificationManager = (NotificationManager) MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        builder = new Notification.Builder(getApplicationContext());
        pendingNotificationIntent = PendingIntent.getActivity(MainActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        mode = 1;
        menu = new boolean[4];
        placeTime = 1000;
        backPressCloseHandler = new BackPressCloseHandler(this);

        user_place = (TextView) findViewById(R.id.user_place_info);
        user_main_id = (TextView) findViewById(R.id.main_user_id);
        TextView my_page_user_id = (TextView) findViewById(R.id.my_page_user_id);

        mainPage = (LinearLayout) findViewById(R.id.main_page);
        searchPage = (LinearLayout) findViewById(R.id.search_page);
        likeFollowPage = (LinearLayout) findViewById(R.id.like_follow);
        myPage = (LinearLayout) findViewById(R.id.my_page);
        serch_view = (LinearLayout) findViewById(R.id.serch_view);
        mylogvisible = (LinearLayout) findViewById(R.id.mylogvisible);
        travelvisible = (LinearLayout) findViewById(R.id.travelvisible);

        logCount = (TextView) findViewById(R.id.logCount);
        friendsCount = (TextView) findViewById(R.id.friendsCount);
        my_page_profile_picture = (ImageView) findViewById(R.id.my_page_profile_picture);

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
        user_id = login.getString("user_id", "0");
        prifile_pict = login.getString("prifile_picture", "default.png");

        stepkeep = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor2 = stepkeep.edit();
        steplogkeep = stepkeep.getString("steplogkeep", "0");
        int stepsize = stepkeep.getInt("stepdatasize", 0);

        alram = getSharedPreferences("pushAlram", MODE_PRIVATE);
        editor3 = alram.edit();

        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        sc_Division = smartCost.getString("sc_Division", "0");

        my_page_user_id.setText(user_id);
        search_Text = (EditText) findViewById(R.id.search_Text);
        search_Text_view = (TextView) findViewById(R.id.search_Text_view);

        profile = (ImageView) findViewById(R.id.profile_picture);

        if (!prifile_pict.equals("0")) {
            profile_pic();
        }
        Drawable d;

        if (steplogkeep.equals("1")) {
            steparr = new ArrayList<LocationInfo>();
            builder.setSmallIcon(R.drawable.foot).setTicker("StepLog").setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle("Step Log").setContentText("Step Log 작성중...").setOngoing(true)
                    .setContentIntent(pendingNotificationIntent);
            notificationManager.notify(1, builder.build());
            for (int i = 0; i < stepsize; i++) {
                Double latitude = Double.parseDouble(stepkeep.getString("latitude" + i, "0"));
                Double longitude = Double.parseDouble(stepkeep.getString("longitude" + i, "0"));

                steparr.add(new LocationInfo(latitude, longitude));
            }
        } else {
            steparr = new ArrayList<LocationInfo>();
        }

        Log.d("step_log", steplogkeep);
        DBinput();

        user_main_id.setText(user_id);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;// 가로
        int height = displayMetrics.heightPixels;

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*위치정보*/
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    placeTime, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        }else{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    placeTime, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        }


        /*검색시 해시태그 View 클릭시*/
        search_Text.setOnFocusChangeListener(new View.OnFocusChangeListener() { // 포커스를 얻으면
            @Override
            public void onFocusChange(View v, boolean hasFocus) { // 포커스가 한뷰에서 다른뷰로 바뀔때
                if (hasFocus == false) {
                    String hashhint = search_Text.getHint().toString();
                    String hashtest = search_Text.getText().toString();
                    serch_view.setVisibility(View.GONE);
                    search_Text_view.setVisibility(View.VISIBLE);
                    if (!hashtest.equals("")) {
                        search_Text_view.setText(hashtest);
                    } else {
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

        GridView gv = (GridView) findViewById(R.id.list);
        /*list에 뿌려진 로그 클릭시*/
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity2.class);
                intent.putExtra("board_Code", parsedata[position][0]);
                intent.putExtra("board_Title", parsedata[position][1]);
                intent.putExtra("board_Content", parsedata[position][2]);
                intent.putExtra("log_longtitude", parsedata[position][3]);
                intent.putExtra("log_latitude", parsedata[position][4]);
                intent.putExtra("board_Date", parsedata[position][5]);
                intent.putExtra("write_user_id", parsedata[position][6]);
                intent.putExtra("user_id", user_id);
                intent.putExtra("file_Type", parsedata[position][7]);
                intent.putExtra("file_Content", parsedata[position][8]);
                if (parsedata[position][7].equals("3")) {
                    intent.putExtra("step_log_code", parsedata[position][9]);
                }
                intent.putExtra("write_type", parsedata[position][10]);
                Log.d("profile_pic", parsedata[position][11]);
                intent.putExtra("profile_picture", parsedata[position][11]);
                startActivity(intent);
            }
        });
        GridView mygv = (GridView) findViewById(R.id.mypage_list);
        /*list에 뿌려진 로그 클릭시*/
        mygv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity2.class);
                intent.putExtra("board_Code", parsedata[position][0]);
                intent.putExtra("board_Title", parsedata[position][1]);
                intent.putExtra("board_Content", parsedata[position][2]);
                intent.putExtra("log_longtitude", parsedata[position][3]);
                intent.putExtra("log_latitude", parsedata[position][4]);
                intent.putExtra("board_Date", parsedata[position][5]);
                intent.putExtra("write_user_id", parsedata[position][6]);
                intent.putExtra("user_id", user_id);
                intent.putExtra("file_Type", parsedata[position][7]);
                intent.putExtra("file_Content", parsedata[position][8]);
                if (parsedata[position][7].equals("3")) {
                    intent.putExtra("step_log_code", parsedata[position][9]);
                }
                intent.putExtra("write_type", parsedata[position][10]);
                Log.d("profile_pic", parsedata[position][11]);
                intent.putExtra("profile_picture", parsedata[position][11]);
                startActivity(intent);
            }
        });
    }

    /*StepLog Insert*/
    private void pushalram(String token, double longitude, double latitude) {

        final double Mylongitude = longitude;
        final double Mylatitude = latitude;
        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                FirebaseMessagingService.push_text = s;
                super.onPostExecute(s);

                Log.d("result", s);

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String token = (String) params[0];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("userDeviceIdKey", token);
                    loginParam.put("longitude", Mylongitude + "");
                    loginParam.put("latitude", Mylatitude + "");
                    loginParam.put("user_id", user_id);

                    String link = dataurl.getServerUrl() + "push_alram"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(loginParam);


                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();
                    return body;

                    // Read Server Res

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }
        insertData task = new insertData();
        task.execute(token);
    }


    /*StepLog Insert*/
    private void StepInsert(String user_id) {

        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);

                    String link = dataurl.getServerUrl() + "stepinsert"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(loginParam);

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
        }
        insertData task = new insertData();
        task.execute(user_id);
    }

    /*메인 db 연결시도*/
    public void DBinput() {
        mainlistAll task = new mainlistAll();
        String result = null;
        try {
            result = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        jsonParse(result);
        serpic setimage = new serpic();
        setimage.execute();
    }

    /*result JSon Parese*/
    public void jsonParse(String s) {
        Log.d("json", s);
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][12];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("board_code");
                parsedata[i][1] = jobject.getString("board_title");
                parsedata[i][2] = jobject.getString("board_content");
                parsedata[i][3] = jobject.getString("log_longtitude");
                parsedata[i][4] = jobject.getString("log_latitude");
                parsedata[i][5] = jobject.getString("board_date");
                parsedata[i][6] = jobject.getString("user_id");
                if (json.getJSONObject(i).isNull("file_content") == false) {
                    parsedata[i][7] = jobject.getString("file_type");
                    parsedata[i][8] = jobject.getString("file_content");
                } else {
                    parsedata[i][7] = "0";
                    parsedata[i][8] = "1";
                }
                if (parsedata[i][7].equals("3")) {
                    parsedata[i][9] = jobject.getString("step_log_code");
                }
                parsedata[i][10] = jobject.getString("write_type");
                if (json.getJSONObject(i).isNull("user_profile") == false) {
                    parsedata[i][11] = jobject.getString("user_profile");
                } else {
                    parsedata[i][11] = prifile_pict;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*메인 gridview에 뿌리기*/
    public void mainApeter(Bitmap[] images) {
        String[] board_code = new String[parsedata.length];
        String[] title = new String[parsedata.length];
        String[] Content = new String[parsedata.length];
        String[] date = new String[parsedata.length];
        String[] writeuser_id = new String[parsedata.length];
        String[] file_type = new String[parsedata.length];
        String[] adress = new String[parsedata.length];
        String[] file_Content = new String[parsedata.length];
        String[] step_log_code = new String[parsedata.length];
        String[] write_type = new String[parsedata.length];
        String[] user_profile = new String[parsedata.length];

        for (int i = 0; i < parsedata.length; i++) {
            board_code[i] = parsedata[i][0];
            title[i] = parsedata[i][1];
            Content[i] = parsedata[i][2];
            date[i] = parsedata[i][5];
            writeuser_id[i] = parsedata[i][6];
            file_type[i] = parsedata[i][7];
            file_Content[i] = parsedata[i][8];
            if (file_type.equals("3")) {
                step_log_code[i] = parsedata[i][9];
            } else {
                step_log_code[i] = "0";
            }
            write_type[i] = parsedata[i][10];
            user_profile[i] = parsedata[i][11];
        }
        Log.d("image", images + "");
        mainapter = new MainAdapter(
                MainActivity.this,
                R.layout.main_log_view, board_code,       // GridView 항목의 레이아웃 row.xml
                title, Content, date, writeuser_id, file_type, adress, file_Content, step_log_code, write_type, user_id, user_profile);
        mainapter.image(images, 1);
        mainapter.pimage(pImage, 1);
        GridView gv = (GridView) findViewById(R.id.main_list);
        gv.setAdapter(mainapter);

    }

    /*검색 gridview에 뿌리기*/
    public void Apeter(Bitmap[] images) {
        String[] text = new String[parsedata.length];
        String[] file_type = new String[parsedata.length];
        for (int i = 0; i < parsedata.length; i++) {
            if (parsedata[i][7].equals("0")) {
                text[i] = parsedata[i][1];
            } else {
                text[i] = parsedata[i][8];
            }
            file_type[i] = parsedata[i][7];
        }
        adapter = new MyAdapter(
                MainActivity.this,
                R.layout.pop_view_list,       // GridView 항목의 레이아웃 row.xml
                text, file_type);
        adapter.image(images);
        GridView gv = null;
        if (mode == 2) {
            gv = (GridView) findViewById(R.id.list);
        } else if (mode == 4) {
            gv = (GridView) findViewById(R.id.mypage_list);
        }
        gv.setAdapter(adapter);

    }

    /*gridView 웹서버 이미지 뿌리기*/
    class serpic extends AsyncTask<String, Void, Bitmap[]> {
        ProgressDialog loading;

        private InputStream is = null;
        private String KMlurl;

        @Override
        protected Bitmap[] doInBackground(String... params) {
            Bitmap[] images = new Bitmap[parsedata.length];
            pImage = new Bitmap[parsedata.length];
            try {
                for (int i = 0; i < parsedata.length; i++) {

                    String purl = dataurl.getProfile() + parsedata[i][11];
                    InputStream iss = (InputStream) new URL(purl).getContent();
                    BitmapFactory.Options optionss = new BitmapFactory.Options();
                    optionss.inSampleSize = 1;
                    optionss.inJustDecodeBounds = false;
                    Bitmap resizedBitmaps = BitmapFactory.decodeStream(iss, null, optionss);
                    pImage[i] = resizedBitmaps;
                    if (parsedata[i][7].equals("3")) {
                        String urltext = dataurl.getStepUrl() + parsedata[i][8];
                        Log.d("KMLurl", urltext);
                        URL url = new URL(urltext);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.connect();
                        File file = new File(urltext);
                        //FileInputStream is =new FileInputStream(file);
                        is = urlConnection.getInputStream();

                        InputStreamReader inputReader = new InputStreamReader(is);

                        String column = null;
                        BufferedReader br = new BufferedReader(inputReader);
                        boolean flag = false;
                        while ((column = br.readLine()) != null) {
                            int coordin = column.indexOf("<coordinates>");

                            if (coordin != -1 || flag) {
                                Log.d("폴리라인 그림", "걸러내는중");
                                int j = 0;
                                flag = true;
                                String tmpCoordin = column;
                                tmpCoordin = tmpCoordin.replaceAll("<coordinates>", "");
                                tmpCoordin = tmpCoordin.replaceAll("</coordinates>", "");
                                if (tmpCoordin.trim().equals("</LineString>")) {
                                    break;
                                }
                                location.add(tmpCoordin.trim());
                            }


                        }

                        Log.d("size", location.size()+"");
                        KMlurl = "";
                        KMlurl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&path=";
                        for (int k = 0; k < location.size(); k++) {
                            String[] coos = location.get(k).toString().split(",");
                            KMlurl += coos[1] + "," + coos[0];
                            if (k != location.size() - 1) {
                                KMlurl += "|";
                            }else {
                                KMlurl += "&sensor=false";
                            }
                            Log.d("dd", coos[1] + "," + coos[0]);
                            Log.d("result", parsedata[i][8]);
                        }

                    }
                    if (parsedata[i][7].equals("1")) {
                        String url = dataurl.getTumnailUrl() + parsedata[i][8];
                        Log.d("URL", url);
                        InputStream is = (InputStream) new URL(url).getContent();
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inJustDecodeBounds = false;
                        Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                        images[i] = resizedBitmap;
                    }else if(parsedata[i][7].equals("3")){
                        Log.d("url", parsedata[i][8]);
                        InputStream is = (InputStream) new URL(KMlurl).getContent();
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inJustDecodeBounds = false;
                        Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                        images[i] = resizedBitmap;
                    }
                }
                return images;

                // Read Server Response

            } catch (Exception e) {
                images = null;
                return images;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap[] s) {
            super.onPostExecute(s);
            if (mode == 1) {
                mainApeter(s);
            } else {
                Log.d("image", "dd");
                Apeter(s);
            }
            this.cancel(true);
            loading.dismiss();
        }
    }

    public void backView(View view) {
        search_Text.clearFocus();
    }

    /*뷰버튼을 editView로*/
    public void modeWrite(View view) {
        String hashtest = (String) search_Text_view.getText();
        search_Text_view.setVisibility(view.GONE);
        serch_view.setVisibility(view.VISIBLE);
        if (hashtest.equals("검색")) {
            search_Text.setHint("해시태그 검색");
        } else {
            search_Text.setText(hashtest);
        }
        search_Text.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    /*메뉴버튼을 눌러 뷰바꿀시*/
    public void viewPageChange(View v) {
        switch (v.getId()) {
            case R.id.view_home_icon:
                mode = 1;
                mainPage.setVisibility(v.VISIBLE);
                searchPage.setVisibility(v.INVISIBLE);
                likeFollowPage.setVisibility(v.INVISIBLE);
                myPage.setVisibility(v.INVISIBLE);
                menu[0] = true;
                menu[1] = false;
                menu[2] = false;
                menu[3] = false;
                DBinput();
                break;
            case R.id.view_search_icon:
                mode = 2;
                searchPage.setVisibility(v.VISIBLE);
                mainPage.setVisibility(v.INVISIBLE);
                likeFollowPage.setVisibility(v.INVISIBLE);
                myPage.setVisibility(v.INVISIBLE);
                menu[1] = true;
                menu[0] = false;
                menu[2] = false;
                menu[3] = false;
                DBinput();
                break;
            case R.id.view_heart_icon:
                mode = 3;
                likeFollowPage.setVisibility(v.VISIBLE);
                searchPage.setVisibility(v.INVISIBLE);
                mainPage.setVisibility(v.INVISIBLE);
                myPage.setVisibility(v.INVISIBLE);
                menu[2] = true;
                menu[1] = false;
                menu[0] = false;
                menu[3] = false;
                break;
            case R.id.view_mypage_icon:
                mode = 4;
                myPage.setVisibility(v.VISIBLE);
                likeFollowPage.setVisibility(v.INVISIBLE);
                searchPage.setVisibility(v.INVISIBLE);
                mainPage.setVisibility(v.INVISIBLE);
                menu[3] = true;
                menu[1] = false;
                menu[2] = false;
                menu[0] = false;
                DBinput();
                profile_count profile_count = new profile_count();
                profile_count.execute();
                break;
        }
    }

    /*메인 클릭시 db시*/
    class mainlistAll extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Log.d("mode", mode + "");
                Map<String, String> seldata = new HashMap<String, String>();
                if (mode == 1) {
                    seldata.put("user_id", user_id);
                    DBserver = "main_View_DB";
                } else if (mode == 2) {
                    DBserver = "all_list_View";
                } else if (mode == 4) {
                    seldata.put("user_id", user_id);
                    DBserver = "myLog";
                }
                String link = dataurl.getServerUrl() + DBserver; //92.168.25.25
                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                if (mode == 1 || mode == 4) {
                    http.addAllParameters(seldata);
                }
                // HTTP 요청 전송
                HttpClient post = http.create();
                post.request();
                // 응답 상태코드 가져오기
                int statusCode = post.getHttpStatusCode();
                // 응답 본문 가져오기
                String body = post.getBody();
                return body;

            } catch (Exception e) {
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
            this.cancel(true);
        }
    }

    /*프로필 사진*/
    private void profile_pic() {
        final Bitmap[] resizedBitmaps = new Bitmap[1];
        class write extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                this.cancel(true);
                my_page_profile_picture.setScaleType(ImageView.ScaleType.FIT_XY);
                profile.setImageBitmap(resizedBitmaps[0]);
                my_page_profile_picture.setImageBitmap(resizedBitmaps[0]);
                profile.setBackground(new ShapeDrawable(new OvalShape()));
                profile.setClipToOutline(true);
                my_page_profile_picture.setBackground(new ShapeDrawable(new OvalShape()));
                my_page_profile_picture.setClipToOutline(true);
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    String url = dataurl.getProfile() + prifile_pict;
                    Log.d("profile", url);
                    InputStream is = (InputStream) new URL(url).getContent();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = false;
                    resizedBitmaps[0] = BitmapFactory.decodeStream(is, null, options);
                    return "success";
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        write task = new write();
        task.execute();
    }

    class profile_count extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {

                Map<String, String> seldata = new HashMap<String, String>();
                seldata.put("user_id", user_id);

                String link = dataurl.getServerUrl() + "count_profile"; //92.168.25.25
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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("카운트 결과값", s);
            String lCount = s.substring(0, s.indexOf(","));
            String fCount = s.substring(s.indexOf(",") + 1);

            friendsCount.setText(fCount);
            logCount.setText(lCount);
            this.cancel(true);
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

                String link = dataurl.getServerUrl() + "search_View"; //92.168.25.25
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
            loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            this.cancel(true);
            loading.dismiss();
        }
    }

    public void myLogList(View view) {
        mode = 4;
        mylogvisible.setVisibility(View.VISIBLE);
        travelvisible.setVisibility(View.GONE);
        DBinput();
    }

    public void myTravel(View view) {
        mylogvisible.setVisibility(View.GONE);
        travelvisible.setVisibility(View.VISIBLE);
        Travel task = new Travel();
        task.execute(user_id); // 메소드를 실행한당
    }

    //AsyncTask 라는 스레드를 시작 시켜 DB연결
    class Travel extends AsyncTask<String, Void, String> {

        //doInBackGround가 종료후 실행되는 메서드
        @Override
        protected void onPostExecute(String s) { // 웹 -> 앱으로 받는값
            super.onPostExecute(s);
            Log.d("onPostExecute: ", s);

            liston(s);
        }

        @Override
        protected String doInBackground(String... params) { // 실행 메서드

            try {
                String link = "";
                String data = "";
                link = dataurl.getServerUrl(); // 집 : 192.168.1.123, 학교 : 172.20.10.203, 에이타운 : 192.168.0.14

                Map<String, String> insertParam = new HashMap<String, String>(); // key, value

                String user_id = (String) params[0];
                insertParam.put("user_id", user_id);

                Log.d("스프링으로 보내는 값", insertParam.get("user_id").toString());

                link += "/titleSearch";

                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(insertParam); // 앱 -> 스프링으로 데이터보냄, 매개값을

                // HTTP 요청 전송
                HttpClient post = http.create();
                post.request();
                // 응답 상태코드 가져오기
                int statusCode = post.getHttpStatusCode();
                // 응답 본문 가져오기
                String body = post.getBody();
                return body;
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
    }

    public void liston(String title) {
        // Adapter 생성
        Tadapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(Tadapter);

        JsonArray json = (JsonArray) new JsonParser().parse(title);

////        Log.d("TAG", "onCreate: "+title);
        for (int i = 0; i < json.size(); i++) {
            Log.d("TAG Object", json.get(i).toString());
            JsonObject obj = json.get(i).getAsJsonObject(); // 오브젝트
//
            Log.d("TAG", "" + obj.get("travel_title"));
            Tadapter.addItem(ContextCompat.getDrawable(this, R.drawable.story), obj.get("travel_title").toString().replaceAll("\"", ""),
                    obj.get("start_date").toString().replaceAll("\"", ""), obj.get("end_date").toString().replaceAll("\"", ""),
                    obj.get("group_Code").toString().replaceAll("\"", ""));
//
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListViewItem listViewItem = (ListViewItem) adapterView.getItemAtPosition(i);
                travelStory = getSharedPreferences("title", MODE_PRIVATE);
                editor4 = travelStory.edit();
                editor4.putString("selectTitle", listViewItem.getTitle()); // 선택한 제목
                editor4.putString("selectgroupCode", listViewItem.getGcode()); // 선택한 코드
                editor4.commit();

                Log.d("디비시작전", listViewItem.getGcode());
                SearhToDatabase(listViewItem.getGcode());

                if (sc_Division.equals("차감")) {
                    Intent intent = new Intent(MainActivity.this, SmartCostSubActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, SmartCostAddActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 현재날짜와 회원이 참여한 그룹의 날짜들을 비교하여 상태를 지정
        java.util.Date mDate = new java.util.Date(); // 현재날짜 구하기
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd"); // 형식지정
        String tempDate = mFormat.format(mDate); // 현재날짜 형식에 맞춰 초기화
        java.util.Date nowDate = java.sql.Date.valueOf(tempDate); //현재날짜


        for (int i = 0; i < json.size(); i++) { // 현재날짜와 그룹의 날짜를 비교
            JsonObject obj = json.get(i).getAsJsonObject(); // json 오브젝트
            String startDate = obj.get("start_date").toString().replaceAll("\"", "");
            String endDate = obj.get("end_date").toString().replaceAll("\"", "");

            java.util.Date st_Date = java.sql.Date.valueOf(startDate);
            java.util.Date en_Date = java.sql.Date.valueOf(endDate);

            Log.d("날짜 ", "제목:" + obj.get("travel_title") + ", 현재:" + String.valueOf(nowDate) + ", 시작:" + String.valueOf(st_Date) + ", 종료:" + String.valueOf(en_Date));
            if (nowDate.compareTo(st_Date) != -1 && nowDate.compareTo(en_Date) != 1) { // 현재날짜가 여행중인 날짜이면 joinCode를 1로 세팅
                joinCode = 1;
                select_group_Code = obj.get("group_Code").toString().replaceAll("\"", "");
//                Log.d("여행 중", String.valueOf(joinCode));
            } else {
                if (joinCode == 1) {
//                    Log.d("여행 중 아닌데 이미 여행중인 날짜가 존재", String.valueOf(joinCode));
                    break;
                }
                joinCode = 0;
//                Log.d("여행 중 아님", String.valueOf(joinCode));
            }
        }
        Log.d("최종 joinCode값", String.valueOf(joinCode));
        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        editor4 = smartCost.edit();
        editor4.putString("joinCode", String.valueOf(joinCode)); // 선택한 제목
        editor4.commit();
    }

    private void SearhToDatabase(String group_Code) {
        SearhData task = new SearhData();
        Log.d("최지훈디비전송", group_Code);
        task.execute(group_Code); // 메소드를 실행한당
    }

    //AsyncTask 라는 스레드를 시작 시켜 DB연결
    class SearhData extends AsyncTask<String, Void, String> {

        //doInBackGround가 종료후 실행되는 메서드
        @Override
        protected void onPostExecute(String s) { // 웹 -> 앱으로 받는값

            super.onPostExecute(s);

            JsonObject obj = (JsonObject) new JsonParser().parse(s);

            smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
            editor4 = smartCost.edit();
            editor4.putString("coin_Limit", obj.get("coin_Limit").toString().replaceAll("\"", "")); // 선택한 일정의 한도
            editor4.putString("sc_Division", obj.get("sc_Division").toString().replaceAll("\"", "")); // 선택한 일정의 스마트 코스트 구분
            editor4.commit();
        }

        @Override
        protected String doInBackground(String... params) { // 실행 메서드

            try {
                String link = "";
                String data = "";
                link = dataurl.getServerUrl();
                ; // 집 : 192.168.1.123, 학교 : 172.20.10.203, 에이타운 : 192.168.0.14

                Map<String, String> insertParam = new HashMap<String, String>(); // key, value

                String group_Code = (String) params[0];
                insertParam.put("group_Code", group_Code);

                link += "/scDivisionSearch";

                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(insertParam); // 앱 -> 스프링으로 데이터보냄, 매개값을

                // HTTP 요청 전송
                HttpClient post = http.create();
                post.request();
                // 응답 상태코드 가져오기
                int statusCode = post.getHttpStatusCode();
                // 응답 본문 가져오기
                String body = post.getBody();
                return body;
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            FirebaseMessaging.getInstance().subscribeToTopic("notice");
            String token = FirebaseInstanceId.getInstance().getToken();

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도

            pushalram(token, longitude, latitude);
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            user_place.setText(getAddress(latitude, longitude));
            if (steplogkeep.equals("1")) {
                steparr.add(new LocationInfo(latitude, longitude));
                Log.d("dd",steparr.size()+"");
                Log.d("dd","dd");
            }
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

    public String getAddress(double lat, double lng) {
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) {
            Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }
        if (list.size() > 0) {
            Address addr = list.get(0);
            address =   addr.getLocality() + " "
                    + addr.getThoroughfare() + " ";
        }
        return address;
    }

    public void log_Write(View view) {
        LogWriteDialog();
    }

    public void option(View v) {
        Intent intent = new Intent(getApplicationContext(), option.class);
        startActivity(intent);
    }

    public void profile_edit(View v) {
        Intent intent = new Intent(getApplicationContext(), profileEditActivity.class);
        startActivity(intent);
    }

    /*카메라 종류 선택 dialog*/
    public void PictureSel(View v) {
        switch (v.getId()) {
            case R.id.Camera_sel_pop:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }

    /*글쓰기 버튼 클릿*/
    private void LogWriteDialog() {

        String[] item = getResources().getStringArray(R.array.log_wrtie_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String>(listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener() {


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), Life_LogActivity.class);
                    intent.putExtra("stepLog", steplogkeep);
                    startActivity(intent);
                } else if (position == 1) {
                    stepkeep = getSharedPreferences("LoginKeep", MODE_PRIVATE);
                    editor = stepkeep.edit();
                    steplogkeep = stepkeep.getString("steplogkeep", "0");
                    Log.d("steplog", steplogkeep);
                    if (steplogkeep.equals("0")) {
                        steplogkeep = "1";
                        editor2.putString("steplogkeep", steplogkeep);
                        editor2.commit();
                        StepInsert(user_id);
                        builder.setSmallIcon(R.drawable.foot).setTicker("StepLog").setWhen(System.currentTimeMillis())
                                .setNumber(1).setContentTitle("Step Log").setContentText("Step Log 작성중...").setOngoing(true)
                                .setContentIntent(pendingNotificationIntent);


                        notificationManager.notify(1, builder.build());

                    } else {
                        Intent intent = new Intent(getApplicationContext(), StepLogActivity.class);

                        double[] latitude = new double[steparr.size()];
                        double[] longitude = new double[steparr.size()];
                        for (int i = 0; i < steparr.size(); i++) {
                            latitude[i] = steparr.get(i).getLatitude();
                            longitude[i] = steparr.get(i).getLongitude();
                        }

                        intent.putExtra("user_id", user_id);
                        intent.putExtra("stepsize", steparr.size());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);

                        startActivityForResult(intent, 3);
                    }
                    Log.d("step_log", steplogkeep);
                } else if (position == 2) {
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }

    /*카메라 버튼 클릭*/
    private void showListDialog() {

        String[] item = getResources().getStringArray(R.array.list_dialog_main_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String>(listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener() {


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), TravelCameraActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
        if (mainPage.getVisibility() == View.VISIBLE) {
            backPressCloseHandler.onBackPressed();
            if (steplogkeep.equals("1")) {

                editor.putInt("stepdatasize", steparr.size()); /*sKey is an array*/

                Log.d("step_log", steparr.size() + "");
                for (int i = 0; i < steparr.size(); i++) {
                    editor.putString("latitude" + i, steparr.get(i).getLatitude() + "");
                    editor.putString("longitude" + i, steparr.get(i).getLongitude() + "");
                }

                editor.commit();
            }
        } else {
            mainPage.setVisibility(View.VISIBLE);
            searchPage.setVisibility(View.INVISIBLE);
            likeFollowPage.setVisibility(View.INVISIBLE);
            myPage.setVisibility(View.INVISIBLE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) // 액티비티가 정상적으로 종료되었을 경우
        {
            if (requestCode == 1 || requestCode == 2) // requestCode==1 로 호출한 경우에만 처리.
            {
                editor.clear();
                editor.commit();
                finish();
            }
        }
    }
}
