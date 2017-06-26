package turn.zio.zara.travel_log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static turn.zio.zara.travel_log.TravelListActivity.select_group_Code;

/**
 * Created by Hoonhoon94 on 2017-06-08.
 */

public class SmartCostAddActivity extends AppCompatActivity {
    private ListViewDialog mDialog;
    SharedPreferences travelStory;
    String selectGroupCode;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_smart_cost_add);
        travelStory = getSharedPreferences("title", MODE_PRIVATE);
        selectGroupCode = travelStory.getString("selectgroupCode", "0");
        String selectTitle = travelStory.getString("selectTitle", "0");
        Log.d("smartcostadd", selectGroupCode);
        Log.d("smartcostadd",select_group_Code);
        Log.d("smartcost, 선택한 제목", selectTitle);
        TextView travelName = (TextView) findViewById(R.id.travelTitle); // 텍스트뷰 객체 선언
        travelName.setText(selectTitle); // 텍스트뷰에 데이터를 넣음
    }

    // 액티비티 전환시 애니메이션 제거
    public void onResume() {
        this.overridePendingTransition(0, 0);
        super.onResume();
    }

    public void travel_Story(View view) {
        Intent intent = new Intent(this, TravelStoryActivity.class);
        startActivity(intent);
    }

    public void travel_Map(View view) {
        Intent intent = new Intent(this, TravelMapActivity.class);
        startActivity(intent);
    }

    public void travel_Supply(View view) {
        Intent intent = new Intent(this, TravelSupplyActivity.class);
        startActivity(intent);
    }

    public void travel_Group(View view) {
        Intent intent = new Intent(this, TravelGroupActivity.class);
        startActivity(intent);
    }

    public void selectInsert(View v) {
        switch (v.getId()) {
            case R.id.selectInsert:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }

    private void showListDialog() {

        String[] item = getResources().getStringArray(R.array.list_dialog_money_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String>(listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener() {


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub
                if(selectGroupCode.equals(select_group_Code)) {
                    if (position == 0) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(intent, position);
                    } else if (position == 1) {
                        Intent intent = new Intent(getApplicationContext(), InsertCoinSmsActivity.class); // 문자로 입력 작성화면으로 이동
                        startActivityForResult(intent, position);
                    } else if (position == 2) {
                        Intent intent = new Intent(getApplicationContext(), InsertCoinActivity.class); // 직접입력 작성화면으로 이동
                        startActivityForResult(intent, position);
                    } else if (position == 3) {
                        mDialog.dismiss();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "현재 진행 중인 일정이 아닙니다.", Toast.LENGTH_LONG).show();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}