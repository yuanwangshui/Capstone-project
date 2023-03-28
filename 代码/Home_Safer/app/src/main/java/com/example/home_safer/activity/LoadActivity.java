package com.example.home_safer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.example.home_safer.R;
import com.example.home_safer.database.FlagSQLiteDataBase;
import com.example.home_safer.database.MySQLiteDataBase;

import java.util.List;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoadActivity extends AppCompatActivity //初始界面加载活动
{
    TextView load_name;
    private MySQLiteDataBase mySQLiteDataBase;//本地数据库
    private FlagSQLiteDataBase flagSQLiteDataBase;//标志数据库
    int auto_flag=0,remember_flag=0,insert_flag=0;//标志
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        load_name=findViewById(R.id.load_name);
        mySQLiteDataBase=new MySQLiteDataBase(this);//初始化数据库
        flagSQLiteDataBase=new FlagSQLiteDataBase(this);
        Typeface typeface= ResourcesCompat.getFont(this,R.font.songti);
        new Thread(new Runnable() {
            @Override
            public void run() {
                load_name.post(new Runnable() {
                    @Override
                    public void run() {
                        load_name.setTypeface(typeface);
                    }
                });
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int flag=0;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!flagSQLiteDataBase.query().equals("false"))
                {
                    String []str=flagSQLiteDataBase.query().split(",");
                    if(str[1].equals("true"))//是否自动登录
                    {
                        auto_flag=1;
                        //auto_login.setChecked(true);
                        if(!mySQLiteDataBase.query_auto_account("1").equals("false"))
                        {
                            String []str_b=mySQLiteDataBase.query_Remember_account("1").split(",");
                            //account.setText(str_b[0]);
                            //password.setText(str_b[1]);
                            if(str_b.length==0)
                            {
                                str_b=mySQLiteDataBase.query_auto_account("1").split(",");
                            }
                            thread t1=new thread(str_b[0],str_b[1]);
                            try {
                                t1.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            t1.start();
                            flag=1;
                            //Login(str_b[0],str_b[1]);
                        }
                    }
                }
                if(flag==0) {
                    Intent intent = new Intent();
                    intent.setClass(LoadActivity.this, LoginActivity.class);
                    startActivity(intent);
                    ((Activity) LoadActivity.this).overridePendingTransition(0, 0);
                    finish();
                }
            }
        }).start();
    }

    private class thread extends Thread{
        String phonenumber;
        String password;
        public thread(String phonenumber,String password){
            this.phonenumber=phonenumber;
            this.password=password;
        }
        @Override
        public void run() {
            super.run();
            Login(phonenumber,password);
        }
    }

    private void Login(String toString, String toString1)//登录函数
    {
        if(toString.equals(""))
        {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            return ;
        }
        if(toString1.equals(""))
        {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return ;
        }
        LCQuery<LCObject> query = new LCQuery<>("my_user");
        query.whereEqualTo("phonenumber", toString);
        query.findInBackground().subscribe(new Observer<List<LCObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<LCObject> user) {
                // students 是包含满足条件的 Student 对象的数组
                if(user.size()==0){
                    Looper.prepare();
                    Toast.makeText(LoadActivity.this, "用户名或密码输入错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                LCObject lcObject=user.get(0);
                //lcObject.getString("password");//得到该用户的密码
                if(lcObject.getString("password").equals(toString1))
                {
                    if(insert_flag==1)
                    {
                        if(remember_flag==1&&auto_flag==1)
                        {
                            flagSQLiteDataBase.insert("true","true");
                        }
                        if(remember_flag==0&&auto_flag==1)
                        {
                            flagSQLiteDataBase.insert("false","true");
                        }
                        if(remember_flag==1&&auto_flag==0)
                        {
                            flagSQLiteDataBase.insert("true","false");
                        }
                        if(remember_flag==0&&auto_flag==0)
                        {
                            flagSQLiteDataBase.insert("false","false");
                        }
                    }
                    changeDate(toString,toString1);
                    Toast.makeText(LoadActivity.this,"登录成功",Toast.LENGTH_SHORT).show();//登录成功
                    Intent intent=new Intent(LoadActivity.this,MainActivity.class);
                    intent.putExtra("device_secret",lcObject.getString("device_secret"));//交流消息
                    intent.putExtra("device_name",lcObject.getString("device_name"));
                    startActivity(intent);//跳转界面
                    ((Activity) LoadActivity.this).overridePendingTransition(0, 0);
                    finish();
                }
                else Toast.makeText(LoadActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();//错误登录
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
    }

    public void changeDate(String username,String password)//改变数据库数据
    {
        if(remember_flag==1&&auto_flag==0)
        {
            if(insert_flag==0)
            {
                flagSQLiteDataBase.updateDate("true","false");
            }
            if(mySQLiteDataBase.query_account(username,password)==1)//本地数据库存有账号
            {
                if(mySQLiteDataBase.query_remember(username,password)!=1)//查询是否有记住账号标志
                {
                    String state="";
                    if(mySQLiteDataBase.query_auto(username,password)==1)
                    {
                        state="true";
                    }
                    else
                    {
                        state="false";
                    }
                    mySQLiteDataBase.updateDate(username,password,state,"true","0","1");
                }
            }
            else
            {
                mySQLiteDataBase.insert_remember(username,password,"false","true");
            }
        }
        else if(remember_flag==0&&auto_flag==0)
        {
            if(insert_flag==0)
            {
                flagSQLiteDataBase.updateDate("false","false");
            }
            if(mySQLiteDataBase.query_account(username,password)==1)//本地数据库存有账号
            {
                if(mySQLiteDataBase.query_remember(username,password)!=0)//查询是否有记住账号标志
                {
                    String state="";
                    if(mySQLiteDataBase.query_auto(username,password)==1)
                    {
                        state="true";
                    }
                    else
                    {
                        state="false";
                    }
                    mySQLiteDataBase.updateDate(username,password,state,"false","0","0");
                }
            }
            else
            {
                mySQLiteDataBase.insert(username,password,"false","false");
            }
        }
        if(auto_flag==1&&remember_flag==0)
        {
            if(insert_flag==0)
            {
                flagSQLiteDataBase.updateDate("false","true");
            }
            if(mySQLiteDataBase.query_account(username,password)==1)//本地数据库存有账号
            {
                if(mySQLiteDataBase.query_auto(username,password)!=1)//查询是否有自动登录标志
                {
                    String remember="";
                    if(mySQLiteDataBase.query_remember(username,password)==1)
                    {
                        remember="true";
                    }
                    else
                    {
                        remember="false";
                    }
                    mySQLiteDataBase.updateDate(username,password,"true",remember,"1","0");
                }
            }
            else
            {
                mySQLiteDataBase.insert_auto(username,password,"true","false");
            }
        }
        else if(auto_flag==1&&remember_flag==1)
        {
            if(insert_flag==0)
            {
                flagSQLiteDataBase.updateDate("true","true");
            }
            if(mySQLiteDataBase.query_account(username,password)==1)//本地数据库存有账号
            {
                if(mySQLiteDataBase.query_auto(username,password)!=0)//查询是否有自动登录标志
                {
                    String remember="";
                    if(mySQLiteDataBase.query_remember(username,password)==1)
                    {
                        remember="true";
                    }
                    else
                    {
                        remember="false";
                    }
                    mySQLiteDataBase.updateDate(username,password,"false",remember,"1","1");
                }
            }
            else
            {
                mySQLiteDataBase.insert_all(username,password,"true","true");
            }
        }
    }
}
