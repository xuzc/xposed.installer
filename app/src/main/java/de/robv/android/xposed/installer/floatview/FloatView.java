package de.robv.android.xposed.installer.floatview;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.robv.android.xposed.installer.R;
import de.robv.android.xposed.installer.util.AppUtil;
import de.robv.android.xposed.installer.util.CommandUtil;
import de.robv.android.xposed.installer.util.CommonDataUtil;
import de.robv.android.xposed.installer.util.SharedPreferencesHelper;

/**
 * Created by lvyonggang on 2017/5/16.
 */

public class FloatView extends RelativeLayout implements View.OnClickListener{

    /**注意要和view_float布局里的宽高保持一致**/
    public static int width = 800 ;
    public static int hegint= 400 ;

    public static final int TIME_ALL = 10000 ; //倒计时的总时间10000毫秒
    public static final int TIME_STEP = 1000 ; //倒计时单次的频率1000毫秒

    private FloatCall floatCall ;
    private TextView  inforTextView ;
    private RebootCountDown countDown ;

    private String timeInfo ;

    public FloatView(Context context) {
        super(context);
        init(context);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        timeInfo = context.getString(R.string.change_info);
        String value = getTimeInfo(TIME_ALL/1000);
        View view =  LayoutInflater.from(context).inflate(R.layout.view_float, this);
        inforTextView = (TextView)view.findViewById(R.id.info_textview);
        view.findViewById(R.id.sure_textview).setOnClickListener(this);
        view.findViewById(R.id.cancle_textview).setOnClickListener(this);
        inforTextView.setText(value);
    }

    public FloatCall getFloatCall() {
        return floatCall;
    }

    public void setFloatCall(FloatCall floatCall) {
        this.floatCall = floatCall;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sure_textview :
                reboot();
                break;
            case R.id.cancle_textview :
                cancle();
                break;
        }
    }

    private void reboot(){
        try {
            //关机前留一定时间写入文件
            CommonDataUtil.writeData(getContext());
            Thread.sleep(1500);
        }catch (Exception e){

        }
        dimisssCall();
        cancleCountDown();
        CommandUtil.reboot();
    }

    private void cancle(){
        dimisssCall();
        cancleCountDown();
    }

    public void startCountDown(){
        countDown = new RebootCountDown();
        countDown.start();
    }

    public void cancleCountDown(){
        if (countDown!=null){
            countDown.cancel();
        }
    }

    public void dimisssCall(){
        if (floatCall!=null){
            floatCall.dismiss();
        }
    }

    private String getTimeInfo(int replace){
         if (!TextUtils.isEmpty(timeInfo)){
            return String.format(timeInfo, replace);
         }
        return null;
    }

    private void writeData(){
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
        sharedPreferencesHelper.setXposedAppCode(AppUtil.getAppCode(getContext()));
    }

    /*定义一个倒计时的内部类*/
    class RebootCountDown extends CountDownTimer {

        public RebootCountDown() {
            super(TIME_ALL, TIME_STEP);
        }

        @Override
        public void onFinish() {
            reboot();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            inforTextView.setText(getTimeInfo((int)millisUntilFinished/1000));
        }
    }

    public interface  FloatCall{
        /**消失窗体**/
        void dismiss();
    }

}
