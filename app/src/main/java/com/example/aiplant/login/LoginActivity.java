package com.example.aiplant.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;
import com.example.aiplant.model.User;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.google.GoogleCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;

import org.bson.Document;

import java.security.SecureRandom;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String Google_Tag = "GoogleActivity";

    //Google signIn
    private static final int RC_SIGN_IN = 9001;

    //database
    private MongoDbSetup mongoDbSetup;
    private DatabaseReference user_ref;
    private DatabaseReference myRef;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private StitchAuth mStitchAuth;
    private StitchUser mStitchUser;
    private Document updateDoc, fetchedDOc;

//    private Block<Document> printBlock = new Block<Document>() {
//        @Override
//        public void apply(final Document document) {
//            System.out.println(document.toJson());
//        }
//    };

    // widgets
    private MaterialButton loginButton;
    private TextView click_here_text, sign_up_text, orView, forgotPass_logIn;
    private RelativeLayout loginLayout;
    private EditText mEmailField, mPasswordField;
    private FragmentManager fragmentManager;
    private Context mContext;
    private StitchAppClient appClient;
    private String client_id, lastName, firstName, mail, birthday, photo;
    private String randomAvatarURL = "https://drive.google.com/file/d/1x2e9wUyRtzV9nEL2u9QeVDWvAWfVnh5d/view?usp=sharing";
    private ProgressDialog progressDialog;
    private User emailPassUser;
    private User googleUser;
    private Document googleUserUpdateDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fragmentManager = getSupportFragmentManager();
        mContext = this;
        connectMongoDb();

        initLayout();
        buttonListeners();


    }

    private void connectMongoDb() {
        mongoDbSetup = MongoDbSetup.getInstance(getApplicationContext());
        MongoDbSetup.runAppClientInit();
        mGoogleSignInClient = MongoDbSetup.getGoogleSignInClient();

        setMongoDbForLaterUse(mongoDbSetup);
        appClient = MongoDbSetup.getAppClient();

        mStitchAuth = mongoDbSetup.getStitchAuth();
        mStitchUser = mongoDbSetup.getStitchUser();
        Log.d(TAG, "connectMongoDb: auth: " + mStitchAuth.getUser());
        setMongoDbForLaterUse(mongoDbSetup);

        String bs = "s";
    }

    public void setMongoDbForLaterUse(MongoDbSetup mongoDbSetup) {
        this.mongoDbSetup = mongoDbSetup;
    }

    public MongoDbSetup getMongoDbForLaterUse() {
        return mongoDbSetup;
    }

    public void initLayout() {
        mEmailField = findViewById(R.id.email_id_logIn);
        mPasswordField = findViewById(R.id.password_id_logIn);
        forgotPass_logIn = findViewById(R.id.forgotPass_logIn);

        loginLayout = findViewById(R.id.login_activity);
        click_here_text = findViewById(R.id.click_here_text);
        sign_up_text = findViewById(R.id.sign_up_text);
        sign_up_text.setPaintFlags(sign_up_text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        orView = findViewById(R.id.orView);

        fragmentManager = getSupportFragmentManager();

        mongoDbSetup.findPlantsList();

    }

    public void buttonListeners() {

        findViewById(R.id.button_id_log_in).setOnClickListener(this);
        findViewById(R.id.googleSignInButton).setOnClickListener(this);
        findViewById(R.id.forgotPass_logIn).setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                handleGoogleSignInResult(task);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(Google_Tag, "Google sign in failed", e);
            }
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            assert account != null;

            GoogleCredential googleCredential = new GoogleCredential(account.getServerAuthCode());
            final RemoteMongoCollection<Document> user_coll = mongoDbSetup.getUsers_collection();

            Stitch.getDefaultAppClient().getAuth().loginWithCredential(googleCredential)
                    .continueWithTask(
                            task -> {
                                if (!task.isSuccessful()) {
                                    Log.e("STITCH", "Login failed!");
                                    Log.w(Google_Tag, "signInWithCredential:failure", task.getException());
                                    Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                                    throw task.getException();

                                }
                                //todo  add filter for not duplicating users on insert
                                else {
                                    final String displayName = task.getResult().getProfile().getFirstName()
                                            + " " + task.getResult().getProfile().getLastName();
                                    final String email = task.getResult().getProfile().getEmail();
                                    final String photoURL = task.getResult().getProfile().getPictureUrl();
                                    final String birthday = task.getResult().getProfile().getBirthday();

                                    Log.d(TAG, "google sign in result: " + "\n" + "displayName: " + displayName + "\n" + "email: " + email
                                            + "\n" + "PictureURL: " + photoURL);

                                    googleUser = new User(task.getResult().getId(), displayName, email, photoURL, 0, birthday);

                                    googleUserUpdateDoc = mongoDbSetup.createUserDocument(
                                            googleUser.getId(), googleUser.getName(),
                                            googleUser.getEmail(), googleUser.getProfilePicture(),
                                            googleUser.getNumber_of_plants(), googleUser.getBirthday()
                                    );

                                    mongoDbSetup.checkIfExists(user_coll, googleUserUpdateDoc);

                                    return null;

                                }
                            }

                    )
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("STITCH", "Found docs: " + task.getResult().toString());

                            new Handler().postDelayed(() -> mongoDbSetup.goToWhereverWithFlags(getApplicationContext(),
                                    getApplicationContext(), HomeActivity.class), Toast.LENGTH_SHORT);
                            return;
                        }
                        Log.e("STITCH", "Error: " + task.getException().toString());
                        task.getException().printStackTrace();
                    });
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private boolean existsAlready() {

        return false;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [END signOut]
    private void loginEmailMongoDb() {
        String emailToUse = mEmailField.getText().toString();
        String passToUse = mPasswordField.getText().toString();

        // first check if our textFields aren't empty
        if (TextUtils.isEmpty(emailToUse) && TextUtils.isEmpty(passToUse)) {
            mEmailField.setError("Required.");
            mPasswordField.setError("Required.");

            Toast.makeText(getApplicationContext(), "Please type in email and password", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(emailToUse)) {
            mEmailField.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please type in email or phone", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(passToUse)) {
            mPasswordField.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please choose password", Toast.LENGTH_SHORT).show();

        } else {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Signing in");
            progressDialog.setMessage("Signing in, please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIcon(R.drawable.ai_plant);
            progressDialog.show();

            SecureRandom random = new SecureRandom();
            int randomInt = random.nextInt((int) Math.pow(10.0, 1000.0));

            final RemoteMongoCollection<Document> user_coll = mongoDbSetup.getUsers_collection();
            UserPasswordCredential credential = new UserPasswordCredential(emailToUse, passToUse);

            mStitchAuth.loginWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    Toast.makeText(mContext, "Logged in succesfully", Toast.LENGTH_SHORT).show();

                mStitchUser = task.getResult();
                client_id = mStitchUser.getId();
                firstName = mStitchUser.getProfile().getFirstName();
                lastName = mStitchUser.getProfile().getLastName();
                mail = mStitchUser.getProfile().getEmail();
                birthday = mStitchUser.getProfile().getBirthday();
                photo = mStitchUser.getProfile().getPictureUrl();

                if (firstName == null) {
                    firstName = "user";
                }
                if (lastName == null) {
                    lastName = "_" + randomInt;
                }
                if (birthday == null) {
                    birthday = "01/01/1919";
                }
                if (photo == null) {
                    photo = "https://drive.google.com/file/d/1QYW_j4Twu2Vj0dHWDfr9A_LcTZybwUKI/view?usp=sharing";
                }
//                fetchedDOc = credential.getMaterial();

                updateDoc = new Document();
                updateDoc.put("logged_user_id", client_id);
                updateDoc.put("name", firstName + lastName);
                updateDoc.put("email", mail);
                updateDoc.put("picture", photo);
                updateDoc.put("number_of_plants", 0);
                updateDoc.put("birthday", birthday);

                mongoDbSetup.checkIfExists(user_coll, updateDoc);

                progressDialog.dismiss();


            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Log in Error: " + e.getCause(), Toast.LENGTH_SHORT).show();
                mStitchAuth.logout();
            });

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_id_log_in:
                loginEmailMongoDb();
                break;

            case R.id.googleSignInButton:
                signIn();
                break;

            case R.id.forgotPass_logIn:

                Fragment fragmentForgotPass = fragmentManager.findFragmentById(R.id.useThisFragmentID);

                if (fragmentForgotPass == null) {
                    fragmentForgotPass = new ForgotPassFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.add(R.id.useThisFragmentID, fragmentForgotPass).commit();
                }

                break;

            case R.id.sign_up_text:
                Fragment fragmentRegister = fragmentManager.findFragmentById(R.id.useThisFragmentID);
                if (fragmentRegister == null) {
                    fragmentRegister = new SignUpFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.add(R.id.useThisFragmentID, fragmentRegister).commit();
                }

                break;


        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
