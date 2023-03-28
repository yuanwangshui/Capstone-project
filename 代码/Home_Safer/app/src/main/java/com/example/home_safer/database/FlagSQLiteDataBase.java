package com.example.home_safer.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FlagSQLiteDataBase extends SQLiteOpenHelper//标志符数据库
{


    private static final String SQLite_NAME="FlagSQLite.db";
    private static final String TABLE_NAME="FLAG";
    private static final String CREATE_TABLE_SQL="create table "+TABLE_NAME+"(id integer primary key autoincrement ,remember text,auto text,prime_id text)";
    public FlagSQLiteDataBase(Context context)
    {
        super(context,SQLite_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public int  insert(String remember, String auto)//添加函数
    {
        if(!remember.equals("") && !auto.equals(""))
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("remember", remember);
            values.put("auto", auto);
            values.put("prime_id","1");
            db.insert(TABLE_NAME, null, values);
            return 1;
        }
        else return 0;
    }
    @SuppressLint("Range")
    public String query()//密码是否正确
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor=db.query(TABLE_NAME,null,"prime_id like ?",new String[]{"1"},null,null,null);
        String remember="";
        String auto="";
        if(cursor!=null)
        {
            if(cursor.moveToNext())
            {
                remember=cursor.getString(cursor.getColumnIndex("remember"));
                auto=cursor.getString(cursor.getColumnIndex("auto"));
            }
            if(!remember.equals("") && !auto.equals(""))
                return remember+","+auto;
        }
        return "false";
    }
    @SuppressLint("Range")
    public int query_auto(String username, String password)//是否自动登录
    {
        if(!username.equals("") && !password.equals(""))
        {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor=db.query(TABLE_NAME,null,"username like ?",new String[]{username},null,null,null);
            String Password="";
            String State="";
            if(cursor!=null)
                if(cursor.moveToNext())
                {
                    Password=cursor.getString(cursor.getColumnIndex("password"));
                    State=cursor.getString(cursor.getColumnIndex("state"));
                }
            if(Password.equals(password)&& State.equals("true"))
                return 1;
            else return 0;
        }
        else return 0;
    }
    @SuppressLint("Range")
    public String query_Remember_account(String remember_id)//是否记住账号并返回账号
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor=db.query(TABLE_NAME,null,"remember_id like ?",new String[]{remember_id},null,null,null);
        String ID="";
        String UserName="";
        String PassWord="";
        if(cursor!=null)
        {
            if(cursor.moveToNext())
            {
                ID=cursor.getString(cursor.getColumnIndex("remember_id"));
                UserName=cursor.getString(cursor.getColumnIndex("username"));
                PassWord=cursor.getString(cursor.getColumnIndex("password"));
                //return UserName+","+PassWord;
            }
            return UserName+","+PassWord;
        }
        else
            return "false";
    }
    @SuppressLint("Range")
    public String query_auto_account(String auto_id)//是否自动登录账号并返回账号
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor=db.query(TABLE_NAME,null,"auto_id like ?",new String[]{auto_id},null,null,null);
        String ID="";
        String UserName="";
        String PassWord="";
        if(cursor!=null)
        {
            if(cursor.moveToNext())
            {
                ID=cursor.getString(cursor.getColumnIndex("auto_id"));
                UserName=cursor.getString(cursor.getColumnIndex("username"));
                PassWord=cursor.getString(cursor.getColumnIndex("password"));
                //return UserName+","+PassWord;
            }
            return UserName+","+PassWord;
        }
        else
            return "false";
    }
    @SuppressLint("Range")
    public int query_remember(String username, String password)//是否自动登录
    {
        if(!username.equals("") && !password.equals(""))
        {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor=db.query(TABLE_NAME,null,"username like ?",new String[]{username},null,null,null);
            String Password="";
            String Remember="";
            if(cursor!=null)
                if(cursor.moveToNext())
                {
                    Password=cursor.getString(cursor.getColumnIndex("password"));
                    Remember=cursor.getString(cursor.getColumnIndex("state"));
                }
            if(Password.equals(password)&& Remember.equals("true"))
                return 1;
            else return 0;
        }
        else return 0;
    }
    public int updateDate(String remember,String auto)//更新数据
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("remember", remember);
        values.put("auto",auto);
        db.update(TABLE_NAME, values, "prime_id like ?", new String[]{"1"});
        return 1;
        /*try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            db.update(TABLE_NAME, values, "username like ?", new String[]{username});
            return 1;
        } finally {
            return 0;
        }*/
    }
}
