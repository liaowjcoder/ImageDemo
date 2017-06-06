package zeal.com.imagedemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import zeal.com.imagedemo.libcore.io.DiskLruCache;

import static android.R.attr.key;

public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;

    private String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496047902&di=c9eb9a14079d6f6d11f882847a7ba087&imgtype=jpg&er=1&src=http%3A%2F%2Fwww.sxdaily.com.cn%2FNMediaFile%2F2014%2F0327%2FSXRB201403270751000502473647385.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageview);
        init();

        display();

//        diskLruCacheTest();
//        hashMap();


//        System.out.println("start");
//        T t = new T();
//        System.out.println("end");

//        int i = 0;
//        while (true) {
//
//            if(i++== 55) {
//                //遇到break 就不再继续 while 循环了。
//                break;
//            }
//            System.out.println("i:"+i);
//        }
        //linkedHashMapDemo2();

//        filterInputStreamDemo();

//        jpgDemo();
    }


    private void display() {

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        //Config.RGB_565（一个像素用2个byte表示）比 ARGB_8888（一个像素用4个byte表示）开销低2倍
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        builder.cacheInMemory(true);
//        ImageSize targetSize = new ImageSize(100, 300);
        ImageLoader.getInstance().
                displayImage(url, new ImageViewAware(mImageView), builder.build(), /*targetSize*/null, null, null);

//    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
//                             ImageSize targetSize, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {


    }

    private void init() {

        if(ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().clearMemoryCache();
            //ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().destroy();
        }

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        //
        config.memoryCache(new WeakMemoryCache());
        //这个不用设置内部也会有一个默认的 memoryCache 对象。
        //if (memoryCacheSize == 0) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        int memoryClass = am.getMemoryClass();
//        if (hasHoneycomb() && isLargeHeap(context)) {
//            memoryClass = getLargeMemoryClass(am);
//        }
//        memoryCacheSize = 1024 * 1024 * memoryClass / 8;
//    }
        //config.memoryCacheSize()

        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        try {
            config.diskCache(new LruDiskCache(getCacheDir(), new Md5FileNameGenerator(), 30 * 1024 * 1024));
        } catch (IOException e) {
            e.printStackTrace();
        }
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        //网速缓慢时调用。
        //ImageLoader.getInstance().handleSlowNetwork(true);
        //没有网络权限时，应该拒绝下载
        //ImageLoader.getInstance().denyNetworkDownloads(true);
        ImageLoader.getInstance().init(config.build());
    }


    private void diskLruCacheTest() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    //打开缓存
                    DiskLruCache diskLruCache = DiskLruCache.open(getDirectory(MainActivity.this, "bitmap"), getVersion(MainActivity.this), 1, 10 * 1024 * 1024);


                    String imageUrl = "http://img03.tooopen.com/images/20131111/sy_46708898917.jpg";

                    //写入缓存
                    DiskLruCache.Editor editor = diskLruCache.edit(hashKeyForDisk(imageUrl));


                    OutputStream outputStream = editor.newOutputStream(0);

                    boolean isSuccess = downloadUrlToStream(imageUrl, outputStream);

                    if (isSuccess) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }

                    //刷新一些缓存空间。
                    diskLruCache.flush();


