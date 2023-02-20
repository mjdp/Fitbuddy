package ph.dlsu.s11.fitbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URL;

import ph.dlsu.s11.fitbuddy.model.Meal;

public class EditMealActivity extends AppCompatActivity {
    private ImageView iv_profilePhoto;
    private TextView tv_pageTitle;
    private ImageView iv_notification_btn;
    private ImageView iv_cover;
    private LinearLayout ll_upload;
    private TextView tv_upload;
    private LinearLayout ll_capture;
    private TextView tv_capture;
    private EditText et_description;
    private TextView tv_calories;
    private EditText et_calories;
    private TextView tv_fats;
    private EditText et_fats;
    private TextView tv_carbs;
    private EditText et_carbs;
    private TextView tv_sodium;
    private EditText et_sodium;
    private TextView tv_protein;
    private EditText et_protein;
    private TextView tv_sugar;
    private EditText et_sugar;
    private TextView tv_servings;
    private EditText et_servings;
    private Button btn_save_meal;
    private Button btn_delete_meal;
    private ImageView iv_imageCover;
    private EditText et_foodName;
    private Meal meal;
    private String mealChoice;
    private String[] items = new String[]{"Breakfast", "Lunch", "Dinner", "Snack"};
    private Spinner dropdown;

    private FirebaseFirestore fStore;
    private Uri imageUri;


