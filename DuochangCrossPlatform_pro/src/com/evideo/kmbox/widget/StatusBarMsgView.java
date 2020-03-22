package com.evideo.kmbox.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evideo.kmbox.R;

public class StatusBarMsgView extends LinearLayout {
    
    private static final int ID_SONG = 1;
    private static final int ID_DATABASE = 2;
    
    private static final int ANIM_DURATION_ONE_STEP = 500;
    
    private int mCurrentId = -1;
    
    private TextView mTV1;
    private TextView mTV2;
    
    private ObjectAnimator mVisToInvis;
    private ObjectAnimator mInvisToVis;
    private AnimatorSet mAnimatorSet;
    
    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();
    
    private SparseArray<StatusBarMsg> mMsgContainer = new SparseArray<StatusBarMsg>();
    
    public StatusBarMsgView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public StatusBarMsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StatusBarMsgView(Context context) {
        super(context);
        initView(context);
    }
    
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.launcher_status_bar_msg_lay, this, true);
        
        mTV1 = (TextView) findViewById(R.id.status_bar_msg_tv1);
        mTV2 = (TextView) findViewById(R.id.status_bar_msg_tv2);
        mTV1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.status_bar_msg_text_size));
        mTV2.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.status_bar_msg_text_size));
//        mTV1.setTextSize(TypedValue.COMPLEX_UNIT_PX, R.dimen.status_bar_msg_text_size);
//        mTV2.setTextSize(TypedValue.COMPLEX_UNIT_PX, R.dimen.status_bar_msg_text_size);
        
        mVisToInvis = new ObjectAnimator();
        mVisToInvis.setFloatValues(0f, 90f);
        mVisToInvis.setPropertyName("rotationX");
        mVisToInvis.setDuration(ANIM_DURATION_ONE_STEP);
        mVisToInvis.setInterpolator(accelerator);
        
        mInvisToVis = new ObjectAnimator();
        mInvisToVis.setFloatValues(90f, 0f);
        mInvisToVis.setPropertyName("rotationX");
        mInvisToVis.setDuration(ANIM_DURATION_ONE_STEP);
        mInvisToVis.setInterpolator(decelerator);
        
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(mVisToInvis).before(mInvisToVis);
    }
    
    /**
     * @brief : [添加消息]
     * @param msg
     * @return 返回消息id
     */
    /*public int addStatusBarMsg(StatusBarMsg msg) {
        if(msg == null) {
            return -1;
        }
        mCurrentId = mCurrentId != ID_SONG ? ID_SONG : ID_DATABASE;
        mMsgContainer.put(mCurrentId, msg);
        flip(msg);
        return mCurrentId;
    }*/
    
    /**
     * @brief : [添加歌曲消息]
     * @param msg
     * @return
     */
    public int addSongMsg(StatusBarMsg msg) {
        if (msg == null) {
            return -1;
        }
        
        mMsgContainer.put(ID_SONG, msg);
        if (mCurrentId == ID_DATABASE) {
            return ID_SONG;
        } else {
            mCurrentId = ID_SONG;
            flip(msg);
        }
        
        return ID_SONG;
    }
    
    /**
     * @brief : [添加数据库更新消息]
     * @param msg
     * @return
     */
    public int addDatabaseMsg(StatusBarMsg msg) {
        if (msg == null) {
            return -1;
        }
        mMsgContainer.put(ID_DATABASE, msg);
        mCurrentId = ID_DATABASE;
        flip(msg);
        return ID_DATABASE;
    }
    
    /**
     * @brief : [根据消息id更新消息]
     * @param id
     * @param msg
     */
    public void updateStatusBarMsgById(int id, StatusBarMsg msg) {
        if(id < 0 || msg == null) {
            return;
        }
        int index = mMsgContainer.indexOfKey(id);
        if(index < 0) {
            return;
        }
        mMsgContainer.put(id, msg);
        if(mCurrentId == id) {
            updateVisibleTVByMsg(msg);
        }
    }
    
    /**
     * @brief : [根据消息id删除消息]
     * @param id
     * @param msg
     */
    public void removeStatusBarMsgById(int id) {
        int index = mMsgContainer.indexOfKey(id);
        if(index < 0) {
            return;
        }
        mMsgContainer.remove(id);
        int size = mMsgContainer.size();
        if(size <= 0) {
            mCurrentId = -1;
            setEmptyView();
        } else {
            mCurrentId = mMsgContainer.keyAt(0);
            StatusBarMsg msg = mMsgContainer.get(mCurrentId);
            flip(msg);
        }
    }
    
    private void setEmptyView() {
        mTV1.setText("");
        mTV1.setCompoundDrawablePadding(0);
        mTV1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mTV2.setText("");
        mTV2.setCompoundDrawablePadding(0);
        mTV2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
    
    private void flip(StatusBarMsg msg) {
        final TextView visibleTV;
        final TextView invisibleTV;
        
        if(mTV1.getVisibility() == View.GONE) {
            visibleTV = mTV2;
            invisibleTV = mTV1;
        } else {
            visibleTV = mTV1;
            invisibleTV = mTV2;
        }
        
        if(msg != null) {
            updateTVByMsg(invisibleTV, msg);
        }
        
        mVisToInvis.setTarget(visibleTV);
        mInvisToVis.setTarget(invisibleTV);
        
        mVisToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                visibleTV.setVisibility(View.GONE);
                invisibleTV.setVisibility(View.VISIBLE);
            }
        });
        
        mAnimatorSet.start();
    }
    
    private void updateVisibleTVByMsg(StatusBarMsg msg) {
        if(msg == null) {
            return;
        }
        if(mTV1.getVisibility() == View.GONE) {
            if (mAnimatorSet.isStarted()) {
                updateTVByMsg(mTV1, msg);
            } else {
                updateTVByMsg(mTV2, msg);
            }
        } else {
            if (mAnimatorSet.isStarted()) {
                updateTVByMsg(mTV2, msg);
            } else {
                updateTVByMsg(mTV1, msg);
            }
        }
    }
    
    private void updateTVByMsg(TextView tv, StatusBarMsg msg) {
        if(tv == null || msg == null) {
            return;
        }
        tv.setText(msg.content);
        if(msg.iconResId > 0) {
            tv.setCompoundDrawablePadding(10);
            tv.setCompoundDrawablesWithIntrinsicBounds(msg.iconResId, 0, 0, 0);
        } else {
            tv.setCompoundDrawablePadding(0);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }
    
}
