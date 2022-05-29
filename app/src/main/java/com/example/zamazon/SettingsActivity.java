package com.example.zamazon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zamazon.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullnameEdt, userPhoneEdt, addressEdt;
    private TextView profileChangeTexttxt, closeTexttxt, saveTxt;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePicture;
    private String checker = "";
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageView = findViewById(R.id.settings_profile_image);
        fullnameEdt = findViewById(R.id.settings_full_name);
        userPhoneEdt = findViewById(R.id.settings_phone_number);
        addressEdt = findViewById(R.id.settings_address);
        profileChangeTexttxt = findViewById(R.id.txt_profile_image_change);
        closeTexttxt = findViewById(R.id.txt_Close_settings);
        saveTxt = findViewById(R.id.txt_Update_settings);
        storageProfilePicture = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        userInfoDisplay(profileImageView, fullnameEdt, userPhoneEdt, addressEdt);

        closeTexttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }});

        saveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(checker.equals("clicked")){
//                    userInfoSaved();
//                }else {
                    updateOnlyUserInfo();
//                }
            }
        });

        profileChangeTexttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }else {
            Toast.makeText(this, "Error, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullnameEdt.getText().toString());
        userMap.put("address", addressEdt.getText().toString());
        userMap.put("phoneOrder", userPhoneEdt.getText().toString());
        reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile info update successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullnameEdt.getText().toString())) {
            Toast.makeText(this, "Name is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEdt.getText().toString())) {
            Toast.makeText(this, "Address is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPhoneEdt.getText().toString())) {
            Toast.makeText(this, "Phone is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = storageProfilePicture.child(Prevalent.currentOnlineUser.getPhone()
                    + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullnameEdt.getText().toString());
                        userMap.put("address", addressEdt.getText().toString());
                        userMap.put("phoneOrder", userPhoneEdt.getText().toString());
                        userMap.put("image", myUrl);
                        reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile info update successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullnameEdt, EditText userPhoneEdt, EditText addressEdt) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("image").exists()){
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnameEdt.setText(name);
                        userPhoneEdt.setText(phone);
                        addressEdt.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}