package turn.zio.zara.travel_log;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class LifeLogViewActivity extends Activity {
    private TextView log_title;
    private TextView log_Content;
    private TextView log_Place;
    private TextView log_date;
    private TextView profile_user_id;

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

        Intent intent = getIntent();
        int board_code = Integer.parseInt(intent.getExtras().getString("board_Code"));
        String boder_Title = intent.getExtras().getString("board_Title");
        String board_Content = intent.getExtras().getString("board_Content");
        Double log_longtitude = Double.parseDouble(intent.getExtras().getString("log_longtitude"));
        Double log_latitude = Double.parseDouble(intent.getExtras().getString("log_latitude"));
        String user_id = intent.getExtras().getString("user_id");
        String String_Date = intent.getExtras().getString("board_Date");

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
        finish();
    }
}
