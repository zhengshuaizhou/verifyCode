package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.Random;

/**
 * Created by zhou on 2019/6/14.
 */
public class HanZiView extends AppCompatImageView {
    /**
     * 分割数
     */
    private static final int WIDTH = 9, HEIGHT = 9;
    /**
     * 焦点数
     */
    private static final int COUNT = (WIDTH + 1) * (HEIGHT + 1);
    /**
     * 位图对象
     */
    private Bitmap mBitmap;
    /**
     * 基准点坐标数组
     */
    private float[] matrixOriganal = new float[COUNT * 2];
    /**
     * 变换后点坐标数组
     */
    private float[] matrixMoved = new float[COUNT * 2];

    private float clickX, clickY;// 触摸屏幕时手指的xy坐标

    private Paint origPaint, movePaint, linePaint;// 基准点、变换点和线段的绘制Paint
    /**
     * 避免过多的重绘
     */
    private boolean needDraw = true;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 文字大小
     */
    private int mTextSize;
    /**
     * 文字
     */
    private String mText = "鼎";
    /**
     * 文字颜色
     */
    private int mTextColor;
    /**
     * 矩形区域
     */
    private Rect mBounds;

    /**
     * 获取扭曲点的随机位置
     */
    private float mRandomX, mRandomY;
    /**
     * 字体旋转的角度
     */
    private int DEFAULT_ROTATE_SIZE;
    /**
     * 扭曲的系数
     */
    private float SMUDGE_COEFFICIENT;
    /**
     * 旋转的角度
     */
    private int rotate;
    /**
     * 随机
     */
    private Random r = new Random();

    public HanZiView(Context context) {
        this(context, null);
    }

    public HanZiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HanZiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 实例画笔并设置颜色
        origPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        origPaint.setColor(0x660000FF);
        movePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        movePaint.setColor(0x99FF0000);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xFFFFFB00);
        // 获取自定义属性
        DEFAULT_ROTATE_SIZE = 40;
        SMUDGE_COEFFICIENT = 0.9f;
        mTextSize = dip2px(context,25);
        mTextColor = 0;
        init();

    }

    /**
     * 初始化数据
     */
    private void init() {
        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        if (0 != mTextColor) {
            mPaint.setColor(mTextColor);
        } else {
            mPaint.setColor(Color.BLACK);
        }
        // 随机使用粗体
        mPaint.setFakeBoldText(r.nextBoolean());
        mBounds = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);

    }

    /**
     * 随机生成扭曲位置的XY坐标
     */
    private void initRandomXY() {
        mRandomX = (float) Math.random() * getWidth();
        mRandomY = (float) Math.random() * getHeight();
    }

    /**
     * 设置显示的文字
     *
     * @param text
     */
    public void setText(String text) {
        this.mText = text;
        needDraw = true;
        invalidate();

    }

    /**
     * 获取选中的文字
     * @return
     */
    public String getmText(){
        return mText;
    }

    /**
     * 设置显示的文字的颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mPaint.setColor(color);
        needDraw = true;
        invalidate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;

        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                width = getPaddingLeft() + getPaddingRight() + specSize ;
                break;
            case MeasureSpec.AT_MOST:
                width = getPaddingLeft() + getPaddingRight() + mBounds.width();
                break;
            case MeasureSpec.UNSPECIFIED:
                width = getPaddingLeft() + getPaddingRight() + mBounds.width();
                break;
        }
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                height = getPaddingTop() + getPaddingBottom() + specSize;
                break;
            case MeasureSpec.AT_MOST:
                height = getPaddingTop() + getPaddingBottom() + mBounds.height();
                break;
            case MeasureSpec.UNSPECIFIED:
                height = getPaddingTop() + getPaddingBottom() + mBounds.height();
                break;

        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (needDraw) {
            // 创建带有文字的图片
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mBitmap);
            c.drawText(mText, getWidth() / 2 - mBounds.width() / 2, mBounds.height() / 2 + getHeight() / 2, mPaint);
            // 初始化坐标数组
            int index = 0;
            for (int y = 0; y <= HEIGHT; y++) {
                float fy = mBitmap.getHeight() * y / HEIGHT;

                for (int x = 0; x <= WIDTH; x++) {
                    float fx = mBitmap.getWidth() * x / WIDTH;
                    setXY(matrixMoved, index, fx, fy);
                    setXY(matrixOriganal, index, fx, fy);
                    index += 1;
                }
            }
            initRandomXY();
            smudgeMatrix();
            // 随机旋转角度
            rotate = (int) (Math.floor(Math.random() * DEFAULT_ROTATE_SIZE + 1)
                    - Math.floor(Math.random() * DEFAULT_ROTATE_SIZE * 2 + 1));
        }
        needDraw = false;
        // 绘制网格位图
        canvas.drawBitmapMesh(rotateBitmap(rotate, mBitmap), WIDTH, HEIGHT, matrixMoved, 0, null, 0, null);
        // 绘制参考元素
//        drawGuide(canvas);
    }

    /**
     * 设置坐标数组
     *
     * @param array 坐标数组
     * @param index 标识值
     * @param x     x坐标
     * @param y     y坐标
     */
    private void setXY(float[] array, int index, float x, float y) {
        array[index * 2 + 0] = x;
        array[index * 2 + 1] = y;
    }


    /**
     * 计算变换数组坐标
     */
    private void smudgeMatrix() {
        for (int i = 0; i < COUNT * 2; i += 2) {

            float xOriginal = matrixOriganal[i + 0];
            float yOriginal = matrixOriganal[i + 1];

            float dist_random_to_origin_x = mRandomX - xOriginal;
            float dist_random_to_origin_y = mRandomY - yOriginal;
//            float dist_random_to_origin_x = clickX - xOriginal;
//            float dist_random_to_origin_y = clickY - yOriginal;

            float kv_kat = dist_random_to_origin_x * dist_random_to_origin_x + dist_random_to_origin_y * dist_random_to_origin_y;

            float pull = (float) (1000 * SMUDGE_COEFFICIENT / kv_kat / Math.sqrt(kv_kat));

            if (pull >= 1) {
                matrixMoved[i + 0] = mRandomX;
                matrixMoved[i + 1] = mRandomY;
//                matrixMoved[i + 0] = clickX;
//                matrixMoved[i + 1] = clickY;
            } else {
                matrixMoved[i + 0] = xOriginal + dist_random_to_origin_x * pull;
                matrixMoved[i + 1] = yOriginal + dist_random_to_origin_y * pull;
            }
        }
    }

    /**
     * 旋转图片
     *
     * @param degree
     * @param bitmap
     * @return
     */
    public Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bm;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
