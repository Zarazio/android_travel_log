package turn.zio.zara.travel_log;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class profileEditActivity extends AppCompatActivity {
    private ImageView profile_Picutre;
    private TextView change_user_id;
    private TextView change_email;
    private TextView change_number;
    DataBaseUrl dataurl = new DataBaseUrl();

    Spinner userGender;

    String profile_img;
    String profile_ori;

    SharedPreferences login;
    SharedPreferences.Editor editor;
    private boolean regicheck2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        profile_Picutre = (ImageView)findViewById(R.id.profile_picture);
        change_user_id = (TextView) findViewById(R.id.change_user_id);
        change_email = (TextView)findViewById(R.id.change_email);
        change_number = (TextView)findViewById(R.id.change_number);

        userGender = (Spinner)findViewById(R.id.user_gender);
        ArrayAdapter userAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_gender, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGender.setAdapter(userAdapter);
    }

    public void bakcMain(View view){
        finish();
    }

    public void profile_picture_chagne(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    public void profile_submit(View view){
        String c_user_id = change_user_id.getText().toString();
        String c_email = change_email.getText().toString();
        String c_number = change_number.getText().toString();
        String c_user_gencer = userGender.getSelectedItem().toString();
        Log.d("gender",c_user_gencer);
        if(c_user_gencer.equals("남자")){
            c_user_gencer = "0";
        }else{
            c_user_gencer = "1";
        }
        Log.d("gender",c_user_gencer);
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();


        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        String this_user = user.getString("user_id", "0");


        if(c_email.equals(" ")){
            regicheck2=true;
        }else {
            checkrole(c_email,c_number);
        }
        if(c_number.equals(" ")){
            regicheck2=true;
        }else {
            checkrole(c_email,c_number);
        }

        if(c_user_id.isEmpty()){
            regicheck2 = false;
            Toast.makeText(getApplicationContext(),"변경할 아이디를 입력해주세요.",Toast.LENGTH_LONG).show();
            change_user_id.requestFocus();
        }else if(this_user.equals(c_user_id)){
            regicheck2 = false;
            Toast.makeText(getApplicationContext(),"현재 아이디 입니다.",Toast.LENGTH_LONG).show();
            change_user_id.requestFocus();
        }
        if(regicheck2==true){
            Change_user_profile(this_user,c_user_id,c_email,c_number,c_user_gencer);
        }
    }

    private void Change_user_profile(String this_id, String c_user_id, String c_user_email, String c_user_phone, String c_user_gencer){

        class Change_user extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(profileEditActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result",s);
                if(s.equals("idcheck")){
                            Toast.makeText(getApplicationContext(),"이미 존재하는 아이디 입니다.",Toast.LENGTH_SHORT).show();
                    change_user_id.requestFocus();
                }else if(s.equals("success")){
                    Toast.makeText(getApplicationContext(),"회원정보 업데이트 완료 다시 로그인 해주세요.",Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"오류 발생.",Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String this_id = (String)params[0];
                    String c_user_id = (String)params[1];
                    String c_user_email = (String)params[2];
                    String c_user_phone = (String)params[3];
                    String c_user_gencer = (String)params[4];


                    Map<String, String> changeParame = new HashMap<String,String>() ;

                    changeParame.put("this_id",this_id) ;
                    changeParame.put("user_id",c_user_id) ;
                    changeParame.put("user_email",c_user_email) ;
                    changeParame.put("user_phone",c_user_phone) ;
                    changeParame.put("user_gender",c_user_gencer) ;


                    String link= dataurl.getServerUrl()+"change_profile"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(changeParame);

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
        }

        Change_user task = new Change_user();
        task.execute(this_id,c_user_id,c_user_email,c_user_phone, c_user_gencer);
    }

    public void checkrole(String email, String phone){
        regicheck2 = false;
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getApplicationContext(), "이메일형식이 아닙니다.", Toast.LENGTH_LONG).show();
            change_email.requestFocus();
        }else if(!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phone))
        {
            Toast.makeText(getApplicationContext(),"올바른 핸드폰 번호가 아닙니다.",Toast.LENGTH_LONG).show();
            change_number.requestFocus();
        }else{
            regicheck2 = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //이미지 데이터를 비트맵으로 받아온다.
                    //전송할 데이터
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    int width = image_bitmap.getWidth();
                    int height = image_bitmap.getHeight();

                    getImageName(data.getData());
                    Log.d("Dd",profile_ori);
                    Matrix matrix = new Matrix();
                    if(profile_ori.equals("180")){
                        matrix.postRotate(180);
                    }else if(profile_ori.equals("270")){
                        matrix.postRotate(270);
                    }else if(profile_ori.equals("90")){
                        matrix.postRotate(90);
                    }else{
                        matrix.postRotate(0);
                    }
                    //배치해놓은 ImageView에 set

                    //화면에 표시할 데이터

                    Bitmap resizedBitmap = Bitmap.createBitmap(image_bitmap, 0, 0, width, height, matrix, true);

                    profile_Picutre.setImageBitmap(resizedBitmap);
                    profile_Picutre.setScaleType(ImageView.ScaleType.FIT_XY );


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getImageName(Uri data){
        Log.d("Dd",data+"");
        String[] proj={
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION,
        };
        Cursor cursor = this.getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        int column_ori = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);

        profile_img = cursor.getString(column_data);
        profile_ori = cursor.getString(column_ori);
    }

}
