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
import android.widget.Button;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String Google_Tag = "GoogleActivity";

    //Google signIn
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
//    private CallbackManager mCallbackManager;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // widgets
    private MaterialButton loginButton;
    private TextView click_here_text,sign_up_text, orView, forgotPass_logIn;
    private RelativeLayout loginLayout;
    private EditText mEmailField, mPasswordField;
    private FragmentManager fragmentManager;
    private boolean isVerified;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initLayout();
        buttonListeners();
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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //token taken from firebase authentication data
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Initialize Facebook Login button
//        mCallbackManager = CallbackManager.Factory.create();

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
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(Google_Tag, "Google sign in failed", e);
            }
        }
    }

    // sign in with email method
    private void signInWithEmail() {

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        // first check if our textFields aren't empty
        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            mPasswordField.setError("Required.");

            Toast.makeText(getApplicationContext(), "Please type in email and password", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please type in email or phone", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            Toast.makeText(getApplicationContext(), "Please choose password", Toast.LENGTH_SHORT).show();

        } else {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Signing in");
            progressDialog.setMessage("Signing in, please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIcon(R.drawable.ai_plant);
            progressDialog.show();

            // after checking, we try to login
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                // if sign in is successful
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    verifyAccount(email); // check if user is verified by email
                }
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            });
        }
    }

    // verification  if user has validated or not

    /**
     * @param email : email of the registered user, allows login if & only if validated
     * @author Mo.Msaad
     **/
    private void verifyAccount(String email) {

        try {
            FirebaseUser user = mAuth.getCurrentUser();
            isVerified = user.isEmailVerified(); // getting boolean true or false from database
            if (isVerified) {
//                verifyFirstEmailLogin(email, "Chose a user name", avatarURL);
//                addUserToDataBase();
                goToWhereverWithFlags(getApplicationContext(), getApplicationContext(), HomeActivity.class); // if yes goto mainActivity
            } else {
                // else we first sign out the user, until he checks his email then he can connect
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Please verify your account.", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Somthing went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(Google_Tag, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        final String displayName = task.getResult().getUser().getDisplayName();
                        final String email = task.getResult().getUser().getEmail();
                        final String photoURL = task.getResult().getUser().getPhotoUrl().toString();

                        Log.d(TAG, "google sign in result: " + "\n" + "displayName: " + displayName + "\n" + "email: " + email
                                + "\n" + "PictureURL: " + photoURL);

//                        verifyFirstGoogleLogin(email, displayName, photoURL);
//                        addUserToDataBase();
                        Log.d(Google_Tag, "signInWithCredential:success");
                        Snackbar.make(findViewById(R.id.login_layout), "Authentication successful.", Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> goToWhereverWithFlags(getApplicationContext(), getApplicationContext(), HomeActivity.class), Toast.LENGTH_SHORT);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(Google_Tag, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }

                    if (!task.isSuccessful()) {
                        Toast.makeText(mContext, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_id_log_in:
                signInWithEmail();
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

    //
//    /**
//     * @param displayName: email fetched from google provider, used to add user email
//     * @param email:       name fetched from google provider, used to add user name
//     * @param photoURL:    phot fetched from google provider, used to add profile pic
//     * @author Mo.Msaad
//     **/
//    private void verifyFirstGoogleLogin(String email, String displayName, String photoURL) {
//
//        SharedPreferences ggPrefs = getSharedPreferences("ggPrefs", MODE_PRIVATE);
//        boolean googleFirstLogin = ggPrefs.getBoolean("ggPrefs", true);
//
//        //if its the first run we change the boolean to false
//        if (googleFirstLogin) {
//            addNewUser(email, displayName, "description", "website", photoURL);
//            SharedPreferences.Editor editor = ggPrefs.edit();
//            editor.putBoolean("ggPrefs", false);
//            editor.apply();
//            Log.d(TAG, "verifyFirstRun: boolean first run is: " + googleFirstLogin);
//        }
//    }
//    /**
//     * @param  email: email fetched   used to login
//     * @param displayName: display name provider
//     * @param photoURL: photo url fetched from google provider
//     *
//     * */
//    private void verifyFirstEmailLogin(String email, String displayName, String photoURL) {
//
//        SharedPreferences firstLogin = getSharedPreferences("logPrefs", MODE_PRIVATE);
//        boolean FirstLogin = firstLogin.getBoolean("logPrefs", true);
//
//        //if its the first run we change the boolean to false
//        if (FirstLogin) {
//            addNewUser(email, displayName, "description", "website", photoURL);
//            SharedPreferences.Editor editor = firstLogin.edit();
//            editor.putBoolean("logPrefs", false);
//            editor.apply();
//            Log.d(TAG, "verifyFirstRun: boolean first run is: " + FirstLogin);
//        }
//    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [START signOut]
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    // [START_EXCLUDE]
                    new Handler().postDelayed(() -> goToWhereverWithFlags(getApplicationContext(),
                            getApplicationContext(), LoginActivity.class), Toast.LENGTH_SHORT);

                    // [END_EXCLUDE]
                });
    }
    // [END signOut]

    public static void goToWhereverWithFlags(Context activityContext, Context c, Class<? extends AppCompatActivity> cl) {

        activityContext.startActivity(new Intent(c, cl).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

}
