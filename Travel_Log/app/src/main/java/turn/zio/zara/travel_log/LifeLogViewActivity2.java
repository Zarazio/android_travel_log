package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class LifeLogViewActivity2 extends AppCompatActivity {

    private TextView log_title;
    private TextView log_Content;
    private TextView log_Place;
    private TextView log_date;
    private TextView profile_user_id;

    private LinearLayout picutre_Linear;

    private ImageView image;
    private ImageView bakcMain_icon;
    private Drawable drawable;
    String file_Content;
    MediaPlayer player;
    String imageURL = "http://211.211.213.218:8084/android/resources/upload/";
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
        Drawable drawable = getResources().getDrawable(R.drawable.backbutton2);

        bakcMain_icon.setImageDrawable(drawable);
        Intent intent = getIntent();
        int board_code = Integer.parseInt(intent.getExtras().getString("board_Code"));
        String boder_Title = intent.getExtras().getString("board_Title");
        String board_Content = intent.getExtras().getString("board_Content");
        Double log_longtitude = Double.parseDouble(intent.getExtras().getString("log_longtitude"));
        Double log_latitude = Double.parseDouble(intent.getExtras().getString("log_latitude"));
        String user_id = intent.getExtras().getString("user_id");
        String String_Date = intent.getExtras().getString("board_Date");
        String file_Type = intent.getExtras().getString("file_Type");
        file_Content = intent.getExtras().getString("file_Content");

        if(file_Type.equals("1")) {
            Log.d("이미지","이미지");
            picutre_Linear.setVisibility(View.VISIBLE);
            serpic webserver = new serpic();
            webserver.execute();
        }else{
            if(file_Type.equals("2")){
                Log.d("뷰",file_Content);
                picutre_Linear.setVisibility(View.VISIBLE);
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
            }
        }
        String address = getAddress(log_latitude, log_longtitude);

        log_title.setText(boder_Title);
        log_Content.setText(board_Content);
        log_Place.setText(address);
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
}
