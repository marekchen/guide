package com.droi.guide.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.droi.guide.interfaces.BackHandlerInterface;

/**
 * Created by chenpei on 16/9/14.
 */
public abstract class BackHandledFragment extends Fragment {

    protected BackHandlerInterface mBackHandlerInterface;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BackHandlerInterface) {
            mBackHandlerInterface = (BackHandlerInterface) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        mBackHandlerInterface.setSelectedFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBackHandlerInterface = null;
    }

}
