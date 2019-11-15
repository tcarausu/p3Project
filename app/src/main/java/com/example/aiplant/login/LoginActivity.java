package com.example.aiplant.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    // widgets
    private Button loginButton;
    private TextView signUp, orView;
    private RelativeLayout loginLayout;
    private EditText mEmailField, mPasswordField;
    private FragmentManager fragmentManager;
    private boolean isVerified;
    private Context mContext;
//    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLayout();
        buttonListeners();
    }

    public void initLayout() {
        mEmailField = findViewById(R.id.email_id_logIn);
        mPasswordField = findViewById(R.id.password_id_logIn);
        loginLayout = findViewById(R.id.login_activity);
        signUp = findViewById(R.id.sign_up);
        orView = findViewById(R.id.orView);

        fragmentManager = getSupportFragmentManager();

    }

    public void buttonListeners() {

        findViewById(R.id.button_id_log_in).setOnClickListener(this);
        findViewById(R.id.googleSignInButton).setOnClickListener(this);
        findViewById(R.id.textView_id_forgotPass_logIn).setOnClickListener(this);
    }

//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_id_log_in:
//                    signInWithEmail();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                break;
//                case R.id.textView_id_forgotPass_logIn:
//
//                    Fragment fragmentForgotPass = fragmentManager.findFragmentById(R.id.useThisFragmentID);
//
//                    if (fragmentForgotPass == null) {
//                        fragmentForgotPass = new ForgotPassFragment();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.add(R.id.useThisFragmentID, fragmentForgotPass).commit();
//                    }
//
//                    break;
//
//                case R.id.sign_up:
//                    Fragment fragmentRegister = fragmentManager.findFragmentById(R.id.useThisFragmentID);
//                    if (fragmentRegister == null) {
//                        fragmentRegister = new SignUpFragment();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.addToBackStack(null);
//
//                        fragmentTransaction.add(R.id.useThisFragmentID, fragmentRegister).commit();
//                    }
//
//                    break;
//
//                case R.id.googleSignInButton:
////                    signIn();
//                    break;
        }
    }
}
