package com.example.aiplant.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchAuthListener;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.google.GoogleCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;

import org.bson.BsonBinary;
import org.bson.Document;

import java.security.SecureRandom;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String Google_Tag = "GoogleActivity";

    //Google signIn
    private static final int RC_SIGN_IN = 9001;

    //database
    private MongoDbSetup mongoDbSetup;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private StitchAuth mStitchAuth;
    private StitchUser mStitchUser;
    private Document updateDoc, fetchedDOc;

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
    private StitchAuthListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fragmentManager = getSupportFragmentManager();
        mContext = getApplicationContext();
        connectMongoDb();
        mCallbackManager = CallbackManager.Factory.create();

        initLayout();
        buttonListeners();


    }

    private void connectMongoDb() {
        mongoDbSetup = MongoDbSetup.getInstance(getApplicationContext());
        mongoDbSetup.runAppClientInit();
        mGoogleSignInClient = MongoDbSetup.getGoogleSignInClient();
        mStitchAuth = mongoDbSetup.getStitchAuth();
        mStitchUser = mStitchAuth.getUser();
        Log.d(TAG, "auth: user " + mStitchAuth.getUser());
        Log.d(TAG, "auth: isLoggedIn: " + mStitchAuth.isLoggedIn());
        setMongoDbForLaterUse(mongoDbSetup);
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

    }

    public void buttonListeners() {

        findViewById(R.id.button_id_log_in).setOnClickListener(this);
        findViewById(R.id.googleSignInButton).setOnClickListener(this);
        findViewById(R.id.forgotPass_logIn).setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                Log.w(Google_Tag, "Google sign in failed", e.getCause());
            }
        }
    }

    private void addListener() {
        listener = new StitchAuthListener() {
            @Override
            public void onUserLoggedIn(StitchAuth auth, StitchUser loggedInUser) {
                auth.addAuthListener(listener);
                Log.d(TAG, "onUserLoggedIn: auth.isLoggedIn: " + auth.isLoggedIn() + "user_id " + loggedInUser.getId());
                Log.d(TAG, "onUserLoggedIn: listener: " + listener.toString());

            }
        };
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        GoogleSignInAccount account = null;
        try {
            account = completedTask.getResult(ApiException.class);
        } catch (ApiException e) {
            Log.d(TAG, "signInResult:failed code=" + e.getCause());
        }

        assert account != null;
        GoogleCredential googleCredential = new GoogleCredential(account.getServerAuthCode());
        final RemoteMongoCollection<Document> user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing in");
        progressDialog.setMessage("Signing in, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.ai_plant);

        progressDialog.show();
        mStitchAuth.loginWithCredential(googleCredential).continueWithTask(
                task -> {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        Log.e("STITCH", "Login failed!");
                        Log.w(Google_Tag, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        mStitchAuth.logout();
                        throw task.getException();

                    } else {
                        mStitchUser = task.getResult();
                        String id = mStitchUser.getId();
                        String displayName = task.getResult().getProfile().getFirstName()
                                + " " + task.getResult().getProfile().getLastName();
                        String email = task.getResult().getProfile().getEmail();
                        String photoURL = task.getResult().getProfile().getPictureUrl();
                        String birthday = task.getResult().getProfile().getBirthday();
                        byte[] data = new byte[]{};
                        BsonBinary edited_pic = new BsonBinary(data);

                        if (birthday == null) {
                            birthday = "01/01/1919";
                        }
                        if (photoURL == null) {
                            photoURL = "https://drive.google.com/file/d/1QYW_j4Twu2Vj0dHWDfr9A_LcTZybwUKI/view?usp=sharing";
                        }
                        Log.d(TAG, "google sign in result: " + "\n" + "user_id:" + id + "\n" + "displayName: " + displayName + "\n" + "email: " + email
                                + "\n" + "PictureURL: " + photoURL);

                        googleUserUpdateDoc = new Document("logged_user_id", mStitchUser.getId()).append(getResources().getString(R.string.name_for_db), displayName)
                                .append(getResources().getString(R.string.email_for_db), email).append(getResources().getString(R.string.picture_for_db), photoURL)
                                .append(getResources().getString(R.string.number_of_plants_for_db), 0).append(getResources().getString(R.string.birthday_for_db), birthday).append("edited_pic", edited_pic);
                        googleUser = new User(googleUserUpdateDoc.getString("logged_user_id"), googleUserUpdateDoc.getString("name"),
                                googleUserUpdateDoc.getString("email"), googleUserUpdateDoc.getString("picture"),
                                googleUserUpdateDoc.getInteger("number_of_plants"), googleUserUpdateDoc.getString("birthday"),
                                googleUserUpdateDoc.get("edited_pic", BsonBinary.class));
                        addListener();
                        mongoDbSetup.checkIfExists(user_coll, googleUserUpdateDoc);

                        new Handler().postDelayed(() -> {
                            progressDialog.dismiss();
                            mongoDbSetup.goToWhereverWithFlags(mContext, mContext, HomeActivity.class);
                        }, 0);

                        return null;

                    }
                }

        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("STITCH", "Found docs: " + task.getResult().toString());

                new Handler().postDelayed(() -> mongoDbSetup.goToWhereverWithFlags(getApplicationContext(),
                        getApplicationContext(), HomeActivity.class), Toast.LENGTH_SHORT);
                return;
            }
            Log.e("STITCH", "Error: " + task.getException().toString());
            task.getException().printStackTrace();
        }).addOnFailureListener(e -> Log.d(TAG, "signInResult:failed code=" + e.getCause()));

    }

    private void loginEmailMongoDb() {

        String emailToUse = mEmailField.getText().toString();
        String passToUse = mPasswordField.getText().toString();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing in");
        progressDialog.setMessage("Signing in, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.ai_plant);

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
            progressDialog.show();

            SecureRandom random = new SecureRandom();
            int randomInt = random.nextInt((int) Math.pow(10.0, 1000.0));

            UserPasswordCredential credential = new UserPasswordCredential(emailToUse, passToUse);
            final RemoteMongoCollection<Document> user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));

            mStitchAuth.loginWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mStitchUser = task.getResult();
                    client_id = mStitchUser.getId();
                    firstName = mStitchUser.getProfile().getFirstName();
                    lastName = mStitchUser.getProfile().getLastName();
                    mail = mStitchUser.getProfile().getEmail();
                    birthday = mStitchUser.getProfile().getBirthday();
                    photo = mStitchUser.getProfile().getPictureUrl();
                    byte[] data = new byte[]{};
                    BsonBinary edited_pic = new BsonBinary(data);

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

                    updateDoc = new Document("logged_user_id", client_id).append("name", firstName + lastName).append("email", mail)
                            .append("picture", photo).append("number_of_plants", 0).append("birthday", birthday)
                            .append("edited_pic", edited_pic);

                    Toast.makeText(mContext, "Logged in successfully", Toast.LENGTH_SHORT).show();

                    emailPassUser = new User(updateDoc.getString("logged_user_id"), updateDoc.getString("name"),
                            updateDoc.getString("email"), updateDoc.getString("picture"),
                            updateDoc.getInteger("number_of_plants"), updateDoc.getString("birthday"),
                            updateDoc.get("edited_pic", BsonBinary.class));

                    addListener();
                    mongoDbSetup.checkIfExists(user_coll, updateDoc);

                    new Handler().postDelayed(() -> {
                        progressDialog.dismiss();
                        mongoDbSetup.goToWhereverWithFlags(mContext, mContext, HomeActivity.class);
                    }, 0);

                    progressDialog.dismiss();

                }

            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Log in Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                mStitchAuth.logout();
            });

        }
    }

    private boolean fieldChecker(String email, String pass) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(getApplicationContext(), "Please type in email and password", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_id_log_in:
                loginEmailMongoDb();
//                new LoginWithCredentials().execute((Void[]) null);

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
        if (mStitchAuth.isLoggedIn()) {
            mongoDbSetup.goToWhereverWithFlags(mContext, mContext, HomeActivity.class);
        }
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

    private class LoginWithCredentials extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            Looper.prepare();
            loginEmailMongoDb();
            return null;
        }

        protected void onPostExecute(Void param) {

            Toast.makeText(mContext, "Jobs Done", Toast.LENGTH_SHORT).show();
        }
    }

}
