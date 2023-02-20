package ph.dlsu.s11.fitbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView iv_back_btn;
    private TextView tv_pageTitle;
    private ImageView iv_notification_btn;

    private ImageView iv_cover;
    private ImageView iv_profile;
    private LinearLayout ll_upload;
    private TextView tv_upload;
    private LinearLayout ll_capture;
    private TextView tv_capture;
    private EditText et_name;
    private TextView et_email;
    private TextView tv_password;
    private Button btn_password;

    private EditText old_password;
    private EditText new_password;
    private EditText new_confirm_password;

    private String string_new_password, string_old_password, string_new_confirmpassword;
    private String passwordDeletion;
    private ImageView iv_blood;
    private TextView tv_blood;
    private EditText et_blood_input;
    private ImageView iv_weight;
    private TextView tv_weight;
    private EditText et_weight_input;
    private ImageView iv_height;
    private TextView tv_height;
    private EditText et_height_input;
    private Button btn_save_profile;
    private Button btn_delete;
    private EditText et_birthday;

    private List<String> meals;

    private String fullName, emailAddress, password, height, weight, birthday, imageURL;
    private int year, month, day;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private Uri imageUri;


    private static final String ALLOW_KEY = "ALLOWED";
    private static final String CAMERA_PREF = "camera_pref";
    private static final String GALLERY_PREF = "gallery_pref";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_GALLERY = 101;
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();

        iv_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        et_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog();
            }
        });

        btn_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteAccount();
            }
        });

        ll_capture.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                if(getFromPrefCamera(this, ALLOW_KEY)){
                    showSettingsAlertCamera();

                }else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                        showAlertCamera();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                }
            }else{
                openCamera();
            }

        });

        ll_upload.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(getFromPrefGallery(this, ALLOW_KEY)){
                    showSettingsAlertGallery();

                }else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                        showAlertGallery();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_GALLERY);
                    }
                }
            }else{
                openGallery();
            }
        });

    }

    public void dialogDeleteAccount(){

        ViewGroup viewGroup = findViewById(android.R.id.content);


        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_deleteaccount, viewGroup, false);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EditText et_deletePassword = (EditText) dialogView.findViewById(R.id.password_deletion);
        passwordDeletion = et_deletePassword.getText().toString();
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount(passwordDeletion);

                    }
                });

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onCreateDialog(){

        ViewGroup viewGroup = findViewById(android.R.id.content);


        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_changepassword, viewGroup, false);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        new_password = (EditText) dialogView.findViewById(R.id.new_password);
        old_password = (EditText) dialogView.findViewById(R.id.old_password);
        new_confirm_password = (EditText) dialogView.findViewById(R.id.new_confirm_password);

        builder.setView(dialogView)
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(new_password.getText().toString() != null &&
                            old_password.getText().toString() != null &&
                            new_confirm_password.getText().toString() != null){


                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(emailAddress, old_password.getText().toString());

                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(new_password.getText().toString().equals(new_confirm_password.getText().toString())) {
                                                user.updatePassword(new_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            mAuth.signOut();
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();
                                                            Toast.makeText(getApplicationContext(), "Change Password Success! Please Login Again!", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                                }
                                                else{
                                                    Toast.makeText(getApplicationContext(), "New Password is not same as Confirm Password! Password Not Changed", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Old Password is incorrect! Password Not Changed", Toast.LENGTH_LONG).show();
                                    }
                                });


                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Fill up all necessary inputs", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void init() {
        //Assign Variable
        iv_back_btn = (ImageView) findViewById(R.id.iv_profilePhoto);
        tv_pageTitle = (TextView) findViewById(R.id.tv_pageTitle);


        //Set Home Selected
        iv_back_btn.setImageResource(R.drawable.ic_baseline_arrow_back);

        iv_cover = (ImageView) findViewById(R.id.iv_cover);
        iv_cover.setImageResource(R.drawable.top_bcakground);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (TextView) findViewById(R.id.et_email);
        et_birthday = (EditText) findViewById(R.id.et_birthday);
        btn_password = (Button) findViewById(R.id.btn_password);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        iv_weight = (ImageView) findViewById(R.id.iv_weight);
        tv_weight = (TextView) findViewById(R.id.tv_weight);
        et_weight_input = (EditText) findViewById(R.id.et_weight_input);
        iv_height = (ImageView) findViewById(R.id.iv_height);
        tv_height = (TextView) findViewById(R.id.tv_height);
        et_height_input = (EditText) findViewById(R.id.et_height_input);
        btn_save_profile = (Button) findViewById(R.id.btn_save_profile);

        ll_upload = (LinearLayout) findViewById(R.id.ll_upload);
        ll_capture = (LinearLayout) findViewById(R.id.ll_capture);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        getUserData();
    }

    private void getUserData(){
        DocumentReference docRef = fStore.collection("users").document(mAuth.getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {

                    et_name.setText(documentSnapshot.getString("fName"));
                    fullName = documentSnapshot.getString("fName");
                    et_email.setText(documentSnapshot.getString("emailAd"));
                    emailAddress = documentSnapshot.getString("emailAd");
                    et_weight_input.setText(documentSnapshot.getString("weight"));
                    weight = documentSnapshot.getString("weight");
                    et_height_input.setText(documentSnapshot.getString("height"));
                    height = documentSnapshot.getString("height");
                    et_birthday.setText(documentSnapshot.getString("birthday"));
                    birthday = documentSnapshot.getString("birthday");
                    meals = (List<String>) documentSnapshot.get("meals");
//                    Log.d("mealId", meals.get(0));
                    if(documentSnapshot.getString("imageURL")!=""){
                        Picasso.get().load(documentSnapshot.getString("imageURL")).fit().into(iv_profile);
                    }




                }
                else{
                    Toast.makeText(EditProfileActivity.this, "Does not Exist", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public void saveUserData(){
        DocumentReference docRef = fStore.collection("users").document(mAuth.getCurrentUser().getUid());

        if(imageUri!=null) {
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            Toast.makeText(getApplicationContext(), "Updating Profile! Please Wait..", Toast.LENGTH_LONG).show();

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURL = uri.toString();
                            docRef.update("fName", et_name.getText().toString());
                            docRef.update("emailAd", et_email.getText().toString());
                            docRef.update("birthday", et_birthday.getText().toString());
                            docRef.update("weight", et_weight_input.getText().toString());
                            docRef.update("height", et_height_input.getText().toString());
                            docRef.update("imageURL", imageURL);

                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
        else{
            docRef.update("fName", et_name.getText().toString());
            docRef.update("emailAd", et_email.getText().toString());
            docRef.update("birthday", et_birthday.getText().toString());
            docRef.update("weight", et_weight_input.getText().toString());
            docRef.update("height", et_height_input.getText().toString());
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            finish();
            Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_LONG).show();
        }

    }


    private void deleteAccount(String password){
        FirebaseUser userDelete = mAuth.getCurrentUser();


        for(int i=0; i<meals.size(); i++){
            fStore.collection("meals").document(meals.get(i)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }

        fStore.collection("users").document(mAuth.getCurrentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfileActivity.this, "Deleting Account. Please wait...", Toast.LENGTH_SHORT).show();
            }
        });

        userDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Delete", "deleting");
                if(task.isSuccessful()){
                    Log.d("Deleted", "deleted");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    Toast.makeText(EditProfileActivity.this, "Account Deleted! We hope to see you again!", Toast.LENGTH_SHORT).show();
                }
            }
        });





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
                            saveToPreferencesCamera(EditProfileActivity.this, ALLOW_KEY, true);
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
                            saveToPreferencesCamera(EditProfileActivity.this, ALLOW_KEY, true);
                        }
                    }
                }
            }
        }
    }

    private void showAlertCamera() {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        });

        alertDialog.show();
    }

    private void showAlertGallery() {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Gallery.");

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_GALLERY);
            }
        });

        alertDialog.show();
    }

    private void showSettingsAlertCamera() {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startInstalledAppDetailsActivity(EditProfileActivity.this);
            }
        });


        alertDialog.show();


    }


    private void showSettingsAlertGallery() {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Gallery.");

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startInstalledAppDetailsActivity(EditProfileActivity.this);
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
            iv_profile.setImageBitmap(takenImage);

        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(iv_profile);
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



}