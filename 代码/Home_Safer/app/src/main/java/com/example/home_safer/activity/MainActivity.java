package com.example.home_safer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.home_safer.R;
import com.example.home_safer.fragment.CameraFragment;
import com.example.home_safer.fragment.HomeFragment;
import com.example.home_safer.fragment.MeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    HomeFragment homeFragment;
    CameraFragment cameraFragment;
    MeFragment meFragment;
    Fragment fragment;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_re);
        homeFragment = new HomeFragment();
        cameraFragment = new CameraFragment();
        meFragment = new MeFragment();
        fragment = homeFragment;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.frame_layout, homeFragment)
              .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                fragment = homeFragment;
                break;
            case R.id.camera:
                fragment = cameraFragment;
                break;
            case R.id.me:
                fragment = meFragment;
                break;
        }
        getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.frame_layout, fragment)
              .commit();
        return true;
    }
}