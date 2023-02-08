package com.example.home_safer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    /*
    变量
     */
    LinearLayout Person;//导航键 个人

    //发布订阅
    private String host = "tcp://183.230.40.39:6002";//onenet
    //这个host是固定的，不用动，下面的改成你自己的。
    private String userName = "571035";//product ID产品🆔
    private String passWord = "ZcvPvS0HDkHdGxJViKLoHG9ciW1c6nV2v0drUZlY";// APIKEY
    private String mqtt_id = "1043894223"; //device ID
    private String mqtt_sub_topic = "humid"; //订阅主题
    private String mqtt_pub_topic = "topic_classroo1mapp"; //发布主题

    private MqttAndroidClient client;
    private MqttConnectOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Log.e("tagtag","连接失败");
        init_view();
        band_view();
        band_click();
        Toast.makeText(getBaseContext(), "nihao", Toast.LENGTH_SHORT).show();
        isConnectIsNomarl();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mqtt_init();
                try {
                    Thread.sleep(1000);
                    publishMessagePlus(mqtt_sub_topic,"34");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    private void init_view()//初始化控件
    {
        Person=new LinearLayout(this);
    }
    private void band_view()//绑定控件函数
    {
        Person=findViewById(R.id.person);
    }
    private void band_click()//绑定点击事件
    {
        Person.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) //控件点击事件
    {
        switch(view.getId())
        {
            case R.id.person://跳转至个人界面
                Intent intent=new Intent(this,PersonActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnectedS()) {
            try {
                client.unregisterResources();
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    private void Mqtt_init() {
        try {
            //host为主机名，mqtt_id为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
            client = new MqttAndroidClient(getBaseContext(), host, mqtt_id);
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置mqtt版本号A
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            // 客户端是否自动尝试重新连接到服务器
            options.setAutomaticReconnect(true);
            // 最后的遗言(连接断开时， 发动"close"给订阅了topic该主题的用户告知连接已中断)
            options.setWill(mqtt_sub_topic, "close".getBytes(), 1, true);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    stringToast("connect,error");
                    try {
                        client.connect(options, null, iMqttActionListener);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    stringToast("publish,success");
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    stringToast("subscrib,success");
                    Log.i("订阅消息", message.toString());
                }
            });

            if (!client.isConnected() && isConnectIsNomarl()) {
                client.connect(options, null, iMqttActionListener);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //判断网络状态
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            //stringToast("network,当前网络:" + name);
            return true;
        } else {
            stringToast("network,没有可用网络:");
            return false;
        }
    }

    //发布消息
    private void publishMessagePlus(String topic, String messageS) {
        if (client != null && client.isConnected()) {

            MqttMessage message = new MqttMessage();
            message.setPayload(messageS.getBytes());
            message.setQos(1);
            try {
                client.publish(topic, message);
                Log.e("tagtag","上传成功");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }
    //mqtt连接回调监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            //stringToast("MTQQ,连接成功");
            Log.e("tagtag","连接成功");
            try {
                client.subscribe(mqtt_sub_topic, 1);//订阅主题
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.e("tagtag","连接失败");
            //stringToast("MTQQ,连接失败");
            // 连接失败，重连
            try {
                if (client != null) client.connect(options, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };
    //toast工具类
    public void stringToast(String field) {
        Toast.makeText(getBaseContext(), field, Toast.LENGTH_SHORT).show();
    }


    /**
     * 判断是否连接
     */
    public Boolean isConnectedS() {
        return client != null && client.isConnected();
    }



}
