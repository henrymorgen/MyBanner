package com.example.liuwangshu.mybanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MyBannerActivity extends AppCompatActivity {
    private SlideShowView SlideShowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_banner);
        String[] imageUrls = {"http://img0.imgtn.bdimg.com/it/u=4087866388,590061000&fm=21&gp=0.jpg",
                "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1212/25/c0/16875142_1356415699831.jpg",
                "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1212/25/c0/16875142_1356415699827.jpg",
                "http://attimg.dospy.com/img/day_140825/20140825_206ace20a2932eb6748d7L7kk7MrkYmu.jpg",
                "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1212/25/c0/16875142_1356415699833.jpg"};
        SlideShowView= (com.example.liuwangshu.mybanner.SlideShowView) this.findViewById(R.id.sv_photo);
        SlideShowView.setView(imageUrls);
    }
}
