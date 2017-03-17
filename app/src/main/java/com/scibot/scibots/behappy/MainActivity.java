package com.scibot.scibots.behappy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
//                .setTrackingEnabled(false)
//                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//                .build();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.aniket);
//        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
//        Canvas c = new Canvas(tempBitmap);
//        c.drawBitmap(bitmap,0,0,null);
//        Paint drawPaint = new Paint();
//        drawPaint.setColor(Color.YELLOW);
//        drawPaint.setAntiAlias(true);
//        drawPaint.setStrokeWidth(5);
//        SparseArray<Face> faces = detector.detect(frame);
//        for (int i = 0; i < faces.size(); ++i) {
//            Face face = faces.valueAt(i);
//            for (Landmark landmark : face.getLandmarks()) {
//                int cx = (int) (landmark.getPosition().x *1);
//                int cy = (int) (landmark.getPosition().y * 1);
//                Log.d("tags",cx+cy+"");
//                c.drawCircle(cx, cy, 10, drawPaint);
//
//            }
//        }
//        ImageView img = (ImageView)findViewById(R.id.image);
////        ImageView im = (ImageView)findViewById(R.id.img);
//
//        img.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
//        detector.release();

    }
    private boolean checkCameraHardware(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;

        }
        else{
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


}
