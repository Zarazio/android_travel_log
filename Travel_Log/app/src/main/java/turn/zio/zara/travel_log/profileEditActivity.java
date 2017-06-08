package turn.zio.zara.travel_log;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class profileEditActivity extends AppCompatActivity {
    private ImageView profile_Picutre;

    String profile_img;
    String profile_ori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        profile_Picutre = (ImageView)findViewById(R.id.profile_picture);

    }

    public void bakcMain(View view){
        finish();
    }

    public void profile_picture_chagne(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    public void passChangesubmit(View view){

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
