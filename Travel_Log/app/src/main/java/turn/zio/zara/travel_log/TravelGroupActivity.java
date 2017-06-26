package turn.zio.zara.travel_log;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Hoonhoon94 on 2017-06-08.
 */

public class TravelGroupActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_travel_group);
    }

    // 액티비티 전환시 애니메이션 제거
    public void onResume(){
        this.overridePendingTransition(0,0);
        super.onResume();
    }

    public void smart_Cost(View view){ // 여비관리 액티비티로 이동
        Intent intent = new Intent(this, SmartCostAddActivity.class);
        startActivity(intent);
    }

    public void travel_Map(View view){ // 지도 액티비티로 이동
        Intent intent = new Intent(this, TravelMapActivity.class);
        startActivity(intent);
    }

    public void travel_Supply(View view){ // 준비물 액티비티로 이동
        Intent intent = new Intent(this, TravelSupplyActivity.class);
        startActivity(intent);
    }

    public void travel_Story(View view){ // 스토리 액티비티로 이동
        Intent intent = new Intent(this, TravelStoryActivity.class);
        startActivity(intent);
    }
}
