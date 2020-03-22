package com.evideo.kmbox.widget.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evideo.kmbox.R;

public class ImageQrDialog extends Dialog {
    
    private TextView mContentTv;
    private ImageView mQRImageView = null;
    private ImageView mLogoImageView = null;
    private Context mContext = null;
    private Bitmap mDefaultBmp = null;
    private Bitmap mBigBmp;
    private int mQrWidth = 360;
    private int mQrHeight = 360;
    
    public ImageQrDialog(Context context) {
        super(context, R.style.QrDialogStyle);
        setContentView(R.layout.dialog_qr_code);
        mContext = context;
        mQrHeight = (int)context.getResources().getDimension(R.dimen.dialog_qr_bmp_height);
        mQrWidth = (int)context.getResources().getDimension(R.dimen.dialog_qr_bmp_width);
        init();
    }
    
    private void init() {
        setCancelable(true);
        mContentTv = (TextView) findViewById(R.id.loading_content_tv);
        
        mQRImageView = (ImageView) findViewById(R.id.qr);
        mLogoImageView = (ImageView) findViewById(R.id.qr_logo);
        
    }
    
    public void setContent(String content) {
        if(!TextUtils.isEmpty(content)) {
            mContentTv.setText(content);
        }
    }
    
    public void setContent(int resId) {
        if(resId > 0) {
            mContentTv.setText(resId);
        }
    }
    
    public void updateQR(Bitmap bmp){
        if (bmp != null) {
            mBigBmp = big(bmp, mQrWidth,mQrHeight);
            mQRImageView.setImageBitmap(mBigBmp);
            mLogoImageView.setVisibility(View.VISIBLE);
            bmp.recycle();
        }else {
            if ( mDefaultBmp == null ) {
                Bitmap defaultBmp =  BitmapFactory.decodeResource(mContext.getResources()
                        , R.drawable.main_menu_order_song_quick_mark);
                mDefaultBmp = big(defaultBmp, mQrWidth,mQrHeight);
                //defaultBmp.recycle();//这里回收会造成系统挂掉
            }
            mQRImageView.setImageBitmap(mDefaultBmp);
            mLogoImageView.setVisibility(View.INVISIBLE);
        }         
    }
    
    /**
     * [功能说明]回收bitmap
     */
    public void releaseBitmap() {
        if (mBigBmp != null && !mBigBmp.isRecycled()) {
            mBigBmp.recycle();
            mBigBmp = null;
        }
        if (mDefaultBmp != null && !mDefaultBmp.isRecycled()) {
            mDefaultBmp.recycle();
            mDefaultBmp = null;
        }
    }
    
    private  Bitmap big(Bitmap b, float x, float y) {
        int w = b.getWidth();
        int h = b.getHeight();
        float sx = (float) x / w;// 要强制转换，不转换我的在这总是死掉。
        float sy = (float) y / h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        return resizeBmp;
    }
    
}
