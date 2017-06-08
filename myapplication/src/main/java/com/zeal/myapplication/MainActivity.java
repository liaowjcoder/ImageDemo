package com.zeal.myapplication;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    /*
    Bitmap 的高效加载：

    为什么要对需要加载的图片进行压缩呢？

        举例：假如 ImageView 大小： 200*400
        需要加载的图片大小 1080*1920
        这样要把这么大的图片加载到 ImageView 上就完全没有必要了，而且很容易 oom

        假如图片采用 ARGB8888 格式存储的话，那么一个像素就占用了 4 个字节
        1080*1920*4 = 7.9m 也就是说加载这么一张图片需要 7.9m 的空间。

    核心就是对需要加载图片进行压缩，使用 BitmapFactory.Option 来加载指定大小的图片。

    压缩图片有四种方式：
       BitmapFactory.decodeFile:针对文件系统
       BitmapFactory.decodeResource：针对资源文件
       BitmapFactory.decodeByteArray:针对字节数组
       BitmapFactory.decodeStream：针对输入流


    操作步骤：
        1.options.inJustDecodeBounds = true;
        设置该标记未 true 表示只加载 bitmap 的大小信息，并不会将整个 Bitmap 加载到内存中

        2.BitmapFactory.decodeXX(options)

        3.根据得到的 options 计算 inSampleSize

        4.options.inJustDecodeBounds = false;
        设置该标记为 false 表示将 Bitamo 真正加载到内存中。

        5.Bitmap bmp = BitmapFactory.decodeXX(options)
     */

    private void decode() {

        BitmapFactory.Options opts = new BitmapFactory.Options();

        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, opts);


        //计算 inSampleSize
        opts.inSampleSize = calculateInSampleSize(opts, 300, 300);

        //加载 Bitmap 到内存中
        opts.inJustDecodeBounds = false;

        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, opts);

    }

    /**
     * 计算 inSampleSize
     *
     * @param opts
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options opts, int reqWidth, int reqHeight) {

        int inSampleSize = 1;

        final int width = opts.outWidth;
        final int height = opts.outHeight;

        if (height > reqHeight || width > reqWidth) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;


            //500*800 1
            //250*400 2
            //125*200 4 不成立，最后 inSampleSize = 4
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
