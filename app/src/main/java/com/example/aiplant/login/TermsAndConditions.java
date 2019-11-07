package com.example.aiplant.login;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.example.aiplant.R;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

public class TermsAndConditions extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private static final String TAG = "ForgotPassFragment";

    private TextView register_for_free, terms_of_conditions;
    private TextView terms_of_conditions_governing_law, terms_of_conditions_governing_law_text;
    private TextView terms_of_conditions_accounts, terms_of_conditions_accounts_text;
    private TextView terms_of_conditions_changes, terms_of_conditions_changes_text;

    private ImageView aiplant_icon;
    private ScrollView terms_and_conditions_scroll_view;
    private MaterialButton I_agree_to_terms_and_conditions;
    private Bundle savedInstanceState;
    private Context mContext;

    public TermsAndConditions() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        View v = inflater.inflate(R.layout.fragment_terms_of_conditions, container, false);
        mContext = getActivity();

        findWidgets(v);

        return v;
    }


    private void findWidgets(View v) {
        aiplant_icon = v.findViewById(R.id.aiplant_icon);
        terms_and_conditions_scroll_view = v.findViewById(R.id.terms_and_conditions_scroll_view);

        register_for_free = v.findViewById(R.id.register_for_free);

        terms_of_conditions = v.findViewById(R.id.terms_of_conditions);
        terms_of_conditions_governing_law = v.findViewById(R.id.terms_of_conditions_governing_law);
        terms_of_conditions_governing_law_text = v.findViewById(R.id.terms_of_conditions_governing_law_text);
        terms_of_conditions_accounts = v.findViewById(R.id.terms_of_conditions_accounts);
        terms_of_conditions_accounts_text = v.findViewById(R.id.terms_of_conditions_accounts_text);
        terms_of_conditions_changes = v.findViewById(R.id.terms_of_conditions_changes);
        terms_of_conditions_changes_text = v.findViewById(R.id.terms_of_conditions_changes_text);


        I_agree_to_terms_and_conditions = v.findViewById(R.id.I_agree_to_terms_and_conditions);

        I_agree_to_terms_and_conditions.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.back_button) {
        if (v.getId() == R.id.I_agree_to_terms_and_conditions) {

            Bundle bundle_with_arguments = getArguments();

            Objects.requireNonNull(bundle_with_arguments).putBoolean("clicked", true);

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

            SignUpFragment signUpFragment = new SignUpFragment();
            signUpFragment.setArguments(bundle_with_arguments);

            fragmentTransaction.replace(R.id.useThisFragmentID_sign_up, signUpFragment);
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();

        }
    }
}
