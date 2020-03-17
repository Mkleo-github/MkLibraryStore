package com.mkleo.chat.bean;

/**
 * @Description: 用户类, 存储用户的基本信息
 * @author: WangHJin
 * @date: 2017/11/9 16:18
 */

public class User<T> {

    public enum Type {
        MINE,//当前用户
        OTHER,//其他用户
        SYSTEM//系统用户
    }

    //用户ID
    private String id;
    //用户名称
    private String name;
    //用户头像
    private int avatar;
    //用户类型
    private Type type;
    //可以携带参数
    private T data;

    public User(String id, String name, Type type) {
        this(id, name, -1, type);
    }

    public User(String id, String name, int avatar, Type type) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public boolean isSelf() {
        return type == Type.MINE;
    }
}
