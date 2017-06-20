package turn.zio.zara.travel_log;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 하루마다 on 2017-06-14.
 */

class MainAdapter extends BaseAdapter implements OnMapReadyCallback {

    private final int layout;
    Context context;

    LayoutInflater mInflater;
    String[] title;
    LayoutInflater inf;
    String[] Content;
    String[] date;
    String[] writeuser_id;
    String[] file_type;
    String[] adress;
    String[] file_Content;

    Drawable drawable;

    private Bitmap[] images;
    private boolean flag = true;
    private GoogleMap mMap;
    private String kmlFile;
    private InputStream is;
    String[] step_log_code;
    String imageURL = "http://211.211.213.218:8084/android/resources/upload/";

    String titletext;
    String Contenttext;
    String datetext;
    String writeuser_idtext;
    String file_typetext;
    String adresstext;
    String file_Contenttext;
    String step_log_codetext;

    ArrayList<String> location = new ArrayList<String>();
    //생성자
    public MainAdapter(Context context, int layout, String[] title, String[] Content, String[] date,
                       String[] writeuser_id, String[] file_type, String[] adress, String[] file_Content, String[] step_log_code) {
        //인플레이트 준비를 합니다.
        this.context = context;
        this.layout = layout;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.title = title;
        this.Content = Content;
        this.date = date;
        this.writeuser_id = writeuser_id;
        this.file_type = file_type;
        this.adress = adress;
        this.file_Content = file_Content;
        this.step_log_code = step_log_code;

        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return title[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void image(Bitmap[] images){
        this.images = images;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //최초 호출이면 항목 뷰를 생성한다.
        //타입별로 뷰를 다르게 디자인 할 수 있으며 높이가 달라도 상관없다.
        if(convertView == null)
            //인플레이트합니다. 즉 화면에 뿌립니다.
            convertView = inf.inflate(layout, null);

        //화면에 뿌린뒤 여기서 각항목에 해당하는 값을 바꿔주는 부분입니다.
        TextView titles = (TextView)convertView.findViewById(R.id.log_title);
        titles.setText(title[position]);
        TextView Contents = (TextView)convertView.findViewById(R.id.log_cotennt);
        Contents.setText(Content[position]);
        TextView log_place = (TextView)convertView.findViewById(R.id.log_place);
        log_place.setText(adress[position]);
        TextView log_date = (TextView)convertView.findViewById(R.id.log_date);
        log_date.setText(date[position]);
        TextView user_id = (TextView)convertView.findViewById(R.id.user_id);
        user_id.setText(writeuser_id[position]);

        ImageView iv = (ImageView)convertView.findViewById(R.id.log_picture);
        LinearLayout picView = (LinearLayout)convertView.findViewById(R.id.log_picture_Linear);
        LinearLayout map = (LinearLayout) convertView.findViewById(R.id.MapContainer);
        LinearLayout text = (LinearLayout) convertView.findViewById(R.id.text);

        float mScale = context.getResources().getDisplayMetrics().density;
        if(file_type[position].equals("0")) {
            int calHeight = (int)(60*mScale);
            picView.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            map.setVisibility(View.GONE);
            Contents.setHeight(calHeight);
            flag = true;
        }
        else if (file_type[position].equals("1")) {
            int calHeight = (int)(40*mScale);
            picView.setVisibility(View.VISIBLE);
            map.setVisibility(View.GONE);
            iv.setImageBitmap(images[position]);
            Contents.setHeight(calHeight);
            flag = true;
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
        } else if(file_type[position].equals("2")){
            int calHeight = (int)(40*mScale);
            picView.setVisibility(View.VISIBLE);
            map.setVisibility(View.GONE);
            Contents.setHeight(calHeight);
            flag = true;
            drawable = context.getResources().getDrawable(R.drawable.voice);
            iv.setImageDrawable(drawable);
        }else if(file_type[position].equals("3")){
            picView.setVisibility(View.GONE);
            map.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
            titletext = title[position];
            Contenttext = Content[position];
            datetext = date[position];
            writeuser_idtext = writeuser_id[position];
            file_typetext = file_type[position];
            adresstext = adress[position];
            kmlFile = file_Content[position];
            step_log_codetext = step_log_code[position];
            if(flag){
                FragmentTransaction fragmentTransaction = ((Activity) context).getFragmentManager().beginTransaction();
                MapFragment mMapFragment = MapFragment.newInstance();
                fragmentTransaction.add(R.id.MapContainer, mMapFragment);
                fragmentTransaction.commit();
                mMapFragment.getMapAsync(this);
                selFile();
                flag=false;
                if(mMap != null){
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            Log.d("dd","dd");
                            Intent intent = new Intent(context, LifeLogViewActivity2.class);
                            intent.putExtra("board_Code",titletext);
                            intent.putExtra("board_Title",titletext);
                            intent.putExtra("board_Content",Contenttext);
                            intent.putExtra("board_Date",datetext);
                            intent.putExtra("user_id",writeuser_idtext);
                            intent.putExtra("file_Type",file_typetext);
                            intent.putExtra("file_Content", kmlFile);
                            intent.putExtra("step_log_code", step_log_codetext);
                            context.startActivity(intent);
                        }
                    });
                }
            }
        }

        return convertView;//getCount만큼 반복한다고 했죠?
        //리스트의 갯수만큼 반복하게 됩니다.
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    private void selFile(){

        class loginData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            private InputStream is =null;
            KmlLayer layer  = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("result",s);

                if(mMap != null) {

                    String[] coo = location.get((location.size()-1/2)).toString().split(",");
                    LatLng startPoint = new LatLng(Double.parseDouble(coo[1]), Double.parseDouble(coo[0]));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                    mMap.animateCamera(zoom);
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
                }

            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String urltext = imageURL + "step_Log/" + kmlFile;
                    Log.d("url", urltext);
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
                            int i= 0;
                            flag = true;
                            String tmpCoordin = column;
                            tmpCoordin = tmpCoordin.replaceAll("<coordinates>", "");
                            tmpCoordin = tmpCoordin.replaceAll("</coordinates>", "");
                            if(tmpCoordin.trim().equals("</LineString>")){
                                break;
                            }
                            location.add(tmpCoordin.trim());
                        }

                    }

                }  catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "success";
            }
        }

        loginData task = new loginData();
        task.execute();
    }

}
