package com.mkleo.chat.example;

import com.mkleo.chat.R;
import com.mkleo.chat.bean.Emoji;
import com.mkleo.chat.widget.emoji.EmojiLayoutAdapter;

import java.util.ArrayList;
import java.util.List;


public final class MkEmojiAdapter extends EmojiLayoutAdapter {

    @Override
    public List<Emoji> onSetup() {
        List<Emoji> models = new ArrayList<>();
        models.add(new Emoji("爱你", R.mipmap.d_aini));
        models.add(new Emoji("奥特曼", R.mipmap.d_aoteman));
        models.add(new Emoji("拜拜", R.mipmap.d_baibai));
        models.add(new Emoji("悲伤", R.mipmap.d_beishang));
        models.add(new Emoji("鄙视", R.mipmap.d_bishi));
        models.add(new Emoji("闭嘴", R.mipmap.d_bizui));
        models.add(new Emoji("馋嘴", R.mipmap.d_chanzui));
        models.add(new Emoji("吃惊", R.mipmap.d_chijing));
        models.add(new Emoji("哈欠", R.mipmap.d_dahaqi));
        models.add(new Emoji("打脸", R.mipmap.d_dalian));
        models.add(new Emoji("顶", R.mipmap.d_ding));
        models.add(new Emoji("doge", R.mipmap.d_doge));
        models.add(new Emoji("肥皂", R.mipmap.d_feizao));
        models.add(new Emoji("感冒", R.mipmap.d_ganmao));
        models.add(new Emoji("鼓掌", R.mipmap.d_guzhang));
        models.add(new Emoji("哈哈", R.mipmap.d_haha));
        models.add(new Emoji("害羞", R.mipmap.d_haixiu));
        models.add(new Emoji("汗", R.mipmap.d_han));
        models.add(new Emoji("微笑", R.mipmap.d_hehe));
        models.add(new Emoji("黑线", R.mipmap.d_heixian));
        models.add(new Emoji("哼", R.mipmap.d_heng));
        models.add(new Emoji("色", R.mipmap.d_huaxin));
        models.add(new Emoji("挤眼", R.mipmap.d_jiyan));
        models.add(new Emoji("可爱", R.mipmap.d_keai));
        models.add(new Emoji("可怜", R.mipmap.d_kelian));
        models.add(new Emoji("酷", R.mipmap.d_ku));
        models.add(new Emoji("困", R.mipmap.d_kun));
        models.add(new Emoji("白眼", R.mipmap.d_landelini));
        models.add(new Emoji("泪", R.mipmap.d_lei));
        models.add(new Emoji("马到成功", R.mipmap.d_madaochenggong));
        models.add(new Emoji("喵喵", R.mipmap.d_miao));
        models.add(new Emoji("男孩儿", R.mipmap.d_nanhaier));
        models.add(new Emoji("怒", R.mipmap.d_nu));
        models.add(new Emoji("怒骂", R.mipmap.d_numa));
        models.add(new Emoji("女孩儿", R.mipmap.d_numa));
        models.add(new Emoji("钱", R.mipmap.d_qian));
        models.add(new Emoji("亲亲", R.mipmap.d_qinqin));
        models.add(new Emoji("傻眼", R.mipmap.d_shayan));
        models.add(new Emoji("生病", R.mipmap.d_shengbing));
        models.add(new Emoji("草泥马", R.mipmap.d_shenshou));
        models.add(new Emoji("失望", R.mipmap.d_shiwang));
        models.add(new Emoji("衰", R.mipmap.d_shuai));
        models.add(new Emoji("睡", R.mipmap.d_shuijiao));
        models.add(new Emoji("思考", R.mipmap.d_sikao));
        models.add(new Emoji("太开心", R.mipmap.d_taikaixin));
        models.add(new Emoji("偷笑", R.mipmap.d_touxiao));
        models.add(new Emoji("吐", R.mipmap.d_tu));
        models.add(new Emoji("兔子", R.mipmap.d_tuzi));
        models.add(new Emoji("挖鼻", R.mipmap.d_wabishi));
        models.add(new Emoji("委屈", R.mipmap.d_weiqu));
        models.add(new Emoji("笑cry", R.mipmap.d_xiaoku));
        models.add(new Emoji("熊猫", R.mipmap.d_xiongmao));
        models.add(new Emoji("嘻嘻", R.mipmap.d_xixi));
        models.add(new Emoji("嘘", R.mipmap.d_xu));
        models.add(new Emoji("阴险", R.mipmap.d_yinxian));
        models.add(new Emoji("疑问", R.mipmap.d_yiwen));
        models.add(new Emoji("右哼哼", R.mipmap.d_youhengheng));
        models.add(new Emoji("晕", R.mipmap.d_yun));
        models.add(new Emoji("炸鸡啤酒", R.mipmap.d_zhajipijiu));
        models.add(new Emoji("抓狂", R.mipmap.d_zhuakuang));
        models.add(new Emoji("猪头", R.mipmap.d_zhutou));
        models.add(new Emoji("最右", R.mipmap.d_zuiyou));
        models.add(new Emoji("左哼哼", R.mipmap.d_zuohengheng));
        models.add(new Emoji("给力", R.mipmap.f_geili));
        models.add(new Emoji("互粉", R.mipmap.f_hufen));
        models.add(new Emoji("囧", R.mipmap.f_jiong));
        models.add(new Emoji("萌", R.mipmap.f_meng));
        models.add(new Emoji("神马", R.mipmap.f_shenma));
        models.add(new Emoji("威武", R.mipmap.f_v5));
        models.add(new Emoji("喜", R.mipmap.f_xi));
        models.add(new Emoji("织", R.mipmap.f_zhi));
        models.add(new Emoji("NO", R.mipmap.h_buyao));
        models.add(new Emoji("good", R.mipmap.h_good));
        models.add(new Emoji("haha", R.mipmap.h_haha));
        models.add(new Emoji("来", R.mipmap.h_lai));
        models.add(new Emoji("OK", R.mipmap.h_ok));
        models.add(new Emoji("拳头", R.mipmap.h_quantou));
        models.add(new Emoji("弱", R.mipmap.h_ruo));
        models.add(new Emoji("握手", R.mipmap.h_woshou));
        models.add(new Emoji("耶", R.mipmap.h_ye));
        models.add(new Emoji("赞", R.mipmap.h_zan));
        models.add(new Emoji("作揖", R.mipmap.h_zuoyi));
        models.add(new Emoji("伤心", R.mipmap.l_shangxin));
        models.add(new Emoji("心", R.mipmap.l_xin));
        models.add(new Emoji("蛋糕", R.mipmap.o_dangao));
        models.add(new Emoji("飞机", R.mipmap.o_feiji));
        models.add(new Emoji("干杯", R.mipmap.o_ganbei));
        models.add(new Emoji("话筒", R.mipmap.o_huatong));
        models.add(new Emoji("蜡烛", R.mipmap.o_lazhu));
        models.add(new Emoji("礼物", R.mipmap.o_liwu));
        models.add(new Emoji("绿丝带", R.mipmap.o_lvsidai));
        models.add(new Emoji("围脖", R.mipmap.o_weibo));
        models.add(new Emoji("围观", R.mipmap.o_weiguan));
        models.add(new Emoji("音乐", R.mipmap.o_yinyue));
        models.add(new Emoji("照相机", R.mipmap.o_zhaoxiangji));
        models.add(new Emoji("钟", R.mipmap.o_zhong));
        models.add(new Emoji("浮云", R.mipmap.w_fuyun));
        models.add(new Emoji("沙尘暴", R.mipmap.w_shachenbao));
        models.add(new Emoji("太阳", R.mipmap.w_taiyang));
        models.add(new Emoji("微风", R.mipmap.w_weifeng));
        models.add(new Emoji("鲜花", R.mipmap.w_xianhua));
        models.add(new Emoji("下雨", R.mipmap.w_xiayu));
        models.add(new Emoji("月亮", R.mipmap.w_yueliang));
        return models;
    }

    @Override
    protected Emoji.Format onSetFormat() {
        return new Emoji.Format("[", "]");
    }
}
