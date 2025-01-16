package com.nam4.otpsend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLogout = findViewById(R.id.btnLogout);

        // Xử lý sự kiện nút đăng xuất
        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut(); // Đăng xuất khỏi Firebase
            Toast.makeText(HomeActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            // Quay lại màn hình đăng nhập
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng màn hình hiện tại
        });
    }
}
