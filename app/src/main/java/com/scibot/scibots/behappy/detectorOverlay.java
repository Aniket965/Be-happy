package com.scibot.scibots.behappy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.vision.face.Landmark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aniket sharma on 18-03-2017.
 */

public class detectorOverlay extends GraphicOverlay.Graphic {
    Paint drawPaint;
   float cx;
    float cy;
List <Landmark> lm;


    public detectorOverlay(GraphicOverlay overlay) {
        super(overlay);
        drawPaint = new Paint();
        drawPaint.setColor(Color.YELLOW);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
    }

    void updatelandmark(List<Landmark> mlm) {
       lm= mlm;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        for(Landmark landmark :lm){
            cx = (int) (landmark.getPosition().x *1);
            cy = (int) (landmark.getPosition().y * 1);


            canvas.drawCircle(cx,cy,10,drawPaint);
        }
    }
}
