package com.croshe.android.base.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.croshe.android.base.activity.CrosheBaseActivity;
import com.croshe.android.base.extend.listener.CrosheAnimationListener;
import com.croshe.android.base.fragment.CrosheBaseFragment;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.ViewUtils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * 评论控件面板的基类，此类未做任何处理，需继承实现
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/12/19 11:02.
 */
public abstract class CrosheBaseCommentView extends FrameLayout implements View.OnClickListener {

    /**
     * 评论的类型
     */
    public static String RESULT_COMMENT_TYPE = "RESULT_COMMENT_TYPE";

    /**
     * 评论的文件
     */
    public static String RESULT_DATA_FILE = "RESULT_DATA_FILE";


    /**
     * 评论的扩展数据
     */
    public static String RESULT_COMMENT_DATA = "RESULT_COMMENT_DATA";

    /**
     * 评论内容
     */
    public static String RESULT_COMMENT_CONTENT = "RESULT_COMMENT_CONTENT";

    protected Handler handler = new Handler();
    protected View maskView,editView;
    protected boolean isAnimation;
    protected Bundle extraData;//自定义的扩展数据
    protected String action;
    protected OnCommentListener onCommentListener;


    public CrosheBaseCommentView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheBaseCommentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheBaseCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }



    private void initView() {
        maskView = new View(getContext());
        maskView.setBackgroundColor(Color.parseColor("#66000000"));
        FrameLayout.LayoutParams maskLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        maskView.setLayoutParams(maskLayout);
        maskView.setOnClickListener(this);
        this.addView(maskView);


        editView = LayoutInflater.from(getContext()).inflate(getItemViewLayout(), null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        editView.setLayoutParams(layoutParams);
        initData();


        this.addView(editView);
    }

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 获得评论控件的布局
     * @return
     */
    public abstract int getItemViewLayout();

    /**
     * 获得编辑控件的Id
     * @return
     */
    public abstract EditText getEditView();


    /**
     * 显示
     */
    public final void show(){

        if (isAnimation) return;

        isAnimation = true;

        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
            viewGroup.addView(this);
            this.getLayoutParams().height = (int) DensityUtils.getHeightInPx();
        }

        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        animation.setAnimationListener(new CrosheAnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                super.onAnimationEnd(arg0);
                isAnimation = false;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getEditView().requestFocus();
                        ViewUtils.openKeyboard( getEditView());
                    }
                });
                if (extraData != null && extraData.containsKey("hint") && getEditView() != null) {
                    getEditView().setHint(extraData.getString("hint"));
                }
            }
        });
        animation.setDuration(150);

        editView.startAnimation(animation);
    }



    /**
     * 关闭
     */
    public final void close(){
        if (isAnimation) return;

        isAnimation = true;
        ViewUtils.closeKeyboard(this);
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        animation.setDuration(150);
        animation.setAnimationListener(new CrosheAnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                super.onAnimationEnd(arg0);
                isAnimation = false;

                if (getContext() instanceof Activity) {
                    Activity activity = (Activity) getContext();
                    ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                    viewGroup.removeView(CrosheBaseCommentView.this);
                }
            }
        });
        editView.startAnimation(animation);
    }



    /**
     * 设置返回数据
     * @param data
     */
    public void setResultData(Intent data) {
        if (data == null) {
            throw new RuntimeException("评论返回数据不可为空！");
        }
        if (extraData != null && !extraData.isEmpty()) {
            data.putExtras(extraData);
        }
        data.putExtra(CrosheBaseActivity.EXTRA_DO_ACTION, CrosheBaseFragment.class.getSimpleName());
        if (StringUtils.isNotEmpty(action)) {
            data.putExtra(CrosheBaseActivity.EXTRA_DO_ACTION, action);
        }
        EventBus.getDefault().post(data);

        if (getOnCommentListener() != null) {
            getOnCommentListener().onComment(data.getStringExtra(RESULT_COMMENT_CONTENT));
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Bundle getExtraData() {
        return extraData;
    }

    public void setExtraData(Bundle extraData) {
        this.extraData = extraData;
    }

    @Override
    public void onClick(View v) {
        if (v == maskView) {
            close();
        }
    }

    public OnCommentListener getOnCommentListener() {
        return onCommentListener;
    }

    public void setOnCommentListener(OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
    }

    public interface  OnCommentListener{
        void onComment(String content);
    }
}
