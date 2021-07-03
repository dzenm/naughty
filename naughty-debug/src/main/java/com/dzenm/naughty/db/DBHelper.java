package com.dzenm.naughty.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

import com.dzenm.naughty.db.model.Column;
import com.dzenm.naughty.db.model.Row;
import com.dzenm.naughty.db.model.Table;
import com.dzenm.naughty.util.FileUtils;
import com.dzenm.naughty.util.ViewUtils;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    /**
     * The max width of a column can be.
     */
    private static final int maxColumnWidth = ViewUtils.dp2px(300);

    /**
     * The min width of a column can be.
     */
    private static final int minColumnWidth = ViewUtils.dp2px(20);

    /**
     * 获取数据库所在文件夹
     *
     * @param context 上下文
     * @return SharedPreferences文件夹
     */
    private static String getDBDir(Context context) {
        return context.getFilesDir().getParent() + "/databases";
    }

    /**
     * 获取数据库文件夹下的所有文件
     *
     * @param context 上下文
     * @return 数据库所有文件
     */
    public static List<File> getDBFiles(Context context) {
        List<File> files = FileUtils.getFiles(getDBDir(context), null);
        return files == null ? new ArrayList<File>() : files;
    }

    /**
     * 通过文件路径打开数据库
     *
     * @param path 文件路径
     * @return 数据库链接
     */
    public static SQLiteDatabase openDatabase(String path) {
        return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * 从数据库文件获取数据库表
     *
     * @param dbPath 数据库路径
     * @return 数据库表
     */
    public static List<Table> getTableFromDB(String dbPath) {
        SQLiteDatabase db = DBHelper.openDatabase(dbPath);
        List<Table> tables = new ArrayList<>();
        String sqlString = "select * from sqlite_master where type = ?";
        Cursor c = db.rawQuery(sqlString, new String[]{"table"});
        if (c.moveToFirst()) {
            do {
                String type = c.getString(c.getColumnIndexOrThrow("type"));
                String name = c.getString(c.getColumnIndexOrThrow("name"));
                String tbl_name = c.getString(c.getColumnIndexOrThrow("tbl_name"));
                int rootpage = c.getInt(c.getColumnIndexOrThrow("rootpage"));
                String sql = c.getString(c.getColumnIndexOrThrow("sql"));
                Table table = new Table(type, name, tbl_name, rootpage, sql);
                tables.add(table);
                Log.d("DZY", "查询所有表: " + table.toString());
            } while (c.moveToNext());
        }
        c.close();
        return tables;
    }


    /**
     * 从数据库文件获取数据表列头
     *
     * @param dbPath    数据库路径
     * @param tableName 数据库表
     * @return 数据库表列头
     */
    public static List<Column> getColumnFromDB(String dbPath, String tableName) {
        SQLiteDatabase db = DBHelper.openDatabase(dbPath);
        List<Column> columns = new ArrayList<>();
        String sqlString = "pragma table_info(" + tableName + ")";
        Cursor c = db.rawQuery(sqlString, null);
        if (c.moveToFirst()) {
            do {
                int cid = c.getInt(c.getColumnIndexOrThrow("cid"));
                String name = c.getString(c.getColumnIndexOrThrow("name"));
                String type = c.getString(c.getColumnIndexOrThrow("type"));
                int notnull = c.getInt(c.getColumnIndexOrThrow("notnull"));
                String dflt_value = c.getString(c.getColumnIndexOrThrow("dflt_value"));
                int pk = c.getInt(c.getColumnIndexOrThrow("pk"));
                Column column = new Column(cid, name, type, notnull, dflt_value, pk);
                columns.add(column);
                Log.d("DZY", "查询表列名: " + column.toString());
            } while (c.moveToNext());
        }
        c.close();
        return columns;
    }

    /**
     * 从数据库文件获取数据表列头
     *
     * @param dbPath  数据库路径
     * @param table   数据库表
     * @param columns 数据列
     * @return 数据库表列头
     */
    public static List<List<Row>> getRowFromDB(String dbPath, String table,
                                               List<Column> columns) {
        SQLiteDatabase db = DBHelper.openDatabase(dbPath);
        List<List<Row>> list = new ArrayList<>();
        String sqlString = "select * from " + table;
        Cursor c = db.rawQuery(sqlString, null);
        if (c.moveToFirst()) {
            do {
                List<Row> rows = new ArrayList<>();
                for (Column column : columns) {
                    String key = column.getName();
                    String type = column.getType().toLowerCase();
                    Object value = null;
                    int index = c.getColumnIndexOrThrow(key);
                    switch (type) {
                        case "text":
                            value = c.getString(index);
                            break;
                        case "integer":
                            value = c.getInt(index);
                            break;
                        case "real":
                            value = c.getFloat(index);
                            break;
                    }
                    rows.add(new Row(key, value));
                }
                list.add(rows);
                Log.d("DZY", "" + new JSONArray(rows).toString());
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    /**
     * Measure the proper width of each column. They should just wrap the text content, but they can't
     * be smaller than the min width or larger than the max width.
     */
    public static void measureColumnsWidth(Context context, List<Column> columns,
                                           List<List<Row>> data) {
        TextPaint paint = new TextView(context).getPaint();
        // we iterate the first page data and evaluate the proper width of each column.
        for (List<Row> row : data) {
            for (int i = 0; i < row.size(); i++) {
                Column column = columns.get(i);
                Object value = row.get(i).getValue();
                int keyWidth = minColumnWidth;
                if (value != null) {
                    keyWidth = (int) paint.measureText(value.toString());
                }
                int valueWidth = (int) paint.measureText(column.getName());
                keyWidth = Math.min(Math.max(keyWidth, minColumnWidth), maxColumnWidth);
                valueWidth = Math.min(Math.max(valueWidth, minColumnWidth), maxColumnWidth);
                column.setWidth(Math.max(keyWidth, valueWidth));
            }
        }
    }
}
