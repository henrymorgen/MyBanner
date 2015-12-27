package com.example.liuwangshu.mybanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlideShowView extends FrameLayout {
    private final static int IMAGE_COUNT = 5;
    private final static int TIME_INTERVAL = 5;
    private final static boolean isAutoPlay = true;
    private String[] imageUrls;
    private String[] urls;
    private String[] titles;
    private String[] contents;
    private List<ImageView> imageViewsList;
    private List<View> dotViewsList;

    private ViewPager viewPager;
    private int currentItem = 0;
    private ScheduledExecutorService scheduledExecutorService;

    private Context context;

    // Handler
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }

    };
    private LinearLayout dotLayout;

    public SlideShowView(Context context) {
        this(context, null);
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
                true);
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();
    }

    public void setView(String[] imageUrls) {
        this.imageUrls = imageUrls;

        initUI(context);
        if (isAutoPlay) {
            startPlay();
        }
    }

    public void setUrl(String[] urls) {
        this.urls = urls;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public void setContents(String[] contents) {
        this.contents = contents;
    }

    private void startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4,
                TimeUnit.SECONDS);
    }

    private void stopPlay() {
        scheduledExecutorService.shutdown();
    }

    private void initUI(Context context) {
        if (imageUrls == null || imageUrls.length == 0)
            return;
        if (dotLayout != null) {
            dotLayout.removeAllViews();
        } else {
            dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        }
        dotLayout.removeAllViews();
        dotViewsList.clear();
        imageViewsList.clear();

        for (int i = 0; i < imageUrls.length; i++) {
            ImageView view = new ImageView(context);
            view.setTag(imageUrls[i]);
            view.setScaleType(ScaleType.FIT_XY);
            imageViewsList.add(view);
            ImageView dotView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 4;
            params.rightMargin = 4;
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);
        viewPager.setAdapter(new PhotoAdapter(imageUrls));
        viewPager.setOnPageChangeListener(new MyPageChangeListener());

    }

    private class PhotoAdapter extends PagerAdapter {
        private String[] images;
        private LayoutInflater inflater;

        PhotoAdapter(String[] images) {
            this.images = images;

        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(View container, final int position) {
            if (position > imageViewsList.size() - 1
                    || images.length < position + 1) {
                return null;
            }

            ImageView imageView = imageViewsList.get(position);
            if (!TextUtils.isEmpty(images[position])) {
                loadImageByVolley(imageView,images[position]);
            }
            ((ViewPager) container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        /**
         * 检查点击的轮播图是否可以跳转
         *
         * @param position
         * @return
         */
        private boolean isItemAvailable(int position) {
            boolean isUrlsAvaiable = null != urls && urls.length > position;
            boolean isTitiesAvaiable = null != titles && titles.length > position;
            boolean isContentsAvaiable = null != contents && contents.length > position;
            return isUrlsAvaiable && isTitiesAvaiable && isContentsAvaiable;
        }

        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public void finishUpdate(View arg0) {
        }

    }

    private class MyPageChangeListener implements OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (viewPager.getCurrentItem() == viewPager.getAdapter()
                            .getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    } else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager
                                .setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {
            currentItem = pos;
            for (int i = 0; i < dotViewsList.size(); i++) {
                if (i == pos) {
                    dotViewsList.get(pos)
                            .setBackgroundResource(R.drawable.dot_focus);
                } else {
                    dotViewsList.get(i)
                            .setBackgroundResource(R.drawable.dot_blur);
                }
            }
        }
    }

    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    }
    private void loadImageByVolley(ImageView imageView ,String imageUrl){
        RequestQueue mQueue = Volley.newRequestQueue(context);
        final BitmapCache lruCache=new BitmapCache();
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.putBitmap(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.getBitmap(key);
            }
        };
        ImageLoader imageLoader = new ImageLoader(mQueue, imageCache);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(
                imageView, 0,0);
        imageLoader.get(imageUrl, listener);
    }

    private void destoryBitmaps() {
        for (int i = 0; i < imageViewsList.size(); i++) {
            ImageView imageView = imageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                drawable.setCallback(null);
            }
        }
    }

}