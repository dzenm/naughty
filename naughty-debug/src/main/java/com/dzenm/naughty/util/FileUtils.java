package com.dzenm.naughty.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /**
     * 读取文件内容
     *
     * @param file 读取的文件
     * @return 文件的文本内容
     */
    public static String readFileText(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            // 构造一个BufferedReader类来读取文件
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            // 使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取文件夹下的所有文件
     *
     * @param path       文件路径
     * @param filterType 过滤的文件后缀(文件类型)
     * @return 该路径下的所有文件
     */
    public static List<File> getFiles(String path, String filterType) {
        File dir = new File(path);
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }

        List<File> fileList = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                if (TextUtils.isEmpty(filterType)) {
                    fileList.add(f);
                } else if (f.getName().endsWith(filterType)) {
                    fileList.add(f);
                }
            } else if (f.isDirectory()) {
                List<File> childFiles = getFiles(f.getAbsolutePath(), filterType);
                if (childFiles != null) {
                    fileList.addAll(childFiles);
                }
            }
        }
        return fileList;
    }

    /**
     * 判断文件是否是数据库文件
     * @param file 文件
     * @return 是否是数据库文件
     */
    public static boolean isDBFile(File file) {
        return file.getName().endsWith(".db");
    }

    /**
     * 判断数据库文件是否存在
     * @param path 文件路径
     * @return 文件是否存在
     */
    public static boolean exist(String path) {
        return new File(path).exists();
    }

    /**
     * 判断数据库有效是否有效
     * @param path 文件路径
     * @return 是否是有效的数据库文件
     */
    public static boolean isValidDBFile(String path) {
        try {
            FileReader reader = new FileReader(new File(path));
            char[] buffer = new char[16];
            reader.read(buffer, 0, 16);
            String str = new String(buffer);
            reader.close();
            return str.equals("SQLite format 3\u0000");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
