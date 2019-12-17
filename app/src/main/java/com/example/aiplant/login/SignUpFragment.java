package com.example.aiplant.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.aiplant.R;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.google.android.material.button.MaterialButton;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;

import static android.widget.Toast.LENGTH_LONG;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ForgotPassFragment";

    private MongoDbSetup mongoDbSetup;

    private TextView register_for_free, terms_and_conditions;
    private EditText name_last_name, signUp_email, pass_field, confirm_pass;

    private ImageView aiPlant_icon;
    private MaterialButton send_registration_instructions;
    private ProgressDialog loadingBar;
    private Bundle savedInstanceState;
    private CheckBox checkbox;
    private Context mContext;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        mContext = getActivity();

        mongoDbSetup = MongoDbSetup.getInstance(mContext);

        loadingBar = new ProgressDialog(this.getContext());
        findWidgets(v);

        return v;
    }

    private void findWidgets(View v) {
        aiPlant_icon = v.findViewById(R.id.aiplant_icon);
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

        Bundle bundle_with_arguments = getArguments();

        if (bundle_with_arguments != null) {
            boolean clicked = bundle_with_arguments.getBoolean("clicked");

            String email = bundle_with_arguments.getString("email");
            String name_lastName = bundle_with_arguments.getString("name_lastName");
            String password = bundle_with_arguments.getString("password");
            String confirm_password = bundle_with_arguments.getString("confirm_password");

            signUp_email.setText(email);
            name_last_name.setText(name_lastName);
            pass_field.setText(password);
            confirm_pass.setText(confirm_password);

            checkbox.setChecked(clicked);
        }

    }

    /**
     * Create an User with Email/Password Provider, this method checks for empty input,
     * in case of successfully filling those fields the user has an email sent which asks the user to
     * confirm his email to use the app.
     * <p>
     * Here the user also ticks(the checkbox) of terms and conditions or just reads them and then successfully can use the app.
     */
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
                && !checkbox.isChecked()
                && !password.equals(confPass)
        ) {
            signUp_email.setError(getString(R.string.required));
            name_last_name.setError(getString(R.string.required));
            pass_field.setError(getString(R.string.required));
            confirm_pass.setError(getString(R.string.required));
            checkbox.setError(getString(R.string.agree_to_terms_first));

            Toast.makeText(getContext(), getString(R.string.please_fill), LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(email)) {
            signUp_email.setError(getString(R.string.required));
            Toast.makeText(getContext(), getString(R.string.please_valid_email), LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(user_name)) {
            name_last_name.setError(getString(R.string.required));
            Toast.makeText(getContext(), getString(R.string.please_valid_name), LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(password)) {
            pass_field.setError(getString(R.string.required));
            Toast.makeText(getContext(), getString(R.string.please_choose_pass), LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(confPass)) {
            confirm_pass.setError(getString(R.string.required));
            Toast.makeText(getContext(), getString(R.string.please_conf_pass), LENGTH_LONG).show();

        } else if (!password.equals(confPass)) {
            pass_field.setError(getString(R.string.pass_no_match));
            confirm_pass.setError(getString(R.string.pass_no_match));
            Toast.makeText(getContext(), getString(R.string.pass_must_match), LENGTH_LONG).show();

        } else if (!checkbox.isChecked()) {
            checkbox.setError(getString(R.string.agree_to_terms_first));
            Toast.makeText(getContext(), getString(R.string.agree_terms_toast), LENGTH_LONG).show();
        } else {

            if (mongoDbSetup.checkInternetConnection(mContext)) {
                loadingBar.setTitle("Creating account...");
                loadingBar.setMessage("Please wait while your account is being created...");
                loadingBar.setIcon(R.drawable.ai_plant);
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);
                registerToMongoDbWithEmail(signUp_email.getText().toString(), pass_field.getText().toString());
            } else
                Toast.makeText(getContext(), getString(R.string.check_internet_connection), LENGTH_LONG).show();

        }

    }

    /**
     * @param email    is the String value take from the Email Field
     * @param password is the String value take from the Password Field
     *                 <p>
     *                 This method Handles UserPasswordAuthProviderClient data provided by the Stitch Authentication.
     *                 In case the Task is successful the User is sent an email with the a link where he can make a new password.
     *                 Followed by the user being redirected to Login so that he could proceed with the login.
     */
    private void registerToMongoDbWithEmail(String email, String password) {

        UserPasswordAuthProviderClient emailPassClient = Stitch.
                getDefaultAppClient()
                .getAuth().getProviderClient(UserPasswordAuthProviderClient.factory);

        emailPassClient.registerWithEmail(email, password)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("stitch", "Successfully sent account confirmation email");
                                new Handler().postDelayed(() -> mongoDbSetup.intentWithFlag(getActivity(),
                                        getActivity(), LoginActivity.class), LENGTH_LONG);
                                Toast.makeText(getContext(), getString(R.string.reg_complete), LENGTH_LONG).show();
                                getActivity().finish();

                            } else {
                                Log.e("stitch", "Error registering new user:", task.getException());

                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(), "Error: " + error, LENGTH_LONG).show();
                            }
                        }
                );
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
                    bundleFunctionality(terms);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.useThisFragmentID_sign_up, terms);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    break;
                }
        }
    }

    private void bundleFunctionality(Fragment terms) {
        Bundle bundle = new Bundle();
        String email = signUp_email.getText().toString();
        String name_lastName = name_last_name.getText().toString();
        String password = pass_field.getText().toString();
        String confirm_password = confirm_pass.getText().toString();

        bundle.putString("email", email);
        bundle.putString("name_lastName", name_lastName);
        bundle.putString("password", password);
        bundle.putString("confirm_password", confirm_password);

        terms.setArguments(bundle);

    }

}
