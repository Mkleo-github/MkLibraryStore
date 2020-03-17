package com.mkleo.bases.view;

import java.util.Vector;

/**
 * des: 统一的更新线程
 * by: Mk.leo
 * date: 2019/8/6
 */
public class ViewPoster {

    /* 图形容器 */
    private static Vector<FlashView> sViewContainer = new Vector<>();
    /* 总体帧率 */
    static int sFps = 45;

    private static Poster sPoster;

    private ViewPoster() {
        throw new RuntimeException("Uninstance!");
    }

    /**
     * 总体的刷新帧率
     *
     * @param fps
     */
    public static void setFps(int fps) {
        sFps = fps;
    }

    /**
     * 添加
     *
     * @param flashView
     */
    static void attachView(FlashView flashView) {
        if (null == sPoster) {
            sPoster = new Poster();
            sPoster.start();
        }
        if (!sViewContainer.contains(flashView))
            sViewContainer.add(flashView);
    }

    /**
     * 移除
     *
     * @param flashView
     */
    static void detachView(FlashView flashView) {
        sViewContainer.remove(flashView);
        //如果容器内没有图形,那么关闭线程
        if (sViewContainer.size() == 0) {
            sPoster.quit();
            sPoster = null;
        }
    }


    private static class Poster extends Thread {

        private boolean isQuit = false;

        @Override
        public void run() {
            super.run();

            while (!isQuit) {

                for (FlashView flashView : sViewContainer) {
                    flashView.postView();
                }

                try {
                    sleep(1000L / sFps);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * 停止刷新
         */
        void quit() {
            isQuit = true;
        }
    }

}
