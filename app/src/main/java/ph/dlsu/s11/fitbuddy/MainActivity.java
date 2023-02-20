package ph.dlsu.s11.fitbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView tv_title;
    private TextView tv_phrase;
    private TextView tv_account;
    private TextView tv_login_btn;
    private ImageView iv_get_started_images;
    private Button button;
    private Button button2;
    private Button button3;
    private Button btn_get_started;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }




        tv_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });



        btn_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    public void init(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_phrase = (TextView) findViewById(R.id.tv_phrase);
        tv_account = (TextView) findViewById(R.id.tv_account);
        tv_login_btn = (TextView) findViewById(R.id.tv_login_btn);
        iv_get_started_images = (ImageView) findViewById(R.id.iv_get_started_images);

        btn_get_started = (Button) findViewById(R.id.btn_get_started);
        tv_login_btn.setPaintFlags(tv_login_btn.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        iv_get_started_images.setImageResource(R.drawable.undraw_healthy_lifestyle_6tyl);
    }
}