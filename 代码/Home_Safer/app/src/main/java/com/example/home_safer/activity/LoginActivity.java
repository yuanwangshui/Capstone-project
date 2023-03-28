package com.example.home_safer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.home_safer.database.FlagSQLiteDataBase;
import com.example.home_safer.database.MySQLiteDataBase;

import java.util.List;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.example.home_safer.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener//登录界面类
{

    Button login,register;//登录按钮和注册按钮
    EditText account,password;//账户、密码编辑框
    CheckBox auto_login,remember_login;//自动登录和记住密码
    private MySQLiteDataBase mySQLiteDataBase;//本地数据库
    private FlagSQLiteDataBase flagSQLiteDataBase;//标志数据库
    int auto_flag=0,remember_flag=0,insert_flag=0;//标志
    @Override
    protected void onCreate(Bundle savedInstanceState)//活动初始化
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init_view();
        set_onClick();//为button设置点击响应事件
        mySQLiteDataBase=new MySQLiteDataBase(this);//初始化数据库
        flagSQLiteDataBase=new FlagSQLiteDataBase(this);
        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//自动登录
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    auto_flag=1;
                }
                else
                {
                    auto_flag=0;
                    //Toast.makeText(MainActivity.this, "未选中", Toast.LENGTH_SHORT).show();
                }
            }
        });
        remember_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    remember_flag=1;
                    //Toast.makeText(MainActivity.this, "选中", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    remember_flag=0;
                    //Toast.makeText(MainActivity.this, "未选中", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(!flagSQLiteDataBase.query().equals("false"))
        {
            String []str=flagSQLiteDataBase.query().split(",");
            if(str[0].equals("true"))//是否记住密码
            {
                remember_flag=1;
                remember_login.setChecked(true);
                if(!mySQLiteDataBase.query_Remember_account("1").equals("false"))
                {
                    String []str_a=mySQLiteDataBase.query_Remember_account("1").split(",");
                    account.setText(str_a[0]);
                    password.setText(str_a[1]);
                }
            }
            else//没有记住密码
            {
                remember_flag=0;
                remember_login.setChecked(false);
            }
            if(str[1].equals("true"))//是否自动登录
            {
                auto_flag=1;
                auto_login.setChecked(true);
                if(!mySQLiteDataBase.query_auto_account("1").equals("false"))
                {
                    String []str_b=mySQLiteDataBase.query_Remember_account("1").split(",");
                    account.setText(str_b[0]);
                    password.setText(str_b[1]);
                    thread t1=new thread(str_b[0],str_b[1]);
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    t1.start();
                    //Login(str_b[0],str_b[1]);
                }
            }
            else//没有自动登录
            {
                auto_flag=0;
                auto_login.setChecked(false);
            }
        }
        else
        {
            insert_flag=1;//数据库内无数据，可进行插入操作
        }
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
                    Toast.makeText(LoginActivity.this, "用户名或密码输入错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                LCObject lcObject=user.get(0);
                //lcObject.getString("password");//得到该用户的密码
                if(lcObject.getString("password").equals(toString1))
                {
                    changeDate(toString,toString1);
                    int id=mySQLiteDataBase.query_id(toString,toString1);//获取用户id
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

                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();//登录成功
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("device_secret",lcObject.getString("device_secret"));//交流消息
                    intent.putExtra("device_name",lcObject.getString("device_name"));
                    startActivity(intent);//跳转界面
                    ((Activity) LoginActivity.this).overridePendingTransition(0, 0);
                    finish();
                }
                else Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();//错误登录
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
    }

    private void set_onClick()//控件点击初始化
    {
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    public void jumptodormitory()//跳转到寝室页面
    {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        this.startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v)//控件点击函数
    {
        switch (v.getId()) {
            case R.id.login:
                try {
                    Login(account.getText().toString(), password.getText().toString());//登录验证函数
                }
                catch (IllegalArgumentException e){
                    Toast.makeText(this, "输入有误",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.register:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                this.startActivity(intent);
                ((Activity) LoginActivity.this).overridePendingTransition(0, 0);
                finish();
                break;
        }
    }


    private void init_view()//初始化按钮
    {
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        changStatusIconCollor(true);
        account=findViewById(R.id.account);
        password=findViewById(R.id.password);
        auto_login=findViewById(R.id.auto_login);
        remember_login=findViewById(R.id.remember_login);
    }
    public void changStatusIconCollor(boolean setDark)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = getWindow().getDecorView();
            if(decorView != null){
                int vis = decorView.getSystemUiVisibility();
                if(setDark){
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else{
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
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