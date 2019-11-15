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
import com.google.firebase.auth.FirebaseAuth;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;

public class ForgotPassFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private static final String TAG = "ForgotPassFragment";

    private MongoDbSetup mongoDbSetup;
    private Context mContext;

    private FirebaseAuth mAuth;
    private TextView forgot_password, simply_enter;
    private EditText forgot_email;
    private ImageView aiplant_icon;
    private MaterialButton send_forgot_instructions;

    public ForgotPassFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_pass, container, false);
        mContext = getActivity();

        mongoDbSetup = ((LoginActivity) getActivity()).getMongoDbForLaterUse();

        mAuth = FirebaseAuth.getInstance();
        findWidgets(v);

        return v;
    }

    private void findWidgets(View v) {
        forgot_password = v.findViewById(R.id.forgot_password);
        simply_enter = v.findViewById(R.id.simply_enter);

        forgot_email = v.findViewById(R.id.forgot_email);
        aiplant_icon = v.findViewById(R.id.aiplant_icon);

        send_forgot_instructions = v.findViewById(R.id.send_forgot_instructions);

        send_forgot_instructions.setOnClickListener(this);
    }

    private void handlePasswordReset(EditText forgot_email) {
        String forgot_mail = forgot_email.getText().toString();

        UserPasswordAuthProviderClient emailPassClient = Stitch.getDefaultAppClient().getAuth().getProviderClient(
                UserPasswordAuthProviderClient.factory
        );

        emailPassClient.sendResetPasswordEmail(forgot_mail)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("stitch", getResources().getString(R.string.sent_reset_email));
                                new Handler().postDelayed(() ->
                                        mongoDbSetup.goToWhereverWithFlags(getActivity(), getActivity(), LoginActivity.class), Toast.LENGTH_SHORT);

                                Toast.makeText(getActivity(), getResources().getString(R.string.sent_reset_email), Toast.LENGTH_SHORT).show();

                            } else {
                                Log.e("stitch", "Error sending password reset email:", task.getException());
                            }
                        }
                );
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_forgot_instructions) {
            handlePasswordReset(forgot_email);
        }
    }
}

