package com.mkleo.chat.widget.emoji;

import com.mkleo.chat.bean.Emoji;
import com.mkleo.chat.widget.base.ILayoutAdapter;


public abstract class EmojiLayoutAdapter implements ILayoutAdapter<Emoji, EmojiLayout> {

    /**
     * 设置读取规则
     *
     * @return
     */
    protected abstract Emoji.Format onSetFormat();

    @Override
    public final void build(EmojiLayout layout) {
        layout.setFormat(onSetFormat()).create(onSetup());
    }
}
