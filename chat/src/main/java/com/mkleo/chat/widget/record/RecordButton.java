package com.mkleo.chat.widget.record;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.mkleo.chat.Constants;
import com.mkleo.chat.R;
import com.mkleo.chat.utils.RecordUtil;
import com.mkleo.chat.utils.ToastUtil;

/**
 * @Description: 录音按钮
 * @author: WangHJin
 * @date: 2017/11/9 20:44
 */

public class RecordButton extends AppCompatButton implements View.OnTouchListener, View.OnClickListener, RecordUtil.Callback {

    private RecordUtil mRecordUtil = RecordUtil.getInstance();
    private RecordDialog mDialog;
    private String mRecordPath = Constants.DefaultPath.PATH_AUDIO;

    public RecordButton(Context context) {
        super(context);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isTouch = false;//是否按下
    private boolean isCancel = false;//是否取消
    private float mCancelPixel;
    private float mDownY;
    private RecordCallback mCallback;

    public void init(Context context) {
        mRecordUtil.setCallback(this);
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mCancelPixel = windowManager.getDefaultDisplay().getHeight() * 0.3f;//以手机高度的百分之30为界限
        this.setFocusable(true);
        this.setOnTouchListener(this);
        this.setOnClickListener(this);
        mDialog = new RecordDialog(context);
    }

    @Override
    public void onClick(View v) {

    }

    public void setRecordPath(String path) {
        this.mRecordPath = path;
    }

    @Override
    public synchronized boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN://按下
                if (isTouch) return false;
                isTouch = true;
                isCancel = false;
                mDownY = event.getY();

                setBackgroundResource(R.drawable.btn_record_background_down);
                setText(getResources().getString(R.string.move_up_cancel_record));
                setTextColor(getResources().getColor(R.color.colorDarkGrey));//字体灰色

                //显示dialog
                mDialog.display();
                mDialog.onChanged(Constants.RecordState.NORMAL);
                //开始录制,以时间戳
                mRecordUtil.start(mRecordPath, System.currentTimeMillis()
                        + Constants.MEDIA_FORMAT.AUDIO_RECORD_FORMAT);
                break;

            case MotionEvent.ACTION_UP: //抬起
                if (!isTouch) return false;
                isTouch = false;

                setBackgroundResource(R.drawable.btn_record_background_up);
                setText(getResources().getString(R.string.touch_long_record));
                setTextColor(getResources().getColor(R.color.colorDarkGrey));//字体灰色

                //隐藏dialog
                mDialog.close();
                if (isCancel) {
                    mRecordUtil.cancel(); //取消
                } else {
                    mRecordUtil.stop();   //停止
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mDownY - event.getY() > mCancelPixel) {
                    isCancel = true;
                    setText(getResources().getString(R.string.move_hand_cancel_record));
                    setTextColor(getResources().getColor(R.color.colorRed));//字体红色
                    mDialog.onChanged(Constants.RecordState.CACEL);
                } else {
                    isCancel = false;
                    setText(getResources().getString(R.string.move_up_cancel_record));
                    setTextColor(getResources().getColor(R.color.colorDarkGrey));//字体灰色
                    mDialog.onChanged(Constants.RecordState.NORMAL);
                }
                break;

            default:

                break;

        }

        return false;
    }

    public void setRecordCallback(RecordCallback callback) {
        this.mCallback = callback;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDialog.dismiss();
        mDialog = null;
    }

    /* ---------------录音回调----------------- */
    @Override
    public void onStart() {

    }

    @Override
    public void onRecording(int db, long length) {
        mDialog.sendDb(db);
    }

    @Override
    public void onStop(String folderPath, String recordName) {
        if (mCallback != null) {
            mCallback.onRecordCompleted(folderPath + "/" + recordName);
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFailed(String msg) {
        ToastUtil.show(getContext(), msg);
    }


    public interface RecordCallback {

        void onRecordCompleted(String path);
    }
}
