package ph.dlsu.s11.fitbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ph.dlsu.s11.fitbuddy.model.Meal;

public class ViewMealActivity extends AppCompatActivity {
    private ImageView iv_profilePhoto;
    private TextView tv_pageTitle;
    private ImageView iv_notification_btn;
    private ImageView iv_cover;
    private TextView tv_description;
    private TextView tv_calories;
    private TextView tv_calories_input;
    private TextView tv_fats;
    private TextView tv_fats_input;
    private TextView tv_carbs;
    private TextView tv_carbs_input;
    private TextView tv_sodium;
    private TextView tv_sodium_input;
    private TextView tv_protein;
    private TextView tv_protein_input;
    private TextView tv_sugar;
    private TextView tv_sugar_input;
    private TextView tv_servings;
    private TextView tv_servings_input;
    private TextView tv_mealName;
    private Button btn_edit_meal;
    private Meal meal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal);

        init();

        iv_profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DiaryActivity.class));
                overridePendingTransition(0,0);
            }
        });



        btn_edit_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditMealActivity.class);
                i.putExtra("ChosenMeal", meal);
                startActivity(i);
                overridePendingTransition(0,0);
                finish();

            }
        });


    }

    public void init(){
        //Assign Variable
        iv_profilePhoto = (ImageView) findViewById(R.id.iv_profilePhoto);
        tv_pageTitle = (TextView) findViewById(R.id.tv_pageTitle);

        iv_cover = (ImageView) findViewById(R.id.iv_cover);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_calories = (TextView) findViewById(R.id.tv_calories);
        tv_calories_input = (TextView) findViewById(R.id.tv_calories_input);
        tv_fats = (TextView) findViewById(R.id.tv_fats);
        tv_fats_input = (TextView) findViewById(R.id.tv_fats_input);
        tv_carbs = (TextView) findViewById(R.id.tv_carbs);
        tv_carbs_input = (TextView) findViewById(R.id.tv_carbs_input);
        tv_sodium = (TextView) findViewById(R.id.tv_sodium);
        tv_sodium_input = (TextView) findViewById(R.id.tv_sodium_input);
        tv_protein = (TextView) findViewById(R.id.tv_protein);
        tv_protein_input = (TextView) findViewById(R.id.tv_protein_input);
        tv_sugar = (TextView) findViewById(R.id.tv_sugar);
        tv_sugar_input = (TextView) findViewById(R.id.tv_sugar_input);
        tv_servings = (TextView) findViewById(R.id.tv_servings);
        tv_servings_input = (TextView) findViewById(R.id.tv_servings_input);
        btn_edit_meal = (Button) findViewById(R.id.btn_edit_meal);
        tv_mealName = (TextView) findViewById(R.id.tv_mealName);

        //Set Home Selected
        iv_profilePhoto.setImageResource(R.drawable.ic_baseline_arrow_back);

        iv_cover.setImageResource(R.drawable.undraw_healthy_lifestyle_6tyl);

        meal = (Meal) getIntent().getSerializableExtra("ChosenMeal");

        tv_fats_input.setText(meal.getFatsPerServing());
        tv_protein_input.setText(meal.getProteinPerServing());
        tv_calories_input.setText(meal.getCaloriesPerServing());
        tv_carbs_input.setText(meal.getCarbsPerServing());
        tv_sodium_input.setText(meal.getSodiumPerServing());
        tv_sugar_input.setText(meal.getSugarPerServing());
        tv_servings_input.setText(meal.getServings());
        tv_description.setText(" - " + meal.getDescription());
        tv_mealName.setText(meal.getMealName());
        Picasso.get().load(meal.getImage()).fit().into(iv_cover);

        tv_pageTitle.setText(meal.getMealChoice() + " - "  + meal.getDate());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}