package com.mkleo.chat.bean;

import java.io.Serializable;


public class Emoji implements Serializable {


    private final String emojiName;
    private final int emojiSource;

    public Emoji(String emojiName, int emojiSource) {
        this.emojiName = emojiName;
        this.emojiSource = emojiSource;
    }

    public String getEmojiName() {
        return emojiName;
    }


    public int getEmojiSource() {
        return emojiSource;
    }

    /**
     * @Description: 表情识别规则
     * @author: WangHJin
     * @date: 2017/11/17 9:45
     */

    public static class Format {

        private final String prefix;
        private final String suffix;

        public Format(String prefix, String suffix) {
            if (prefix == null || suffix == null ||
                    prefix.trim().equals("") || suffix.trim().equals(""))
                throw new NullPointerException("请确认前缀和后缀不能为空");
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getSuffix() {
            return suffix;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
