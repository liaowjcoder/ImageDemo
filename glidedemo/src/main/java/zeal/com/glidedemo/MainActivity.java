package zeal.com.glidedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class MainActivity extends AppCompatActivity {
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageview = (ImageView) findViewById(R.id.imageview);

        display4();
    }


    private void display() {
        //加载网络资源
        Glide.with(MainActivity.this)
                .load("http://s5.51cto.com/wyfs01/M00/09/A4/wKioJlGUh0Kx_g-aAACh-2hVxZo895.jpg").into(imageview);


        //加载应用图片资源
//        int res = R.mipmap.ic_launcher;
//        Glide.with(MainActivity.this).load(res).into(imageview);
    }

    private void display2() {
        //加载网络资源
        Glide.with(MainActivity.this)
                .load("http://s5.51cto.com/wyfs01/M00/09/A4/wKioJlGUh0Kx_g-aAACh-2hVxZo895.jpg")
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageview);


    }

    //gif
    private void display3() {
        Glide.with(MainActivity.this)
                .load("http://p1.pstatp.com/large/166200019850062839d3")
                .asGif()
//                .asBitmap()//只作为静态图片加载。
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageview);


    }
    //指定图片大小
    private void display4() {
        Glide.with(MainActivity.this)
                .load("http://s5.51cto.com/wyfs01/M00/09/A4/wKioJlGUh0Kx_g-aAACh-2hVxZo895.jpg")
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.error)
                .override(100,100)//指定glide加载到内存的大小,这个指定的大小貌似在展示的gif不起作用。
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageview);


    }

}
