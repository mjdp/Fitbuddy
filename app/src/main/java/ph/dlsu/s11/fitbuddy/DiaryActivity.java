package ph.dlsu.s11.fitbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.dlsu.s11.fitbuddy.adapter.MealAdapter;
import ph.dlsu.s11.fitbuddy.model.Meal;

public class DiaryActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageView iv_profilePhoto;
    private TextView tv_pageTitle;

    private LinearLayout ll_addmeal;
    private ImageView iv_addmeal;
    private TextView tv_addmeal;
    private String formattedDate;
    private int year, month, day;
    private ArrayList<Meal> mealArrayList;
    private Button btn_previousDate;
    private Button btn_nextDate;
    private TextView tv_dateDisplay;
    private RecyclerView rv_list;
    private MealAdapter mealAdapter;
    private List<String> meals;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String currentDateDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        init();


        bottomNavigationView.setSelectedItemId(R.id.diary);
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
                        startActivity(new Intent(getApplicationContext(),CalendarActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.diary:
                        return true;
                    case R.id.Profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        btn_previousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formattedDate = getPreviousDate(currentDateDisplayed);
                currentDateDisplayed = formattedDate;
                tv_dateDisplay.setText(formattedDate);
                mealArrayList.removeAll(mealArrayList);
                mealAdapter.notifyDataSetChanged();
                getMeals();
            }
        });

        btn_nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formattedDate = getNextDate(currentDateDisplayed);
                currentDateDisplayed = formattedDate;
                tv_dateDisplay.setText(formattedDate);
                mealArrayList.removeAll(mealArrayList);
                mealAdapter.notifyDataSetChanged();
                getMeals();
            }
        });

        tv_dateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                day = cldr.get(Calendar.DAY_OF_MONTH);
                month = cldr.get(Calendar.MONTH);
                year = cldr.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String date = df.format(new Date(year-1900,month,day));
                        tv_dateDisplay.setText(date);
                        currentDateDisplayed = date;
                        formattedDate = currentDateDisplayed;
                        mealArrayList.removeAll(mealArrayList);
                        mealAdapter.notifyDataSetChanged();
                        getMeals();
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        iv_profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0,0);
            }
        });


        ll_addmeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddMealActivity.class);
                i.putExtra("dateDiary", formattedDate);
                startActivity(i);
                overridePendingTransition(0,0);

            }
        });

    }

    public void init(){
        //Assign Variable
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        iv_profilePhoto = (ImageView) findViewById(R.id.iv_profilePhoto);
        tv_pageTitle = (TextView) findViewById(R.id.tv_pageTitle);
        ll_addmeal = (LinearLayout) findViewById(R.id.ll_addmeal);
        iv_addmeal = (ImageView) findViewById(R.id.iv_addmeal);
        tv_addmeal = (TextView) findViewById(R.id.tv_addmeal);
        btn_previousDate = (Button) findViewById(R.id.btn_previousDate);
        btn_nextDate = (Button) findViewById(R.id.btn_nextDate);
        tv_dateDisplay = (TextView) findViewById(R.id.tv_dateDisplay);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);


        iv_addmeal.setImageResource(R.drawable.ic_baseline_add_24);


        fStore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mealArrayList = new ArrayList<>();
        meals = new ArrayList<>();

        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mealAdapter = new MealAdapter(getApplicationContext(), mealArrayList);
        rv_list.setAdapter(mealAdapter);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        currentDateDisplayed = df.format(c);
        formattedDate = currentDateDisplayed;
        tv_dateDisplay.setText(formattedDate);
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
                                        meal.setMealID(docRefmeals.getId());
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
                    Toast.makeText(DiaryActivity.this, "Does not Exist", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    private String getPreviousDate(String inputDate){
        SimpleDateFormat  format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = format.parse(inputDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            c.add(Calendar.DATE, -1);
            inputDate = format.format(c.getTime());
            Log.d("asd", "selected date : "+inputDate);

            System.out.println(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            inputDate ="";
        }
        return inputDate;
    }

    private String getNextDate(String inputDate){
        SimpleDateFormat  format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = format.parse(inputDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            c.add(Calendar.DATE, +1);
            inputDate = format.format(c.getTime());
            Log.d("asd", "selected date : "+inputDate);

            System.out.println(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            inputDate ="";
        }
        return inputDate;
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mealAdapter.notifyDataSetChanged();
    }
}