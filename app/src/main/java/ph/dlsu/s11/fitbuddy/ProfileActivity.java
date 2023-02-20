package ph.dlsu.s11.fitbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView iv_back_btn;
    private TextView tv_pageTitle;


    private ImageView iv_cover;
    private ImageView iv_profile;
    private TextView tv_name;
    private TextView tv_email;
    private ImageView iv_weight;
    private TextView tv_weight;
    private TextView tv_weight_input;
    private ImageView iv_height;
    private TextView tv_height;
    private TextView tv_height_input;
    private TextView tv_bmi;
    private TextView tv_bmi_input;
    private TextView tv_bmiClassification;
    private TextView tv_bmiReasoning;
    private Button btn_logout;
    private Button btn_edit_profile;

    private String fullName;
    private String emailAddress;
    private String weight;
    private String height;
    private Float bmi;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();





        bottomNavigationView.setSelectedItemId(R.id.Profile);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.calendar:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.diary:
                        startActivity(new Intent(getApplicationContext(), DiaryActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.Profile:
                        return true;
                }
                return false;
            }
        });


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mAuth.signOut();
            }
        });

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
            }
        });

        iv_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
            }
        });


    }

    public void init() {
        //Assign Variable
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        iv_back_btn = (ImageView) findViewById(R.id.iv_profilePhoto);
        tv_pageTitle = (TextView) findViewById(R.id.tv_pageTitle);
        btn_edit_profile = (Button) findViewById(R.id.btn_edit_profile);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);



        iv_cover = (ImageView) findViewById(R.id.iv_cover);
        iv_cover.setImageResource(R.drawable.top_bcakground);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);

        tv_bmi = (TextView) findViewById(R.id.tv_bmi);
        tv_bmi_input = (TextView) findViewById(R.id.tv_bmi_input);

        iv_weight = (ImageView) findViewById(R.id.iv_weight);
        iv_weight.setImageResource(R.drawable.weight);
        tv_weight = (TextView) findViewById(R.id.tv_weight);
        tv_weight_input = (TextView) findViewById(R.id.tv_weight_input);
        iv_height = (ImageView) findViewById(R.id.iv_height);
        iv_height.setImageResource(R.drawable.height);
        tv_height = (TextView) findViewById(R.id.tv_height);
        tv_height_input = (TextView) findViewById(R.id.tv_height_input);

        tv_bmiClassification = (TextView) findViewById(R.id.tv_bmiClassification);
        tv_bmiReasoning = (TextView) findViewById(R.id.tv_bmiReasoning);


        btn_logout = (Button) findViewById(R.id.btn_logout);
        fStore = FirebaseFirestore.getInstance();

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(authStateListener);

   getUserInfo();

    }



    public void getUserInfo(){

        DocumentReference docRef = fStore.collection("users").document(mAuth.getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {

                    fullName = documentSnapshot.getString("fName");
                    emailAddress = documentSnapshot.getString("emailAd");
                    weight = documentSnapshot.getString("weight");
                    height = documentSnapshot.getString("height");
                    tv_name.setText(fullName);
                    tv_email.setText(emailAddress);
                    tv_weight_input.setText(weight + " kg");
                    tv_height_input.setText(height + " cm");

                    if(documentSnapshot.getString("imageURL")!=""){
                        Picasso.get().load(documentSnapshot.getString("imageURL")).fit().into(iv_profile);
                    }

                    DecimalFormat formatter = new DecimalFormat("0.00");

                    bmi =  (Float.parseFloat(weight)/Float.parseFloat(height) / Float.parseFloat(height)) * 10000;

                    String formattedBMI = formatter.format(bmi);
                    tv_bmi_input.setText(formatter.format(bmi));

                    if(bmi<18.5){
                        tv_bmiClassification.setText("You are Underweight!");
                        tv_bmiReasoning.setText("Your current BMI is " + formattedBMI + "\n" + "Normal weight BMI is within 18.5 to 24.9" );
                    }
                    else if(bmi>=18.5 && bmi<=24.9){
                        tv_bmiClassification.setText("You are on Normal Weight!");
                        tv_bmiReasoning.setText("Keep it up!" );
                    }
                    else if (bmi>=25 && bmi<=29.9){
                        tv_bmiClassification.setText("You are Overweight!");
                        tv_bmiReasoning.setText("Your current BMI is " + formattedBMI + "\n" + "Normal weight BMI is within 18.5 to 24.9" );
                    }
                    else if(bmi>=30){
                        tv_bmiClassification.setText("You are Obese!");
                        tv_bmiReasoning.setText("Your current BMI is " + formattedBMI + "\n" + "Normal weight BMI is within 18.5 to 24.9" );
                    }

                }
                else{
                    Toast.makeText(ProfileActivity.this, "Does not Exist", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



    }


    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}