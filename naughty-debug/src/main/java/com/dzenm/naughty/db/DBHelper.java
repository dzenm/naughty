package com.dzenm.naughty.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dzenm.naughty.db.model.Column;
import com.dzenm.naughty.db.model.Table;
import com.dzenm.naughty.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

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
     * @param path 文件路径
     * @return 数据库链接
     */
    public static SQLiteDatabase openDatabase(String path) {
        return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * 从数据库文件获取数据库表
     * @param db 数据库链接
     * @return 数据库表
     */
    public static List<Table> getTableFromDB(SQLiteDatabase db) {
        List<Table> tables = new ArrayList<>();
        Cursor c = db.rawQuery("select * from sqlite_master where type = ?", new String[]{"table"});
        if (c.moveToFirst()) {
            do {
                String tableName = c.getString(c.getColumnIndexOrThrow("tbl_name"));
                tables.add(new Table(tableName));
            } while (c.moveToNext());
        }
        c.close();
        return tables;
    }

    /**
     * 从数据库文件获取数据表列头
     * @param db 数据库链接
     * @param table 数据库表
     * @return 数据库表列头
     */
    public static List<Column> getColumnFromDB(SQLiteDatabase db, String table) {
        List<Column> columns = new ArrayList<>();
        String columnSql = "pragma table_info(" + table + ")";
        Cursor c = db.rawQuery(columnSql, null);
        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndexOrThrow("name"));
                String type = c.getString(c.getColumnIndexOrThrow("type"));
                Column column = new Column(name, type);
                columns.add(column);
            } while (c.moveToNext());
        }
        c.close();
        return columns;
    }
}
