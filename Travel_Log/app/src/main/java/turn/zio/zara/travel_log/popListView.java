package turn.zio.zara.travel_log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static turn.zio.zara.travel_log.CameraOverlayView.mVisibleDistance;


public class popListView extends Activity {

    private ArrayList<ListItem> arItem;
    private String jsondata;
    private popListView mContext;
    private String[][] parsedata;


    public static boolean touch= true;
    JSONArray json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_log_listview);
        getWindow().setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        touch= true;

        Intent intent = getIntent();
        jsondata = intent.getExtras().getString("jsonData");
        double mlongitude = intent.getExtras().getDouble("mlongitude");
        double mlatitude = intent.getExtras().getDouble("mlatitude");
        Log.d("Dd",jsondata);

        Location locationA = new Location("Point A");
        Location locationB = new Location("Point B");

        locationA.setLongitude(mlongitude);
        locationA.setLatitude(mlatitude);


       parsedata = new String[0][9];

        arItem = new ArrayList<ListItem>();

        try {
            json = new JSONArray(jsondata);
            parsedata = new String[json.length()][9];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("board_Code");
                parsedata[i][1] = jobject.getString("board_Title");
                parsedata[i][2] = jobject.getString("board_Content");
                parsedata[i][3] = jobject.getString("log_longtitude");
                parsedata[i][4] = jobject.getString("log_latitude");
                parsedata[i][5] = jobject.getString("randomViewY");
                parsedata[i][6] = jobject.getString("user_id");
                parsedata[i][7] = jobject.getString("board_Date");

                Double tBx = Double.parseDouble(parsedata[i][3]);
                Double tBy = Double.parseDouble(parsedata[i][4]);

                locationB.setLongitude(tBx);
                locationB.setLatitude(tBy);

                int distance = (int) locationA.distanceTo(locationB);

                if (distance <= mVisibleDistance * 1000) {
                    if (distance < 1000) {
                        String board_Code = parsedata[i][0];
                        String title = parsedata[i][1];
                        String user_id = parsedata[i][6];
                        arItem.add(new ListItem(board_Code,title, user_id));
                        Log.d("Dd",arItem.size()+"");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MultiAdapter MyAdapter = new MultiAdapter(this, arItem);

        //리스트뷰를 만들고
        ListView MyList;
        MyList = (ListView)findViewById(R.id.list);
        MyList.setAdapter(MyAdapter);//이어줍니다.

        MyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sel_boardCode = arItem.get(position).board_Code();
                try {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jobject = json.getJSONObject(i);


                        parsedata[i][0] = jobject.getString("board_Code");
                        parsedata[i][1] = jobject.getString("board_Title");
                        parsedata[i][2] = jobject.getString("board_Content");
                        parsedata[i][3] = jobject.getString("log_longtitude");
                        parsedata[i][4] = jobject.getString("log_latitude");
                        parsedata[i][5] = jobject.getString("randomViewY");
                        parsedata[i][6] = jobject.getString("user_id");
                        parsedata[i][7] = jobject.getString("board_Date");

                        if(sel_boardCode.equals(parsedata[i][0]) && touch==true) {
                            Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity.class);
                            touch= false;
                            intent.putExtra("board_Code",parsedata[i][0]);
                            intent.putExtra("board_Title",parsedata[i][1]);
                            intent.putExtra("board_Content",parsedata[i][2]);
                            intent.putExtra("log_longtitude",parsedata[i][3]);
                            intent.putExtra("log_latitude",parsedata[i][4]);
                            intent.putExtra("user_id",parsedata[i][6]);
                            intent.putExtra("board_Date",parsedata[i][7]);

                            startActivity(intent);
                            CameraOverlayView.DBselect = false;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void backAR(View view){
        CameraOverlayView.drawtext = true;
        finish();
    }
    @Override
    public void onBackPressed(){
        CameraOverlayView.drawtext = true;
        finish();
    }

}
class ListItem{
    String title;
    String user_id;
    String board_Code;

    ListItem(String board_Code, String title, String user_id){
        this.board_Code= board_Code;
        this.title = title;
        this.user_id = user_id;
    }


    public String board_Code() {
        return this.board_Code;
    }
}

class MultiAdapter extends BaseAdapter {


    LayoutInflater mInflater;
    ArrayList<ListItem> arSrc;

    //생성자
    public MultiAdapter(Context context, ArrayList<ListItem> arItem) {
        //인플레이트 준비를 합니다.
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = arItem;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arSrc.size();
    }

    @Override
    public ListItem getItem(int position) {
        // TODO Auto-generated method stub
        return arSrc.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //최초 호출이면 항목 뷰를 생성한다.
        //타입별로 뷰를 다르게 디자인 할 수 있으며 높이가 달라도 상관없다.
        if(convertView == null){


            //인플레이트합니다. 즉 화면에 뿌립니다.
            convertView = mInflater.inflate(R.layout.pop_log_list, parent, false);

        }

        //화면에 뿌린뒤 여기서 각항목에 해당하는 값을 바꿔주는 부분입니다.

        //화면에 뿌린뒤 여기서 각항목에 해당하는 값을 바꿔주는 부분입니다.
        TextView title = (TextView)convertView.findViewById(R.id.log_title);
        title.setText(arSrc.get(position).title);
        TextView user_id = (TextView)convertView.findViewById(R.id.user_id);
        user_id.setText(arSrc.get(position).user_id);


        return convertView;//getCount만큼 반복한다고 했죠?
        //리스트의 갯수만큼 반복하게 됩니다.
    }


}