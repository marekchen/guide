package com.droi.guide.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.interfaces.OnFragmentInteractionListener;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmPinFragment extends BackHandledFragment {

    private static final String TAG = "ConfirmPinFragment";
    private OnFragmentInteractionListener mListener;
    CountDownTimerUtils mCountDownTimerUtils;

    @BindView(R.id.pin_code)
    EditText pinCodeEditText;
    @BindView(R.id.timer)
    TextView timerTextView;

    public ConfirmPinFragment() {
        // Required empty public constructor
    }

    public static ConfirmPinFragment newInstance() {
        ConfirmPinFragment fragment = new ConfirmPinFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_pin, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCountDownTimerUtils = new CountDownTimerUtils(60000, 1000);
        mCountDownTimerUtils.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCountDownTimerUtils.cancel();
    }

    @OnClick(R.id.confirm_button)
    public void onConfirmButtonPressed() {
        DroiUser user = DroiUser.getCurrentUser();
        String pinCode = pinCodeEditText.getText().toString();
        DroiError error = user.confirmPhoneNumberPinCode(pinCode);
        if (error.isOk()) {
            Log.i(TAG, "validatePinCode:success");
            if (mListener != null) {
                //成功 回到ProfileActivity
                mListener.onFragmentInteraction(2);
            }
        } else {
            Log.i(TAG, "validatePinCode:failed:" + error.toString());
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class CountDownTimerUtils extends CountDownTimer {

        public CountDownTimerUtils(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timerTextView.setClickable(false); //设置不可点击
            timerTextView.setTextColor(getResources().getColor(R.color.text_color_gray));
            timerTextView.setText(millisUntilFinished / 1000 + "秒后可重新发送");
        }

        @Override
        public void onFinish() {
            timerTextView.setText("重新获取验证码");
            timerTextView.setClickable(true);//重新获得点击
            timerTextView.setTextColor(getResources().getColor(R.color.text_blue));
            timerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 需要写异步
                    CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(60000, 1000);
                    mCountDownTimerUtils.start();
                    DroiUser user = DroiUser.getCurrentUser();
                    DroiError error = user.validatePhoneNumber();
                    if (error.isOk()) {
                        Log.i(TAG, "sendPinCode:success");
                    } else {
                        Log.i(TAG, "sendPinCode:failed:" + error.toString());
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DroiAnalytics.onFragmentStart(getActivity(), "ConfirmPinFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "ConfirmPinFragment");
    }

}
