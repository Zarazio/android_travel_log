package turn.zio.zara.travel_log;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Life_LogActivity extends AppCompatActivity {

    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOrient = null;

    FileInputStream mFileInputStream;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    private EditText log_Content;
    private EditText log_Title;
    private TextView user_id;
    private ImageView image;
    private ListViewDialog mDialog;
    private TextView place_info;

    List<Object> hash = new ArrayList<Object>();

    LocationManager lm;

    SharedPreferences login;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_log);

        log_Content = (EditText) findViewById(R.id.view_Travel_logTxt);
        log_Title = (EditText) findViewById(R.id.view_Travel_logTitle);
        place_info = (TextView) findViewById(R.id.user_place_info);
        user_id = (TextView) findViewById(R.id.user_profile_id);
        image = (ImageView)findViewById(R.id.view_Travel_Picture);

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();

        Log.d("lmage", image+"");

        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        String userkeep = user.getString("user_id", "0");

        user_id.setText(userkeep);
        /*log.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                //텍스트가 변경 될때마다 Call back
                 getSpans(log.getText().toString(), '#');


                for(int i =0; i<hash.size();i= i+2) {
                    Log.d("추출", hash.get(i).toString());
                    Log.d("추출", hash.get(i+1).toString());
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                //텍스트 입력이 모두 끝았을때 Call back
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                //텍스트가 입력하기 전에 Call back
            }
        });*/


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

    public void getSpans(String body, char prefix) {

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();

            hash.add(currentSpan[0]);
            hash.add(currentSpan[1]);
        }
    }

    public void getImageName(Uri data){
        Log.d("Dd",data+"");
        String[] proj={
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION
        };
        Cursor cursor = this.getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        int column_orientation= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);

        mImgPath = cursor.getString(column_data);
        mImgTitle = cursor.getString(column_title);
        mImgOrient = cursor.getString(column_orientation);

    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도

            place_info.setText(getAddress(latitude, longitude));
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

    public void Travel_log_submit(View view){
        String Title = log_Title.getText().toString();
        String Content = log_Content.getText().toString();
        Travel_log_Write(Title, Content);
    }

    public void addFile(View v){
        switch(v.getId()){
            case R.id.addFile:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }

    private void Travel_log_Write(final String Title, String Contnet){

        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Life_LogActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result",s);

                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection conn = null;
                StringBuilder sb = new StringBuilder();
                try {
                    String log_Title = params[0];
                    String log_Content = params[1];


                    RequestParams insertData = new RequestParams();
                    insertData.put("log_Title",log_Title);
                    insertData.put("log_Content",log_Content);
                    if(mImgPath !=null){
                        File file = new File(mImgPath);
                        mFileInputStream = new FileInputStream(mImgPath);
                        insertData.put("image",mImgPath);
                    }

                    URL url = new URL("http://211.211.213.218:8084/android/insertLog"); //요청 URL을 입력
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)
                    conn.setUseCaches(false);

                    conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"log_Title\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(log_Title.getBytes("UTF-8"));
                    dos.writeBytes( lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"log_Content\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(log_Content.getBytes("UTF-8"));
                    dos.writeBytes( lineEnd);

                    if(mImgPath != null) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + mImgPath + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        int bytesAvailable = mFileInputStream.available();
                        int maxBufferSize = 1024;
                        int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                        byte[] buffer = new byte[bufferSize];
                        int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = mFileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                        }
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    }else{
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + null + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                    }
                    conn.connect();
                    if(mFileInputStream != null){
                        mFileInputStream.close();
                    }
                    Log.e("Test" , "File is written");

                    dos.flush(); // finish upload...
                    int ch;
                    InputStream is = conn.getInputStream();
                    StringBuffer b =new StringBuffer();
                    while( ( ch = is.read() ) != -1 ){
                        b.append( (char)ch );
                    }
                    String s=b.toString();
                    dos.close();

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return sb.toString();
            }
        }

        insertData task = new insertData();
        task.execute(Title,Contnet);
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
                    Intent intent = new Intent(getApplicationContext(), TravelCameraActivity.class);
                    intent.putExtra("action", "0");
                    startActivityForResult(intent, position);
                } else if (position == 1){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, position);
                } else if(position == 2){

                } else if(position == 3){

                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
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

    public void bakcMain(View view){
        finish();
    }
    public void HashTagAdd(View view){
        log_Content.append("#");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode== Activity.RESULT_OK) {
                try {
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    getImageName(data.getData());
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }else if (requestCode == 0){
            if(resultCode== Activity.RESULT_OK) {
                String path = data.getStringExtra("filepath");
                String degrees = data.getStringExtra("degrees");
                String file_name = data.getStringExtra("file_name");
                Log.d("img",path);
                mImgPath = path;
                mImgOrient = degrees;
                mImgTitle = file_name;
                File imgFile = new  File(path);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);
            }
        }
    }
}
