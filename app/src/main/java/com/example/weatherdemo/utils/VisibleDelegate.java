package com.example.weatherdemo.utils;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public final class VisibleDelegate {

    private boolean mIsFirstVisible = true;

    private boolean isViewCreated = false;

    private boolean currentVisibleState = false;

    private ISupportFragment mISupportFragment;


    public VisibleDelegate(ISupportFragment fragment) {
        this.mISupportFragment = fragment;
    }


    public void setUserVisibleHint(boolean isVisibleToUser) {
        // 对于默认 tab 和 间隔 checked tab 需要等到 isViewCreated = true 后才可以通过此通知用户可见
        // 这种情况下第一次可见不是在这里通知 因为 isViewCreated = false 成立,等从别的界面回到这里后会使用 onSupportVisible 通知可见
        // 对于非默认 tab mIsFirstVisible = true 会一直保持到选择则这个 tab 的时候，因为在 onActivityCreated 会返回 false
        if (isViewCreated) {
            if (isVisibleToUser && !currentVisibleState) {
                dispatchUserVisibleHint(true);
            } else if (!isVisibleToUser && currentVisibleState) {
                dispatchUserVisibleHint(false);
            }
        }
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mISupportFragment == null) {
            return;
        }

        if (!(mISupportFragment instanceof Fragment)) {
            return;
        }

        isViewCreated = true;
        // !isHidden() 默认为 true  在调用 hide show 的时候可以使用
        if (!((Fragment) mISupportFragment).isHidden() && ((Fragment) mISupportFragment).getUserVisibleHint()) {
            // 这里的限制只能限制 A - > B 两层嵌套
            dispatchUserVisibleHint(true);
        }
    }


    public void onHiddenChanged(boolean hidden) {
        Log.e("BaseFragment",getClass().getSimpleName() + "  onHiddenChanged dispatchChildVisibleState  hidden " + hidden);
        if (hidden) {
            dispatchUserVisibleHint(false);
        } else {
            dispatchUserVisibleHint(true);
        }
    }


    public void onResume() {
        if (mISupportFragment == null) {
            return;
        }

        if (!(mISupportFragment instanceof Fragment)) {
            return;
        }

        if (!mIsFirstVisible) {
            if (!((Fragment) mISupportFragment).isHidden() && !currentVisibleState && ((Fragment) mISupportFragment).getUserVisibleHint()) {
                dispatchUserVisibleHint(true);
            }
        }
    }


    public void onPause() {
        if (mISupportFragment == null) {
            return;
        }

        if (!(mISupportFragment instanceof Fragment)) {
            return;
        }

        // 当前 Fragment 包含子 Fragment 的时候 dispatchUserVisibleHint 内部本身就会通知子 Fragment 不可见
        // 子 fragment 走到这里的时候自身又会调用一遍 ？
        if (currentVisibleState && ((Fragment) mISupportFragment).getUserVisibleHint()) {
            dispatchUserVisibleHint(false);
        }
    }


    /**
     * 统一处理 显示隐藏
     *
     * @param visible
     */
    public void dispatchUserVisibleHint(boolean visible) {
        if (mISupportFragment == null) {
            return;
        }

        if (!(mISupportFragment instanceof Fragment)) {
            return;
        }

        // 如果app出现异常，比如崩溃，在重启承载多层fragment的activity时
        // 父类fragment会首先执行onCreateView->onViewCreated->onActivityCreated
        // 当执行到onActivityCreated时，会分化visible事件给子fragment
        // 此刻子fragment可能还没执行onCreateView->onViewCreated，如果不再此处拦截雕visible事件
        // 子fragment就可能会执行onLazyInited，这是错误的
        if (!isViewCreated) {
            return;
        }

        //当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment getUserVisibleHint = true
        //但当父 fragment 不可见所以 currentVisibleState = false 直接 return 掉
//        Log.e("BaseFragment", getClass().getSimpleName() + "  dispatchUserVisibleHint isParentInvisible() " + isParentInvisible());

        // 这里限制则可以限制多层嵌套的时候子 Fragment 的分发
        if (visible && isParentInvisible())
            return;

        //此处是对子 Fragment 不可见的限制，因为 子 Fragment 先于父 Fragment回调本方法 currentVisibleState 置位 false
        // 当父 dispatchChildVisibleState 的时候第二次回调本方法 visible = false 所以此处 visible 将直接返回
        if (currentVisibleState == visible) {
            return;
        }

        currentVisibleState = visible;

        if (visible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                mISupportFragment.onLazyInited();
            }
            mISupportFragment.onSupportVisible();
            dispatchChildVisibleState(true);
        } else {
            dispatchChildVisibleState(false);
            mISupportFragment.onSupportInVisible();
        }
    }


    /**
     * 用于分发可见时间的时候父获取 fragment 是否隐藏
     *
     * @return true fragment 不可见， false 父 fragment 可见
     */
    private boolean isParentInvisible() {
        if (mISupportFragment == null) {
            return false;
        }

        if (!(mISupportFragment instanceof Fragment)) {
            return false;
        }

        Fragment fragment = ((Fragment) mISupportFragment).getParentFragment();
        if (fragment != null) {
            if (fragment instanceof ISupportFragment) {
                return !((ISupportFragment)fragment).isSupportVisible();
            }
        }
        return false;
    }


    public boolean isSupportVisible() {
        return currentVisibleState;
    }


    /**
     * 当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment 的唯一或者嵌套 VP 的第一 fragment 时 getUserVisibleHint = true
     * 但是由于父 Fragment 还进入可见状态所以自身也是不可见的， 这个方法可以存在是因为庆幸的是 父 fragment 的生命周期回调总是先于子 Fragment
     * 所以在父 fragment 设置完成当前不可见状态后，需要通知子 Fragment 我不可见，你也不可见，
     *
     * 因为 dispatchUserVisibleHint 中判断了 isParentInvisible 所以当 子 fragment 走到了 onActivityCreated 的时候直接 return 掉了
     *
     * 当真正的外部 Fragment 可见的时候，走 setVisibleHint (VP 中)或者 onActivityCreated (hide show) 的时候
     * 从对应的生命周期入口调用 dispatchChildVisibleState 通知子 Fragment 可见状态
     *
     * @param visible
     */
    private void dispatchChildVisibleState(boolean visible) {
        if (mISupportFragment == null) {
            return;
        }

        if (!(mISupportFragment instanceof Fragment)) {
            return;
        }

        FragmentManager childFragmentManager = ((Fragment) mISupportFragment).getChildFragmentManager();
        List<Fragment> fragments = childFragmentManager.getFragments();
        if (!fragments.isEmpty()) {
            for (Fragment child : fragments) {
                if (child instanceof ISupportFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    ((ISupportFragment) child).dispatchUserVisibleHint(visible);
                }
            }
        }
    }


    public void onDestroyView() {
        isViewCreated = false;
        mIsFirstVisible = true;
    }
}
