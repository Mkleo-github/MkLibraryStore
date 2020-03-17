package com.mkleo.chat.robot;

import com.mkleo.chat.bean.User;

/**
 * @Description:
 * @author: Wang HengJin
 * @date: 2018/2/11 12:37 星期日
 */
public abstract class Robot implements IRobot {

    protected User mRobot;

    public Robot() {
        mRobot = new User("MkRobot", setRobotName(), User.Type.OTHER);
    }

    protected abstract String setRobotName();
}
