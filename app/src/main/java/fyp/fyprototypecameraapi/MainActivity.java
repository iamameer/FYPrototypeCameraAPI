package fyp.fyprototypecameraapi;
//http://www.vogella.com/tutorials/AndroidCamera/article.html#links-and-literature

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;
    private Button button;

    private boolean isTorchOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // do we have a camera?
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFacingCamera();
            /*if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
            }*/
            camera = Camera.open(cameraId);
        }

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.startPreview();
                camera.takePicture(null, null, new PhotoHandler(getApplicationContext()));
            }
        });
    }

    private int findFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(MainActivity.DEBUG_TAG,"onPause()");
        Camera.Parameters p = camera.getParameters();
        try{
            //if(!isTorchOn){
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();
            isTorchOn = false;
            // }

        }catch (Exception e){
            Log.d(DEBUG_TAG,e.toString());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.DEBUG_TAG,"onResume()");
        Camera.Parameters p = camera.getParameters();
        try{
           // if(isTorchOn){
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
                isTorchOn = true;
            //}
        }catch (Exception e){
            Log.d(DEBUG_TAG,e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MainActivity.DEBUG_TAG,"onStop()");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(MainActivity.DEBUG_TAG,"onDestroy()");
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
