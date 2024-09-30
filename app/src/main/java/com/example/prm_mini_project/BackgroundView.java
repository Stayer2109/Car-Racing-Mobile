package com.example.prm_mini_project;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class BackgroundView extends AppCompatActivity {

    ImageView backgroundView1;
    ImageView backgroundView2;

    Handler handler = new Handler();
    Runnable runnable;
    int screenWidth;
    int speed = 8; // Tốc độ di chuyển của background (pixel)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_background_view);

        backgroundView1 = findViewById(R.id.backgroundView1);
        backgroundView2 = findViewById(R.id.backgroundView2);

        backgroundView1.post(() -> {
            screenWidth = backgroundView1.getWidth();
            startAnimation();
        });
    }

    private void startAnimation() {
        // Đặt vị trí ban đầu của ảnh thứ hai nằm bên phải ảnh thứ nhất
        backgroundView2.setX(screenWidth);

        // Runnable để tạo hiệu ứng cuộn liên tục
        runnable = new Runnable() {
            @Override
            public void run() {
                // Di chuyển backgroundView1 và backgroundView2 sang trái
                backgroundView1.setX(backgroundView1.getX() - speed);
                backgroundView2.setX(backgroundView2.getX() - speed);

                // Kiểm tra nếu backgroundView1 đã di chuyển ra khỏi màn hình
                if (backgroundView1.getX() + backgroundView1.getWidth() <= 0) {
                    // Đặt backgroundView1 ngay phía sau backgroundView2
                    backgroundView1.setX(backgroundView2.getX() + backgroundView2.getWidth());
                }

                // Kiểm tra nếu backgroundView2 đã di chuyển ra khỏi màn hình
                if (backgroundView2.getX() + backgroundView2.getWidth() <= 0) {
                    // Đặt backgroundView2 ngay phía sau backgroundView1
                    backgroundView2.setX(backgroundView1.getX() + backgroundView1.getWidth());
                }

                // Gọi lại runnable sau 16ms (~60fps)
                handler.postDelayed(this, 16);
            }
        };

        // Bắt đầu Runnable
        handler.post(runnable);
    }


}