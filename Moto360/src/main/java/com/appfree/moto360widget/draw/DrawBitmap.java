package com.appfree.moto360widget.draw;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.appfree.moto360widget.R;

import java.io.IOException;

/**
 * Created by truongtvd on 7/30/14.
 */
public class DrawBitmap {

    public static Bitmap drawCircleBitmap(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("content", Context.MODE_PRIVATE);
        String bg = sharedPreferences.getString("background", "");
        Bitmap bmp = null;
        if (TextUtils.isEmpty(bg)) {
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
        } else {
            Uri uri = Uri.parse(bg);
            try {
                bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bmp = bmp.copy(bmp.getConfig(), true);
        Bitmap myBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(myBitmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bmp.getWidth(),
                bmp.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(myBitmap.getWidth() / 2,
                myBitmap.getHeight() / 2, myBitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmp, rect, rect, paint);
        return myBitmap;
    }
}
