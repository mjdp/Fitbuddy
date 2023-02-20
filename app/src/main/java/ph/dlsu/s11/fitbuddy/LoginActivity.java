package ph.dlsu.s11.fitbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextView tv_welcome_back;
    private ImageView iv_logo;
    private EditText et_login_email;
    private EditText et_login_password;
    private TextView tv_forgetPassword;
    private Button btn_login;


    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = et_login_email.getText().toString().trim();
                String password = et_login_password.getText().toString().trim();

                if(emailAddress.isEmpty()){
                    et_login_email.setError("Email Address is required");
                    et_login_email.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    et_login_password.setError("Password is required");
                    et_login_password.requestFocus();
                    return;
                }

                fAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            LoginActivity.this.startActivity(intent);
                        } else{
                            Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }



                    }
                });


            }
        });

        tv_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String forgetEmail = et_login_email.getText().toString();
                if(forgetEmail.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Email is Required!", Toast.LENGTH_LONG).show();

                }
                else {
                    sendEmailReset();
                }




            }
        });


    }

    public void sendEmailReset(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = et_login_email.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Email Sent!", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Account Does Not Exist!", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void init(){
        tv_welcome_back = (TextView) findViewById(R.id.tv_welcome_back);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        et_login_email= (EditText) findViewById(R.id.et_login_email);
        et_login_email.setText("");
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        iv_logo.setImageResource(R.drawable.undraw_healthy_lifestyle_6tyl);
        tv_forgetPassword = (TextView) findViewById(R.id.tv_forgetPassword);
        fAuth = FirebaseAuth.getInstance();
    }


}