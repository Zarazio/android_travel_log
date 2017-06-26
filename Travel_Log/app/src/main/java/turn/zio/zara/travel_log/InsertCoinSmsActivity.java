package turn.zio.zara.travel_log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by Hoonhoon94 on 2017-06-21.
 */

public class InsertCoinSmsActivity extends AppCompatActivity {
    public static String Switch_Stat;
    SharedPreferences smartCost;
    SharedPreferences.Editor editor;
    Switch optionSwitch;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_insert_coin_sms);
        optionSwitch = (Switch)findViewById(R.id.SmsOptionSwitch);

        smartCost = getSharedPreferences("switchOP", MODE_PRIVATE);
        Switch_Stat =  smartCost.getString("switchOP", "0");

        if(Switch_Stat.equals("true")){
            optionSwitch.setChecked(true);
        }else {
            optionSwitch.setChecked(false);
        }

        // 스위치의 체크 이벤트를 위한 리스너 등록
        optionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    Switch_Stat = "true";
                }else{
                    Switch_Stat = "false";
                }
            }
        });
    }
    public void sms_option_submit(View view){
        smartCost = getSharedPreferences("switchOP", MODE_PRIVATE);
        editor = smartCost.edit();
        editor.putString("switchOP", Switch_Stat);
        editor.commit();

        Intent intent = new Intent(this, TravelStoryActivity.class);
        startActivity(intent);
    }
    public void bakcMain(View view){
        finish();
    }
}