package com.example.weatherdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UIUtils {

    private final static Handler sMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 上下文的获取
     *
     * @return
     */
    public static Context getContext() {
        return Utils.getApp();
    }

    /**
     * 刘海屏全屏显示FLAG
     */
    public static final int FLAG_NOTCH_SUPPORT = 0x00010000;

    /**
     * 设置应用窗口在华为刘海屏手机不使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setNotFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(ViewGroup.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("clearHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException
                | InvocationTargetException e) {
            Log.e("test", "hw clear notch screen flag api error");
        } catch (Exception e) {
            Log.e("test", "other Exception");
        }
    }


    /**
     * UI加载过程中，会导致输入框获取焦点无效，稍延迟让输入框获得焦点
     *
     * @param editText 控件
     */
    public static void focusDelay(EditText editText) {
        focusDelay(editText, 100);
    }

    public static void focusDelay(EditText editText, long ms) {
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
                editText.requestFocus();
            }
        }, ms);
    }

    /**
     * 执行延时操作
     *
     * @param task
     * @param delay
     */
    public static void postDelayed(Runnable task, long delay) {
        getMainThreadHandler().postDelayed(task, delay);
    }

    public static Handler getMainThreadHandler() {
        return sMainHandler;
    }

    /**
     * 判断当前view是否在屏幕可见
     */
    public static boolean getLocalVisibleRect(Context context, View view, int offsetY) {
        Point p = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(p);
        int screenWidth = p.x;
        int screenHeight = p.y;
        Rect rect = new Rect(0, 0, screenWidth, screenHeight);
        int[] location = new int[2];
        location[1] = location[1] + dp2px(context, offsetY);
        view.getLocationInWindow(location);
        view.setTag(location[1]);//存储y方向的位置
        if (view.getLocalVisibleRect(rect)) {
            return true;
        } else {
            return false;
        }
    }

    public static int getScroollY(View targetView) {
        int[] intArray = new int[2];
        targetView.getLocationOnScreen(intArray);//测量View相对于屏幕的距离
        return intArray[1];
    }

    public static int getLocationInWindow(View view){
        //1.获取控件在屏幕总的位置
        int[] location = new int[2];
        view.getLocationInWindow(location);
//或 view.getLocationOnScreen(location)
        Log.e("getLocationInWindow", location[0]+"  "+location[1]);
        return location[1];
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * sp转px
     */
    public static int sp2px(float sp) {
        float scaledDensity = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaledDensity + 0.5f);
    }

    /***
     * 返回一个动态设置drawableTop drawableLeft需要的drawable
     * @param drawId
     * @return
     */
    public static Drawable getDrawable(Context context, int drawId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

    /**
     * //表示在View原有范围的基础上在四周增加20dp的区域
     * app:expandTouchArea="@{`20`}
     * //表示在View原有范围的基础上左右增加20dp, 上下增加10dp的区域
     * app:expandTouchArea="@{`20 10`}
     * //表示在View原有范围的基础上, 左上右下分别增加20dp 10dp 50dp 20dp的区域
     * app:expandTouchArea="@{`20 10 50 20`}
     * */
    public static void expandTouchArea(View view, String size) {
        Context context = view.getContext();
        view.postDelayed(() -> {
            Rect rect = new Rect();
            view.getHitRect(rect);

            int left = 0;
            int top = 0;
            int right = 0;
            int bottom = 0;

            /*
             *size 举例 ‘2’ or '2 4 ' or '2 4 6 8'
             * */
            String mSize = size.trim();
            String[] split = mSize.split(" ");
            switch (split.length) {
                case 1:
                    int oneP = split[0] == null ? 0 : Integer.parseInt(split[0]);
                    left = dp2px(context, oneP);
                    top = dp2px(context, oneP);
                    right = dp2px(context, oneP);
                    bottom = dp2px(context, oneP);
                    break;
                case 2:
                    int tdp1 = split[0] == null ? 0 : Integer.parseInt(split[0]);
                    int tdp2 = split[1] == null ? 0 : Integer.parseInt(split[1]);
                    left = dp2px(context, tdp1);
                    top = dp2px(context, tdp2);
                    right = dp2px(context, tdp1);
                    bottom = dp2px(context, tdp2);
                    break;

                case 4:
                    left = dp2px(context, split[0] == null ? 0 : Integer.parseInt(split[0]));
                    top = dp2px(context, split[1] == null ? 0 : Integer.parseInt(split[1]));
                    right = dp2px(context, split[2] == null ? 0 : Integer.parseInt(split[2]));
                    bottom = dp2px(context, split[3] == null ? 0 : Integer.parseInt(split[3]));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + split.length);
            }

            rect.left -= left;
            rect.top -= top;
            rect.right += right;
            rect.bottom += bottom;

            TouchDelegate touchDelegate = new TouchDelegate(rect, view);
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup){
                ((ViewGroup) parent).setTouchDelegate(touchDelegate);
            }
        }, 100);
    }
}
