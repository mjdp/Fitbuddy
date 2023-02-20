package ph.dlsu.s11.fitbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private TextView tv_getting_started;
    private TextView tv_login;
    private EditText et_confirmPassword;
    private EditText et_fullName;
    private EditText et_password;
    private EditText et_emailAddress;

    private EditText et_birthday;
    private EditText et_weight;
    private EditText et_height;
    private Button btn_register;

    DatePickerDialog.OnDateSetListener setListener;
    private int year, month, day;


    private String confirmPassword;
    private String fullName;
    private String birthday;
    private String password;
    private String emailAddress;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private String height;
    private String weight;
    private ArrayList<String> meals = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                RegisterActivity.this.startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getRegisterInfo()) {

                    mAuth.createUserWithEmailAndPassword(emailAddress, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        userID = mAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fStore.collection("users").document(userID);

                                        Map<String,Object> user = new HashMap<>();
                                        user.put("fName", fullName);
                                        user.put("emailAd", emailAddress);
                                        user.put("imageURL", "");
                                        user.put("birthday", birthday);
                                        user.put("weight", weight);
                                        user.put("height", height);
                                        user.put("meals", meals);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("ddd","onSuccess: user profile created for" + userID);
                                            }
                                        });
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);


                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Failed to register. Try again!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });



                }
            }
        });
    }

    public void init(){
        tv_getting_started = (TextView) findViewById(R.id.tv_getting_started);
        tv_login = (TextView) findViewById(R.id.tv_login);
        et_confirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
        et_fullName = (EditText) findViewById(R.id.et_fullName);
        et_password = (EditText) findViewById(R.id.et_password);
        et_emailAddress = (EditText) findViewById(R.id.et_emailAddress);
        btn_register = (Button) findViewById(R.id.btn_register);
        et_birthday = (EditText) findViewById(R.id.et_birthday);
        et_weight= (EditText) findViewById(R.id.et_weight);
        et_height = (EditText) findViewById(R.id.et_height);
        tv_login.setPaintFlags(tv_login.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        et_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        et_birthday.setText(date);
                        birthday = date;
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

    }

    private boolean getRegisterInfo(){


        confirmPassword = et_confirmPassword.getText().toString().trim();
        fullName = et_fullName.getText().toString().trim();
        password = et_password.getText().toString().trim();
        emailAddress = et_emailAddress.getText().toString().trim();
        height = et_height.getText().toString().trim();
        weight = et_weight.getText().toString().trim();




        if(emailAddress.isEmpty()){
            et_emailAddress.setError("Email Address is required");
            et_emailAddress.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            et_emailAddress.setError("Please provide valid email");
            et_emailAddress.requestFocus();
            return false;
        }

        if(fullName.isEmpty()){
            et_fullName.setError("Full Name is required");
            et_fullName.requestFocus();
            return false ;
        }


        if(password.isEmpty() || password.length()<8){
            et_password.setError("Password is required. Password must be at least 8 characters");
            et_password.requestFocus();
            return false;
        }
        if(password.length()<8){
            et_password.setError("Password must be at least 8 characters");
            et_password.requestFocus();
            return false;
        }

        if(confirmPassword.isEmpty()){
            et_confirmPassword.setError("Please enter your password again ");
            et_confirmPassword.requestFocus();
            return false;
        }

        if(confirmPassword.compareTo(password)!=0)
        {
            et_confirmPassword.setError("Password mismatch");
            et_confirmPassword.requestFocus();
            return false;
        }
        if(weight.isEmpty()){
            et_weight.setError("Weight is required ");
            et_weight.requestFocus();
            return false;
        }

        if(height.isEmpty()){
            et_weight.setError("Height is required ");
            et_weight.requestFocus();
            return false;
        }



        return true;
    }
}