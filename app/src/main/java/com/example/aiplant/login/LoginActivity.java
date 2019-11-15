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
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.google.GoogleCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String Google_Tag = "GoogleActivity";

    //Google signIn
    private static final int RC_SIGN_IN = 9001;

    //firebase
    private FirebaseAuth mAuth;
    private MongoDbSetup mongoDbSetup;
    private DatabaseReference user_ref;
    private DatabaseReference myRef;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    // widgets
    private MaterialButton loginButton;
    private TextView click_here_text, sign_up_text, orView, forgotPass_logIn;
    private RelativeLayout loginLayout;
    private EditText mEmailField, mPasswordField;
    private FragmentManager fragmentManager;
    private Context mContext;
    private StitchAppClient appClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;
        connectMongoDb();

        initLayout();
        buttonListeners();
    }

    private void connectMongoDb() {
        mongoDbSetup = MongoDbSetup.getInstance(getApplicationContext());
        MongoDbSetup.runAppClientInit();
        fragmentManager = getSupportFragmentManager();

        mGoogleSignInClient = MongoDbSetup.getClient();
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

    }

    public void buttonListeners() {

        findViewById(R.id.button_id_log_in).setOnClickListener(this);
        findViewById(R.id.googleSignInButton).setOnClickListener(this);
        findViewById(R.id.forgotPass_logIn).setOnClickListener(this);

//        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //token taken from firebase authentication data
//                .requestServerAuthCode("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //auth code from firebase authentication data
//                .requestEmail()
//                .build();

//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        mGoogleSignInClient = MongoDbSetup.getClient();
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
//                firebaseAuthWithGoogle(account);
                handleGoogleSignInResult(task);
//                instantUserDb(task);
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

            GoogleCredential googleCredential =
                    new GoogleCredential(account.getServerAuthCode());

            Stitch.getDefaultAppClient().getAuth().loginWithCredential(googleCredential)
                    .continueWithTask(
                            task -> {
                                if (!task.isSuccessful()) {
                                    Log.e("STITCH", "Login failed!");
                                    Log.w(Google_Tag, "signInWithCredential:failure", task.getException());
                                    Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                                    throw task.getException();
                                } else {
                                    final String displayName = task.getResult().getProfile().getFirstName()
                                            + " " + task.getResult().getProfile().getLastName();
                                    final String email = task.getResult().getProfile().getEmail();
                                    final String photoURL = task.getResult().getProfile().getPictureUrl();

                                    Log.d(TAG, "google sign in result: " + "\n" + "displayName: " + displayName + "\n" + "email: " + email
                                            + "\n" + "PictureURL: " + photoURL);

                                    final Document userDoc = new Document(
                                            "logged_user_id",
                                            task.getResult().getId())
                                            .append(
                                                    getResources().getString(R.string.email_for_db),
                                                    email)
                                            .append(getResources().getString(R.string.picture_for_db),
                                                    photoURL);

                                    final Document userDoc2 = new Document(
                                            "logged_user_id",
                                            task.getResult().getId())
                                            .append(
                                                    getResources().getString(R.string.email_for_db),
                                                    "boiii")
                                            .append(getResources().getString(R.string.picture_for_db),
                                                    "piiic");


                                    List<Document> docs = new ArrayList<>();
                                    docs.add(userDoc);
                                    docs.add(userDoc2);
                                    return MongoDbSetup.getUsers_collection().insertMany(docs);
                                }
                            }

                    )
//
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
//                    .addOnCompleteListener(
//                            task -> {
//                                if (task.isSuccessful()) {
//                                    final String displayName = task.getResult().getProfile().getFirstName()
//                                            + " " + task.getResult().getProfile().getLastName();
//                                    final String email = task.getResult().getProfile().getEmail();
//                                    final String photoURL = task.getResult().getProfile().getPictureUrl();
//
//                                    Log.d(TAG, "google sign in result: " + "\n" + "displayName: " + displayName + "\n" + "email: " + email
//                                            + "\n" + "PictureURL: " + photoURL);
//
////                        verifyFirstGoogleLogin(email, displayName, photoURL);
//                                    addUserToMongoDatabase(email, photoURL);
//
//                                    Log.d(Google_Tag, "signInWithCredential:success");
//                                    Snackbar.make(findViewById(R.id.login_layout), "Authentication successful.", Snackbar.LENGTH_SHORT).show();
//
//                                    new Handler().postDelayed(() -> mongoDbSetup.goToWhereverWithFlags(getApplicationContext(),
//                                            getApplicationContext(), HomeActivity.class), Toast.LENGTH_SHORT);
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w(Google_Tag, "signInWithCredential:failure", task.getException());
//                                    Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                                }
//
//                                if (!task.isSuccessful()) {
//                                    Toast.makeText(mContext, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    public void instantUserDb(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            assert account != null;

            GoogleCredential googleCredential =
                    new GoogleCredential(account.getServerAuthCode());

            Stitch.getDefaultAppClient().getAuth().loginWithCredential(googleCredential)
                    .continueWithTask(
                            task -> {
                                final String displayName = task.getResult().getProfile().getFirstName()
                                        + " " + task.getResult().getProfile().getLastName();
                                final String email = task.getResult().getProfile().getEmail();
                                final String photoURL = task.getResult().getProfile().getPictureUrl();

                                if (!task.isSuccessful()) {
                                    Log.e("STITCH", "Login failed!");
                                    throw task.getException();
                                }

                                final Document updateDoc = new Document(
                                        "owner_id",
                                        task.getResult().getId())
                                        .append(
                                                getResources().getString(R.string.email_for_db),
                                                email)
                                        .append(getResources().getString(R.string.picture_for_db),
                                                photoURL);


                                return MongoDbSetup.getUsers_collection().updateOne(
                                        null, updateDoc, new RemoteUpdateOptions().upsert(true)
                                );
                            }
                    ).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    Log.e("STITCH", "Update failed!");
                    throw task.getException();
                }
                List<Document> docs = new ArrayList<>();
                return MongoDbSetup.getUsers_collection()
                        .find(new Document("owner_id", MongoDbSetup.getAppClient().getAuth().getUser().getId()))
                        .limit(100)
                        .into(docs);
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("STITCH", "Found docs: " + task.getResult().toString());
                    return;
                }
                Log.e("STITCH", "Error: " + task.getException().toString());
                task.getException().printStackTrace();
            });
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
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
                        new Handler().postDelayed(() -> mongoDbSetup.goToWhereverWithFlags(getApplicationContext(), getApplicationContext(), HomeActivity.class), Toast.LENGTH_SHORT);

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
                loginEmailMongoDb(mEmailField, mPasswordField);

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

