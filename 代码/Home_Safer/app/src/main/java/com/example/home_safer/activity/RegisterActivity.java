package com.example.home_safer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.example.home_safer.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener//登记模块类
{
    EditText register_name,register_class,register_gender,register_dormitory,register_account,register_password_1,register_password_2;//编辑框
    Button register_confirm;//按钮
    String r_name,r_class,r_gender,r_dormitory,r_account,r_password_1,r_password_2;//字符串
    String d_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        changStatusIconCollor(true);
        //get_device();
        //Login("2698296400","zjc");
    }

    private void Login(String toString, String toString1) {
        LCQuery<LCObject> query = new LCQuery<>("my_user");
        query.whereEqualTo("account", toString);
        query.findInBackground().subscribe(new Observer<List<LCObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<LCObject> user) {
                // students 是包含满足条件的 Student 对象的数组
                LCObject lcObject=user.get(0);
                //lcObject.getString("password");//得到该用户的密码
                if(lcObject.getString("password").equals(toString1))
                {
                    finish();
                }
                else Toast.makeText(RegisterActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();//错误登录
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
    }

    public void init()//初始化控件
    {
        register_name=findViewById(R.id.register_name);
        register_class=findViewById(R.id.register_class);
        register_gender=findViewById(R.id.register_gender);
        register_dormitory=findViewById(R.id.register_dormitory);
        register_account=findViewById(R.id.register_account);
        register_password_1=findViewById(R.id.register_password_1);
        register_password_2=findViewById(R.id.register_password_2);
        register_confirm=findViewById(R.id.register_confirm);
        register_confirm.setOnClickListener(this);
    }

    public void changStatusIconCollor(boolean setDark)//状态栏设置
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

    @Override
    public void onClick(View view)
    {
        r_name=register_name.getText().toString();
        r_class=register_class.getText().toString();
        r_gender=register_gender.getText().toString();
        r_dormitory=register_dormitory.getText().toString();
        r_account=register_account.getText().toString();
        r_password_1=register_password_1.getText().toString();
        r_password_2=register_password_2.getText().toString();
        if(r_password_1.equals(r_password_2))
            register();
                /*Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();//登录成功
                Intent intent = new Intent(this, DormitoryActivity.class);
                intent.putExtra("device_secret", DynamicRegisterByMqtt.register_device_secret);//交流消息
                intent.putExtra("device_name", d_name);
                startActivity(intent);//跳转界面
                ((Activity) this).overridePendingTransition(0, 0);
                finish();*/

        else {
            Toast.makeText(this, "请重新输入密码", Toast.LENGTH_SHORT).show();
            register_password_1.setText("");
            register_password_2.setText("");
        }
    }


    /*private boolean Register() {
        int flag=0;
        d_name=get_device();
        try {
            DynamicRegisterByMqtt.main(d_name);//动态注册设备，注册账号用，不是该页面
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 构建对象
        LCObject todo = new LCObject("my_user");
        // 为属性赋值
        todo.put("account", r_account);
        todo.put("password", r_password_1);
        todo.put("user_name",r_name);

        todo.put("device_secret",DynamicRegisterByMqtt.register_device_secret);//设备密钥
        todo.put("device_name",d_name);//设备名
        // 将对象保存到云端
        todo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // 成功保存之后，执行其他逻辑
                System.out.println("保存成功。objectId：" + todo.getObjectId());
            }
            public void onError(Throwable throwable) {
                // 异常处理
                Log.e("MyApplication", "onError: 错误" );
            }
            public void onComplete() {}
        });
        return true;
    }*/

    private void register() //得到未激活设备名
    {
        final String[] device_n = {""};
        LCQuery<LCObject> query = new LCQuery<>("device");
        query.whereEqualTo("is_register", "false");
        query.findInBackground().subscribe(new Observer<List<LCObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<LCObject> device) {
                // students 是包含满足条件的 Student 对象的数组
                LCObject lcObject=device.get(0);
                device_n[0] =lcObject.getString("name");//得到未被激活的设备名
                String id=lcObject.getObjectId();//获取对象id
                //lcObject.getString("is_register")="true";
                LCObject update = LCObject.createWithoutData("device", id);
                update.put("is_register", "true");
                update.saveInBackground().subscribe(new Observer<LCObject>() {
                    public void onSubscribe(Disposable disposable) {}
                    public void onNext(LCObject savedTodo) {
                        System.out.println("设置成功");
                        try {
                            DynamicRegisterByMqtt.main(device_n[0]);//动态注册设备，注册账号用，不是该页面
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //int run_flag=0;
                                while(!DynamicRegisterByMqtt.register_device_secret.equals(""))
                                {
                                    // 构建对象
                                    LCObject todo = new LCObject("my_user");
                                    // 为属性赋值
                                    todo.put("account", r_account);
                                    todo.put("password", r_password_1);
                                    todo.put("user_name",r_name);
                                    todo.put("class",r_class);
                                    todo.put("gender",r_gender);
                                    todo.put("dormitory",r_dormitory);
                                    todo.put("device_secret",DynamicRegisterByMqtt.register_device_secret);//设备密钥
                                    todo.put("device_name",device_n[0]);//设备名
                                    // 将对象保存到云端
                                    todo.saveInBackground().subscribe(new Observer<LCObject>() {
                                        public void onSubscribe(Disposable disposable) {}
                                        public void onNext(LCObject todo) {
                                            // 成功保存之后，执行其他逻辑
                                            System.out.println("保存成功。objectId：" + todo.getObjectId());
                                            Log.e("budedeliao", "onNext: 执行");
                                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();//登录成功
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.putExtra("device_secret", DynamicRegisterByMqtt.register_device_secret);//交流消息
                                            intent.putExtra("device_name", device_n[0]);
                                            intent.putExtra("name",r_name);
                                            intent.putExtra("class",r_class);
                                            intent.putExtra("gender",r_gender);
                                            intent.putExtra("dormitory",r_dormitory);
                                            startActivity(intent);//跳转界面
                                            ((Activity) RegisterActivity.this).overridePendingTransition(0, 0);
                                            finish();
                                        }
                                        public void onError(Throwable throwable) {
                                            // 异常处理
                                            Log.e("MyApplication", "onError: 错误" );
                                        }
                                        public void onComplete() {}
                                    });
                                    break;
                                }
                            }
                        }).start();

                    }
                    public void onError(Throwable throwable) {
                        System.out.println("设置失败！");
                    }
                    public void onComplete() {}
                });;
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        }
}
