package de.robv.android.xposed.installer.update;

import android.app.Activity;
import android.app.Dialog;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import de.robv.android.xposed.installer.R;

public class LoadingDialog extends Dialog {

    private ImageView loadingImageView;
    private TextView loadingMessageTextView;

    public LoadingDialog(Activity activity) {
        super(activity , R.style.base_loading_dialog);
        setContentView(R.layout.dialog_loading);
        initView();
    }

    public LoadingDialog(Activity activity, String message) {
        super(activity);
        setContentView(R.layout.dialog_loading);
        initView();
        setMessage(message);
    }

    public void initView(){
        loadingImageView = (ImageView) findViewById(R.id.loading_imageview);
        loadingMessageTextView = (TextView) findViewById(R.id.loading_tips_textview);
    }


    @Override
    public void show() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.global_loading_anim);
        //只有在代码中设置才是匀速
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        animation.setInterpolator(linearInterpolator);
        loadingImageView.startAnimation(animation);
        super.show();
    }

    public void setMessage(String message) {
        loadingMessageTextView.setText(message);
    }

    public void setMessage(int msgId) {
        loadingMessageTextView.setText(getContext().getString(msgId)+"");
    }

    public String getMsg(){
        if (loadingMessageTextView!=null){
           return loadingMessageTextView.getText().toString();
        }
        return  null;
    }
}
