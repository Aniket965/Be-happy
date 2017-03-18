package com.scibot.scibots.behappy;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "BeHappy";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private boolean mIsFrontFacing = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);

        final Button button = (Button) findViewById(R.id.flipButton);
        button.setOnClickListener(mFlipButtonListener);
        if (savedInstanceState != null) {
            mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
        }
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }





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
    /*checks for camera hardware*/
//    private boolean checkCameraHardware(Context context){
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//            return true;
//        }
//        else{
//            return false;
//        }
//    }
//
//    /** A safe way to get an instance of the Camera object. */
//    public static Camera getCameraInstance(){
//        Camera c = null;
//        try {
//            c = Camera.open(); // attempt to get a Camera instance
//        }
//        catch (Exception e){
//            // Camera is not available (in use or does not exist)
//        }
//        return c; // returns null if camera is unavailable
//    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };


    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };


    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }
    private View.OnClickListener mFlipButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mIsFrontFacing = !mIsFrontFacing;

            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }

            createCameraSource();
            startCameraSource();
        }};







        @NonNull
        private FaceDetector createFaceDetector(Context context) {

            FaceDetector detector = new FaceDetector.Builder(context)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setTrackingEnabled(true)
                    .setMode(FaceDetector.FAST_MODE)
                    .setProminentFaceOnly(mIsFrontFacing)
                    .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                    .build();
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.aniket);
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
            Detector.Processor<Face> processor;
            if (mIsFrontFacing) {

                Tracker<Face> tracker = new FaceTracker(mGraphicOverlay);
                processor = new LargestFaceFocusingProcessor.Builder(detector, tracker).build();
            } else {

                MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
                    @Override
                    public Tracker<Face> create(Face face) {
                        return new FaceTracker(mGraphicOverlay);
                    }
                };
                processor = new MultiProcessor.Builder<>(factory).build();
            }

            detector.setProcessor(processor);

            if (!detector.isOperational()) {

                Log.w(TAG, "Face detector dependencies are not yet available.");

                IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(this,"" , Toast.LENGTH_LONG).show();
                    Log.w(TAG, " ");
                }
            }


            return detector;
        }






        private void createCameraSource() {
            Context context = getApplicationContext();
            FaceDetector detector = createFaceDetector(context);

            int facing = CameraSource.CAMERA_FACING_FRONT;
            if (!mIsFrontFacing) {
                facing = CameraSource.CAMERA_FACING_BACK;
            }
            Display display =  getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            mCameraSource = new CameraSource.Builder(context, detector)
                    .setFacing(facing)
                    .setRequestedPreviewSize(480,800)
                    .setRequestedFps(60.0f)
                    .setAutoFocusEnabled(true)
                    .build();
        }

        private void startCameraSource() {
            int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                    getApplicationContext());
            if (code != ConnectionResult.SUCCESS) {
                Dialog dlg =
                        GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, code, RC_HANDLE_GMS);
                dlg.show();
            }

            if (mCameraSource != null) {
                try {
                    mPreview.start(mCameraSource, mGraphicOverlay);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to start camera source.", e);
                    mCameraSource.release();
                    mCameraSource = null;
                }
            }
        }
}



