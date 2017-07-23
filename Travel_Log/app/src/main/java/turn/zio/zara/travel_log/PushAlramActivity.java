package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class PushAlramActivity extends AppCompatActivity {

    EditText write;
    SharedPreferences login;
    SharedPreferences.Editor editor;
    DataBaseUrl dataurl = new DataBaseUrl();
    String userkeep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_alram);

        write = (EditText) findViewById(R.id.write);
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();

        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        userkeep = user.getString("user_id", "0");
    }

    public void bakcMain(View v) {
        finish();
        editor.remove("pushAlram");
        editor.commit();
    }

    public void profile_submit(View v) {
        String write_gi = write.getText().toString();
        InsertData in = new InsertData();
        in.execute(write_gi);
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //처음 execute시 실행되는 메소드
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PushAlramActivity.this, "Please Wait", null, true, true);
        }

        //doInBackGround가 종료후 실행되는 메소드
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("result LOGINCHECK : ", s);
            editor.remove("pushAlram");
            editor.commit();
            loading.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String link = "";
                String data = "";
                link = dataurl.getServerUrl() + "insertQA"; //192.168.25.25


                Map<String, String> insertParam = new HashMap<String, String>();

                String write_gi = (String) params[0];
                insertParam.put("write_gi", write_gi);
                insertParam.put("user_id", userkeep);

                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(insertParam);

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
}
