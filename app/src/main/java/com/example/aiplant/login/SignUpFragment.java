package com.example.aiplant.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aiplant.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private static final String TAG = "ForgotPassFragment";
    private static final String AI_PLANT_PREFS = "AI_PLANT_PREFS";
    private static final String TERMS_AND_CONDITIONS = "TERMS_AND_CONDITIONS";

    private FirebaseAuth mAuth;
    private TextView register_for_free, terms_and_conditions;
    private EditText name_last_name, signUp_email, pass_field, confirm_pass;

    private ImageView aiplant_icon;
    private MaterialButton send_registration_instructions;
    private ProgressDialog loadingBar;
    private Bundle savedInstanceState;
    private CheckBox checkbox;
    private Context mContext;
    private FragmentManager fragmentManager;

    public SignUpFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        mContext = getActivity();


        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this.getContext());
        findWidgets(v);

        return v;
    }

    private void findWidgets(View v) {
        aiplant_icon = v.findViewById(R.id.aiplant_icon);
        checkbox = v.findViewById(R.id.checkbox);

        register_for_free = v.findViewById(R.id.register_for_free);

        signUp_email = v.findViewById(R.id.signUpFragment_email_field);
        name_last_name = v.findViewById(R.id.name_last_name);
        pass_field = v.findViewById(R.id.pass_field);
        confirm_pass = v.findViewById(R.id.confirm_pass);


        send_registration_instructions = v.findViewById(R.id.send_registration_instructions);
        terms_and_conditions = v.findViewById(R.id.terms_and_conditions);

        send_registration_instructions.setOnClickListener(this);
        terms_and_conditions.setPaintFlags(terms_and_conditions.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        terms_and_conditions.setOnClickListener(this);

    }

    private void createUserWithEmail() {
        // getting input from device
        final String email = signUp_email.getText().toString();
        final String user_name = name_last_name.getText().toString();
        String password = pass_field.getText().toString();
        String confPass = confirm_pass.getText().toString();

        //checking if any is empty or pass doesn't match, the rest mAuth takes care of
        if (TextUtils.isEmpty(email)
                && TextUtils.isEmpty(user_name)
                && TextUtils.isEmpty(password)
                && TextUtils.isEmpty(confPass)
                && !checkbox.isChecked()) {
            signUp_email.setError("Required");
            name_last_name.setError("Required");
            pass_field.setError("Required");
            confirm_pass.setError("Required");
            checkbox.setError("Agree to Terms and Conditions first");

            Toast.makeText(getContext(), "Please fill in the information and Agree to Terms and Conditions", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(email)) {
            signUp_email.setError("Required");
            Toast.makeText(getContext(), "Please type in a valid email", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(user_name)) {
            name_last_name.setError("Required");
            Toast.makeText(getContext(), "Please fill in with Name and Last Name", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {
            pass_field.setError("Required");
            Toast.makeText(getContext(), "Please choose a password", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(confPass)) {
            confirm_pass.setError("Required");
            Toast.makeText(getContext(), "Please confirm password", Toast.LENGTH_SHORT).show();

        } else if (!password.equals(confPass)) {
            pass_field.setError("Password doesn't match");
            confirm_pass.setError("Password doesn't match");
            Toast.makeText(getContext(), "Error: Password must match confirm password. Try again", Toast.LENGTH_SHORT).show();

        } else if (!checkbox.isChecked()) {
            checkbox.setError("Agree to Terms and Conditions first");
            Toast.makeText(getContext(), "Error: Agree to Terms and Conditions first.", Toast.LENGTH_SHORT).show();

        } else {
            loadingBar.setTitle("Creating account...");
            loadingBar.setMessage("Please wait while your account is being created...");
            loadingBar.setIcon(R.drawable.ai_plant);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            //if all are fine, then try to create a user

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                // if success

                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    Toast.makeText(getContext(), R.string.registration_success, Toast.LENGTH_SHORT).show();
                    sendVerifyEmail();

                    mAuth.signOut();
                    new Handler().postDelayed(() ->
                            LoginActivity.goToWhereverWithFlags(getActivity(), getActivity(), LoginActivity.class), Toast.LENGTH_SHORT);

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    mAuth.signOut(); // always sign out the user if something goes wrong
                }


            });

        }

    }

    // verification email
    private void sendVerifyEmail() {

        FirebaseUser user = mAuth.getCurrentUser();// check user
        if (mAuth != null && user != null) {

            user.sendEmailVerification().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    mAuth.signOut();// need to sign out the user every time until he confirms email

                } else {
                    String error = task.getException().getMessage();// get error from fireBase
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    mAuth.signOut();// need to sign out the user every time until he confirms email
                }

            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.send_registration_instructions:
                createUserWithEmail();

                break;

            case R.id.terms_and_conditions:
                if (!checkbox.isChecked()) {
                    TermsAndConditions terms = new TermsAndConditions();

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.useThisFragmentID_sign_up, terms);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    break;
                }
        }
    }

}
