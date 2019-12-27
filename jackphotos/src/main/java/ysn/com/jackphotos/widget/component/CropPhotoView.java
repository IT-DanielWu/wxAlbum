package ysn.com.jackphotos.widget.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * @Author yangsanning
 * @ClassName CropPhotoView
 * @Description 裁剪控件
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class CropPhotoView extends AppCompatImageView {

    private PointF downPoint;
    private PointF middlePoint;
    private Matrix matrix;
    private Matrix tempMatrix;

    private int bitmapWidth;
    private int bitmapHeight;

    private final int MODE_NONE = 0;
    private final int MODE_DRAG = 1;
    private final int MODE_ZOOM = 2;
    private final int MODE_POINTER_UP = 3;
    private int CURR_MODE = MODE_NONE;

    private float lastDistance;

    private Paint frontGroundPaint = new Paint();
    private int targetWidth;
    private int targetHeight;
    private Xfermode xfermode;
    private Rect r;
    private RectF rf;

    private float circleCenterX, circleCenterY;
    private float circleX, circleY;
    private boolean isCutImage;
    private float ratio = 1.0f;

    public CropPhotoView(Context context) {
        super(context);
        setRadius();
    }

    public CropPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRadius();
    }

    /**
     * 设置要剪裁的图片
     */
    public void setBitmapData(Bitmap bitmap) {

        if (bitmap == null) {
            return;
        }

        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        setImageBitmap(bitmap);
        init();
    }

    private void init() {
        downPoint = new PointF();
        middlePoint = new PointF();
        matrix = new Matrix();
        tempMatrix = new Matrix();
        frontGroundPaint.setColor(Color.parseColor("#ac000000"));
        frontGroundPaint.setAntiAlias(true);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        setScaleType(ScaleType.MATRIX);
        post(new Runnable() {
            @Override
            public void run() {
                center();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setRadius();
    }

    private void setRadius() {
        targetWidth = getScreenWidth(getContext());
        targetHeight = (int) (targetWidth * ratio);
        circleCenterX = getWidth() / 2;
        circleCenterY = getHeight() / 2;
        circleX = circleCenterX - targetWidth / 2;
        circleY = circleCenterY - targetHeight / 2;
    }

    public void setRatio(float ratio) {
        if (this.ratio != ratio) {
            this.ratio = ratio;
            setRadius();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isCutImage) {
            return;
        }
        if (rf == null || rf.isEmpty()) {
            r = new Rect(0, 0, getWidth(), getHeight());
            rf = new RectF(r);
        }
        // 画入前景圆形蒙板层
        int sc = canvas.saveLayer(rf, null, Canvas.ALL_SAVE_FLAG);
        // 画入矩形黑色半透明蒙板层
        canvas.drawRect(r, frontGroundPaint);
        // 设置Xfermode，目的是为了去除矩形黑色半透明蒙板层和圆形的相交部分
        frontGroundPaint.setXfermode(xfermode);
        //画入正方形
        float left = circleCenterX - targetWidth / 2;
        float top = circleCenterY - targetHeight / 2;
        float right = circleCenterX + targetWidth / 2;
        float bottom = circleCenterY + targetHeight / 2;
        canvas.drawRect(left, top, right, bottom, frontGroundPaint);

        canvas.restoreToCount(sc);
        // 清除Xfermode，防止影响下次画图
        frontGroundPaint.setXfermode(null);
    }

    /**
     * 截取Bitmap
     */
    public Bitmap clipImage() {
        isCutImage = true;
        Paint paint = new Paint();
        setDrawingCacheEnabled(true);
        Bitmap bitmap = getDrawingCache();
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        int left = -bitmap.getWidth() / 2 + targetWidth / 2;
        int top = -getHeight() / 2 + targetHeight / 2;
        int right = bitmap.getWidth() / 2 + targetWidth / 2;
        int bottom = getHeight() / 2 + targetHeight / 2;
        RectF dst = new RectF(left, top, right, bottom);
        canvas.drawBitmap(bitmap, null, dst, paint);
        setDrawingCacheEnabled(false);
        bitmap.recycle();
        bitmap = null;
        isCutImage = false;
        //返回正方形图片
        return targetBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (matrix == null) {
            return super.onTouchEvent(event);
        }

        float[] values = new float[9];
        matrix.getValues(values);
        float left = values[Matrix.MTRANS_X];
        float top = values[Matrix.MTRANS_Y];
        float right = (left + bitmapWidth * values[Matrix.MSCALE_X]);
        float bottom = (top + bitmapHeight * values[Matrix.MSCALE_Y]);
        float x = 0f;
        float y = 0f;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                CURR_MODE = MODE_DRAG;
                downPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (getDistance(event) > 10f) {
                    CURR_MODE = MODE_ZOOM;
                    midPoint(middlePoint, event);
                    lastDistance = getDistance(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //如果当前模式为拖曳（单指触屏）
                if (CURR_MODE == MODE_DRAG || CURR_MODE == MODE_POINTER_UP) {
                    if (CURR_MODE == MODE_DRAG) {

                        x = event.getX() - downPoint.x;
                        y = event.getY() - downPoint.y;
                        //left靠边
                        if (x + left > circleX) {
                            x = 0;
                        }
                        //right靠边
                        if (x + right < circleX + targetWidth) {
                            x = 0;
                        }
                        //top靠边
                        if (y + top > circleY) {
                            y = 0;
                        }
                        //bottom靠边
                        if (y + bottom < circleY + targetHeight) {
                            y = 0;
                        }
                        matrix.postTranslate(x, y);
                        downPoint.set(event.getX(), event.getY());

                    } else {
                        CURR_MODE = MODE_DRAG;
                        downPoint.set(event.getX(), event.getY());
                    }
                } else {
                    //否则当前模式为缩放（双指触屏）
                    float distance = getDistance(event);
                    if (distance > 10f) {
                        float scale = distance / lastDistance;

                        //left靠边
                        if (left >= circleX) {
                            middlePoint.x = 0;
                        }
                        //right靠边
                        if (right <= circleX + targetWidth) {
                            middlePoint.x = right;
                        }
                        //top靠边
                        if (top >= circleY) {
                            middlePoint.y = 0;
                        }
                        //bottom靠边
                        if (bottom <= circleY + targetHeight) {
                            middlePoint.y = bottom;
                        }
                        tempMatrix.set(matrix);
                        tempMatrix.postScale(scale, scale, middlePoint.x, middlePoint.y);

                        float[] tempValues = new float[9];
                        tempMatrix.getValues(tempValues);
                        float tempLeft = tempValues[Matrix.MTRANS_X];
                        float tempTop = tempValues[Matrix.MTRANS_Y];
                        float tempRight = (tempLeft + bitmapWidth * tempValues[Matrix.MSCALE_X]);
                        float tempBottom = (tempTop + bitmapHeight * tempValues[Matrix.MSCALE_Y]);
                        //靠边预判断
                        if (tempLeft > circleX || tempRight < circleX + targetWidth ||
                            tempTop > circleY || tempBottom < circleY + targetHeight) {
                            return true;
                        }
                        matrix.postScale(scale, scale, middlePoint.x, middlePoint.y);
                        lastDistance = getDistance(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                CURR_MODE = MODE_NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                CURR_MODE = MODE_POINTER_UP;
                break;
            default:
                break;
        }
        setImageMatrix(matrix);
        return true;
    }

    /**
     * 两点的距离
     */
    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 横向、纵向居中
     */
    protected void center() {
        float height = bitmapHeight;
        float width = bitmapWidth;
        float scale = Math.max(targetWidth / width, targetHeight / height);

        float deltaX = -(width * scale - getWidth()) / 2.0f;
        float deltaY = -(height * scale - getHeight()) / 2.0f;
        matrix.postScale(scale, scale);
        matrix.postTranslate(deltaX, deltaY);
        setImageMatrix(matrix);
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
