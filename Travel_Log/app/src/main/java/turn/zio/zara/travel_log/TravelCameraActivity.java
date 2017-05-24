package turn.zio.zara.travel_log;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class TravelCameraActivity extends AppCompatActivity {

    ImageView iv2 ;

    private CameraSurfaceView cameraSurfaceView;
    private FrameLayout frameLayout;
    private String fileName;
    private final String SAVE_FOLDER = "/save_folder" ;

    String action;
    public static String IMAGE_FILE = "capture.jpg" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_camera);

        action = getIntent().getStringExtra("action");

        if(action.equals("1")) {
            String f = getIntent().getStringExtra("img");
            iv2 = (ImageView) findViewById(R.id.imageView6);
            Drawable alpha = iv2.getDrawable();
            alpha.setAlpha(50);
            iv2.setImageURI(Uri.parse(f));
        }

        cameraSurfaceView = new CameraSurfaceView(getApplicationContext());
        frameLayout = (FrameLayout) findViewById(R.id.travel_camera_frame);
        frameLayout.addView(cameraSurfaceView);

        Intent i = getIntent() ;
        // File f = (File)i.getExtras().getParcelable("img") ;
    }


    public void takePhoto(View view) {

        cameraSurfaceView.takePhoto(new Camera.PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {

                String path ;
                String pathState = Environment.getExternalStorageState() ;

                if(pathState.equals(Environment.MEDIA_MOUNTED)) {
                     path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/" ;
                    File file = new File(path) ;

                    if(!file.exists())
                        file.mkdirs() ;
                }
                else {
                    Toast.makeText(getApplicationContext(), "sd card 인식 실패", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length) ;
               saveBitmapToJpeg(bitmap,System.currentTimeMillis()+"_Travel_log");

               // String image = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap , "","") ;

                    Toast.makeText(getApplicationContext(), "찍은 사진이 저장되었습니다", Toast.LENGTH_LONG).show();
                    if(action.equals("1")){
                        camera.startPreview();
                    }
                     else {
                        camera.stopPreview();
                        finish();
                    }

            }
        });
    }

    public static void saveBitmapToJpeg(Bitmap bitmap, String name){
        String storage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/" ;

        String fileName =  name + ".jpg";
        File file_path ;

        file_path = new File(storage) ;
        try{
            if(!file_path.isDirectory()) {
                file_path.mkdirs() ;
            }
            FileOutputStream out = new FileOutputStream(storage + fileName) ;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out) ;
            out.flush();
            out.close();
        }catch (FileNotFoundException exception){

        }catch (IOException ex) {

        }

    }
}

