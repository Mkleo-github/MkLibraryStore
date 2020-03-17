package com.mkleo.chat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.mkleo.chat.bean.Emoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiUtil {

    //表情对照表
    private static List<Emoji> sEmojiTable = new ArrayList<>();
    //表情缓存
    private static HashMap<String, Bitmap> sEmojiCache = new HashMap<>();
    //格式
    private static Emoji.Format sFormat;

    /**
     * 初始化表情的格式
     *
     * @param format
     */
    public static void init(Emoji.Format format) {
        sFormat = format;
    }

    /**
     * 创建对照表
     *
     * @param table
     */
    public static void buildTable(Context context, List<Emoji> table) {
        sEmojiTable.clear();
        sEmojiTable.addAll(table);
        /* 自动生成表情缓存 */
        sEmojiCache.clear();
        for (Emoji model : sEmojiTable) {
            sEmojiCache.put(model.getEmojiName(),
                    BitmapFactory.decodeResource(context.getResources(), model.getEmojiSource()));
        }
    }

    /**
     * 获取缓存的表情图片
     *
     * @return
     */
    public static HashMap<String, Bitmap> getEmojis() {
        return sEmojiCache;
    }


    /**
     * 解析内容
     *
     * @param context
     * @param format
     * @param textView
     * @param content
     */
    public static void resolveContent(Context context, Emoji.Format format, TextView textView, String content) {
        if (null == format) {
            textView.setText(content);
            return;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        StringBuilder regex = new StringBuilder();
        regex.append("\\")
                .append(format.getPrefix())
                .append("(\\S+?)")
                .append("\\")
                .append(format.getSuffix());

        float emojiSize = textView.getTextSize() * 1.5f;

        Pattern p = Pattern.compile(regex.toString());
        Matcher m = p.matcher(content);
        Iterator<Emoji> iterator;
        Emoji emojiModel = null;
        while (m.find()) {
            iterator = sEmojiTable.iterator();
            String tempText = m.group();
            while (iterator.hasNext()) {
                emojiModel = iterator.next();
                if (tempText.equals(formatName(format, emojiModel.getEmojiName()))) {
                    //转换为Span并设置Span的大小
                    ssb.setSpan(new ImageSpan(context, createEmoji(emojiModel.getEmojiName(), (int) emojiSize)),
                            m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        textView.setText(ssb);
    }

    public static void resolveContent(Context context, TextView textView, String content) {
        resolveContent(context, sFormat, textView, content);
    }

    /**
     * 创建表情
     *
     * @param emojiName
     * @param size
     * @return
     */
    private static Bitmap createEmoji(String emojiName, int size) {
        return scaleBitmap(sEmojiCache.get(emojiName), size, size);
    }


    /**
     * 自定义宽高的bitmap
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap == null) throw new NullPointerException();
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return newBitmap;
    }


    /**
     * dp 转 px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * @param input
     * @param format
     */
    public static void delete(EditText input, Emoji.Format format) {
        String text = input.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if (format.getPrefix().equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf(format.getSuffix());
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                input.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                return;
            }
            input.getText().delete(index, text.length());
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        input.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    /**
     * 点击表情
     *
     * @param input
     * @param name
     */
    public static void click(EditText input, String name) {
        if (name != null) {
            int index = input.getSelectionStart();
            Editable editable = input.getEditableText();
            if (index < 0) editable.append(name);
            else editable.insert(index, name);
        }
    }

    /**
     * @param format
     * @param name
     * @return
     */
    public static String formatName(Emoji.Format format, String name) {
        if (null == name) throw new NullPointerException("name 不能为 <null>");
        if (!name.startsWith(format.getPrefix()))
            name = format.getPrefix() + name;
        if (!name.endsWith(format.getSuffix()))
            name = name + format.getSuffix();
        return name;
    }


}
