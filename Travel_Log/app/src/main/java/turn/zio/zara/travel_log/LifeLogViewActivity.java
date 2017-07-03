package turn.zio.zara.travel_log;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class LifeLogViewActivity extends Activity {

    private TextView log_title;
    private TextView log_Content;
    private TextView log_Place;
    private TextView log_date;
    private TextView profile_user_id;

    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOri = null;

    private LinearLayout picutre_Linear;

    private ImageView image;
    private Bitmap resizedBitmap;
    private Bitmap bmImg;
    private Drawable drawable;
    private String file_Type;
    private String file_Content;

    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_life_log_view);
        getWindow().setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));




        log_title = (TextView) findViewById(R.id.log_title) ;
        log_Content = (TextView) findViewById(R.id.log_cotennt) ;
        log_Place = (TextView) findViewById(R.id.log_place) ;
        log_date = (TextView) findViewById(R.id.log_date) ;
        profile_user_id = (TextView) findViewById(R.id.user_id) ;

        image = (ImageView) findViewById(R.id.log_picture);

        picutre_Linear= (LinearLayout) findViewById(R.id.log_picture_Linear);

        Intent intent = getIntent();
        int board_code = Integer.parseInt(intent.getExtras().getString("board_Code"));
        String boder_Title = intent.getExtras().getString("board_Title");
        String board_Content = intent.getExtras().getString("board_Content");
        Double log_longtitude = Double.parseDouble(intent.getExtras().getString("log_longtitude"));
        Double log_latitude = Double.parseDouble(intent.getExtras().getString("log_latitude"));
        String user_id = intent.getExtras().getString("user_id");
        String String_Date = intent.getExtras().getString("board_Date");
        String write_type = intent.getExtras().getString("write_type");
        Log.d("dd", String_Date);
        String address = getAddress(log_latitude, log_longtitude);

        log_title.setText(boder_Title);
        log_Place.setText(address);
        profile_user_id.setText(user_id);
        log_date.setText(String_Date);
/*웹으로쓴 글일때*/
        if(write_type.equals("0")){
            ArrayList<Integer> posstart = new ArrayList<Integer>();
            ArrayList<Integer> posend = new ArrayList<Integer>();
            board_Content = board_Content.replaceAll("<br>","");
            int poss = board_Content.indexOf("<img");
            int pose = board_Content.indexOf(";\">");
            Log.d("pos",board_Content.indexOf("<img")+"");
            int j = 0;
            while(poss > -1){
                posstart.add(poss);
                poss =  board_Content.indexOf("<img", poss + 1);
                Log.d("startindex", posstart.get(j).toString());
                j++;
            }
            j = 0;
            while(pose > -1){
                posend.add(pose);
                pose =  board_Content.indexOf(";\">", pose+1);
                Log.d("endindex", posend.get(j).toString());
                j++;
            }
            String testData = null;
            for(int i=posstart.size()-1; i>=0; i--){
                Log.d("result", board_Content);
                testData = replaceLast(board_Content,board_Content.substring(Integer.parseInt(posstart.get(i).toString()), (Integer.parseInt(posend.get(i).toString()))+3),"");
                Log.d("result", testData);
                board_Content =testData;

            }
            log_Content.setText(Html.fromHtml(board_Content));
        }/*앱이면*/
        else{
            log_Content.setText(board_Content);
        }
        select_DB(intent.getExtras().getString("board_Code"));
    }
    public static String replaceLast(String str, String regex, String replacement) {
        int regexIndexOf = str.lastIndexOf(regex);
        if(regexIndexOf == -1){
            return str;
        }else{
            return str.substring(0, regexIndexOf) + replacement + str.substring(regexIndexOf + regex.length());
        }
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
    private void select_DB(String board_code){
        picutre_Linear.setVisibility(View.GONE);
        class selectpic extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new ProgressDialog(LifeLogViewActivity.this);
                loading.setProgressStyle(R.style.MyDialog);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result",s);
                if(!s.equals("failed")) {
                    Log.d("dd","dd");
                    picutre_Linear.setVisibility(View.VISIBLE);
                    Log.d("image",picutre_Linear.getVisibility()+"");
                    if(file_Type.equals("1")) {
                        image.setImageBitmap(resizedBitmap);
                        image.setScaleType(ImageView.ScaleType.FIT_XY);

                    }else if(file_Type.equals("2")){
                        final String url = "http://211.211.213.218:8084/turn/resources/upload/logs/" + file_Content;
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
                    }
                }
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String board_code = (String)params[0];

                    Map<String, String> picsel = new HashMap<String,String>() ;

                    picsel.put("board_code",board_code) ;


                    String link="http://211.211.213.218:8084/android/picture"; //92.168.25.25

                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(picsel);

                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();
                    if(!body.equals("failed")) {
                        Log.d("dd","dd");
                        JSONArray json = null;
                        String[][] parsedata = new String[0][2];
                        try {
                            json = new JSONArray(body);
                            parsedata = new String[json.length()][2];
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jobject = json.getJSONObject(i);

                                parsedata[i][0] = jobject.getString("file_content");
                                parsedata[i][1] = jobject.getString("file_type");
                                file_Type = parsedata[i][1];
                                file_Content =  parsedata[i][0];
                            }
                            String url = "http://211.211.213.218:8084/turn/resources/upload/logs/" + file_Content;
                            if (file_Type.equals("1")) {
                                try {

                                    InputStream is = (InputStream) new URL(url).getContent();

                                    bmImg = BitmapFactory.decodeStream(is);
                                    int width = bmImg.getWidth();
                                    int height = bmImg.getHeight();
                                    //화면에 표시할 데이터
                                    Matrix matrix = new Matrix();
                                    resizedBitmap = Bitmap.createBitmap(bmImg, 0, 0, width, height, matrix, true);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }else if(file_Type.equals("2")){
                                drawable = getResources().getDrawable(R.drawable.voice);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return body;

                    // Read Server Response

                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        selectpic task = new selectpic();
        task.execute(board_code);

    }
    public void backAR(View view){
        CameraOverlayView.DBselect = true;
        LifeLogViewActivity2.oneView =true;
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
        LifeLogViewActivity2.oneView =true;
        popListView.touch= true;
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
        finish();
    }
    @Override
    public void onDestroy(){
        if(picutre_Linear.getVisibility()== View.VISIBLE) {
            Drawable d = image.getDrawable();
            if (d instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                bitmap.recycle();
                bitmap = null;
            }
        }
        super.onDestroy();
    }
}
