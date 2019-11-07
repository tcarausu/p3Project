package com.example.aiplant.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aiplant.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private static final String TAG = "ForgotPassFragment";

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

    // sending the mail to user to reset pass
    private void sendPassResetMail() {

        String email = forgot_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            forgot_email.setError("Required.");
            Toast.makeText(getContext(), "Please type a valid email", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Please check your inbox, we sent you a change password link", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else
                    Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            });

        }
    }

//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_forgot_instructions) {
            sendPassResetMail();
        }
    }
}

