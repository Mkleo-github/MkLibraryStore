package com.mkleo.chat.robot;

import com.mkleo.chat.bean.Message;
import com.mkleo.chat.widget.ChatLayout;

/**
 * @Description: 抽象机器人
 * @author: WangHJin
 * @date: 2017/11/9 20:48
 */

public interface IRobot {


    /**
     * 初始化的时候会调用一次(问候语:您好,请问您需要什么帮助)
     *
     * @param chatLayout
     */
    void greetings(ChatLayout chatLayout);

    /**
     * 自动回复
     *
     * @param userMsg    用户发来的消息
     * @param chatLayout chat主体,用来回复消息
     */
    void autoReply(Message userMsg, ChatLayout chatLayout);

}
