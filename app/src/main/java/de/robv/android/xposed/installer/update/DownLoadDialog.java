package de.robv.android.xposed.installer.update;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.installer.R;
import de.robv.android.xposed.installer.util.ApkUtil;

/**
 * Created by lvyonggang on 2017/3/23.
 *  apk下载界面
 */

public class DownLoadDialog extends Dialog {

    private String TAG ="WechatDownLoadDialog";
    private Activity activity ;
    private DownTaskManager downTaskManager;
    private String downLoadUrl ;//apk的下载地址

    //两个层，过度层及真实显示层
    private TextView loadingTextView ;
    private LinearLayout dataLinearLayout ;

    private TextView titleTextView ;
    private TextView allTextView ;
    private TextView downTextView ;
    private TextView percentTextView ;
    private SeekBar seekBar ;
    private TextView cancleTextView ;
    private View.OnClickListener sureClick ;
    private String title ;

    public DownLoadDialog(Activity activity , String downLoadUrl ) {
        super(activity, R.style.base_dialog);
        this.activity = activity ;
        this.downLoadUrl = downLoadUrl ;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        initView();
        startDownload();
    }

    public void initView(){
        loadingTextView = (TextView)findViewById(R.id.loading_textview);
        dataLinearLayout = (LinearLayout)findViewById(R.id.data_layout);
        titleTextView = (TextView)findViewById(R.id.title_textview);
        downTextView = (TextView)findViewById(R.id.down_textview);
        allTextView = (TextView)findViewById(R.id.all_textview);
        percentTextView = (TextView)findViewById(R.id.percent_textview);
        seekBar = (SeekBar)findViewById(R.id.percent_seekbar);
        cancleTextView = (TextView)findViewById(R.id.cancle_textview);
        seekBar.setMax(100);
        cancleTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        titleTextView.setText(title);
        showUi(true);
    }

    private void showUi(boolean loading){
        if (loading){
            loadingTextView.setVisibility(View.VISIBLE);
            dataLinearLayout.setVisibility(View.INVISIBLE);
        }else{
            loadingTextView.setVisibility(View.GONE);
            dataLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setTitleInfor(String title){
        this.title = title ;
    }

    public void setTitleInfor(int titleId ){
        this.title = activity.getString(titleId) ;
    }


    @Override
    public void show() {
        //show之前设置点击空白区域不消失
        setCanceledOnTouchOutside(false);
        super.show();
    }

    @Override
    public void dismiss() {
        if (downTaskManager!=null){
            //对话框取消的话，要取消下载
            downTaskManager.cancel(true);
        }
        super.dismiss();
    }


    public void updateUI(float downingSize, float totalSize){
        //下载显示的时候要显示小数点
        if (downingSize>0){
            downTextView.setText(ApkUtil.getSizeOfM(downingSize));
            float value = (downingSize*100/totalSize) ;
            percentTextView.setText(ApkUtil.dealValue(value)+"%");
            seekBar.setProgress((int)value);
        }else{
            downTextView.setText(ApkUtil.getSizeOfM(downingSize));
            allTextView.setText("/"+ApkUtil.getSizeOfM(totalSize));
            seekBar.setProgress(0);
        }
    }

    private void startDownload(){
        if (TextUtils.isEmpty(downLoadUrl)){
            return;
        }

        DownTaskManager.DownCall call = new DownTaskManager.DownCall() {
            @Override
            public void downing(float downingSize, float totalSize) {
                showUi(false);
                updateUI(downingSize , totalSize);
            }

            @Override
            public void downed(boolean sucess, String info) {
                if (sucess){
                    downTaskManager.installInUi();
                    dismiss();
                }else{
                    dismiss();
                    if (!TextUtils.isEmpty(info)){
                        Toast.makeText(getContext(),info ,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        downTaskManager = new DownTaskManager(activity , downLoadUrl , call);
        downTaskManager.startDown();
    }

}
