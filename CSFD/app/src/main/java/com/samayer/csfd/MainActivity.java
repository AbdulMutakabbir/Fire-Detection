package com.samayer.csfd;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    String photoFile;
    ImageView imageView;
    TextView textView;

    /////
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";
    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.mimageView);

        if(isMapAPIServicesAvailable()){
            //initMap();
        }
        else{
            Toast.makeText(this, "Google API not available on device for google map", Toast.LENGTH_SHORT).show();
        }

        ////

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        ////


        initCamera();

        Log.d(TAG,"fileIntent to start...");
        //initFile();

    }

    /*private void initFile()
    {
        Button btnGMap = (Button) findViewById(R.id.btnFile);
        btnGMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileActivity.class);
                startActivity(intent);
            }
        });
    }*/

    private void initCamera() {
        Button btnCam = (Button) findViewById(R.id.btnCamera);
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (intent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    Uri photoUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    ////Uri imageUri = getImageUri();
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    //Intent intent = new Intent(MainActivity.this, Test2Activity.class);
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
            }
        });
    }

    //88888888888888888888888888888888888888888888888888888888888888888888888888
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(Uri.parse(imageFilePath));

                ////
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                int counter = 0;
                int j = 0,i;
                int r=0,b=0,g=0;
                for(i = 500; i<bitmap.getHeight()-500;i+=10 )
                {
                    for(j = 500; j<bitmap.getWidth()-500;j+=10){
                        int pixel = bitmap.getPixel(j,i);
                        r+=pixel;



                        //Color.red(pixel);
                        g+=Color.green(pixel);
                        b+=Color.blue(pixel);
                        if(j%100==0||j%101==0||j%102==0){

                        }
                        /*if(Color.red(pixel)>15||Color.green(pixel)>175||Color.blue(pixel)<55)
                        {
                            counter++;
                        }*/
                    }
                }
                textView = findViewById(R.id.textView);
                float red = r*100/(i*j);
                float blue = b*100/(i*j);
                float green = g*100/(i*j);
                if((red>=52 && red <=77) && (green>=48 && green<=63) && (blue>=25 && blue<=49))//(i/10*j/10)/2<counter)
                {

                    Intent intent = new Intent(MainActivity.this, GMapActivity.class);
                    startActivity(intent);
                    textView.setText("Fire Detected");//+red+" "+green+" "+blue+" "+Color.red(bitmap.getPixel(20,30)));//counter+" "+i*j/20);
                }
                else{
                    textView.setText("Fire not Detected"+red+" "+green+" "+blue+" "+i+" "+j+"cal "+r+" "+Color.red(bitmap.getPixel(20,30)));//counter+" "+i*j/20);
                    //Toast.makeText(MainActivity.this,"Fire Not detected. You test a diffrent photo.",Toast.LENGTH_LONG);
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 1003:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //file path of captured image
                    final String filePath = cursor.getString(columnIndex);
                    //file path of captured image
                    File f = new File(filePath);
                    String filename= f.getName();

                    Toast.makeText(MainActivity.this, "Your Path:"+filePath,Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Your Filename:"+filename,Toast.LENGTH_SHORT).show();
                    cursor.close();

                    //Convert file path into bitmap image using below line.
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);


                    //put bitmapimage in your imageview
                    imageView.setImageBitmap(yourSelectedImage);
                }
                break;
        }
    }
*/

    //88888888888888888888888888888888888888888888888888888888888888888888888888

  /*  private void initMap(){
        Button btnGMap = (Button) findViewById(R.id.btnGMap);
        btnGMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GMapActivity.class);
                startActivity(intent);
            }
        });
    }
*/
    public boolean isMapAPIServicesAvailable(){
        Log.d(TAG,"Services(Google API): Checking..");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            //Working fine
            Log.d(TAG, "Services(Google API): Available!");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //User can correct wwe cannot
            Log.d(TAG,"Services(Google API): Error! We cannot fix it, user can.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"Map API not Available!! This won work",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
