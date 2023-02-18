package com.example.home_safer.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.home_safer.R;
import com.example.home_safer.util.mqtt_tool.aliyun_connect;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        aliyun_connect.connect(getContext(),"iQOOz6","1433b0130804ec5d5b3638a9e9a4cb5a");//连接阿里云物联网平台
        //init();
        handle_loop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        init_view();
        band_view(view);
        return view;
    }


    private void init()//初始化
    {
        init_view();
        //band_view();
        //band_click();
    }
    private void init_view()//初始化控件
    {
        Person=new LinearLayout(getContext());
        temperature=new TextView(getContext());
        humidity=new TextView(getContext());
        state_fire_img=new ImageView(getContext());//house里的火焰监控
        gas=new ImageView(getContext());
        //gas_text=new TextView(this);
        //state_fire_text=new TextView(this);
    }
    private void band_view(View view)//绑定控件函数
    {
        //Person=findViewById(R.id.person);
        temperature=view.findViewById(R.id.temperature);
        humidity=view.findViewById(R.id.humidity);
        state_fire_img=view.findViewById(R.id.state_fire_img);
        gas=view.findViewById(R.id.gas);
        bottomNavigationView=view.findViewById(R.id.bottomNavigationView);
        //gas_text=findViewById(R.id.gas_text);
        //state_fire_text=findViewById(R.id.state_fire_text);
    }

    @Override
    public void onClick(View view) {

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


}