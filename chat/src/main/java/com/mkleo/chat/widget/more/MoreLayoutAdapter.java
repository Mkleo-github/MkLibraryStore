package com.mkleo.chat.widget.more;

import com.mkleo.chat.bean.Feature;
import com.mkleo.chat.widget.base.ILayoutAdapter;

public abstract class MoreLayoutAdapter implements ILayoutAdapter<Feature, MoreLayout> {


    protected MoreLayoutAdapter() {
    }

    @Override
    public final void build(MoreLayout layout) {
        layout.create(onSetup());
    }
}
