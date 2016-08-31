package com.droi.guide.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.model.GuideUser;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;

/**
 * Created by chenpei on 2016/5/23.
 */

public class RegisterFragment extends Fragment {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private ProgressDialog mProgressView;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        DroiAnalytics.onFragmentStart(getActivity(), "RegisterFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "RegisterFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);
        mUserNameView = (AutoCompleteTextView) view.findViewById(R.id.user_name);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) view.findViewById(R.id.confirm_password);
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) view.findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        TextView toLoginButton = (TextView) view.findViewById(R.id.to_login_fragment);
        toLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Fragment loginFragment = new LoginFragment();
                transaction.replace(R.id.droi_login_container, loginFragment);
                transaction.commit();
            }
        });

        mProgressView = new ProgressDialog(getActivity());
        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        //计数事件
        Log.i("test","2");
        DroiAnalytics.onEvent(getActivity(), "register");
        Log.i("test","3");
        if (mAuthTask != null) {
            return;
        }
        Log.i("test","4");
        // Reset errors.
        mEmailView.setError(null);
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        Log.i("test","5");
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        Log.i("test","6");
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_not_same));
            focusView = mConfirmPasswordView;
            cancel = true;
        }
        Log.i("test","7");
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        Log.i("test","8");
        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(email,userName, password);
            mAuthTask.execute((Void) null);
        }
        Log.i("test","9");
    }

    private boolean isNameValid(String name) {
        return name.length() > 4;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isConfirmPasswordValid(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    /**
     * Shows the progress
     */
    private void showProgress(final boolean show) {
        if (show) {
            mProgressView.show();
        } else {
            mProgressView.dismiss();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, DroiError> {
        private final String mEmail;
        private final String mPassword;
        private final String mUserName;

        UserRegisterTask(String email, String userName,String password) {
            mEmail = email;
            mPassword = password;
            mUserName = userName;
        }

        @Override
        protected DroiError doInBackground(Void... params) {
            Log.i("test","10");
            GuideUser user = new GuideUser();
            user.setUserId(mEmail);
            user.userName = mUserName;
            //user.setEmail(mEmail);
            user.setPassword(mPassword);
            Log.i("test","11");
            DroiError droiError = user.signUp();
            Log.i("test","12");
            return droiError;
        }

        @Override
        protected void onPostExecute(final DroiError droiError) {
            mAuthTask = null;
            showProgress(false);
            Log.i("test","error"+droiError.toString());
            if (droiError.isOk()) {
                activity.finish();
                Log.i("test","13");
            } else {
                if (droiError.getCode() == DroiError.USER_ALREADY_EXISTS) {
                    mPasswordView.setError(getString(R.string.error_user_already_exists));
                    mPasswordView.requestFocus();
                    Log.i("test","14");
                } else {
                    Log.i("test","15");
                    Toast.makeText(getActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
