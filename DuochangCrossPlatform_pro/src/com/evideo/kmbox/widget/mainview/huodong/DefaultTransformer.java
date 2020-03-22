package com.evideo.kmbox.widget.mainview.huodong;


import android.view.View;

public class DefaultTransformer extends BaseTransformer {
//    private static final float MIN_SCALE = 0.75f;  
    
    
//    假设现在ViewPager在A页现在滑出B页，则:
//        A页的position变化就是( 0, -1]
//        B页的position变化就是[ 1 , 0 ]
                
                
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onTransform(View view, float position) {
        // TODO Auto-generated method stub
/*        EvLog.d("position:" + position);
        view.setAlpha(1);
        view.setTranslationX(0);
        view.setTranslationY(0);
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight() / 2);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setRotation(0);*/
        
        int pageWidth = view.getWidth();  
        
        if (position < -1) { // [-Infinity,-1)  
            // This page is way off-screen to the left.  
            view.setAlpha(0);  
  
        } else if (position <= 0) { // [-1,0]  
            // Use the default slide transition when moving to the left page  
            view.setAlpha(1);  
            view.setTranslationX(0);  
            view.setScaleX(1);  
            view.setScaleY(1);  
  
        } else if (position <= 1) { // (0,1]  
            // Fade the page out.  
            view.setAlpha(1 - position);  
  
            // Counteract the default slide transition  
            view.setTranslationX(pageWidth * -position);  
  
            // Scale the page down (between MIN_SCALE and 1)  
          /*  float scaleFactor = MIN_SCALE  
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));  
            view.setScaleX(scaleFactor);  
            view.setScaleY(scaleFactor);  */
            view.setScaleX(1);  
            view.setScaleY(1);  
        } else { // (1,+Infinity]  
            // This page is way off-screen to the right.  
            view.setAlpha(0);  
        }  
    }  
}
