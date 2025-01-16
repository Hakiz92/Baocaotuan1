package com.nam4.otpsend;

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

    private boolean isRegisterMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các thành phần giao diện
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnAction = findViewById(R.id.btnAction);
        btnSwitch = findViewById(R.id.btnSwitch);
        textViewTitle = findViewById(R.id.textViewTitle);

        // Xử lý khi click nút Đăng ký hoặc Đăng nhập
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

    // Chuyển chế độ giữa Đăng ký và Đăng nhập
    private void switchMode() {
        isRegisterMode = !isRegisterMode;
        if (isRegisterMode) {
            textViewTitle.setText("Đăng ký");
            btnAction.setText("Đăng ký");
            btnSwitch.setText("Chuyển sang Đăng nhập");
            editTextConfirmPassword.setVisibility(EditText.VISIBLE);
        } else {
            textViewTitle.setText("Đăng nhập");
            btnAction.setText("Đăng nhập");
            btnSwitch.setText("Chuyển sang Đăng ký");
            editTextConfirmPassword.setVisibility(EditText.GONE);
        }
        clearForm();
    }

    // Đăng ký người dùng mới
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        clearForm();
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Đăng nhập người dùng
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Chào mừng: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        clearForm();
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Xóa dữ liệu sau khi xử lý xong
    private void clearForm() {
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextConfirmPassword.setText("");
    }
}
