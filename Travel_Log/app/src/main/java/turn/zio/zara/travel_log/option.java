package turn.zio.zara.travel_log;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class option extends AppCompatActivity {

    SharedPreferences login;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
    }

    public void passChangeView(View view) {
        Intent intent = new Intent(getApplicationContext(), passWordChangeActivity.class);
        startActivityForResult(intent, 2);
    }

    public void push_alram_setting(View view) {
        Intent intent = new Intent(getApplicationContext(), pushAlramSettingActivity.class);
        startActivity(intent);
    }

    public void profile_change(View view) {
        Intent intent = new Intent(getApplicationContext(), profileEditActivity.class);
        startActivityForResult(intent, 1);
    }

    public void bakcMain(View view) {
        finish();
    }

    public void user_logout(View view) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(option.this);
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
}
