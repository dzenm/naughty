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
 * LogHelper.getInstance().init(Process.myPid()).setTag("DZY").start(this);
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

    public static final int LEVEL_NONE = 0;
    public static final int LEVEL_VERBOSE = 2;
    public static final int LEVEL_DEBUG = 3;
    public static final int LEVEL_INFO = 4;
    public static final int LEVEL_WARN = 5;
    public static final int LEVEL_ERROR = 6;

    private static final String TAG = LogHelper.class.getSimpleName() + " | ";

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
            "#9E9E9E", "#2196F3", "#4CAF50", "#FFC107", "#F44336", "#000000"
    };

    /**
     * 当前应用的PID
     */
    private int mPid;

    /**
     * 日志的级别
     */
    private int mLevel = LEVEL_NONE;

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

    public LogHelper init(int pid) {
        mPid = pid;
        return this;
    }

    /**
     * 设置显示的日志级别
     *
     * @param level 日志的级别
     * @return this
     */
    public LogHelper setLevel(int level) {
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
     *
     * @param level 日志级别
     * @return this
     */
    public LogHelper reset(int level) {
        if (mTag == null || "".equals(mTag)) {
            mTag = "*";
        }
        System.out.println(TAG + "日志过滤TAG: " + mTag);
        if (level == LEVEL_VERBOSE) {
            mCommands = "logcat -v brief -s " + mTag + ":v";
        } else if (level == LEVEL_DEBUG) {
            mCommands = "logcat -v brief -s " + mTag + ":d";
        } else if (level == LEVEL_INFO) {
            mCommands = "logcat -v brief -s " + mTag + ":i";
        } else if (level == LEVEL_WARN) {
            mCommands = "logcat -v brief -s " + mTag + ":w";
        } else if (level == LEVEL_ERROR) {
            mCommands = "logcat -v brief -s " + mTag + ":e";
        } else if (level == LEVEL_NONE) {
            System.out.println(TAG + "打印所有的日志信息");
            mCommands = "logcat -v brief ";
        }
        System.out.println(TAG + "日志输出命令: " + mCommands);
        return this;
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
     * @return this
     * @see {@link #start(OnChangeListener)}
     */
    public LogHelper start() {
        return start(null);
    }

    /**
     * 开始打印日志
     *
     * @param mOnChangeListener 日志输出监听
     * @return this
     */
    public LogHelper start(OnChangeListener mOnChangeListener) {
        reset(mLevel);
        if (mLogThread == null) {
            mLogThread = new LogThread(mCommands, String.valueOf(mPid));
            mLogThread.mOnChangeListener = mOnChangeListener;
        }
        if (!mLogThread.isAlive()) {
            mLogThread.start();
        }
        System.out.println(TAG + "开始保存日志");
        return this;
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
     * 日志输出的线程, 负责打印, 处理并格式化, 控制日志的输出, 保存日志
     */
    private class LogThread extends Thread {

        private static final int MAX_LOG_COUNT = 10000;

        private String mCommands;
        private String mPid;
        private Process mLogcatProcess;
        private BufferedReader mBufferedReader;

        private boolean mRunning = true;
        private OnChangeListener mOnChangeListener;

        private List<String> mData = new ArrayList<>();

        private LogThread(String commands, String pid) {
            mCommands = commands;
            mPid = pid;
        }

        private void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            super.run();
            try {
                mLogcatProcess = Runtime.getRuntime().exec(mCommands);
                mBufferedReader = new BufferedReader(
                        new InputStreamReader(mLogcatProcess.getInputStream()), 1024
                );
                String line;
                while (mRunning && (line = mBufferedReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
                    String time = format.format(new Date());
                    if (line.contains(mPid)) {
                        String textLine = "[" + time + "] " + line + "\n";
                        if (mOnChangeListener != null) {
                            mOnChangeListener.onChanged(textLine);
                        }
                        adjustCount(textLine);
                    }
                }
                mBufferedReader.close();
                mLogcatProcess.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mLogcatProcess != null) {
                    mLogcatProcess.destroy();
                    mLogcatProcess = null;
                }
                if (mBufferedReader != null) {
                    try {
                        mBufferedReader.close();
                        mBufferedReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
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
                return LEVEL_VERBOSE;
            } else if (text.contains(front + "D/")) {
                return LEVEL_DEBUG;
            } else if (text.contains(front + "I/")) {
                return LEVEL_INFO;
            } else if (text.contains(front + "W/")) {
                return LEVEL_WARN;
            } else if (text.contains(front + "E/")) {
                return LEVEL_ERROR;
            } else {
                return LEVEL_NONE;
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

        private void adjustCount(String text) {
            if (mData.size() > MAX_LOG_COUNT) {
                mData.remove(0);
            }
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