//                    DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyForDisk(imageUrl));
//
//                    InputStream inputStream = snapshot.getInputStream(0);
//
//                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mImageView.setImageBitmap(bitmap);
//                        }
//                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();


    }

    private File getDirectory(Context context, String uniqueName) {
        String cachePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                !Environment.isExternalStorageRemovable()
                ) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getVersion(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * 下载文件到指定的输出流中 outputStream
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

//    /**
//     * JDK1.8新添加的新特性，遍历 map 中的所有元素。
//     */
//    private void hashMap(){
//        HashMap<String,Integer> hashMap = new HashMap<String,Integer>();
//
//        hashMap.forEach(new BiConsumer<String, Integer>() {
//            @Override
//            public void accept(String s, Integer integer) {
//
//            }
//        });
//    }

    private void hashMap() {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        hashMap.put("hello", 1);
        hashMap.put("hello2", 2);
        hashMap.put("hello3", 3);

        hashMap.remove("key");

        Set<Map.Entry<String, Integer>> entries = hashMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            String key = entry.getKey();
            Integer value = entry.getValue();
        }
    }

    class T {

        private String name;

        public T() {

        }

        @Override
        public String toString() {
            this.name = "abc";
            System.out.println("输出了:" + name);
            return super.toString();
        }

    }

    private void demo() {
        List<String> test = null;
        String[] objects = (String[]) test.toArray();
    }


    private void linkedHashMapDemo() {
        //第三个参数是 accessOrder = true 表示按照访问顺序排序链表
        LinkedHashMap<String, String> map = new LinkedHashMap<>(0, 0.75f, true);
        //新插入的数据都会排序到链表的尾部
        map.put("1", "hello1");
        map.put("2", "hello2");
        map.put("3", "hello3");
        //这里又访问的key=1的元素因此它会重新排序到链表尾部
        map.get("1");
        map.put("4", "hello4");


        /*
        map.put("1", "hello1");
        map.put("2", "hello2");
        map.put("3", "hello3");
        map.put("4", "hello4");
        key:1;value:hello1
        key:2;value:hello2
        key:3;value:hello3
        key:4;value:hello4
        可以看出 LinkedHashMap 是按照顺序的方式输出的。

        map.put("1", "hello1");
        map.put("2", "hello2");
        map.put("3", "hello3");
        map.get("1");
        map.put("4", "hello4");
        key:2;value:hello2
        key:3;value:hello3
        key:1;value:hello1
        key:4;value:hello4
         */
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("key:" + entry.getKey() + ";value:" + entry.getValue());
        }
    }

    private void linkedHashMapDemo2() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(0, 0.75f, false) {

            @Override
            protected boolean removeEldestEntry(Entry eldest) {
                //判断当前元素个数若是大于3表示需要移除先前的元素
                return size() > 3;
            }
        };
        map.put("1", "hello1");
        map.put("2", "hello2");
        map.put("3", "hello3");
        map.put("4", "hello4");
        map.put("5", "hello5");
        map.put("6", "hello6");
        /*
        key:4;value:hello4
        key:5;value:hello5
        key:6;value:hello6
         */
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("key:" + entry.getKey() + ";value:" + entry.getValue());
        }
    }

    private void filterInputStreamDemo() {

        String filePath = "";

        InputStream is = null;
        FilterInputStream fis = null;
        int i = 0;
        char c;
        try {
            is = new FileInputStream(filePath);
            //将 inputstream 包装成一个 FilterInputStream 类型的对象
            fis = new BufferedInputStream(is);

            while ((i = fis.read()) != -1) {
                c = (char) i;

                //skip 3bytes
                fis.skip(3);

                System.out.println("ch = " + c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //io close...
        }


    }

    private void jpgDemo() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL("https://img09.rl0.ru/b4550fc108bd88afccbdcd4f79192814/c909x618/xn--90agbbbjecccx3a7cu4dq.xn--p1ai/wp-content/uploads/2015/09/android-.jpg");
                    InputStream content = (InputStream) url.getContent();
                    Bitmap bmp = BitmapFactory.decodeStream(content);
                    System.out.println("bmp = " + bmp);
                    final Drawable d = Drawable.createFromStream(content, "src");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageDrawable(d);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    private void listviewImageLoaderDemo() {

        ImageLoader imageLoader = ImageLoader.getInstance();

        //这里的 onScrollListener 是在 PauseOnScrollListener 中回调的。如果外界需要做一些额外的操作就可以传入一个 onScrollListener
        AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };
        //这样就做到了 ListView 和 imageloader 加载图片过程的绑定了
        PauseOnScrollListener pos = new PauseOnScrollListener(imageLoader, true, true, onScrollListener);

        ListView listview = new ListView(this);
        listview.setOnScrollListener(onScrollListener);

    }

}

