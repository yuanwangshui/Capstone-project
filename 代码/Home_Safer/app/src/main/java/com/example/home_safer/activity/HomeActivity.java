package com.example.home_safer.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.home_safer.R;

import com.example.home_safer.util.mqtt_tool.aliyun_connect;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationBarView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationBarView.OnItemSelectedListener {
    /*
    变量
     */
    LinearLayout Person;//导航键 个人
    TextView temperature;//温度显示
    TextView humidity;//湿度显示
    TextView gas_text;//天然气显示
    TextView state_fire_text;//火焰显示
    ImageView state_fire_img;//火焰图片显示
    ImageView gas;//气体
    BottomNavigationView bottomNavigationView;//底部导航栏


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_home);
        init();//初始化
        aliyun_connect.connect(this,"iQOOz6","1433b0130804ec5d5b3638a9e9a4cb5a");//连接阿里云物联网平台
        //aliyun_connect.connect(this,"raspberry","a0b4b423d27b4a909011e915586429ed");//连接阿里云物联网平台
        String payload2="{\"method\":\"thing.service.property.set\",\"id\":\"234298816\",\"params\":{\"flame\":1,\"gas\":1,\"Temperature\":12,\"Humidity\":56},\"version\":\"1.0.0\"}";
        String payload="{\"id\": 780861, \"version\": \"1.0\", \"params\": {\"Temperature\": 20}, \"method\": \"user.update\"}";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    aliyun_connect.publishMessage_2(payload2);
                    aliyun_connect.publishMessage(payload2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        handle_loop();
        Toast toast=Toast.makeText(this, "发送数据", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private void init()//初始化
    {
        init_view();
        band_view();
        band_click();
    }
    private void init_view()//初始化控件
    {
        Person=new LinearLayout(this);
        temperature=new TextView(this);
        humidity=new TextView(this);
        state_fire_img=new ImageView(this);//house里的火焰监控
        gas=new ImageView(this);
        bottomNavigationView=new BottomNavigationView(this);
        //gas_text=new TextView(this);
        //state_fire_text=new TextView(this);
    }
    private void band_view()//绑定控件函数
    {
        //Person=findViewById(R.id.person);
        temperature=findViewById(R.id.temperature);
        humidity=findViewById(R.id.humidity);
        state_fire_img=findViewById(R.id.state_fire_img);
        gas=findViewById(R.id.gas);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        //gas_text=findViewById(R.id.gas_text);
        //state_fire_text=findViewById(R.id.state_fire_text);
    }
    private void band_click()//绑定点击事件
    {
        //Person.setOnClickListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    private void handle_loop()//开子线程循环处理树莓派发来的数据
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    try {
                        Thread.sleep(300);//让线程休息0.3s，释放cpu
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(aliyun_connect.mqtt_flag==1)
                    {
                        //设置安卓APP上的温湿度数据
                        humidity.setText(String.valueOf(aliyun_connect.Humidity));
                        temperature.setText(String.valueOf(aliyun_connect.Temperature));
                        if(aliyun_connect.fire==1)
                        {
                            state_fire_img.setImageResource(R.drawable.fire_home);
                            //state_fire_text.setText("着火了");
                        }
                        else
                        {
                            state_fire_img.setImageResource(R.drawable.yes);
                            //state_fire_text.setText("正常");
                        }
                        if(aliyun_connect.gas==1)
                        {
                            gas.setImageResource(R.drawable.gas_leak);
                            //gas_text.setText("天然气泄漏");
                        }
                        else
                        {
                            gas.setImageResource(R.drawable.yes);
                            //gas_text.setText("正常");
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) //控件点击事件
    {
//        switch(view.getId())
//        {
//            case R.id.person://跳转至个人界面
//                Intent intent=new Intent(this, PersonActivity.class);
//                startActivity(intent);
//                break;
//        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.home:

                break;
            case R.id.camera:
                break;
            case R.id.me:
                Intent intent=new Intent(HomeActivity.this, PersonActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