//    private void getMongoDbConnection() {
//        MongoClientURI uri = new MongoClientURI(
//                "mongodb+srv://Simortusos:<password>@gettingstarted-zmxym.gcp.mongodb.net/test?retryWrites=true&w=majority");
//
//        MongoClient mongoClient = new MongoClient(uri);
//        MongoDatabase database = mongoClient.getDatabase("test");
//    }

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
//    private void signOut() {
//        mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, task -> {
//                    // [START_EXCLUDE]
//                    new Handler().postDelayed(() -> goToWhereverWithFlags(getApplicationContext(),
//                            getApplicationContext(), LoginActivity.class), Toast.LENGTH_SHORT);
//
//                    // [END_EXCLUDE]
//                });
//    }
    public void addUserToMongoDatabase(String email, String picture) {

        final Document userDoc = new Document(
                getResources().getString(R.string.email_for_db),
                email)
                .append(getResources().getString(R.string.picture_for_db),
                        picture);
        RemoteMongoCollection<Document> usersDb = MongoDbSetup.getUsers_collection();

        usersDb.insertOne(userDoc);
    }

    // [END signOut]
    private void loginEmailMongoDb(
            EditText email, EditText password
    ) {
        String emailToUse = String.valueOf(email.getText());
        String passToUse = String.valueOf(password.getText());
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


            UserPasswordCredential credential = new UserPasswordCredential(emailToUse, passToUse);

            Stitch.
                    getDefaultAppClient()
                    .getAuth().loginWithCredential(credential)
                    .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.e("stitch", "Error logging in with email/password auth:", task.getException());

                                    String error = task.getException().getMessage();// get error from fireBase
                                    Toast.makeText(mContext, "Error: " + error, Toast.LENGTH_SHORT).show();

                                } else {
                                    Log.d("stitch", "Successfully logged in as user " + task.getResult().getId());

                                    new Handler().postDelayed(() ->
                                            mongoDbSetup.goToWhereverWithFlags(getApplicationContext(), getApplicationContext(), HomeActivity.class), Toast.LENGTH_SHORT);
                                }
                            }
                    );
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
