package com.example.home_safer.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.home_safer.R;
import com.example.home_safer.activity.LoadActivity;
import com.example.home_safer.database.FlagSQLiteDataBase;
import com.example.home_safer.database.MySQLiteDataBase;
import com.example.home_safer.util.StringToast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private StringToast stringtoast=new StringToast();
    private Button exit;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MySQLiteDataBase mySQLiteDataBase;//本地数据库
    private FlagSQLiteDataBase flagSQLiteDataBase;//标志数据库

    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.e("me_fragment", "onCreate: 这是我的页面" );
        stringtoast.stringToast(getContext(),"我的");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mySQLiteDataBase=new MySQLiteDataBase(getContext());//初始化数据库
        flagSQLiteDataBase=new FlagSQLiteDataBase(getContext());
    }

    public void init(View view)//初始化
    {
        exit=view.findViewById(R.id.exit);
        exit.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_me, container, false);
        init(view);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit://退出登录
                Toast.makeText(getContext(), "退出登录", Toast.LENGTH_SHORT).show();
                flagSQLiteDataBase.updateDate("false","false");
                Intent intent=new Intent(getActivity(), LoadActivity.class);
                getActivity().startActivity(intent);
        }
    }
}