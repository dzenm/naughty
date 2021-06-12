package com.dzenm.naughty.util;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 格式化Json字符串展示
     *
     * @param json 需要格式化的字符串
     * @return 格式化好的字符串
     */
    public static String formatJson(String json) {
        String message;
        // 格式化json字符串
        try {
            // 最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            if (json.startsWith("{")) {
                message = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                message = new JSONArray(json).toString(4);
            } else {
                message = json;
            }
        } catch (JSONException e) {
            message = json;
        }

        // 添加换行并输出字符串
        String[] lines = message.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            line = line.replace("\\", "");
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * 根据文件大小格式化为B, KB, MB, GB
     *
     * @param size 文件大小
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(long size) {
        String[] suffixes = new String[]{" B", " KB", " MB", " GB", " TB", "PB"};
        int index = 0;
        // 因为要保存小数点，所以需大于102400以便用于后面进行小数分解的运算，index是判断以哪一个单位结尾
        while (size >= 102400) {
            size /= 1024;
            index++;
        }

        long integer = size / 100;
        long decimal = size % 100;
        boolean isNeedDecimal = integer == 0 && decimal == 0;

        return integer + (isNeedDecimal ? "" : "." + decimal) + suffixes[index];
    }

    public static String formatDate(String date) {
        return formatDate("HH:mm:ss SSS", date);
    }

    public static String formatDate(String pattern, String date) {
        return formatDate(pattern, Long.valueOf(date));
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDate(String pattern, long date) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * Created by TanJiaJun on 5/29/21.
     */
    private static final Pattern urlPattern = Pattern.compile(
            // 验证是否是http://、https://、ftp://、rtsp://、mms://其中一个
            "^((http|https|ftp|rtsp|mms)?://)?" +
                    // 判断字符是否为FTP地址（ftp://user:password@）
                    // 判断字符是否为0到9、小写字母a到z、_、!、~、*、'、(、)、.、&、=、+、$、%、-其中一个，匹配零次或者一次
                    "(([0-9a-z_!~*'().&=+$%-]+: )?" +
                    // 判断字符是否为0到9、小写字母a到z、_、!、~、*、'、(、)、.、&、=、+、$、%、-其中一个，匹配一次或者多次
                    "[0-9a-z_!~*'().&=+$%-]+" +
                    // @
                    "@)?" +
                    // 判断字符是否为IP地址，例子：192.168.255.255
                    // 判断字符是否匹配1+[0到9，匹配两次]，例如：192
                    "((1\\d{2}" +
                    // 或者
                    "|" +
                    // 判断字符是否匹配2+[0到4，匹配一次]+[0到9，匹配一次]，例如：225
                    "2[0-4]\\d" +
                    // 或者
                    "|" +
                    // 判断字符是否匹配25+[0到5，匹配一次]，例如：255
                    "25[0-5]" +
                    // 或者
                    "|" +
                    // 判断字符是否匹配[1到9，匹配一次]+[0到9，匹配一次]，例如：25
                    "[1-9]\\d" +
                    // 或者
                    "|" +
                    // 判断字符是否匹配1到9，匹配一次，例如：5
                    "[1-9])" +
                    // 判断字符是否匹配\.(1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|\d)，匹配三次
                    "(\\.(" +
                    // 判断字符是否匹配1+[0到9，匹配两次]，例如：192
                    "1\\d{2}" +
                    // 或者
                    "|" +
                    // 判断字符是否匹配2+[0到4，匹配一次]+[0到9，匹配一次]，例如：225
                    "2[0-4]\\d" +
                    // 或者
                    "|" +
                    // 判断字符是否匹配25+[0到5，匹配一次]，例如：255
                    "25[0-5]" +
                    // 或者
                    "|" +
                    // 判断字符是否匹配[1到9]+[0到9]，例如：25
                    "[1-9]\\d" +
                    // 或者
                    "|" +
                    // 判断字符是否匹配0到9，匹配一次，例如：5
                    "\\d))" +
                    // 匹配三次
                    "{3}" +
                    // 或者
                    "|" +
                    // 判断字符是否为域名（Domain Name）
                    // 三级域名或者以上，判断字符是否为0到9、小写字母a到z、_、!、~、*、'、(、)、-其中一个，匹配零次或者多次，然后加上.，例如：www.
                    "([0-9a-z_!~*'()-]+\\.)*" +
                    // 二级域名，长度不能超过63个字符，先判断第一个字符是否为0到9、小写字母a到z其中一个，匹配一次，然后判断第二个字符是否为0到9、小写字母a到z、-其中一个，最多匹配61次，这两个字符匹配零次或者一次，最后判断第三个字符是否为0到9、小写字母a到z其中一个，然后加上.
                    "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]" +
                    // 顶级域名，判断字符是否为小写字母a到z其中一个，匹配最少两次、最多六次，例如：.com、.cn
                    "\\.[a-z]{2,6})" +
                    // 端口号，判断字符是否匹配:+[0到9，匹配最少一次、最多四次]，匹配零次或者一次
                    "(:[0-9]{1,4})?" +
                    // 判断字符是否为斜杠（/），匹配零次或者一次，如果没有文件名，就不需要斜杠
                    "((/?)|" +
                    // 判断字符是否为0到9、小写字母a到z、大写字母A到Z、_、!、~、*、'、(、)、.、;、?、:、@、&、=、+、$、,、%、#、-其中一个，匹配一次或者多次
                    "(/[0-9a-zA-Z_!~*'(){}.;?:@&=+$,%#-]+)+" +
                    // 判断字符是否为斜杠（/），匹配零次或者一次
                    "/?)$"
    );

    /**
     * Determine if it is a URL.
     * 判断是否为url。
     *
     * @param str The string to be matched.
     * @return Whether the result is a URL.
     */
    public static boolean isUrl(String str) {
        return urlPattern.matcher(str).matches();
    }

    /**
     * Get hierarchy string.
     * 得到带有层次结构的的字符串。
     *
     * @param hierarchy The hierarchy.
     * @return The hierarchy string.
     */
    public static String getHierarchyStr(int hierarchy) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hierarchy; i++) {
            // 空四格
            sb.append("\r\r\r\r");
        }
        return sb.toString();
    }

    /**
     * Format the JSON to indent.
     * 对JSON格式化缩进
     *
     * @param jsonStr The json string.
     * @return The json string that have been formatted and indented.
     */
    public static String jsonFormat(String jsonStr) {
        StringBuilder sb = new StringBuilder();
        int level = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            char ch = jsonStr.charAt(i);
            if (level > 0 && '\n' == sb.toString().charAt(sb.length() - 1))
                sb.append(getHierarchyStr(level));
            if (ch == '{' || ch == '[') {
                sb.append(ch).append("\n");
                level++;
            } else if (ch == '}' || ch == ']') {
                sb.append("\n");
                level--;
                sb.append(getHierarchyStr(level));
                sb.append(ch);
            } else if (ch == ',') {
                sb.append(ch).append("\n");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
