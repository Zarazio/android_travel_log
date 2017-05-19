package turn.zio.zara.travel_log;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static turn.zio.zara.travel_log.R.drawable.camera;


public class TravelCameraActivity extends AppCompatActivity {

    ImageView iv2 ;

    private CameraSurfaceView cameraSurfaceView;
    private FrameLayout frameLayout;
    private String fileName;
    private final String SAVE_FOLDER = "/save_folder" ;


    public static String IMAGE_FILE = "capture.jpg" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_camera);

        String f = getIntent().getStringExtra("img") ;



        iv2 = (ImageView) findViewById(R.id.imageView6) ;
        Drawable alpha = iv2.getDrawable() ;
        alpha.setAlpha(50);
        iv2.setImageURI(Uri.parse(f)) ;

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

//
//                String saveFolderName = "zarazio" ;
//                try{
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss") ;
//                    Date current = new Date();
//                    String dateString = formatter.format(current) ;
//                    File sdCardPath = Environment.getExternalStorageDirectory() ;
//                    File dirs = new File(Environment.getExternalStorageDirectory(),"saveFolderName") ;
//                    if(dirs.mkdirs()) {
//                        Log.d("Camera_Test" , "Directory Created") ;
//                    }
//
//                    +
//                    FileOutputStream out = null ;
//                    String savePicName = sdCardPath.getPath() + "/" + saveFolderName + "/pic" + dateString + ".jpg" ;
//                    out = new FileOutputStream(savePicName) ;
//                    out.write(data);
//                    out.close();
//                 }catch (Excepit)
// getContentResolver() ;

                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length) ;
//                ContentValues values = new ContentValues() ;


//                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") ;
//                values.put(MediaStore.Images.Media.DATA, path+System.currentTimeMillis() +".jpg");

                saveBitmapToJpeg(bitmap,"dd");

               // String image = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap , "","") ;



                //getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,values ) ;


//
//               Uri uri = Uri.parse(image) ;
//                getApplicationContext().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri.fromFile(new File(path))));
//

                    Toast.makeText(getApplicationContext(), "찍은 사진이 저장되었습니다", Toast.LENGTH_LONG).show();

                    camera.startPreview();

            }

        });
    }

    public static void saveBitmapToJpeg(Bitmap bitmap, String name){
        String storage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/" ;

        String fileName = name + ".jpg" ;
        File file_path ;
        try{

            file_path = new File(storage) ;
            if(!file_path.isDirectory()) {
                file_path.mkdirs() ;
            }
            FileOutputStream out = new FileOutputStream(storage + fileName) ;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out) ;

            out.close();
        }catch (FileNotFoundException exception){

        }catch (IOException ex) {

        }

    }
}

