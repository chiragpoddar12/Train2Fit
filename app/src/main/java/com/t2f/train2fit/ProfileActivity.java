package com.t2f.train2fit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private FloatingActionButton signOut;
    private FloatingActionButton editProfile;
    private FloatingActionButton saveButton;
    private FloatingActionButton cancelSave;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference mDatabase;
    private TextView tvName;
    long lastPress;
    Toast backpressToast;
    private TextView tvAddress;
    private TextView tvEmail;
    private TextView tvDateOfBirth;
    private TextView tvMobile;
    public String userId;
    private Button bSelectImage;
    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;
    private ImageView ivProfilePhoto;
    private ImageView ivImageView;
//    private EditText etName;
    private EditText etAddress;
    private EditText etMobile;
    private EditText etDOB;
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        signOut = (FloatingActionButton) findViewById(R.id.sign_out);
        editProfile = (FloatingActionButton) findViewById(R.id.editProfile);
        tvName = (TextView) findViewById(R.id.textViewName);
//        etName = (EditText) findViewById(R.id.editTextName);
        tvAddress = (TextView) findViewById(R.id.textViewAddress);
        tvEmail = (TextView) findViewById(R.id.textViewEmail);
        tvDateOfBirth = (TextView) findViewById(R.id.textViewDateOfBirth);
        tvMobile = (TextView) findViewById(R.id.textViewMobile);
        progressDialog = new ProgressDialog(this);
        ivProfilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
        etAddress = (EditText) findViewById(R.id.editTextAddress);
        etDOB = (EditText) findViewById(R.id.editTextDateOfBirth);
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etMobile = (EditText) findViewById(R.id.editTextMobile);
        saveButton = (FloatingActionButton) findViewById(R.id.save);
        cancelSave = (FloatingActionButton) findViewById(R.id.cancel);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(ProfileActivity.this,DividerItemDecoration.VERTICAL));
        ivImageView = (ImageView)hView.findViewById(R.id.imageView);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userId = user.getUid();//retrieve from session
//        String userId = "sfosniocnoi6868161";
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        mDatabase.child("notificationTokens").setValue(refreshedToken);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setDataOnScreen(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mDatabase.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                setDataOnScreen(dataSnapshot);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                setDataOnScreen(dataSnapshot);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        bSelectImage = (Button) findViewById(R.id.bImageSelect);
        mStorage = FirebaseStorage.getInstance().getReference();

        ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT );
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save to DB
//                ProgressDialog mProgressDialog = new ProgressDialog(getApplicationContext());
//                mProgressDialog.show();
//                mDatabase.child("full_name").setValue(etName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        tvName.setText(etName.getText());
//                        etName.setVisibility(View.INVISIBLE);
//                        tvName.setVisibility(View.VISIBLE);
//                    }
//                });
                mDatabase.child("dob").setValue(etDOB.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tvDateOfBirth.setText(etDOB.getText());
                        etDOB.setVisibility(View.INVISIBLE);
                        tvDateOfBirth.setVisibility(View.VISIBLE);
                    }
                });
                mDatabase.child("address").setValue(etAddress.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tvAddress.setText(etAddress.getText());
                        etAddress.setVisibility(View.INVISIBLE);
                        tvAddress.setVisibility(View.VISIBLE);
                    }
                });
                mDatabase.child("mobile").setValue(etMobile.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tvMobile.setText(etMobile.getText());
                        etMobile.setVisibility(View.INVISIBLE);
                        tvMobile.setVisibility(View.VISIBLE);
                    }
                });
                mDatabase.child("email").setValue(etEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tvEmail.setText(etEmail.getText());
                        etEmail.setVisibility(View.INVISIBLE);
                        tvEmail.setVisibility(View.VISIBLE);
//                        progressDialog.dismiss();
                    }
                });

                cancelSave.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);
            }
        });

        cancelSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvName.setVisibility(View.VISIBLE);
////                etName.setText(tvName.getText());
//                etName.setVisibility(View.INVISIBLE);

                tvAddress.setVisibility(View.VISIBLE);
//                etAddress.setText(tvAddress.getText());
                etAddress.setVisibility(View.INVISIBLE);

                tvDateOfBirth.setVisibility(View.VISIBLE);
//                etDOB.setText(tvDateOfBirth.getText());
                etDOB.setVisibility(View.INVISIBLE);

                tvEmail.setVisibility(View.VISIBLE);
//                etEmail.setText(tvEmail.getText());
                etEmail.setVisibility(View.INVISIBLE);

                tvMobile.setVisibility(View.VISIBLE);
