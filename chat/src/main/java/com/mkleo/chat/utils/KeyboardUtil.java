package com.mkleo.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtil implements ViewTreeObserver.OnGlobalLayoutListener {

    private View mContentView;
    //布局初始高度
    private int mContentHeight = 0;
    //键盘显示时候的高度
    private int mKeyboardHeight = 0;
    //记录高度
    private int mNotesHeight;
    private KeyBoardListener mKeyBoardListener;
    //默认高度,当键盘高度无法获取时返回默认高度
    private final int mDefaultHeight;

    public interface KeyBoardListener {
        void onKeyboardChange(boolean isKeyboardShow, int keyboardHeight);
    }

    public void setKeyBoardListener(KeyBoardListener listener) {
        this.mKeyBoardListener = listener;
    }

    public KeyboardUtil(View contentView) {
        this(contentView, 0);
    }

    /**
     * 该参数一定要是edittext的直接父布局,不能监听嵌套后的布局
     *
     * @param contentView
     */
    public KeyboardUtil(View contentView, int defaultHeight) {
        this.mDefaultHeight = defaultHeight >= 0 ? defaultHeight : 0;
        this.mContentView = contentView;
        if (mContentView != null)
            mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mKeyboardHeight = (int) KeyboardSP.get(contentView.getContext(), KeyboardSP.KEY_KEYBOARD_HEIGHT, 0);
    }


    /**
     * 当布局发生变化
     */
    @Override
    public void onGlobalLayout() {

        boolean isLayoutChanged = false;
        int contentViewNowHeight = mContentView.getHeight();//布局当前高度
        if (contentViewNowHeight > 0) {
            if (mContentHeight == 0) {//记录高度
                mContentHeight = contentViewNowHeight;
                mNotesHeight = contentViewNowHeight;
            }

            if (contentViewNowHeight > mContentHeight) {  //目前出现BUG
                return;
            }

            if (mNotesHeight != contentViewNowHeight) {
                mNotesHeight = contentViewNowHeight;
                isLayoutChanged = true;
            }
            if (mKeyboardHeight != mContentHeight - contentViewNowHeight
                    && mContentHeight - contentViewNowHeight > 0) {
                mKeyboardHeight = mContentHeight - contentViewNowHeight;
                //记录高度
                KeyboardSP.put(mContentView.getContext(), KeyboardSP.KEY_KEYBOARD_HEIGHT, mKeyboardHeight);
            }

            if (isLayoutChanged) {
                boolean isKeyboardShow;
                int keyboardHeight = 0;
                if (mContentHeight == contentViewNowHeight) {
                    isKeyboardShow = false;
                } else {
                    keyboardHeight = mContentHeight - contentViewNowHeight;
                    isKeyboardShow = true;
                }

                if (mKeyBoardListener != null)
                    mKeyBoardListener.onKeyboardChange(isKeyboardShow, keyboardHeight);
            }
        }
    }

    /**
     * 获取键盘高度
     *
     * @return
     */
    public int getKeyboardHeight() {
        if (mKeyboardHeight < 50)
            return mDefaultHeight;
        return mKeyboardHeight;
    }

    /**
     * 获取内容布局高度
     *
     * @return
     */
    public int getContentHeight() {
        return mContentHeight;
    }


    /**
     * 移除监听
     */
    public void removeCallback() {
        if (mContentView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }


    /* 隐藏键盘 */
    public synchronized void hideKeyboard(EditText editText) {
        if (null != editText) {
            InputMethodManager imm = (InputMethodManager) mContentView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    /* 显示键盘 */
    public synchronized void showKeyboard(EditText editText) {
        if (null != editText) {
            InputMethodManager imm = (InputMethodManager) mContentView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }


    private static class KeyboardSP {
        /**
         * 保存在手机里面的文件名
         */
        private static final String FILE_NAME = "KeyboardValues";
        //键盘高度
        static final String KEY_KEYBOARD_HEIGHT = "keyboard_height";

        /**
         * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
         *
         * @param context 上下文
         * @param key     数据的标识
         * @param object  需要保存的数据
         */
        static void put(final Context context, final String key, final Object object) {
            new Thread() {
                @Override
                public void run() {
                    SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    if (object instanceof String) {
                        editor.putString(key, (String) object);
                    } else if (object instanceof Integer) {
                        editor.putInt(key, (Integer) object);
                    } else if (object instanceof Boolean) {
                        editor.putBoolean(key, (Boolean) object);
                    } else if (object instanceof Float) {
                        editor.putFloat(key, (Float) object);
                    } else if (object instanceof Long) {
                        editor.putLong(key, (Long) object);
                    } else {
                        editor.putString(key, object.toString());
                    }
                    editor.commit();
                }
            }.start();
        }

        /**
         * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
         *
         * @param context
         * @param key
         * @param defaultObject
         * @return
         */
        static Object get(Context context, String key, Object defaultObject) {
            SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
            if (defaultObject instanceof String) {
                return sp.getString(key, (String) defaultObject);
            } else if (defaultObject instanceof Integer) {
                return sp.getInt(key, (Integer) defaultObject);
            } else if (defaultObject instanceof Boolean) {
                return sp.getBoolean(key, (Boolean) defaultObject);
            } else if (defaultObject instanceof Float) {
                return sp.getFloat(key, (Float) defaultObject);
            } else if (defaultObject instanceof Long) {
                return sp.getLong(key, (Long) defaultObject);
            }
            return null;
        }
    }
}
