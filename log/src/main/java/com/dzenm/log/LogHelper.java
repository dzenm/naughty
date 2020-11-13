package com.dzenm.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author dzenm
 * <pre>
 * 使用方法
 * LogHelper.getInstance().setTag("DZY").start(this);
 * public void onChanged(final String log) {
 *     runOnUiThread(new Runnable() {
 *         public void run() {
 *             tvLog.append(log);
 *         }
 *     });
 * }
 * </pre>
 */
public class LogHelper {

    private static final String TAG = LogHelper.class.getSimpleName();

    private static volatile LogHelper sInstance = null;

    /**
     * log输出文件线程
     */
    private LogThread mLogThread;

    /**
     * 日志输出命令行
     */
    private String mCommands;

    /**
     * 日志的TAG
     */
    private String mTag;

    /**
     * 不同级别日志的颜色
     */
    private String[] mLevelColor = new String[]{
            "#9E9E9E", "#2196F3", "#4CAF50",
            "#FFC107", "#F44336", "#000000"
    };

    /**
     * 日志的级别
     */
    private int mLevel = Level.NONE;

    public @interface Level {
        int NONE = 1;
        int VERBOSE = 2;
        int DEBUG = 3;
        int INFO = 4;
        int WARN = 5;
        int ERROR = 6;
    }

    private LogHelper() {
    }

    public static LogHelper getInstance() {
        if (sInstance == null)
            synchronized (LogHelper.class) {
                if (sInstance == null) {
                    sInstance = new LogHelper();
                }
            }
        return sInstance;
    }

    /**
     * 设置显示的日志级别
     *
     * @param level 日志的级别
     * @return this
     */
    public LogHelper setLevel(@Level int level) {
        mLevel = level;
        return this;
    }

    /**
     * 设置显示的日志TAG
     *
     * @param tag 日志TAG
     * @return this
     */
    public LogHelper setTag(String tag) {
        mTag = tag;
        return this;
    }

    /**
     * 重置日志打印的条件
     */
    public void reset() {
        reset(mLevel);
        if (mLogThread != null) {
            mLogThread.mCommands = mCommands;
        }
    }

    /**
     * 格式化日志为增加颜色得HTML日志
     *
     * @param text 日志内容
     * @return this
     */
    public String format(String text) {
        return mLogThread.getTextColor(text);
    }

    /**
     * 开始打印日志
     *
     * @param mOnChangeListener 日志输出监听
     */
    public void start(OnChangeListener mOnChangeListener) {
        reset();
        if (mLogThread == null) {
            mLogThread = new LogThread();
            mLogThread.mCommands = mCommands;
            mLogThread.mOnChangeListener = mOnChangeListener;
        }
        if (mLogThread.isAlive()) {
            mLogThread.interrupt();
        }
        if (!mLogThread.isAlive()) {
            mLogThread.start();
        }
        System.out.println(TAG + "开始保存日志");
    }

    /**
     * 停止打印日志
     *
     * @return this
     */
    public LogHelper stop() {
        if (mLogThread.isAlive()) {
            mLogThread.stopLogs();
            sInstance = null;
        }
        System.out.println(TAG + "停止输出日志");
        return this;
    }

    /**
     * 获取日志输出的集合
     *
     * @return 保存日志的集合
     */
    public List<String> getData() {
        if (mLogThread == null) {
            return new ArrayList<>();
        }
        return mLogThread.mData;
    }

    /**
     * 设置日志不同 {@link #mLevel} 输出的颜色
     *
     * @param colors 数量为6, 分别对应 VERBOSE, DEBUG, INFO, WARN, ERROR, NONE
     * @return this
     */
    public LogHelper setColors(String[] colors) {
        mLevelColor = colors;
        return this;
    }

    /**
     * 重置日志打印的条件
     *
     * @param level 日志级别
     */
    private void reset(int level) {
        if (mTag == null || "".equals(mTag)) {
            mTag = "*";
        }
        System.out.println(TAG + "日志过滤TAG: " + mTag + ", 日志Level: " + level);
        mCommands = "logcat -v brief -s " + mTag + getLevelCommand(level);
        System.out.println(TAG + "日志输出命令: " + mCommands);
    }

    private String getLevelCommand(int level) {
        String command;
        if (level == Level.VERBOSE) {
            command = ":v";
        } else if (level == Level.DEBUG) {
            command = ":d";
        } else if (level == Level.INFO) {
            command = ":i";
        } else if (level == Level.WARN) {
            command = ":w";
        } else if (level == Level.ERROR) {
            command = ":e";
        } else {
            command = "";
        }
        return command;
    }

    /**
     * 日志输出的线程, 负责打印, 处理并格式化, 控制日志的输出, 保存日志
     */
    private class LogThread extends Thread {

        private static final int MAX_LOG_COUNT = 10000;

        private final String mPid = String.valueOf(android.os.Process.myPid());
        private String mCommands;
        private Process mLogcatProcess;
        private BufferedReader mBufferedReader;

        private boolean mThreadFlag = true;
        private boolean isRunning = true;
        private OnChangeListener mOnChangeListener;

        private final List<String> mData = new ArrayList<>();

        private LogThread() {
        }

        private void stopLogs() {
            isRunning = false;
        }

        private void reset(String command) {
            execLogCommand(command);
        }

        @Override
        public void run() {
            super.run();
            try {
                execLogCommand(mCommands);
                readLogBuffered();
                mThreadFlag = false;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (mBufferedReader != null) {
                        mBufferedReader.close();
                        mBufferedReader = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void readLogBuffered() throws IOException {
            String line;
            while (mThreadFlag && (line = mBufferedReader.readLine()) != null) {
                if (line.length() == 0 || !line.contains(mPid) || !isRunning) {
                    continue;
                }
                String time = formatTime().format(new Date());
                String text = "[" + time + "] " + line + "\n";
                if (mOnChangeListener != null) {
                    mOnChangeListener.onChanged(text);
                }
                insertNewLogWhenCapacityEnough(text);
            }
            mBufferedReader.close();
            mLogcatProcess.destroy();
        }

        private void execLogCommand(String command) {
            try {
                mData.clear();
                mLogcatProcess = Runtime.getRuntime().exec(command);
                mBufferedReader = new BufferedReader(
                        new InputStreamReader(mLogcatProcess.getInputStream()), 1024);
                System.out.println(TAG + "执行Log输出命令...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private SimpleDateFormat formatTime() {
            return new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
        }

        /**
         * 获取Log级别
         *
         * @param text Log文本内容
         * @return Log级别
         */
        private int getLogLevel(String text) {
            String front = " ";
            if (text.contains(front + "V/")) {
                return Level.VERBOSE;
            } else if (text.contains(front + "D/")) {
                return Level.DEBUG;
            } else if (text.contains(front + "I/")) {
                return Level.INFO;
            } else if (text.contains(front + "W/")) {
                return Level.WARN;
            } else if (text.contains(front + "E/")) {
                return Level.ERROR;
            } else {
                return Level.NONE;
            }
        }

        /**
         * 获取文本颜色
         *
         * @param text 文本内容
         * @return 添加颜色的Html文本
         */
        public String getTextColor(String text) {
            int color = getLogLevel(text);
            return String.format("<font color=\"" + mLevelColor[color - 2] + "\">%s</font>", text);
        }

        private void insertNewLogWhenCapacityEnough(String text) {
            if (mData.size() > MAX_LOG_COUNT) mData.remove(0);
            mData.add(text);
        }
    }

    public interface OnChangeListener {
        /**
         * 监听日志的输出
         *
         * @param log 没有经过任何装饰的普通日志
         */
        void onChanged(String log);
    }
}