package turn.zio.zara.travel_log;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 하루마다 on 2017-04-29.
 */

public class CameraOverlayView  extends View implements SensorEventListener {

    private static float mXCompassDegree;
    private static float mYCompassDegree;
    private CameraActivity mContext;
    private SensorManager mSensorManager;
    private Sensor mOriSensor;
    private double mlongitude;
    private double mlatitude;
    private int mVisibleDistance = 1;
    private int mWidth;
    private int mHeight;
    private int mShadowXMargin;
    private int mShadowYMargin;
    private Paint mPaint;
    private Paint mThemePaint;
    private Paint mSelectedThemePaint;
    private Paint mShadowPaint;
    private Paint mPopPaint;
    private Paint mTouchEffectPaint;
    private Paint mPointPaint1;
    private Paint mPointPaint2;
    private List<PointF> mPointFList = null;
    private HashMap<Integer, String> mPointHashMap;

    public CameraOverlayView(Context context) {
        super(context);
        mContext = (CameraActivity) context;

        initSensor(context);
        initPaints();
    }

    public void onDraw(Canvas canvas) {
        canvas.save();


        // DB의 레코드를 읽어들이고, drawGrid를 실행시켜 랜드마크 아이템들을 그림
        interpretDB(canvas);

        // 회전된 카메라를 원상복귀함
        canvas.restore();
    }

    private void initPaints() {
        // TODO Auto-generated method stub
        mShadowXMargin = 2;
        mShadowYMargin = 2;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.rgb(238, 229, 222));
        mPaint.setTextSize(40);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.BLACK);
        mShadowPaint.setTextSize(40);
    }

    // 센서 초기화
    // TYPE_ORIENTATION 사용할수 있게 설정
    private void initSensor(Context context) {
        // TODO Auto-generated method stub
        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mOriSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mOriSensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            mXCompassDegree = event.values[0];
            mYCompassDegree = event.values[1];

            this.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private PointF drawGrid(double tAx, double tAy, double tBx, double tBy,
                            Canvas pCanvas, Paint pPaint, String title, String content, int placeY) {
        // TODO Auto-generated method stub

        // 현재 위치와 데이터의 위치를 계산하는 공식
        double mXDegree = (double) (Math.atan((double) (tBy - tAy) / (double) (tBx - tAx)) * 180.0 / Math.PI);
        float mYDegree = mYCompassDegree; // 기기의 기울임각도
        // 4/4분면을 고려하여 0~360도가 나오게 설정
        if (tBx > tAx && tBy > tAy) {
            ;
        } else if (tBx < tAx && tBy > tAy) {
            mXDegree += 180;
        } else if (tBx < tAx && tBy < tAy) {
            mXDegree += 180;
        } else if (tBx > tAx && tBy < tAy) {
            mXDegree += 360;
        }

        // 두 위치간의 각도에 현재 스마트폰이 동쪽기준 바라보고 있는 방향 만큼 더해줌
        // 360도(한바퀴)가 넘었으면 한바퀴 회전한것이기에 360를 빼줌
        if (mXDegree + mXCompassDegree < 360) {
            mXDegree += mXCompassDegree;
        } else if (mXDegree + mXCompassDegree >= 360) {
            mXDegree = mXDegree + mXCompassDegree - 360;
        }

        // 계산된 각도 만큼 기기 정중앙 화면 기준 어디에 나타날지 계산함
        // 정중앙은 90도, 시야각은 30도로 75 ~ 105 사이일때만 화면에 나타남
        float mX = 0;
        float mY = 0;

        if (mXDegree > 75 && mXDegree < 105) {
            if (mYDegree > -180 && mYDegree < 0) {

                mX = (float) mWidth
                        - (float) ((mXDegree - 75) * ((float) mWidth / 30));

                mYDegree = -(mYDegree);

                mY = (float) (mYDegree * ((float) mHeight / 180));


                // 두 위치간의 거리를 계산함
                Location locationA = new Location("Point A");
                Location locationB = new Location("Point B");

                locationA.setLongitude(tAx);
                locationA.setLatitude(tAy);

                locationB.setLongitude(tBx);
                locationB.setLatitude(tBy);

                int distance = (int) locationA.distanceTo(locationB);


                // 데이터에 거리에 들어오면 데이터 보임
                if (distance <= mVisibleDistance * 1000) {
                    if (distance < 1000) {

                        pCanvas.drawText(title, mX - pPaint.measureText(title) / 2
                                + mShadowXMargin, mY+placeY
                                + mShadowYMargin, mShadowPaint);

                        pCanvas.drawText(title, mX - pPaint.measureText(title) / 2, mY+placeY, pPaint);

                        pCanvas.drawText(distance + "m",
                                mX - pPaint.measureText(distance + "m") / 2
                                        + mShadowXMargin, mY+30+placeY + mShadowYMargin, mShadowPaint);

                        pCanvas.drawText(distance + "m",
                                mX - pPaint.measureText(distance + "m") / 2, mY+30+placeY
                                , pPaint);

                    }
                }
            }
        }


        // 현재의 회전되기전의 좌표를 회전된 좌표로 변환한후 반환함
        PointF tPoint = new PointF();

        tPoint.set(mX - mWidth / 2, mY - mHeight / 2);
        return tPoint;
    }
    private void interpretDB(Canvas pCanvas) {

        // TODO Auto-generated method stub
        double tAx, tAy, tBx, tBy;

        arData task = new arData();
        String s = null;

        PointF tPoint;

        mPointFList = new ArrayList<PointF>();
        mPointHashMap = new HashMap<Integer, String>();

        tAx = mlongitude;
        tAy = mlatitude;

        try {
            s = task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("result",s);
        String[][] parsedata = new String[0][5];

        String title;
        String content;
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][6];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("test_code");
                parsedata[i][1] = jobject.getString("title");
                parsedata[i][2] = jobject.getString("content");
                parsedata[i][3] = jobject.getString("longitude");
                parsedata[i][4] = jobject.getString("latitude");
                parsedata[i][5] = jobject.getString("myx");

                title = parsedata[i][1];
                content = parsedata[i][2];
                tBx = Double.parseDouble(parsedata[i][3]);
                tBy = Double.parseDouble(parsedata[i][4]);

                int placeY = Integer.parseInt( parsedata[i][5]);

                tPoint = drawGrid(tAx, tAy, tBx, tBy, pCanvas, mPaint, title, content,placeY);

                mPointFList.add(tPoint);
                mPointHashMap.put(i, content);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    class arData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try{


                String link="http://211.211.213.218:8084/android/ardata"; //92.168.25.25
                HttpClient.Builder http = new HttpClient.Builder("GET", link);

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
    // 카메라 액티비티에서 오버레이 화면 크기를 설정함
    public void setOverlaySize(int width, int height) {
        // TODO Auto-generated method stub
        mWidth = width;
        mHeight = height;

    }
    //핸재위치 갱신
    public void setCurrentPoint(double longitude, double latitude) {

        mlongitude = longitude;
        mlatitude = latitude;
    }

    public void viewDestory() {
        mSensorManager.unregisterListener(this);

    }
}