//                etMobile.setText(tvMobile.getText());
                etMobile.setVisibility(View.INVISIBLE);

                cancelSave.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);
                editProfile.setVisibility(View.VISIBLE);
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvName.setVisibility(View.INVISIBLE);
//                etName.setText(tvName.getText());
//                etName.setVisibility(View.VISIBLE);

                tvAddress.setVisibility(View.INVISIBLE);
                etAddress.setText(tvAddress.getText());
                etAddress.setVisibility(View.VISIBLE);

                tvDateOfBirth.setVisibility(View.INVISIBLE);
                etDOB.setText(tvDateOfBirth.getText());
                etDOB.setVisibility(View.VISIBLE);

                tvEmail.setVisibility(View.INVISIBLE);
                etEmail.setText(tvEmail.getText());
                etEmail.setVisibility(View.VISIBLE);

                tvMobile.setVisibility(View.INVISIBLE);
                etMobile.setText(tvMobile.getText());
                etMobile.setVisibility(View.VISIBLE);

                cancelSave.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                editProfile.setVisibility(View.INVISIBLE);
            }
        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

//        FloatingActionButton fbEditProfile = (FloatingActionButton) findViewById(R.id.editProfile);
//        fbEditProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signOut();
//            }
//        });
    }

    private void signOut() {
        auth.signOut();
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void setDataOnScreen(DataSnapshot dataSnapshot){
        progressDialog.show();
        Map<String, Object> userInfos = (Map<String, Object>) dataSnapshot.getValue();
        for(Map.Entry<String, Object> userInfo : userInfos.entrySet()){
            switch(userInfo.getKey()){
                case "address": tvAddress.setText(userInfo.getValue().toString());break;
                case "full_name": tvName.setText(userInfo.getValue().toString()); break;
                case "dob" :  tvDateOfBirth.setText(userInfo.getValue().toString()); break;
                case "email": tvEmail.setText(userInfo.getValue().toString());break;
                case "mobile" : tvMobile.setText(userInfo.getValue().toString()); break;
                case "profile_photo" :  Task<Uri> task = mStorage.child("profilePhotos").child(userId).getDownloadUrl();
                                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.with(getApplicationContext()).load(uri).into(ivProfilePhoto);
                                                Picasso.with(getApplicationContext()).load(uri).into(ivImageView);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Problem getting profile picture", Toast.LENGTH_SHORT);

                                            }
                                        });
                                        break;
            }
            progressDialog.dismiss();
        }
//        for(Map.Entry<String, Object> currentUser : users.entrySet()){
//            Map<String, Object> userValue = (Map<String, Object>) currentUser.getValue();
//            for(Map.Entry<String, Object> currentUserEntity : userValue.entrySet()){
//                System.out.println(currentUserEntity.getValue());
//                String userKey = currentUserEntity.getValue().toString();
//                if(userKey.equals(userId)){
//                    String userDbKey = currentUser.getKey();
//                    DatabaseReference mDatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userDbKey);
//                    mDatabaseUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Map<String, Object> userInfos = (Map<String, Object>) dataSnapshot.getValue();
//                            for(Map.Entry<String, Object> userInfo : userInfos.entrySet()){
//                                switch (userInfo.getKey()){
//                                    case "address": tvAddress.setText(userInfo.getValue().toString());break;
//                                    case "full_name": tvName.setText(userInfo.getValue().toString()); break;
//                                    case "dob" :  tvDateOfBirth.setText(userInfo.getValue().toString()); break;
//                                    case "email": tvEmail.setText(userInfo.getValue().toString());break;
//                                    case "mobile" : tvMobile.setText(userInfo.getValue().toString()); break;
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                    break;
//                }
//            }
//        }
    }

    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 5000) {
            backpressToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG);
            backpressToast.show();
            lastPress = currentTime;

        } else {
            if (backpressToast != null) backpressToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(ProfileActivity.this, AboutActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent bookingIntent=new Intent(ProfileActivity.this, BookingActivity.class);
            startActivity(bookingIntent);
        } else if (id == R.id.showProfilePage) {
            Intent profileActivityIntent=new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(profileActivityIntent);
        } else if (id == R.id.nav_book_appointment) {
            Intent bookAppointmentActivityIntent=new Intent(ProfileActivity.this, BookAppointmentActivity.class);
            startActivity(bookAppointmentActivityIntent);
        }else if(id== R.id.signOutItem){
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onLoginClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        String message = "From main activity to Login Activity";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode==RESULT_OK){
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();
            Uri uri = data.getData();
            String abc = uri.toString();
            StorageReference filepath = mStorage.child("profilePhotos").child(userId);
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri downloadUri = taskSnapshot.getDownloadUrl();


                    mDatabase.child("profile_photo").setValue(downloadUri.toString());
                    Picasso.with(getApplicationContext()).load(downloadUri).into(ivProfilePhoto);
                    Picasso.with(getApplicationContext()).load(downloadUri).into(ivImageView);
                    Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_LONG);
                }
            });
//            System.out.println(uri.getLastPathSegment());
        }
    }


}
