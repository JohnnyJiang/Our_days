package hk.edu.cuhk.ie.iems5722.group4_our_days;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class SnowView extends View {
    public int MAX_SNOW_COUNT=100;

    Bitmap bitmap_snows = null;

    private final Paint mPaint = new Paint();

    private static final Random random = new Random();

    private Snow[] snows = new Snow[MAX_SNOW_COUNT];

    int view_height = 0;
    int view_width = 0;
    int MAX_SPEED = 200;

    /**
     *
     *
     *
     */
    public SnowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    /**
     *
     *
     */
    public void LoadSnowImage() {
        Resources r = this.getContext().getResources();
        bitmap_snows = ((BitmapDrawable) r.getDrawable(R.drawable.snow))
                .getBitmap();
    }

    /**
     *
     *
     */
    public void SetView(int height, int width) {
        view_height = height - 100;
        view_width = width - 50;

    }

    /**
     *
     *
     */
    public void addRandomSnow() {
        for(int i =0; i< MAX_SNOW_COUNT;i++){
            snows[i] = new Snow(random.nextInt(view_width), 0,random.nextInt(MAX_SPEED));
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < MAX_SNOW_COUNT; i += 1) {
            if (snows[i].coordinate.x >= view_width || snows[i].coordinate.y >= view_height) {
                snows[i].coordinate.y = 0;
                snows[i].coordinate.x = random.nextInt(view_width);
            }
            //
            snows[i].coordinate.y += snows[i].speed;
            //

            //
            int tmp = MAX_SPEED/2 - random.nextInt(MAX_SPEED);
            //
            snows[i].coordinate.x += snows[i].speed < tmp ? snows[i].speed : tmp;
            canvas.drawBitmap(bitmap_snows, ((float) snows[i].coordinate.x),
                    ((float) snows[i].coordinate.y), mPaint);
        }

    }

}
