package com.samayer.csfd;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.VolumeShaper;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.cast.CastRemoteDisplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private Boolean CameraPermissionGranted = false;
    private Camera camera;
    private FrameLayout cameraframe;
    private ShowCamera showCamera;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraframe = (FrameLayout)findViewById(R.id.frameCamera);

        getCameraPermission();
    }

    private void initCam(){
        camera = Camera.open();
        showCamera = new ShowCamera(this,camera);
        cameraframe.addView(showCamera);

        Button btnCapture = (Button)findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePic(view);
                Toast.makeText(CameraActivity.this, "Image captured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCameraPermission(){
        String permissions[] = {CAMERA_PERMISSION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED){
                CameraPermissionGranted = true;
                initCam();
        }
        else{
            ActivityCompat.requestPermissions(this,permissions,1002);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CameraPermissionGranted = false;
        switch(requestCode){
            case 1002: {
                if(grantResults.length>0){
                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            CameraPermissionGranted = false;
                            return;
                        }
                    }
                    CameraPermissionGranted = true;
                    initCam();
                }
            }
        }
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bits, Camera camera) {
            File image = getImageFileOutput();
            if(image == null){ return; }
            else{
                try{
                    FileOutputStream fos = new FileOutputStream(image);
                    fos.write(bits);
                    fos.close();
                    camera.startPreview();
                } catch (FileNotFoundException e) {
                    Log.d(TAG,"onPictureTaken(): "+e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG,"onPictureTaken(): "+e.getMessage());
                }
            }
        }
    };

    private File getImageFileOutput(){
        /*String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        else{
            File folder_location = new File(Environment.getExternalStorageDirectory()+File.separator+"CSFD_Pic");
            if(!folder_location.exists()){
                folder_location.mkdirs();
            }
            //Date date = new Date();
            //String stringDate = DateFormat.getDateTimeInstance().format(date);
            File outputFile = new File(folder_location,"test123.jpg");
            return outputFile;
        }*/
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName,".jpg", storageDir);
        } catch (IOException e) {
            Log.d(TAG,"getImageFileOutput(): "+e.getMessage());
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void capturePic(View v){
        if(camera != null){
            camera.takePicture(null,null,mPictureCallback);
        }
    }
}







//**************************************************************************************************



class ShowCamera extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = "ShowCamera";
    private Camera camera;
    private SurfaceHolder holder;

    public ShowCamera(Context context,Camera camera) {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Camera.Parameters param = camera.getParameters();

        List<Camera.Size> sizes = param.getSupportedPictureSizes();
        Camera.Size s = null;

        for(Camera.Size size : sizes)
        {
            s = size;
        }
        param.setPictureSize(s.width,s.height);
        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            param.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            param.setRotation(90);
        }
        else{
            param.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            param.setRotation(0);
        }


        camera.setParameters(param);

        try{
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }catch(IOException e){
            Log.d(TAG,"Camera: SurfaceCreate Error..."+e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        camera.stopPreview();
        camera.release();
    }
}