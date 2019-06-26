package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhou on 2019/6/14.
 */
public class ClickVerifyCode extends RelativeLayout implements View.OnTouchListener {

    private Random mRandom;
    private Context context;
    private int parentX;
    private int parentY;
    private View touchView;
    private int startX, startY;
    private String text = "";
    private String str;
    private int num = 0;
    private VerifyListener listener;

    public ClickVerifyCode(Context context) {
        this(context, null);
    }

    public ClickVerifyCode(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClickVerifyCode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attrName = attrs.getAttributeName(i);
            String attrVal = attrs.getAttributeValue(i);
            switch (attrName) {
                case "layout_width":
                    int xdp = Integer.parseInt(attrVal.substring(0, attrVal.indexOf(".")));
                    parentX = dip2px(context, xdp);
                    break;
                case "layout_height":
                    int ydp = Integer.parseInt(attrVal.substring(0, attrVal.indexOf(".")));
                    parentY = dip2px(context, ydp);
                    break;
            }
        }

        init();
    }

    private void init() {
        setOnTouchListener(this);
        mRandom = new Random(System.nanoTime());
        LayoutParams paramsA = new LayoutParams(parentX / 5 * 3, parentY / 2);
        RelativeLayout layoutA = new RelativeLayout(context);
        layoutA.setId(View.generateViewId());
//        layoutA.setBackgroundColor(Color.BLUE);
        addView(layoutA, paramsA);

        LayoutParams paramsB = new LayoutParams(parentX / 5 * 3, parentY / 2);
        paramsB.addRule(RelativeLayout.BELOW, layoutA.getId());
        RelativeLayout layoutB = new RelativeLayout(context);
//        layoutB.setBackgroundColor(Color.RED);
        addView(layoutB, paramsB);

        LayoutParams paramsC = new LayoutParams(parentX / 5 * 2, parentY);
        paramsC.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RelativeLayout layoutC = new RelativeLayout(context);
//        layoutC.setBackgroundColor(Color.GREEN);
        addView(layoutC, paramsC);

        addTextView(layoutA, layoutB, layoutC);

    }

    private void addTextView(RelativeLayout layoutA, RelativeLayout layoutB, RelativeLayout layoutC) {
        str = getRandomJianHan(6);
        Log.e("随机字符串", str);
        int widthA = parentX / 5 * 3;
        int heightA = parentY / 2;
        RelativeLayout.LayoutParams hanziParams1 = new RelativeLayout.LayoutParams(dip2px(context, 40), dip2px(context, 40));
        HanZiView hanzi1 = new HanZiView(context);
        hanzi1.setText(str.substring(0, 1));
        List<int[]> marginA = getRandomMargin(widthA, heightA);
        hanziParams1.setMargins(marginA.get(0)[0], marginA.get(0)[1], 0, 0);
        layoutA.addView(hanzi1, hanziParams1);

        RelativeLayout.LayoutParams hanziParams2 = new RelativeLayout.LayoutParams(dip2px(context, 40), dip2px(context, 40));
        HanZiView hanzi2 = new HanZiView(context);
        hanzi2.setText(str.substring(1, 2));
        hanziParams2.setMargins(marginA.get(1)[0], marginA.get(1)[1], 0, 0);
        layoutA.addView(hanzi2, hanziParams2);

        //B布局两个汉字
        int widthB = parentX / 5 * 3;
        int heightB = parentY / 2;
        RelativeLayout.LayoutParams hanziParamsB1 = new RelativeLayout.LayoutParams(dip2px(context, 40), dip2px(context, 40));
        HanZiView hanziB1 = new HanZiView(context);
        hanziB1.setText(str.substring(2, 3));
        List<int[]> marginB = getRandomMargin(widthB, heightB);
        hanziParamsB1.setMargins(marginB.get(0)[0], marginB.get(0)[1], 0, 0);
        layoutB.addView(hanziB1, hanziParamsB1);

        RelativeLayout.LayoutParams hanziParamsB2 = new RelativeLayout.LayoutParams(dip2px(context, 40), dip2px(context, 40));
        HanZiView hanziB2 = new HanZiView(context);
        hanziB2.setText(str.substring(3, 4));
        hanziParamsB2.setMargins(marginB.get(1)[0], marginB.get(1)[1], 0, 0);
        layoutB.addView(hanziB2, hanziParamsB2);

        //C布局两个汉字
        int widthC = parentX / 5 * 2;
        int heightC = parentY;
        RelativeLayout.LayoutParams hanziParamsC = new RelativeLayout.LayoutParams(dip2px(context, 40), dip2px(context, 40));
        HanZiView hanziC = new HanZiView(context);
        hanziC.setText(str.substring(4, 5));
        List<int[]> marginC = getRandomMargin(widthC, heightC);
        hanziParamsC.setMargins(marginC.get(0)[0], marginC.get(0)[1], 0, 0);
        layoutC.addView(hanziC, hanziParamsC);

        RelativeLayout.LayoutParams hanziParamsC2 = new RelativeLayout.LayoutParams(dip2px(context, 40), dip2px(context, 40));
        HanZiView hanziC2 = new HanZiView(context);
        hanziC2.setText(str.substring(5, 6));
        hanziParamsC2.setMargins(marginC.get(1)[0], marginC.get(1)[1], 0, 0);
        layoutC.addView(hanziC2, hanziParamsC2);
        Log.e("===========", "绘制完毕");
    }

    /**
     * 获取指定长度随机简体中文
     *
     * @param len int
     * @return String
     */
    private static String getRandomJianHan(int len) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos; // 定义高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); //获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); //获取低位值
            byte[] b = new byte[2];
            b[0] = (Integer.valueOf(hightPos).byteValue());
            b[1] = (Integer.valueOf(lowPos).byteValue());
            try {
                str = new String(b, "GBk"); //转成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            ret.append(str);
        }
        return ret.toString();
    }

    public static int dip2px(Context context, float dpVale) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }

    /**
     * 得到一个布局内不重合的两个字的margin集合
     *
     * @param width
     * @param height
     * @return
     */
    private List<int[]> getRandomMargin(int width, int height) {
        int[] margin1 = getRandom(width, height);
        int[] margin2 = getRandom(width, height);
        boolean can = Math.abs(margin1[0] - margin2[0]) < 40 || Math.abs(margin1[1] - margin2[1]) < 40;
        while (can) {
            margin1 = getRandom(width, height);
            margin2 = getRandom(width, height);
            can = Math.abs(margin1[0] - margin2[0]) < 40 || Math.abs(margin1[1] - margin2[1]) < 40;
        }
        List<int[]> list = new ArrayList<>();
        list.add(margin1);
        list.add(margin2);
        return list;
    }

    /**
     * 获取一组随机的margin
     *
     * @param width
     * @param height
     * @return
     */
    private int[] getRandom(int width, int height) {
        int marginLeft = mRandom.nextInt(width);
        int marginTop = mRandom.nextInt(height);
        if (width > height) {
            if (marginLeft <= dip2px(context, 10)) {
                marginLeft = dip2px(context, 10);
            } else if (marginLeft >= width - dip2px(context, 40)) {
                marginLeft = width - dip2px(context, 40);
            }
            if (marginTop <= dip2px(context, 10)) {
                marginTop = dip2px(context, 10);
            } else if (marginTop >= height - dip2px(context, 40)) {
                marginTop = height - dip2px(context, 40);
            }
            int[] margin = new int[2];
            margin[0] = marginLeft;
            margin[1] = marginTop;
            return margin;
        } else {
            if (marginLeft >= width - dip2px(context, 40)) {
                marginLeft = width - dip2px(context, 50);
            }
            if (marginTop <= dip2px(context, 20)) {
                marginTop = dip2px(context, 20);
            } else if (marginTop >= height - dip2px(context, 40)) {
                marginTop = height - dip2px(context, 50);
            }
            int[] margin = new int[2];
            margin[0] = marginLeft;
            margin[1] = marginTop;
            return margin;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                startX = (int) event.getX();
                startY = (int) event.getY();
                num++;
                if (hasView(startX, startY)) {//如果点击位置已经有view
                    text += ((HanZiView) touchView).getmText();
                    Log.e("==", "点击位置正确");
                } else {
                    text += "";
                    Log.e("==", "点击位置失败");
                }
                if (num == 4) {
                    addItem(startX, startY, String.valueOf(num));
                    if (str.substring(1, 5).equals(text)) {
                        listener.success();
                    } else {
                        listener.fail();
                        clearMarkView();
                        num = 0;
                        text = "";
                    }
                } else if (num < 4) {
                    addItem(startX, startY, String.valueOf(num));
                }
                touchView = null;
                Log.e("点选文字", text);
                Log.i("******点击的位置--x-", startX + "*----y" + startY);
                break;

        }
        return true;
    }

    /**
     * 判断点击位置是否有子view
     *
     * @param x
     * @param y
     * @return
     */
    private boolean hasView(int x, int y) {
        //循环获取子view，判断xy是否在子view上，即判断是否按住了子view
        for (int index = 0; index < this.getChildCount(); index++) {
            if (this.getChildAt(index) instanceof RelativeLayout) {
                Log.e("RelativeLayout循环", "=====次");
                RelativeLayout layout = (RelativeLayout) this.getChildAt(index);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    Log.e("image循环", "====次");
                    View view = layout.getChildAt(i);
                    int left = (int) view.getX();
                    int top = (int) view.getY();
                    int right = view.getRight();
                    int bottom = view.getBottom();
                    if (layout.getY() != 0) {
                        top += layout.getY();
                        bottom += layout.getY();
                    }
                    if (layout.getX() != 0) {
                        left += layout.getX();
                        right += layout.getX();
                    }
                    Rect rect = new Rect(left, top, right, bottom);
                    boolean contains = rect.contains(x, y);
                    //如果是与子view重叠则返回真,表示已经有了view不需要添加新view了
                    if (contains) {
                        touchView = view;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addItem(int x, int y, String num) {
        TextView view = null;
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        view = new TextView(context);
        view.setText(num);
        view.setTextColor(Color.RED);
        view.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_text));
        view.setGravity(Gravity.CENTER);
        view.setLayoutParams(new LinearLayout.LayoutParams(dip2px(context, 30), dip2px(context, 30)));
        int w = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();
        //标签在右面 点击位置 x-标签宽度   右边的标签并不是以圆点开始的  而是以左边的wei
        params.leftMargin = x - width + 10;
        params.topMargin = y - height / 2;

        //上下位置在视图内
        if (params.topMargin <= 0) {
            params.topMargin = 0;
        } else if ((params.topMargin + height) > getHeight()) {
            params.topMargin = getHeight() - height;
        }
        if (params.leftMargin <= 0) {
            params.leftMargin = 0;
        }
        if (params.rightMargin >= getWidth()) {
            params.rightMargin = getWidth();
        }

        this.addView(view,getChildCount(),params);
    }

    public String getVerifyText() {
        return str.substring(1, 5);
    }

    public interface VerifyListener {
        void success();

        void fail();
    }

    public void setOnVerifyListener(VerifyListener listener) {
        this.listener = listener;
    }

    public void clearMarkView(){
        this.removeViews(getChildCount()-4,4);
    }

    public void reSet(){
        this.removeAllViews();
        text = "";
        num = 0;
        init();
    }

}
