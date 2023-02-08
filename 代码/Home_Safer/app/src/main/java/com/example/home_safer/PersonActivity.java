package com.example.home_safer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class PersonActivity extends AppCompatActivity implements View.OnClickListener{
    /*
    变量
     */
    LinearLayout Home;//导航键 首页
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        init_view();
        band_view();
        band_click();
    }

    private void init_view()//初始化控件
    {
        Home=new LinearLayout(this);
    }
    private void band_view()//绑定控件函数
    {
        Home=findViewById(R.id.home);
    }
    private void band_click()//绑定点击事件
    {
        Home.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) //控件点击事件
    {
        switch(view.getId())
        {
            case R.id.home://跳转至个人界面
                Intent intent=new Intent(this,HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
