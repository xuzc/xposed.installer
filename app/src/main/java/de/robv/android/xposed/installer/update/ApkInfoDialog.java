package de.robv.android.xposed.installer.update;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import de.robv.android.xposed.installer.R;

/**
 * Created by lvyonggang on 2017/3/20.
 *  提示对话框
 */

public class ApkInfoDialog extends Dialog{

    private TextView titleTextView ;
    private TextView versionTextView ;
    private TextView infoTextView ;
    private TextView sureTextView ;
    private TextView cancleTextView ;

    private String title ;
    private String version ;
    private String info ;
    private View.OnClickListener sureClick ;

    public ApkInfoDialog(Activity activity) {
        super(activity, R.style.base_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_apk_info);
        initView();
    }

    public void initView(){
        titleTextView = (TextView)findViewById(R.id.message_title);
        versionTextView= (TextView)findViewById(R.id.message_version_title) ;
        infoTextView = (TextView)findViewById(R.id.message_info);
        sureTextView = (TextView)findViewById(R.id.message_sure_textview);
        cancleTextView = (TextView)findViewById(R.id.message_cancle_textview);
        titleTextView.setText(title+"");
        versionTextView.setText(version);
        infoTextView.setText(info+"");
        cancleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        sureTextView.setOnClickListener(sureClick);
    }

    public void setSureClick(View.OnClickListener sureClick) {
        this.sureClick = sureClick;
    }

    public void setTitle(int titleId){
        title = getContext().getString(titleId);
    }

    public void setTitle(String title){
        this.title = title ;
    }

    public void setVersion(String version){
        if (!TextUtils.isEmpty(version)){
            this.version = getContext().getString(R.string.update_version_title)+version ;
        }
    }

    public void setInfo(int infoId){
        info = getContext().getString(infoId);
    }

    public void setInfo(String info){
        this.info = info ;
    }
}
