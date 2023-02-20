package ph.dlsu.s11.fitbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.dlsu.s11.fitbuddy.adapter.MealAdapter;
import ph.dlsu.s11.fitbuddy.model.Meal;

public class CalendarActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView iv_profilePhoto;
    private TextView tv_pageTitle;
    private ImageView iv_notification_btn;
    private TextView tv_date;
    private CalendarView cv_calendar;
    private List<String> meals;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String formattedDate;
    private ArrayList<Meal> mealArrayList;
    private RecyclerView rv_list;
    private MealAdapter mealAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        init();






        cv_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
               formattedDate = df.format(new Date(year-1900,month,dayOfMonth));
                tv_date.setText(formattedDate);
                mealArrayList.removeAll(mealArrayList);
                mealAdapter.notifyDataSetChanged();
                getMeals();





            }
        });

        bottomNavigationView.setSelectedItemId(R.id.calendar);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.calendar:
                        return true;
                    case R.id.diary:
                        startActivity(new Intent(getApplicationContext(),DiaryActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.Profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    default:
                        return false;
                }

            }
        });

        iv_profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                overridePendingTransition(0,0);
            }
        });





    }

    public void init(){
        //Assign Variable
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        iv_profilePhoto = (ImageView) findViewById(R.id.iv_profilePhoto);
        tv_pageTitle = (TextView) findViewById(R.id.tv_pageTitle);
        tv_date = (TextView) findViewById(R.id.tv_date);
        cv_calendar = (CalendarView) findViewById(R.id.cv_calendar);
        fStore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mealArrayList = new ArrayList<>();
        meals = new ArrayList<>();
        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);



        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mealAdapter = new MealAdapter(getApplicationContext(), mealArrayList);
        rv_list.setAdapter(mealAdapter);

        long c = cv_calendar.getDate();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedDate = df.format(c);
        tv_date.setText(formattedDate);
        getMeals();




    }

    public void getMeals(){

        DocumentReference docRef = fStore.collection("users").document(mAuth.getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    meals = (List<String>) documentSnapshot.get("meals");


                    if(meals != null) {


                        for (int i = 0; i < meals.size(); i++) {

                            DocumentReference docRefmeals = fStore.collection("meals").document(meals.get(i));
                            docRefmeals.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    if(documentSnapshot.getString("dateAdded").equals(formattedDate)){

                                        Meal meal = new Meal();
                                        meal.setImage(documentSnapshot.getString("imageURL"));
                                        meal.setDate(documentSnapshot.getString("dateAdded"));
                                        meal.setCaloriesPerServing(documentSnapshot.getString("foodCalories"));
                                        meal.setCarbsPerServing(documentSnapshot.getString("foodCarbs"));
                                        meal.setDescription(documentSnapshot.getString("foodDesc"));
                                        meal.setMealName(documentSnapshot.getString("mealName"));
                                        meal.setFatsPerServing(documentSnapshot.getString("foodFats"));
                                        meal.setProteinPerServing(documentSnapshot.getString("foodProtein"));
                                        meal.setServings(documentSnapshot.getString("foodServings"));
                                        meal.setSodiumPerServing(documentSnapshot.getString("foodSodium"));
                                        meal.setSugarPerServing(documentSnapshot.getString("foodSugar"));
                                        meal.setMealChoice(documentSnapshot.getString("mealChoice"));
                                        mealArrayList.add(meal);
                                        mealAdapter.notifyDataSetChanged();

                                    }

                                }
                            });
                        }
                    }

                }
                else{
                    Toast.makeText(CalendarActivity.this, "Does not Exist", Toast.LENGTH_SHORT).show();
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