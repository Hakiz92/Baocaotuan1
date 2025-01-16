package com.nam4.otpsend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button btnAction, btnSwitch;
    private TextView textViewTitle;

    private boolean isRegisterMode = true;  // Đang ở chế độ đăng ký hay không?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnAction = findViewById(R.id.btnAction);
        btnSwitch = findViewById(R.id.btnSwitch);
        textViewTitle = findViewById(R.id.textViewTitle);

        // Thay đổi màu chữ thành màu trắng
        btnSwitch.setTextColor(getResources().getColor(android.R.color.white));

        // Xử lý khi click vào nút Đăng ký / Đăng nhập
        btnAction.setOnClickListener(view -> {
            if (isRegisterMode) {
                registerUser();
            } else {
                loginUser();
            }
        });

        // Chuyển đổi giữa Đăng ký và Đăng nhập
        btnSwitch.setOnClickListener(view -> switchMode());
    }

    private void switchMode() {
        if (isRegisterMode) {
            isRegisterMode = false;
            textViewTitle.setText("Đăng nhập");
            btnAction.setText("Đăng nhập");
            btnSwitch.setText("Chuyển sang Đăng ký");
            editTextConfirmPassword.setVisibility(EditText.GONE);
        } else {
            isRegisterMode = true;
            textViewTitle.setText("Đăng ký");
            btnAction.setText("Đăng ký");
            btnSwitch.setText("Chuyển sang Đăng nhập");
            editTextConfirmPassword.setVisibility(EditText.VISIBLE);
        }
        clearForm();
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                                if (verifyTask.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Email xác thực đã được gửi. Vui lòng kiểm tra hộp thư!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Không thể gửi email xác thực: " + verifyTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(getApplicationContext(), "Đăng nhập thành công: " + user.getEmail(), Toast.LENGTH_LONG).show();

                            // Chuyển sang màn hình mới (HomeActivity)
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish(); // Đóng màn hình hiện tại
                        } else {
                            Toast.makeText(getApplicationContext(), "Vui lòng xác thực email trước khi đăng nhập!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearForm() {
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextConfirmPassword.setText("");
    }
}
