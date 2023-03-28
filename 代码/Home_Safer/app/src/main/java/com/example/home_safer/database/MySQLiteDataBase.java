package com.example.home_safer.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteDataBase extends SQLiteOpenHelper//本地账号密码数据库类
{


    private static final String SQLite_NAME="MySQLite.db";
    private static final String TABLE_NAME="USER";
    private static final String CREATE_TABLE_SQL="create table "+TABLE_NAME+"(id integer primary key autoincrement ,username text,password text,state text,remember text,auto_id text,remember_id text)";
    public MySQLiteDataBase(Context context)
    {
        super(context,SQLite_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db)//初始化函数
    {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public int  insert(String username, String password,String state,String remember)//添加函数
    {
        if(!username.equals("") && !password.equals(""))
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("state", state);
            values.put("remember", remember);
            values.put("auto_id", "0");//用户自动登录id
            values.put("remember_id", "0");//用户记住账号id
            db.insert(TABLE_NAME, null, values);
            return 1;
        }
        else return 0;
    }
    public int  insert_remember(String username, String password,String state,String remember)//添加函数，记住账号
    {
        if(!username.equals("") && !password.equals(""))
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("state", state);
            values.put("remember", remember);
            values.put("auto_id", "0");//用户自动登录id
            values.put("remember_id", "1");//用户记住账号id
            db.insert(TABLE_NAME, null, values);
            return 1;
        }
        else return 0;
    }
    public int  insert_auto(String username, String password,String state,String remember)//添加函数，自动登录
    {
        if(!username.equals("") && !password.equals(""))
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("state", state);
            values.put("remember", remember);
            values.put("auto_id", "1");//用户自动登录id
            values.put("remember_id", "0");//用户记住账号id
            db.insert(TABLE_NAME, null, values);
            return 1;
        }
        else return 0;
    }
    public int  insert_all(String username, String password,String state,String remember)//添加函数，all
    {
        if(!username.equals("") && !password.equals(""))
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("state", state);
            values.put("remember", remember);
            values.put("auto_id", "1");//用户自动登录id
            values.put("remember_id", "1");//用户记住账号id
            db.insert(TABLE_NAME, null, values);
            return 1;
        }
        else return 0;
    }
    @SuppressLint("Range")
    public int query_id(String username, String password)//获取用户在数据库中的id
    {
        if(!username.equals("") && !password.equals(""))
        {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor=db.query(TABLE_NAME,null,"username like ?",new String[]{username},null,null,null);
            String Password="";
            int id=0;
            if(cursor!=null)
                if(cursor.moveToNext())
                {
                    Password=cursor.getString(cursor.getColumnIndex("password"));
                    id=cursor.getInt(cursor.getColumnIndex("id"));
                }
            if(Password.equals(password))
                return id;
            else return 0;
        }
        else return 0;
    }
    @SuppressLint("Range")
    public int query_account(String username, String password)//密码是否正确
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
            if(Password.equals(password))
                return 1;
            else return 0;
        }
        else return 0;
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
    public int query_remember(String username, String password)//是否记住密码
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
                    Remember=cursor.getString(cursor.getColumnIndex("remember"));
                }
            if(Password.equals(password)&& Remember.equals("true"))
                return 1;
            else return 0;
        }
        else return 0;
    }
    public int updateDate(String username,String password,String state,String remember,String auto_id,String remember_id)//更新数据
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("state", state);
        values.put("remember", remember);
        values.put("auto_id", auto_id);//用户自动登录id
        values.put("remember_id", remember_id);//用户记住账号id
        db.update(TABLE_NAME, values, "username like ?", new String[]{username});
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