    private static final String ALLOW_KEY = "ALLOWED";
    private static final String CAMERA_PREF = "camera_pref";
    private static final String GALLERY_PREF = "gallery_pref";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_GALLERY = 101;
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    private String imageURL;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal);

        init();

        iv_profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewMealActivity.class);
                intent.putExtra("ChosenMeal", meal);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown.setAdapter(adapter);
        if (dropdown.getItemAtPosition(0).equals(meal.getMealChoice())) {
            dropdown.setSelection(0);
        } else if (dropdown.getItemAtPosition(1).equals(meal.getMealChoice())) {
            dropdown.setSelection(1);
        } else if (dropdown.getItemAtPosition(2).equals(meal.getMealChoice())) {
            dropdown.setSelection(2);
        } else if (dropdown.getItemAtPosition(3).equals(meal.getMealChoice())) {
            dropdown.setSelection(3);
        }
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mealChoice = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ll_upload.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (getFromPrefGallery(this, ALLOW_KEY)) {
                    showSettingsAlertGallery();

                } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showAlertGallery();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_GALLERY);
                    }
                }
            } else {
                openGallery();
            }
        });

        ll_capture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (getFromPrefCamera(this, ALLOW_KEY)) {
                    showSettingsAlertCamera();

                } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        showAlertCamera();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                }
            } else {
                openCamera();
            }

        });

        btn_delete_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealDeletion();
            }
        });

        btn_save_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMeal();
                if (imageUri == null) {
                    updateMeal();
                    Intent intent = new Intent(getApplicationContext(), ViewMealActivity.class);
                    intent.putExtra("ChosenMeal", meal);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }

            }
        });
    }

    public void init() {
        //Assign Variable
        iv_profilePhoto = (ImageView) findViewById(R.id.iv_profilePhoto);
        tv_pageTitle = (TextView) findViewById(R.id.tv_pageTitle);

        iv_cover = (ImageView) findViewById(R.id.iv_cover);
        ll_upload = (LinearLayout) findViewById(R.id.ll_upload);
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        ll_capture = (LinearLayout) findViewById(R.id.ll_capture);
        tv_capture = (TextView) findViewById(R.id.tv_capture);
        et_description = (EditText) findViewById(R.id.et_description);
        tv_calories = (TextView) findViewById(R.id.tv_calories);
        et_calories = (EditText) findViewById(R.id.et_calories);
        tv_fats = (TextView) findViewById(R.id.tv_fats);
        et_fats = (EditText) findViewById(R.id.et_fats);
        tv_carbs = (TextView) findViewById(R.id.tv_carbs);
        et_carbs = (EditText) findViewById(R.id.et_carbs);
        tv_sodium = (TextView) findViewById(R.id.tv_sodium);
        et_sodium = (EditText) findViewById(R.id.et_sodium);
        tv_protein = (TextView) findViewById(R.id.tv_protein);
        et_protein = (EditText) findViewById(R.id.et_protein);
        tv_sugar = (TextView) findViewById(R.id.tv_sugar);
        et_sugar = (EditText) findViewById(R.id.et_sugar);
        tv_servings = (TextView) findViewById(R.id.tv_servings);
        et_servings = (EditText) findViewById(R.id.et_servings);
        btn_save_meal = (Button) findViewById(R.id.btn_save_meal);
        btn_delete_meal = (Button) findViewById(R.id.btn_delete_meal);
        iv_imageCover = (ImageView) findViewById(R.id.iv_imageContainer);
        et_foodName = (EditText) findViewById(R.id.et_foodName);
        dropdown = (Spinner) findViewById(R.id.meal_choice);
        //Set Home Selected
        iv_profilePhoto.setImageResource(R.drawable.ic_baseline_arrow_back);

        iv_cover.setImageResource(R.drawable.top_bcakground);

        fStore = FirebaseFirestore.getInstance();
        meal = (Meal) getIntent().getSerializableExtra("ChosenMeal");

        et_fats.setText(meal.getFatsPerServing());
        et_protein.setText(meal.getProteinPerServing());
        et_calories.setText(meal.getCaloriesPerServing());
        et_carbs.setText(meal.getCarbsPerServing());
        et_sodium.setText(meal.getSodiumPerServing());
        et_sugar.setText(meal.getSugarPerServing());
        et_servings.setText(meal.getServings());
        et_description.setText(meal.getDescription());
        et_foodName.setText(meal.getMealName());
        Picasso.get().load(meal.getImage()).fit().into(iv_imageCover);
        imageURL = meal.getImage();
        mealChoice = meal.getMealChoice();


    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(this, permission);

                        if (showRationale) {
                            showAlertCamera();
                        } else if (!showRationale) {
                            saveToPreferencesCamera(EditMealActivity.this, ALLOW_KEY, true);
                        }
                    }
                }
            }
            case MY_PERMISSIONS_REQUEST_GALLERY: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(this, permission);

                        if (showRationale) {
                            showAlertCamera();
                        } else if (!showRationale) {
                            saveToPreferencesCamera(EditMealActivity.this, ALLOW_KEY, true);
                        }
                    }
                }
            }
        }
    }

    private void showAlertCamera() {
        AlertDialog alertDialog = new AlertDialog.Builder(EditMealActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(EditMealActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        });

        alertDialog.show();
    }

    private void showAlertGallery() {
        AlertDialog alertDialog = new AlertDialog.Builder(EditMealActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Gallery.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(EditMealActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_GALLERY);
            }
        });

        alertDialog.show();
    }

    private void showSettingsAlertCamera() {
        AlertDialog alertDialog = new AlertDialog.Builder(EditMealActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startInstalledAppDetailsActivity(EditMealActivity.this);
            }
        });


        alertDialog.show();


    }


    private void showSettingsAlertGallery() {
        AlertDialog alertDialog = new AlertDialog.Builder(EditMealActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Gallery.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startInstalledAppDetailsActivity(EditMealActivity.this);
            }
        });


        alertDialog.show();


    }

    private void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }


    private void saveToPreferencesCamera(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }


    private void saveToPreferencesGallery(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(GALLERY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    private static Boolean getFromPrefCamera(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    private static Boolean getFromPrefGallery(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(GALLERY_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPhoto.jpg");
            imageUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            Bitmap takenImage = BitmapFactory.decodeFile(file.getAbsolutePath());
            iv_imageCover.setImageBitmap(takenImage);

        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(iv_imageCover);
        }
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPhoto.jpg");
        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA_REQUEST); //waiting for reply or trigger or return from activity
    }

    ;

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }


    public File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "directory");

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("directory", "failed to create directory");
        }


        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void updateMeal() {
        if (imageUri != null) {
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            Toast.makeText(getApplicationContext(), "Adding Meal. Please Wait!", Toast.LENGTH_LONG).show();

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            imageURL = uri.toString();
//                          Date c = Calendar.getInstance().getTime();
//
//                          SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                            Intent intent = getIntent();


                            Log.d("DownloadURL", imageURL);

                            fStore.collection("meals")
                                    .document(meal.getMealID())
                                    .update(
                                            "mealName", et_foodName.getText().toString(),
                                            "foodCalories", et_calories.getText().toString(),
                                            "foodCarbs", et_carbs.getText().toString(),
                                            "foodDesc", et_description.getText().toString(),
                                            "foodFats", et_fats.getText().toString(),
                                            "foodProtein", et_protein.getText().toString(),
                                            "foodServings", et_servings.getText().toString(),
                                            "foodSodium", et_sodium.getText().toString(),
                                            "foodSugar", et_sugar.getText().toString(),
                                            "imageURL", imageURL,
                                            "mealChoice", mealChoice);

                            meal.setMealName(et_foodName.getText().toString());
                            meal.setCaloriesPerServing(et_calories.getText().toString());
                            meal.setCarbsPerServing(et_carbs.getText().toString());
                            meal.setDescription(et_description.getText().toString());
                            meal.setFatsPerServing(et_fats.getText().toString());
                            meal.setProteinPerServing(et_protein.getText().toString());
                            meal.setServings(et_servings.getText().toString());
                            meal.setSodiumPerServing(et_sodium.getText().toString());
                            meal.setSugarPerServing(et_sugar.getText().toString());
                            meal.setImage(imageURL);
                            meal.setMealChoice(mealChoice);

                            Intent i = new Intent(getApplicationContext(), ViewMealActivity.class);
                            i.putExtra("ChosenMeal", meal);
                            startActivity(i);
                            Toast.makeText(getApplicationContext(), "Meal Updated Successfully!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        } else {
            fStore.collection("meals")
                    .document(meal.getMealID())
                    .update(
                            "mealName", et_foodName.getText().toString(),
                            "foodCalories", et_calories.getText().toString(),
                            "foodCarbs", et_carbs.getText().toString(),
                            "foodDesc", et_description.getText().toString(),
                            "foodFats", et_fats.getText().toString(),
                            "foodProtein", et_protein.getText().toString(),
                            "foodServings", et_servings.getText().toString(),
                            "foodSodium", et_sodium.getText().toString(),
                            "foodSugar", et_sugar.getText().toString(),
                            "imageURL", imageURL,
                            "mealChoice", mealChoice);

            meal.setMealName(et_foodName.getText().toString());
            meal.setCaloriesPerServing(et_calories.getText().toString());
            meal.setCarbsPerServing(et_carbs.getText().toString());
            meal.setDescription(et_description.getText().toString());
            meal.setFatsPerServing(et_fats.getText().toString());
            meal.setProteinPerServing(et_protein.getText().toString());
            meal.setServings(et_servings.getText().toString());
            meal.setSodiumPerServing(et_sodium.getText().toString());
            meal.setSugarPerServing(et_sugar.getText().toString());
            meal.setImage(imageURL);
            meal.setMealChoice(mealChoice);

        }
    }


    private void mealDeletion() {
        fStore.collection("meals").document(meal.getMealID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                DocumentReference documentReference = fStore.collection("users").document(mAuth.getCurrentUser().getUid());
                documentReference.update("meals", FieldValue.arrayRemove(meal.getMealID()));
                Intent i = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Meal Deleted Successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Meal Not Deleted!", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), ViewMealActivity.class);
        i.putExtra("ChosenMeal", meal);
        startActivity(i);
        finish();
    }
}