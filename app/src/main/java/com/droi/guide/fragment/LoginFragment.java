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
//import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {

    private static String TAG = "LoginFragment";
    private UserLoginTask mAuthTask = null;

    @BindView(R.id.user_name)
    EditText mUserNameView;
    @BindView(R.id.password)
    EditText mPasswordView;
    ProgressDialog mProgressView;
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
       // DroiAnalytics.onFragmentStart(getActivity(), "LoginFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
      // DroiAnalytics.onFragmentEnd(getActivity(), "LoginFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mProgressView = new ProgressDialog(getActivity());
        mProgressView.setMessage("Login...");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.to_register_fragment)
    void toRegister() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment registerFragment = new RegisterFragment();
        transaction.replace(R.id.droi_login_container, registerFragment);
        transaction.commit();
    }

    @OnClick(R.id.login_button)
    void attemptLogin() {
        //计数事件
        //DroiAnalytics.onEvent(getActivity(), "login");
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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
            mAuthTask = new UserLoginTask(userName, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUserNameValid(String userName) {
        return userName.length() > 8;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 8;
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

    @OnClick(R.id.weixin_login)
    void weixinLogin() {
        Log.i("TEST", "weixinLogin");

    }

    public class UserLoginTask extends AsyncTask<Void, Void, DroiError> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected DroiError doInBackground(Void... params) {
            DroiError droiError = new DroiError();
            GuideUser user = DroiUser.login(mEmail,
                    mPassword, GuideUser.class, droiError);
            return droiError;
        }

        @Override
        protected void onPostExecute(final DroiError droiError) {
            mAuthTask = null;
            showProgress(false);
            if (droiError.isOk()) {
                Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                if (droiError.getCode() == DroiError.USER_NOT_EXISTS) {
                    mUserNameView.setError(getString(R.string.error_user_not_exists));
                    mUserNameView.requestFocus();
                } else if (droiError.getCode() == DroiError.USER_PASSWORD_INCORRECT) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                } else {
                    Log.i(TAG, "error:" + droiError.toString());
                    Toast.makeText(getActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
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
