package com.dzenm.crash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author dzenm
 * CrashHelper.getInstance().init(this)
 */
public class CrashHelper implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHelper.class.getSimpleName();

    /**
     * log文件的前缀名
     */
    private static final String FILE_NAME = "crash";
    /**
     * log文件的后缀名
     */
    private static final String SUFFIX = ".txt";

    @SuppressLint("StaticFieldLeak")
    private static volatile CrashHelper sInstance;
    private Context mContext;
    private boolean isDebug = true;
    /**
     * 是否保存为本地文件
     */
    private boolean isCache = true;
    private String mFilePath;

    /**
     * 系统默认异常处理器, （默认情况下，系统会终止当前的异常程序）
     */
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    /**
     * 自定义处理异常信息
     */
    private OnCaughtExceptionMessageListener mOnCaughtExceptionMessageListener;

    private CrashHelper() {
    }

    public static CrashHelper getInstance() {
        if (sInstance == null) {
            synchronized (CrashHelper.class) {
                if (sInstance == null) {
                    sInstance = new CrashHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化异常捕获(默认全局捕获异常)
     *
     * @param context 上下文
     * @return this
     */
    public CrashHelper init(Context context) {
        init(context, true);
        return this;
    }

    /**
     * 初始化异常捕获
     *
     * @param context     上下文
     * @param globalCatch 是否全局捕获异常, 不退出程序
     * @return this
     */
    public CrashHelper init(Context context, boolean globalCatch) {
        mContext = context.getApplicationContext();
        // 获取系统默认的异常处理器
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

        if (globalCatch) interceptException();

        File cacheDir = mContext.getExternalCacheDir();
        if (cacheDir != null) {
            mFilePath = cacheDir.getAbsolutePath() + File.separator + "crash";
        }
        return this;
    }

    public CrashHelper setNoCache() {
        isCache = false;
        return this;
    }

    public CrashHelper setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public CrashHelper setOnCaughtExceptionMessageListener(OnCaughtExceptionMessageListener listener) {
        mOnCaughtExceptionMessageListener = listener;
        return this;
    }

    public CrashHelper setFilePath(String filePath) {
        mFilePath = filePath;
        return this;
    }

    /**
     * 拦截异常处理
     */
    private void interceptException() {
        // 主线程异常拦截
        final Looper looper = Looper.getMainLooper();
        new Handler(looper).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Exception e) {
                        Log.e(TAG, "全局异常捕获中...");
                        handlerException(e, true);
                    }
                }
            }
        });
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用捕获异常的方法
     *
     * @param t  抛出异常的线程
     * @param ex 抛出异常的信息
     */
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable ex) {
        // 拦截子线程的异常
        if (handlerException(ex, false) && mDefaultUncaughtExceptionHandler != null) {
            // 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
            mDefaultUncaughtExceptionHandler.uncaughtException(t, ex);
        }
    }

    /**
     * 处理异常信息
     *
     * @param ex          异常信息
     * @param globalCatch 是否全局捕获异常
     * @return 是否处理成功
     */
    private boolean handlerException(Throwable ex, boolean globalCatch) {
        if (ex == null) return false;
        try {
            // 导出异常信息到SD卡中
            StringBuilder sb = new StringBuilder();
            Log.i(TAG, "开始收集崩溃信息");
            sb.append("-------- 开始收集设备信息 --------\n");
            // 导出手机信息
            String phoneInfo = dumpPhoneInfo();
            sb.append(phoneInfo);
            sb.append("\n-------- 设备信息收集完成 --------\n\n");

            sb.append("-------- 开始收集异常信息 --------\n");
            // 导出异常的调用栈信息
            String exceptionInfo = dumpExceptionInfo(ex);
            sb.append(exceptionInfo);
            sb.append("\n-------- 异常信息收集完成 --------");
            Log.i(TAG, "崩溃信息收集完成");
            Log.e(TAG, exceptionInfo);

            String errorMessage = sb.toString();

            saveAsFile(errorMessage);
            // 这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
            if (mOnCaughtExceptionMessageListener != null) {
                mOnCaughtExceptionMessageListener.onHandlerMessage(errorMessage);
            }

            if (globalCatch) {
                if (isDebug) {
                    showErrorDialog(exceptionInfo);
                }
            } else {
                ActivityHelper.getInstance().exit();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存为文件
     *
     * @param errorMessage 异常信息
     */
    private void saveAsFile(String errorMessage) {
        if (!isCache) return;
        Log.i(TAG, "saveAsFile...");

        // 获取当前时间以创建log文件
        long current = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat")
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
                .format(new Date(current));
        // log文件名
        String fileName = FILE_NAME + "_" + time + SUFFIX;
        // log文件夹
        File parent = new File(mFilePath);
        if (!parent.exists()) parent.mkdirs();
        // 保存文件
        if (createFile(parent, fileName, errorMessage)) {
            // 删除其它文件
            delete(parent, fileName);
            Log.d(TAG, "异常日志文件: " + parent + File.separator + fileName);
        } else {
            Log.d(TAG, "创建异常日志文件失败");
        }
    }

    /**
     * 收集设备参数信息
     */
    private String dumpPhoneInfo() {
        StringBuilder sb = new StringBuilder();
        // 应用的版本名称和版本号
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            // App版本号
            sb.append("\nApp VersionName: ").append(info.versionName);
            sb.append("\nApp VersionCode: ").append(info.versionCode);
            // android版本号
            sb.append("\nOS VersionName: ").append(Build.VERSION.RELEASE);
            sb.append("\nOS VersionCode: ").append(Build.VERSION.SDK_INT);
            //手机制造商
            sb.append("\nVendor: ").append(Build.MANUFACTURER);
            //手机型号
            sb.append("\nModel: ").append(Build.MODEL);
            //cpu架构
            sb.append("\nCPU ABI: ").append(Build.CPU_ABI);
            // 获取设备硬件信息
            Field[] fields = Build.class.getDeclaredFields();
            // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
            for (Field field : fields) {
                field.setAccessible(true);
                sb.append("\n").append(field.getName()).append(": ").append(field.get(""));
            }
        } catch (PackageManager.NameNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "收集设备参数信息失败: " + e);
        }
        return sb.toString();
    }

    /**
     * 收集异常信息
     */
    private String dumpExceptionInfo(Throwable e) throws IOException {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable throwable = e.getCause();
        while (throwable != null) {
            throwable.printStackTrace(printWriter);
            printWriter.append("\r\n");                                     // 换行 每个个异常栈之间换行
            throwable = throwable.getCause();
        }
        printWriter.close();
        String info = writer.toString();
        writer.close();
        return info;
    }

    /**
     * 创建日志文件
     *
     * @param parent   文件位置
     * @param fileName 文件名称
     * @param content  文件内容
     * @return 是否创建成功
     */
    private boolean createFile(File parent, String fileName, String content) {
        try (FileOutputStream fos = new FileOutputStream(new File(parent, fileName));
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)
        ) {
            bw.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除folder文件夹下除了filterName文件的所有文件(不包括文件夹)
     *
     * @param folder     需要删除文件的File
     * @param filterName 过滤的文件名称
     * @return 是否删除成功
     */
    public void delete(File folder, String filterName) {
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) return;
        Log.d(TAG, "路径 " + folder.getPath() + " 下有" + files.length + "个文件");
        for (File file : files) {
            if (file.getName().equals(filterName)) continue;
            String fileType = file.isFile() ? "文件 " : "文件夹 ";
            if (file.isDirectory()) delete(file, filterName);
            Log.d(TAG, fileType + file.getName() + " 删除" + (file.delete() ? "成功" : "失败"));
        }
    }

    /**
     * 显示错误提示框
     *
     * @param exceptionInfo 异常信息
     */
    private void showErrorDialog(final String exceptionInfo) {
        Log.e(TAG, "崩溃错误提示...");
        final Activity activity = ActivityHelper.getInstance().peek();
        if (activity == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                new AlertDialog.Builder(activity)
                        .setTitle("出错了")
                        .setCancelable(false)
                        .setMessage(exceptionInfo)
                        .setPositiveButton("重启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                RestartService.restart(activity);
                                ActivityHelper.getInstance().finish();
                                Log.w(TAG, "准备重启APP...");
                            }
                        })
                        .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityHelper.getInstance().exit();
                                Log.w(TAG, "全部退出APP...");
                            }
                        }).create()
                        .show();
                Looper.loop();
            }
        }).start();
    }

    public interface OnCaughtExceptionMessageListener {
        void onHandlerMessage(String message);
    }
}
