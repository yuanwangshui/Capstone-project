package com.example.home_safer.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home_safer.R;
import com.example.home_safer.adapter.TimeLineAdapter;
import com.example.home_safer.model.TimeLineModel;
import com.example.home_safer.util.AXLog;
import com.example.home_safer.util.OnValueChangeListener;
import com.example.home_safer.view.TunlView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import static com.example.home_safer.view.TunlView.getTime;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment{
    private RecyclerView mRecycler;
    StyledPlayerView mVideoView;
    Button button4;

    TextView textview;
    TunlView tunlView ;
    private String url2 = "http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4";
    private String url8="rtmp://172.27.2.94:1935/live/test";
    private String url9="http://172.27.2.94/live_hls/camera.m3u8";
    private String url10="http://219.151.31.38/liveplay-kk.rtxapp.com/live/program/live/hnwshd/4000000/mnf.m3u8";
    private String url=url9;
    private String muri="172.27.226.184";
    public static final int REFRESH_TIMERTV = 1;

    List<TimeLineModel> models = new ArrayList<TimeLineModel>();
    TimeLineAdapter adapter;
    WebSocketConnection wsc;
    int flag=0;
    String temp_name;
    ExoPlayer player=null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private VideoView videoView;


    @Override
    public void onDestroy() {
        super.onDestroy();//释放资源
        if(models!=null)
            models.clear();
        if (videoView != null) {
            videoView.stopPlayback(); // 停止播放
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getData();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10000);
//                    videoView.seekTo(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();

    }
    public CameraFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {//
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void init_view(View view)//初始化控件
    {
        tunlView = view.findViewById(R.id.tunlview);
        button4=view.findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_rtmp();
            }
        });
    }

    private void init_listener()
    {
        tunlView.setmListener(new OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                AXLog.e("wzytest","滑动 value:"+value);
                textview.setText(getTime(value));
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_camera, container, false);
        init_view(view);
        init_listener();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = dateformat.format(System.currentTimeMillis());
        //textview.setText(dateStr);
        Message msg = Message.obtain();
        msg.what = REFRESH_TIMERTV ;
        handler.sendMessageDelayed(msg,1000);
        //初始化加载库文件
        mVideoView = view.findViewById(R.id.styledPlayerView);
        mRecycler = (RecyclerView) view.findViewById(R.id.time_line_recycler);
        initRecycler();//初始化recyclerview
        wsc = new WebSocketConnection();
        connect();
        get_rtmp();
        return view;
    }
    public void get_rtmp()
    {
        Log.e("button4444", "get_rtmp: ");
        player = new ExoPlayer.Builder(getContext()).build();

        mVideoView.setPlayer(player);
        if(!player.isLoading())
        {
            //MediaItem mediaItem=MediaItem.fromUri("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            //MediaItem mediaItem = MediaItem.fromUri("http://172.27.187.171/live_hls/camera.m3u8");
            MediaItem mediaItem=MediaItem.fromUri(("http://"+muri+"/live_hls/test.m3u8"));
            player.setMediaItem(mediaItem);
            player.prepare();
        }
        player.play();
    }

    public void getNowTime(){
        //String str = (String) textview.getText();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date date = sdf1.parse(str);
//            date.setTime(date.getTime()+1000);
//            textview.setText(sdf1.format(date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    private void connect() {
        System.out.println("开始连接websocket///");

        try {

            wsc.connect("ws://"+muri+":8081/api/websocket/23",
                    new WebSocketConnectionHandler() {

                        @Override
                        public void onClose(int code, String reason) {
                            System.out.println("onClose reason=" + reason);
                        }

                        @Override
                        public void onOpen() {
                            System.out.println("onOpen");
                            showtext("连接成功");
                            // wsc.sendTextMessage("Hello!");
                            // wsc.disconnect();
                        }
                        @Override
                        public void onMessage(String message) {
                            System.out.println("onMessage: " + message);
                            if(flag==0)
                            {
                                temp_name=message.substring(0,message.length()-4);
                                flag=1;
                            }
                            else{
                                Bitmap temp=base64StringToImage(message);

                                models.add(new TimeLineModel(temp_name,temp));
                                adapter.notifyDataSetChanged();
                                flag=0;
                            }

                            //showtext(message);
                        }

                        @Override
                        public void onMessage(byte[] payload, boolean isBinary) {
                            super.onMessage(payload, isBinary);
                            System.out.println("onMessage: " + payload);
                        }
                    });
        } catch (WebSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Bitmap base64StringToImage(String base64String){
        byte[] bytes= Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bitmap;
//		imageView.setImageBitmap(bitmap);
    }

    private void showtext(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }



    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        adapter = new TimeLineAdapter(models);

        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new TimeLineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 处理单击事件
                Log.e("adddd", "onItemClick: "+position );
                player = new ExoPlayer.Builder(getContext()).build();

                mVideoView.setPlayer(player);
                if(!player.isLoading())
                {
                    //MediaItem mediaItem=MediaItem.fromUri("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                    //MediaItem mediaItem = MediaItem.fromUri("http://172.27.187.171/live_hls/camera.m3u8");
                    MediaItem mediaItem=MediaItem.fromUri(("http://"+muri+"/live_hls/video"+models.get(position).getName()+".m3u8"));
                    player.setMediaItem(mediaItem);
                    player.prepare();
                }
                player.play();
                //Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<TimeLineModel> getData() {

        models.add(new TimeLineModel("XiaoMing", 21));

        return models;
    }


    Handler handler =  new Handler(){

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what){
                case REFRESH_TIMERTV:
                    Message msg = Message.obtain();
                    msg.what = REFRESH_TIMERTV ;
                    handler.sendMessageDelayed(msg,1000);
                    getNowTime();
                    break;
            }
        }
    };

}