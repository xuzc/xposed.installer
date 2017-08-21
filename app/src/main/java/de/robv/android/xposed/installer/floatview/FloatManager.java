package de.robv.android.xposed.installer.floatview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

/**
 * Created by lvyonggang on 2017/5/16.
 *  悬浮球控制器
 */

public class FloatManager {

    private WindowManager windowManager ;
    private FloatView floatView ;
    private Context context ;

    public FloatManager(Context context){
        this.context =context ;
        initWindowManager(context);

    }

    private void initWindowManager(Context context) {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
    }

    private WindowManager.LayoutParams getLayoutParams(){
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        windowParams.format = PixelFormat.RGBA_8888;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowParams.width = FloatView.width;
        windowParams.height = FloatView.hegint;
        //左上点的合适位置
        windowParams.x = (screenWidth-FloatView.width )/2;
        windowParams.y = (screenHeight-FloatView.hegint)/2;
        //设置不透明
        windowParams.alpha = (float) 1 ;
        windowParams.dimAmount= (float) 0.5 ;
        return windowParams;
    }


    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     */
    public void showFloatView() {
        if (floatView == null) {
            floatView = new FloatView(context);
            floatView.setFloatCall(new FloatView.FloatCall() {
                @Override
                public void dismiss() {
                    dismissFloatView();
                }
            });
            WindowManager.LayoutParams params= getLayoutParams();
            windowManager.addView(floatView, params);
            floatView.startCountDown();
        }
    }
    public void dismissFloatView(){
        if (windowManager!=null && floatView!=null){
            windowManager.removeView(floatView);
            floatView =null ;
        }
    }
}
