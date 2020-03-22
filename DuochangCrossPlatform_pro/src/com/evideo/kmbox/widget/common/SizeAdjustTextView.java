package com.evideo.kmbox.widget.common;

import com.evideo.kmbox.R;

import android.content.Context;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * @brief : [根据控件宽高自动调整字体大小的TextView]
 */
public class SizeAdjustTextView extends TextView {
    
    private boolean mNeedsResize = false;
    
    private float mTextSize;
    
    private float mPreferSize;
    
    private float mMaxTextSize;
    
    private float mMinTextSize = 18f;
    
    private float mSpacingMult = 1.0f;

    private float mSpacingAdd = 0.0f;

    public SizeAdjustTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTextSize = mMaxTextSize = mPreferSize = getTextSize();
        
        mMinTextSize = getResources().getDimension(R.dimen.song_list_item_text_song_name_min);
        
    }

    public SizeAdjustTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SizeAdjustTextView(Context context) {
        this(context, null);
    }
    
    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
//        Log.v("something", "onTextChanged changed ");
        mNeedsResize = true;
        resetTextSize();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
//        resizeText();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        Log.v("something", "onSizeChanged changed ");
        if (w != oldw || h != oldh) {
            mNeedsResize = true;
        }
    }


    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mTextSize = getTextSize();
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mTextSize = getTextSize();
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }

    public void setMaxTextSize(float maxTextSize) {
        mMaxTextSize = maxTextSize;
        requestLayout();
        invalidate();
    }

    public float getMaxTextSize() {
        return mMaxTextSize;
    }

    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
        requestLayout();
        invalidate();
    }
    
    public void reset() {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mPreferSize);
    }

    public float getMinTextSize() {
        return mMinTextSize;
    }

    private void resetTextSize() {
        if(mTextSize > 0) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            mMaxTextSize = mTextSize;
        }
//        if(mMaxTextSize > 0) {
////            Log.v("something", "mMaxTextSize " + mMaxTextSize);
//            mTextSize = mMaxTextSize;
//            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaxTextSize);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        Log.v("something", "onLayout changed " + changed + 
//                " top " + top + " bottom " + bottom + 
//                " PaddingBottom " + getCompoundPaddingBottom() + " PaddingTop " + getCompoundPaddingTop());
        if (changed || mNeedsResize) {
            int widthLimit = (right - left) - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int heightLimit = (bottom - top) - getCompoundPaddingBottom() - getCompoundPaddingTop();
//            Log.d("something", "onlayout resizeText width " + widthLimit + " height " + heightLimit);
            resizeText(widthLimit, heightLimit);
        }
        super.onLayout(changed, left, top, right, bottom);
    }


    /*private void resizeText() {
        int heightLimit = getHeight() - getPaddingBottom() - getPaddingTop();
        int widthLimit = getWidth() - getPaddingLeft() - getPaddingRight();
//        Log.d("something", "resizeText width " + widthLimit + " height " + heightLimit);
        resizeText(widthLimit, heightLimit);
    }*/

    private void resizeText(int width, int height) {
        CharSequence text = getText();
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize == 0) {
            return;
        }
        float newTextSize = findNewTextSize(width, height, text);
//        Log.v("something", "newTextSize " + newTextSize);
        changeTextSize(newTextSize);
        mNeedsResize = false;
    }

    private void changeTextSize(float newTextSize) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);
    }

    private float findNewTextSize(int width, int height, CharSequence text) {
//        Log.v("something", "findNewTextSize height " + height + " width " + width);
        TextPaint textPaint = new TextPaint(getPaint());

        float targetTextSize = textPaint.getTextSize();
//        float targetTextSize = mMaxTextSize;

        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);
//        Log.v("something", "findNewTextSize textHeight " + textHeight);
        
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 1, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }
        return targetTextSize;
    }

    private int getTextHeight(CharSequence source, TextPaint paint, int width, float textSize) {
        paint.setTextSize(textSize);
        StaticLayout layout = new StaticLayout(source, paint, width, 
                Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
        return layout.getHeight();
    }
    
    @Override
    protected void dispatchSetSelected(boolean selected) {
        super.dispatchSetSelected(selected);
        
        if (mOnSelectedListener != null) {
            mOnSelectedListener.onSelected(this, selected);
        }
        
    }
    
    private OnSelectedListener mOnSelectedListener;
    
    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
    }
    
    public interface OnSelectedListener {
        public void onSelected(SizeAdjustTextView view, boolean selected);
    }

}
