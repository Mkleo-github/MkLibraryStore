package com.mkleo.chat.widget;

import android.view.View;
import android.widget.TextView;

import com.mkleo.chat.R;
import com.mkleo.chat.bean.Message;
import com.mkleo.chat.bean.Text;
import com.mkleo.chat.bean.User;
import com.mkleo.chat.utils.EmojiUtil;

public class TextMessageAdapter extends MessageContentAdpater<Text> {

    private TextView mTextView;

    @Override
    protected int setContentView() {
        return R.layout.message_content_text;
    }

    @Override
    protected void onContentViewCreated(View view, Message<Text> message) {
        mTextView = view.findViewById(R.id.tv_text);
        if (message.getUser().getType() == User.Type.MINE) {        //自己
            mTextView.setTextColor(view.getContext().getResources().getColor(R.color.colorWhite));
            mTextView.setBackgroundResource(R.drawable.local_user_pop_bg);
        } else if (message.getUser().getType() == User.Type.OTHER) {//其他用户
            mTextView.setTextColor(view.getContext().getResources().getColor(R.color.colorBlack));
            mTextView.setBackgroundResource(R.drawable.other_user_pop_bg);
        }

        EmojiUtil.resolveContent(view.getContext(), mTextView, message.getContent().getText());
    }

}
