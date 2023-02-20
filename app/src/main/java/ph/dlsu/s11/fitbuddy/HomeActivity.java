package ph.dlsu.s11.fitbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
//import org.eazegraph.lib.communication.IOnItemFocusChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.dlsu.s11.fitbuddy.model.Meal;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView iv_profilePhoto;
    private TextView tv_pageTitle;

    private TextView tv_calories;
    private TextView tv_fats;
    private TextView tv_carbs;
    private TextView tv_sodium;
    private TextView tv_protein;
    private TextView tv_sugar;
    private TextView tv_startDate;
    private TextView tv_endDate;
    private String startDate;
    private String endDate;
    private int year, month, day;
    private PieChart pieChart;
    private RelativeLayout rl_calories;
    private RelativeLayout rl_fats;
    private RelativeLayout rl_carbs;
    private RelativeLayout rl_sodium;
    private RelativeLayout rl_protein;
    private RelativeLayout rl_sugar;
    private List<String> meals;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private ArrayList<Meal> mealArrayList;

    private Float totalFats = 0.0f;
    private Float totalCalories = 0.0f;
    private Float totalCarbs = 0.0f;
    private Float totalSodium = 0.0f;
    private Float  totalProtein = 0.0f;
    private Float totalSugar = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();


        bottomNavigationView.setSelectedItemId(R.id.home);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.calendar:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(0,0);
                        finish();
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
                }
                return false;
            }
        });

        iv_profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                overridePendingTransition(0,0);
            }
        });



        tv_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                day = cldr.get(Calendar.DAY_OF_MONTH);
                month = cldr.get(Calendar.MONTH);
                year = cldr.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {


                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String date = df.format(new Date(year-1900,month,day));
                        tv_startDate.setText(date);
                        startDate = date;
                       resetData();
                        getMeals();
                        setData();
                        Log.d("testing", String.valueOf(mealArrayList.size()));
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(stringToDate(endDate).getTime());
                datePickerDialog.show();
            }
        });

        tv_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                day = cldr.get(Calendar.DAY_OF_MONTH);
                month = cldr.get(Calendar.MONTH);
                year = cldr.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String date = df.format(new Date(year-1900,month,day));
                        tv_endDate.setText(date);
                        endDate = date;
                        resetData();
                        getMeals();
                        setData();
                        Log.d("testing", String.valueOf(mealArrayList.size()));
                    }
                },year,month,day);

                datePickerDialog.getDatePicker().setMinDate(stringToDate(startDate).getTime());
                datePickerDialog.show();
            }
        });

    }

    public void init(){
        //Assign Variable
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        iv_profilePhoto = (ImageView) findViewById(R.id.iv_profilePhoto);
        tv_pageTitle = (TextView) findViewById(R.id.tv_pageTitle);


        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);


        tv_calories = (TextView) findViewById(R.id.tv_calories);
        tv_fats = (TextView) findViewById(R.id.tv_fats);
        tv_carbs = (TextView) findViewById(R.id.tv_carbs);
        tv_sodium = (TextView) findViewById(R.id.tv_sodium);
        tv_protein = (TextView) findViewById(R.id.tv_protein);
        tv_sugar = (TextView) findViewById(R.id.tv_sugar);
        pieChart =  (PieChart) findViewById(R.id.piechart);

        rl_calories = (RelativeLayout) findViewById(R.id.rl_calories);
        rl_fats = (RelativeLayout) findViewById(R.id.rl_fats);
        rl_carbs = (RelativeLayout) findViewById(R.id.rl_carbs);
        rl_sodium = (RelativeLayout) findViewById(R.id.rl_sodium);
        rl_protein = (RelativeLayout) findViewById(R.id.rl_protein);
        rl_sugar = (RelativeLayout) findViewById(R.id.rl_sugar);

        fStore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mealArrayList = new ArrayList<>();
        meals = new ArrayList<>();

        tv_startDate = (TextView) findViewById(R.id.tv_startDate);
        tv_endDate = (TextView) findViewById(R.id.tv_endDate);


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        startDate = df.format(c);
        endDate = df.format(c);
        tv_startDate.setText(startDate);
        tv_endDate.setText(endDate);
        setData();
        getMeals();



    }

    private void setData(){


        pieChart.clearChart();
        tv_calories.setText(Float.toString(totalCalories));
        tv_fats.setText(Float.toString(totalFats));
        tv_carbs.setText(Float.toString(totalCarbs));
        tv_sodium.setText(Float.toString(totalSodium));
        tv_protein.setText(Float.toString(totalProtein));
        tv_sugar.setText(Float.toString(totalSugar));

        pieChart.addPieSlice(new PieModel("Fats", Float.parseFloat(tv_fats.getText().toString()), Color.parseColor("#FFA726")));
        pieChart.addPieSlice(new PieModel("Carbs", Float.parseFloat(tv_carbs.getText().toString()), Color.parseColor("#EF5350")));
        pieChart.addPieSlice(new PieModel("Sodium", Float.parseFloat(tv_sodium.getText().toString()), Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(new PieModel("Protein", Float.parseFloat(tv_protein.getText().toString()), Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(new PieModel("Sugar", Float.parseFloat(tv_sugar.getText().toString()), Color.parseColor("#B854FF")));

        pieChart.setUseInnerValue(true);
        pieChart.setInnerValueString("Calories" + "\n" + tv_calories.getText().toString());


        rl_calories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.setInnerValueString("Calories" + "\n" + tv_calories.getText().toString());
            }
        });


        rl_fats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.setInnerValueString("Fats" + "\n" + tv_fats.getText().toString());
            }
        });

        rl_carbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.setInnerValueString("Carbs" + "\n" + tv_carbs.getText().toString());
            }
        });

        rl_sodium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.setInnerValueString("Sodium" + "\n" + tv_sodium.getText().toString());
            }
        });

        rl_protein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.setInnerValueString("Protein" + "\n" + tv_protein.getText().toString());
            }
        });

        rl_sugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.setInnerValueString("Sugar" + "\n" + tv_sugar.getText().toString());
            }
        });





        pieChart.startAnimation();



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
                                        Date dateAdded = stringToDate(documentSnapshot.getString("dateAdded"));
                                        Date endDateConverted = stringToDate(endDate);
                                        Date startDateConverted = stringToDate(startDate);


                                    if(isWithinRange(dateAdded, startDateConverted, endDateConverted)){
//                                        Log.d("dateAdded", "entered");
//                                        Log.d("dateStart", startDate);
//                                        Log.d("dateEnd", endDate);
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
                                        computeTotals(meal);
                                        setData();
//

                                    }


                                }
                            });
                        }
                    }

                }
                else{
                    Toast.makeText(HomeActivity.this, "Does not Exist", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private boolean isWithinRange(Date testDate, Date startDate, Date endDate){
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

    private Date stringToDate(String date){
        Date dateConverted = null;
        try {
            dateConverted = new SimpleDateFormat("dd-MMM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateConverted;
    }
    private void computeTotals(Meal meal){

            Float servings = Float.parseFloat(meal.getServings());
            totalFats += servings * Float.parseFloat(meal.getFatsPerServing());
            totalCalories += servings * Float.parseFloat(meal.getCaloriesPerServing());
            totalCarbs += servings * Float.parseFloat(meal.getCarbsPerServing());
            totalSodium += servings * Float.parseFloat(meal.getSodiumPerServing());
            totalProtein += servings * Float.parseFloat(meal.getProteinPerServing());
            totalSugar += servings * Float.parseFloat(meal.getSugarPerServing());

    }

    private void resetData(){
        mealArrayList.removeAll(mealArrayList);
        totalFats = 0.0f;
        totalCalories = 0.0f;
        totalCarbs = 0.0f;
        totalSodium = 0.0f;
        totalProtein = 0.0f;
        totalSugar = 0.0f;

    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}