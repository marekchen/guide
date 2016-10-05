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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.model.GuideUser;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.analytics.DroiAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenpei on 2016/5/23.
 */

public class RegisterFragment extends Fragment {

    private UserRegisterTask mAuthTask = null;

    // UI references.
    @BindView(R.id.user_name)
    EditText mUserNameView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.confirm_password)
    EditText mConfirmPasswordView;

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
        mProgressView = new ProgressDialog(getActivity());
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    }

    @OnClick(R.id.to_login_fragment)
    void toLoginButton() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment loginFragment = new LoginFragment();
        transaction.replace(R.id.droi_login_container, loginFragment);
        transaction.commit();
    }

    @OnClick(R.id.register_button)
    void attemptRegister() {
        //计数事件
        //DroiAnalytics.onEvent(getActivity(), "register");
        if (mAuthTask != null) {
            return;
        }

        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_not_same));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!isUserNameValid(userName)) {
            mUserNameView.setError(getString(R.string.error_invalid_user_name));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            showProgress(true);
            mAuthTask = new UserRegisterTask(userName, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUserNameValid(String userName) {
        return userName.length() > 8;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 8;
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

    public class UserRegisterTask extends AsyncTask<Void, Void, DroiError> {
        private final String mPassword;
        private final String mUserName;

        UserRegisterTask(String userName, String password) {
            mPassword = password;
            mUserName = userName;
        }

        @Override
        protected DroiError doInBackground(Void... params) {
            GuideUser user = new GuideUser();
            user.setUserId(mUserName);
            user.setPassword(mPassword);
            DroiError droiError = user.signUp();
            return droiError;
        }

        @Override
        protected void onPostExecute(final DroiError droiError) {
            mAuthTask = null;
            showProgress(false);
            Log.i("test", "error" + droiError.toString());
            if (droiError.isOk()) {
                activity.finish();
            } else {
                if (droiError.getCode() == DroiError.USER_ALREADY_EXISTS) {
                    mUserNameView.setError(getString(R.string.error_user_already_exists));
                    mUserNameView.requestFocus();
                } else {
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
