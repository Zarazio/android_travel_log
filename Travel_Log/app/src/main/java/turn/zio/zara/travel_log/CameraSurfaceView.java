package turn.zio.zara.travel_log;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by 하루마다 on 2017-04-29.
 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera camera = null;

    public CameraSurfaceView(Context context) {
        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        int m_resWidth;
        int m_resHeight;

        try {
            Camera.Parameters parameters = camera.getParameters();
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
            m_resWidth = 5312;
            m_resHeight = 2988;

            parameters.setPictureSize(m_resWidth, m_resHeight);
            camera.setParameters(parameters);
            camera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            Log.e("CameraSurfaceView", "Failed to set camera preview.", e);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public boolean takePhoto(Camera.PictureCallback handler){
        if(camera != null){
            camera.takePicture(null, null, handler);
            return true;

        }
        else{
            return false ;
        }
    }



}
