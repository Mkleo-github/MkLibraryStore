package com.mkleo.chat.widget.base;

import java.util.List;

/**
 * 拓展适配器
 *
 * @param <Bean>
 * @param <Layout>
 */
public interface ILayoutAdapter<Bean, Layout> {

    /**
     * 加载
     *
     * @return
     */
    List<Bean> onSetup();

    /**
     * 创建
     *
     * @param layout
     */
    void build(Layout layout);
}
