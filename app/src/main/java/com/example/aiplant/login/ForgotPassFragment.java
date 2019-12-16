package com.example.aiplant.login;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aiplant.R;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.google.android.material.button.MaterialButton;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;

import static android.widget.Toast.LENGTH_LONG;

public class ForgotPassFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private static final String TAG = "ForgotPassFragment";

    private MongoDbSetup mongoDbSetup;
    private Context mContext;

    private EditText forgot_email;
    private ImageView aiplant_icon;
    private MaterialButton send_forgot_instructions;

    public ForgotPassFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_pass, container, false);
        mContext = getActivity();
        mongoDbSetup = MongoDbSetup.getInstance(mContext);
        findWidgets(v);

        return v;
    }

    private void findWidgets(View v) {
        v.findViewById(R.id.forgot_password);
        v.findViewById(R.id.simply_enter);
        forgot_email = v.findViewById(R.id.forgot_email);
        aiplant_icon = v.findViewById(R.id.aiplant_icon);
        send_forgot_instructions = v.findViewById(R.id.send_forgot_instructions);
        send_forgot_instructions.setOnClickListener(this);
    }

    /**
     * @param forgot_email_text is the String value take from the Forgot Password Email Field
     * <p>
     *This method Handles UserPasswordAuthProviderClient data provided by the Stitch Authentication.
     *In case the Task is successful the User is sent an email with the a link where he can make a new password.
     *Followed by the user being redirected to Login so that he could proceed with the login.
     */
    private void handlePasswordReset(String forgot_email_text) {
        UserPasswordAuthProviderClient emailPassClient = Stitch.getDefaultAppClient().getAuth().getProviderClient(
                UserPasswordAuthProviderClient.factory
        );

        emailPassClient.sendResetPasswordEmail(forgot_email_text)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                new Handler().postDelayed(() ->
                                        mongoDbSetup.goToWhereverWithFlags(getActivity(), getActivity(), LoginActivity.class), LENGTH_LONG);

                                Toast.makeText(getActivity(), getResources().getString(R.string.sent_reset_email), LENGTH_LONG).show();

                            } else {
                                Log.e("stitch", "Error sending password reset email:", task.getException());
                            }
                        }


                );
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_forgot_instructions) {
            handlePasswordReset(forgot_email.getText().toString());
        }
    }
}

