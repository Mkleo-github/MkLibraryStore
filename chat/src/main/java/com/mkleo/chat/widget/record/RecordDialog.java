package com.mkleo.chat.widget.record;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkleo.chat.Constants;
import com.mkleo.chat.R;
import com.mkleo.chat.widget.base.ScreenDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by WangHJin on 2017/7/18.
 */

public class RecordDialog extends ScreenDialog {


    public RecordDialog(@NonNull Context context) {
        super(context);
    }

    private View mView;
    private int mIds[] = {
            R.id.iv_val_1,
            R.id.iv_val_2,
            R.id.iv_val_3,
            R.id.iv_val_4,
            R.id.iv_val_5,
            R.id.iv_val_6,
            R.id.iv_val_7
    };
    private List<ImageView> mIcons = new ArrayList<>();
    private TextView mTvCancel;


    @Override
    protected View setContentView() {
        mView = View.inflate(getContext(), R.layout.dialog_recording, null);
        return mView;
    }

    @Override
    protected void onDialgoCreate(Bundle savedInstanceState) {
        setCancelable(false);
        mTvCancel = (TextView) mView.findViewById(R.id.tv_cancel);
        for (int i = 0; i < mIds.length; i++) {
            ImageView icon = (ImageView) mView.findViewById(mIds[i]);
            icon.setVisibility(View.INVISIBLE);
            mIcons.add(icon);
        }
    }


    /**
     * 显示
     */
    public void display() {
        if (!isShowing()) {
            show();
        }
    }

    /**
     * 关闭
     */
    public void close() {
        if (isShowing()) {
            dismiss();
        }
    }


    /**
     * 录音状态改变
     *
     * @param status
     */
    public void onChanged(Enum status) {
        if (status == Constants.RecordState.CACEL)
            mTvCancel.setVisibility(View.VISIBLE);
        if (status == Constants.RecordState.NORMAL)
            mTvCancel.setVisibility(View.INVISIBLE);
    }

    private static final int MAX = 7;
    private static final float BASE_VALUE = 100.0f;

    /**
     * 发送分贝
     *
     * @param db
     */
    public void sendDb(int db) {

        float scale = (float) db / BASE_VALUE;
        int val = (int) (MAX * scale);
        if (val > MAX) val = MAX;
        int num = 0;
        for (final ImageView icon : mIcons) {
            if (num < val) {
                icon.post(new Runnable() {
                    @Override
                    public void run() {
                        icon.setVisibility(View.VISIBLE);
                    }
                });
                num++;
            } else {
                icon.post(new Runnable() {
                    @Override
                    public void run() {
                        icon.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }

    }



}
