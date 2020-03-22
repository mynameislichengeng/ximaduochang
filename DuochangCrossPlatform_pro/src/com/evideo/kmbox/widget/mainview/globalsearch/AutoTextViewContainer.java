package com.evideo.kmbox.widget.mainview.globalsearch;

import java.util.List;

import com.evideo.kmbox.R;
import com.evideo.kmbox.util.DimensionsUtil;
import com.evideo.kmbox.util.EvLog;
import com.evideo.kmbox.widget.common.MaskFocusTextView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * [自动填充textview的linearlayout]
 */
public class AutoTextViewContainer extends LinearLayout implements View.OnKeyListener{

    private static final int ID_OFFSET = 10000;
    private Context mContext;
    private TableRow mTableRow1;
    private TableRow mTableRow2;
    private MaskFocusTextView mFocusTv;
    private int mFocusPadding = 0;
    private int mFontsSize = 0;
    private int mRowHeight = 0;
    private int mTotalNum = 0;

    public AutoTextViewContainer(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AutoTextViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoTextViewContainer(Context context) {
        super(context);
        init(context);
    }
    
    public interface IEdgeListener {
        public void onRightEdge();
        public void onUpEdge();
        public void onLeftEdge();
    }
    
    private IEdgeListener mEdgeListener = null;
    public void setEdgeListener(IEdgeListener listener) {
        mEdgeListener = listener;
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.adaptive_textview_container, this);
        mTableRow1 = (TableRow) this.findViewById(R.id.search_keyword_row1);
        mTableRow2 = (TableRow) this.findViewById(R.id.search_keyword_row2);
        mTableRow1.setNextFocusDownId(R.id.search_keyword_row2);
        mTableRow2.setNextFocusUpId(R.id.search_keyword_row1);
//        mTableRow1.setOnKeyListener(this);
//        mTableRow2.setOnKeyListener(this);
        /*mTableRow1.setOnKeyListener(new View.OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    EvLog.i("onUp edge");
                    if (mEdgeListener != null) {
                        mEdgeListener.onUpEdge();
                    }
                }
                return false;
            }
        });*/
        
        mFocusPadding = DimensionsUtil.getDimensionPixelSize(mContext, R.dimen.px23);
        mFontsSize = DimensionsUtil.getDimensionPixelSize(mContext, R.dimen.px30);
        mRowHeight = DimensionsUtil.getDimensionPixelSize(mContext, R.dimen.px54);
    }

    /**
     * [功能说明]
     * 
     * @param list String List
     */
    public void addTexts(List<String> list) {
        mTableRow1.removeAllViews();
        mTableRow2.removeAllViews();
        if (list == null || list.size() == 0) {
            EvLog.i("no data supplyed!!");
            return;
        }
        mTotalNum = list.size();
        int halfLength = (list.size() + 1) / 2;
        
        for (int i = 0; i < list.size(); i++) {
            final String key = list.get(i);
            MaskFocusTextView tv = new MaskFocusTextView(mContext);
            tv.setGravity(Gravity.CENTER);
            tv.setFocusable(true);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mFontsSize);
            tv.setId(ID_OFFSET + i);
            tv.setFocusPadding(mFocusPadding, mFocusPadding, mFocusPadding, mFocusPadding);
            tv.setBackground(mContext.getResources().getDrawable(
                    R.drawable.global_search_keyword_frame));
            tv.setTextColor(mContext.getResources().getColor(R.color.white));
            tv.setFocusFrame(R.drawable.focus_frame_new);
            tv.setHeight(mRowHeight);
            tv.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    if (mItemClickCallback != null) {
                        mItemClickCallback.onItemClick(key);
                        EvLog.i("wrq", "search keyword clicked! " + key);
                    }
                }
            });

            tv.setText(list.get(i));
            tv.setOnKeyListener(this);
            if (i < halfLength) {
                /*tv.setOnKeyListener(new View.OnKeyListener() {
                    
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                                && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                            EvLog.i("onUp edge");
                            if (mEdgeListener != null) {
                                mEdgeListener.onUpEdge();
                                return true;
                            }
                        }
                        
                        return false;
                    }
                });*/
                mTableRow1.addView(tv);
                addGapView(mTableRow1);
            } else {
                mTableRow2.addView(tv);
                addGapView(mTableRow2);
            }
            //default focus: first pos in second row
            if (i == 0) {
                mFocusTv = tv;
            }
        }

        //set focus manually
        for (int i = 0; i < halfLength; i++) {
            if (mTableRow1.findViewById(ID_OFFSET + i) != null) {
                mTableRow1.findViewById(ID_OFFSET + i).setNextFocusDownId(ID_OFFSET + halfLength + i);
            }
            if (mTableRow2.findViewById(ID_OFFSET + halfLength + i) != null) {
                mTableRow2.findViewById(ID_OFFSET + halfLength + i).setNextFocusUpId(ID_OFFSET + i);
            }
        }
        
        //handle rightEdge focus
//        mTableRow1.findViewById(ID_OFFSET + halfLength - 1).setOnKeyListener(this);
//        mTableRow2.findViewById(ID_OFFSET + list.size() - 1).setOnKeyListener(this);
//        mTableRow1.findViewById(ID_OFFSET + halfLength - 1).setNextFocusRightId(ID_OFFSET + halfLength - 1);
//        mTableRow2.findViewById(ID_OFFSET + list.size() - 1).setNextFocusRightId(ID_OFFSET + list.size() - 1);
    }
    
    /**
     * [间隙]
     * @param row 行
     */
    private void addGapView(TableRow row) {
        TextView view = new TextView(mContext);
        view.setWidth(1);
        row.addView(view);
    }
    
    /**
     * [请求焦点]
     */
    public void resetFocus() {
        if (mFocusTv != null) {
            mFocusTv.requestFocus();
        }
    }
    
    /**
     * [item点击回调]
     */
    public interface IOnItemClickCallback {
        /**
         * [点击动作]
         * @param keyword 关键字
         */
        public void onItemClick(String keyword);
    }
    
    private IOnItemClickCallback mItemClickCallback;
    
    /**
     * [设置回调]
     * @param callback 回调
     */
    public void setItemClickCallback(IOnItemClickCallback callback) {
        mItemClickCallback = callback;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int halfLength = (mTotalNum + 1) / 2;
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                && (event.getAction() == KeyEvent.ACTION_DOWN)) {
            if (v.getId() < halfLength) {
                EvLog.i("onUp edge");
                if (mEdgeListener != null) {
                    mEdgeListener.onUpEdge();
                    return true;
                }
            }
            return false;
        }
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                && (event.getAction() == KeyEvent.ACTION_DOWN)) {
            if ( (v.getId() == (ID_OFFSET + halfLength - 1)) ||
                 (v.getId() == (ID_OFFSET + mTotalNum - 1 ))) {
                EvLog.i("on right edge>>>>>>>>>>");
                if (mEdgeListener != null) {
                    mEdgeListener.onRightEdge();
                    return true;
                }
            }
            return false;
        }
        
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                && (event.getAction() == KeyEvent.ACTION_DOWN)) {
            if ((v.getId() == ID_OFFSET) || (v.getId() == (ID_OFFSET + halfLength ))) {
                EvLog.i("on left edge>>>>>>>>>>");
                if (mEdgeListener != null) {
                    mEdgeListener.onLeftEdge();
                    return true;
                }
            }
        }
        return false;
    }
}