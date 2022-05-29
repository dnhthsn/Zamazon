package com.example.zamazon.ui.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.zamazon.Prevalent.Prevalent;
import com.example.zamazon.R;
import com.example.zamazon.SettingsActivity;
import com.example.zamazon.databinding.FragmentSettingsBinding;
import com.example.zamazon.ui.orders.OrdersViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {
    private CircleImageView profileImageView;
    private EditText fullnameEdt, userPhoneEdt, addressEdt;
    private TextView profileChangeTexttxt, closeTexttxt, saveTxt;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePicture;
    private String checker = "";
    private StorageTask uploadTask;
    private Button btnEdit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImageView = view.findViewById(R.id.settings_profile_image);
        fullnameEdt = view.findViewById(R.id.settings_full_name);
        userPhoneEdt = view.findViewById(R.id.settings_phone_number);
        addressEdt = view.findViewById(R.id.settings_address);
        profileChangeTexttxt = view.findViewById(R.id.txt_profile_image_change);
        closeTexttxt = view.findViewById(R.id.txt_Close_settings);
        saveTxt = view.findViewById(R.id.txt_Update_settings);
        btnEdit = view.findViewById(R.id.btn_edit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

//        userInfoDisplay(profileImageView, fullnameEdt, userPhoneEdt, addressEdt);
//
//        closeTexttxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        saveTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(checker.equals("clicked")){
//                    userInfoSaved();
//                }else {
//                    updateOnlyUserInfo();
//                }
//            }
//        });
//
//        profileChangeTexttxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checker = "clicked";
//                CropImage.activity(imageUri)
//                        .setAspectRatio(1, 1)
//                        .start((Activity) getContext());
//            }
//        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            imageUri = result.getUri();
//            profileImageView.setImageURI(imageUri);
//        }else {
//            Toast.makeText(getContext(), "Error, try again.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void updateOnlyUserInfo() {
//    }
//
//    private void userInfoSaved() {
//        if (TextUtils.isEmpty(fullnameEdt.getText().toString())) {
//            Toast.makeText(getContext(), "Name is mandatory...", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(addressEdt.getText().toString())) {
//            Toast.makeText(getContext(), "Address is mandatory...", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(userPhoneEdt.getText().toString())) {
//            Toast.makeText(getContext(), "Phone is mandatory...", Toast.LENGTH_SHORT).show();
//        } else if (checker.equals("clicked")) {
//            uploadImage();
//        }
//    }
//
//    private void uploadImage() {
//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setTitle("Update Profile");
//        progressDialog.setMessage("Please wait, while we are updating your account information");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//
//        if(imageUri != null){
//            final StorageReference fileRef = storageProfilePicture.child(Prevalent.currentOnlineUser.getPhone()
//                    + ".jpg");
//            uploadTask = fileRef.putFile(imageUri);
//            uploadTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if(!task.isSuccessful()){
//                        throw task.getException();
//                    }
//                    return fileRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if(task.isSuccessful()){
//                        Uri downloadUrl = task.getResult();
//                        myUrl = downloadUrl.toString();
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                                .child("Users");
//                        HashMap<String, Object> userMap = new HashMap<>();
//                        userMap.put("name", fullnameEdt.getText().toString());
//                        userMap.put("address", addressEdt.getText().toString());
//                        userMap.put("phoneOrder", userPhoneEdt.getText().toString());
//                        userMap.put("image", myUrl);
//
//                        progressDialog.dismiss();
//                    }
//                }
//            });
//        }
//    }
//
//    private void userInfoDisplay(CircleImageView profileImageView, EditText fullnameEdt, EditText userPhoneEdt, EditText addressEdt) {
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
//        usersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    if(snapshot.child("image").exists()){
//                        String image = snapshot.child("image").getValue().toString();
//                        String name = snapshot.child("name").getValue().toString();
//                        String phone = snapshot.child("phone").getValue().toString();
//                        String address = snapshot.child("address").getValue().toString();
//
//                        Picasso.get().load(image).into(profileImageView);
//                        fullnameEdt.setText(name);
//                        userPhoneEdt.setText(phone);
//                        addressEdt.setText(address);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


}
