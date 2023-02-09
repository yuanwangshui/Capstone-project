package com.example.home_safer.mqtt_tool;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class aliyun_connect {
    private static final String TAG = "AiotMqtt";
    private static int Temperature=0;//室内温度
    private static int humidity=0;//室内湿度
    public static float OutdoorTemperature=0;//室内温度
    public static int mqtt_flag=0;//改变标志
    public static int No_Rain=0;//0表示没雨
    public static int Peculiar_smell=1;//异味
    public static int is_fire=1;//0表示没火
    public static float Humidity=0;//湿度
    public static int LightClock=0;//灯光开关
    public static float IndoorTemperature=0;//室外温度
    public static int state_conditioner=0;//0表示关闭回调，1表示开启
    public static float conditioner_time=0;//预约时长回调
    public static int temp_conditioner=0;//空调设置温度回调 4表示温度为28,5表示温度为21
    public static int pattern_conditioner=0;//2表示制热，3表示制冷
    public static int LightSwitch=0;//灯光闹钟开关
    /* 设备三元组信息 */
    final static private String PRODUCTKEY = "go88FIo7AKL";//gs7skF74Z2Y
    static private String DEVICENAME_1 /*= "Android"*/;
    static private String DEVICESECRET_1 /*= "43993dd5e2a594e29f202fa13c710eff"*/;

    static private EditText editText;
    static private String pub_topic;
    /* 自动Topic, 用于上报消息 */
    static private String PUB_TOPIC /*= "/" + PRODUCTKEY + "/" + DEVICENAME + "/user/update"*/;

    /* 自动Topic, 用于接受消息 */
    static private String SUB_TOPIC /*= "/" + PRODUCTKEY + "/" + DEVICENAME + "/user/get"*/;

    /* 阿里云Mqtt服务器域名 */
    final static String host = "tcp://" + PRODUCTKEY + ".iot-as-mqtt.cn-shanghai.aliyuncs.com:443";
    static private String clientId;
    static private String userName;
    static private String passWord;

    static MqttAndroidClient mqttAndroidClient;


    public static boolean connect(Context context,String DEVICENAME,String DEVICESECRET)//连接aliyun 函数
    {
        DEVICENAME_1=DEVICENAME;

        PUB_TOPIC="/" + PRODUCTKEY + "/" + DEVICENAME + "/user/update";
        pub_topic="/sys" + PRODUCTKEY + "/" + DEVICENAME + "/thing.service.property.set";
        SUB_TOPIC = "/" + PRODUCTKEY + "/" + DEVICENAME + "/user/get";
        AiotMqttOption aiotMqttOption = new AiotMqttOption().getMqttOption(PRODUCTKEY, DEVICENAME, DEVICESECRET);
        if (aiotMqttOption == null) {
            Log.e(TAG, "device info error");
        } else {
            clientId = aiotMqttOption.getClientId();
            userName = aiotMqttOption.getUsername();
            passWord = aiotMqttOption.getPassword();
        }

        /* 创建MqttConnectOptions对象并配置username和password */
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setPassword(passWord.toCharArray());


        /* 创建MqttAndroidClient对象, 并设置回调接口 */
        mqttAndroidClient = new MqttAndroidClient(context, host, clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG, "topic: " + topic + ", msg: " + new String(message.getPayload()));
                String []str=message.toString().split("\"");//用"将字符串分割开
                int temp_1=0;
                String compare_str="\"thing.event.property.post\",\"version\"";
                Judge(str);
                //String []str_1=str[2].split(",");
                //temp=Integer.parseInt(str_1[0]);//转换为int型
                //OutdoorTemperature=Integer.parseInt(str_1[0].substring(0,str_1[0].length()-1));
                mqtt_flag=1;
                for (String s : str) {
                    if (s.equals(compare_str)) {
                        temp_1 = 1;
                        break;
                    }
                }
                publishMessage_2(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i(TAG, "msg delivered");
            }
        });

        /* Mqtt建连 */
        try {
            mqttAndroidClient.connect(mqttConnectOptions,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "connect succeed");

                    subscribeTopic(SUB_TOPIC);
                    String name="/sys/"+PRODUCTKEY + "/" + DEVICENAME+"/thing/event/property/set";
                    subscribeTopic(name);
                    //subscribeTopic("/sys/go88ecqGJf7/host_phone_2/thing/event/property/set");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "connect failed");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void Judge(String[] str) {
        for(int i=0;i<str.length-1;i++)
        {
            if(str[i].equals("LightClock"))//和灯光开关匹配
            {
                if(str[i+1].charAt(3)==','| str[i+1].charAt(3)=='}')
                {
                    LightClock=str[i+1].charAt(2)-'0';//转换为int
                }
                else
                {
                    LightClock=(str[i+1].charAt(2)-'0')*10+str[i+1].charAt(3)-'0';//转换为int，两个数字
                }
            }
            if(str[i].equals("Weather"))//和天气匹配
            {
                if(str[i+1].charAt(3)==','| str[i+1].charAt(3)=='}')
                {
                    No_Rain=str[i+1].charAt(2)-'0';//转换为int
                }
                else
                {
                    No_Rain=(str[i+1].charAt(2)-'0')*10+str[i+1].charAt(3)-'0';//转换为int，两个数字
                }
            }
            if(str[i].equals("Peculiar_smell"))//和异味匹配
            {
                if(str[i+1].charAt(3)==','| str[i+1].charAt(3)=='}')
                {
                    Peculiar_smell=str[i+1].charAt(2)-'0';//转换为int
                }
                else
                {
                    Peculiar_smell=(str[i+1].charAt(2)-'0')*10+str[i+1].charAt(3)-'0';//转换为int，两个数字
                }
            }
            if(str[i].equals("Fire"))//和火焰报警匹配
            {
                if(str[i+1].charAt(3)==','| str[i+1].charAt(3)=='}')
                {
                    is_fire=str[i+1].charAt(2)-'0';//转换为int
                }
                else
                {
                    is_fire=(str[i+1].charAt(2)-'0')*10+str[i+1].charAt(3)-'0';//转换为int，两个数字
                }
            }
            if(str[i].equals("Temperature"))//和室内温度匹配
            {
                Temperature= Integer.parseInt(str[i+1].substring(1,str[i+1].length()-2));
            }
            if(str[i].equals("Humidity"))//和室内湿度匹配
            {
                Humidity=Float.parseFloat(str[i+1].substring(2,str[i+1].length()-3));
                //break;
            }
            if(str[i].equals("IndoorTemperature"))//和室内温度匹配
            {
                IndoorTemperature=Float.parseFloat(str[i+1].substring(2,str[i+1].length()-2));
                //break;
            }
            if(str[i].equals("LightSwitch"))//和灯光闹钟匹配
            {
                LightSwitch=Integer.parseInt(str[i+1].substring(2,str[i+1].length()-3));

                //break;
            }
            if(str[i].equals("AirConditioner"))//和室内温度匹配
            {
                if(str[i+1].charAt(str[i+1].length()-3)=='0')
                {
                    state_conditioner=0;
                }
                else if(str[i+1].charAt(str[i+1].length()-3)=='1')
                {
                    state_conditioner=1;
                }
                else if(str[i+1].charAt(str[i+1].length()-3)=='2')
                {
                    pattern_conditioner=2;
                }
                else if(str[i+1].charAt(str[i+1].length()-3)=='3')
                {
                    pattern_conditioner=3;
                }
                else if(str[i+1].charAt(str[i+1].length()-3)=='4')
                {
                    temp_conditioner=28;
                }
                else if(str[i+1].charAt(str[i+1].length()-3)=='5')
                {
                    temp_conditioner=21;
                }
            }
        }
    }

    /**
     * 订阅特定的主题
     * @param topic mqtt主题
     */
    public static void subscribeTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "subscribed succeed");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "subscribed failed");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * 向默认的主题/user/update发布消息
     * @param payload 消息载荷
     */
    public static void publishMessage(String payload) {
        try {
            if (!mqttAndroidClient.isConnected()) {
                mqttAndroidClient.connect();
            }

            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(0);
            mqttAndroidClient.publish(pub_topic, message,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "publish succeed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "publish failed!");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
    public static void publishMessage_2(String payload) {
        try {
            if (mqttAndroidClient.isConnected() == false) {
                mqttAndroidClient.connect();
            }

            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(0);
            String name="/sys/"+PRODUCTKEY + "/" + DEVICENAME_1+"/thing/event/property/post";
            mqttAndroidClient.publish(name, message,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "publish succeed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "publish failed!");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
}

