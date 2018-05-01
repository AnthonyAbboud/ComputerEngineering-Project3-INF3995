package com.example.mounia.tp3;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


// Ce code est inspiré de code : https://stackoverflow.com/questions/13477820/android-vertical-viewpager

// classe verticalViewPager permet de changer les fragements verticalement
public class VerticalViewPager extends ViewPager {

    // constructeur
    public VerticalViewPager(Context context)
    {
        super(context, null);
    }

    //construteur avec parametre
    public VerticalViewPager(Context context, AttributeSet attribut)
    {
        super(context, attribut);
        init();

    }
    void init() {
        setPageTransformer(true,new VerticalPageTransformer());
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public boolean canScrollHorizontally (int direction)
    {
        return false;
    }

    public boolean canScrollVertically (int direction)
    {
        return super.canScrollHorizontally(direction);
    }

    // cette methode change les coordonnées de x par les coordonnées de y
    private MotionEvent flipXY(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float newX = (event.getY() / height) * width;
        float newY = (event.getX() / width) * height;

        event.setLocation(newX, newY);
        return event;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean toHandle = super.onTouchEvent(flipXY(event));
        flipXY(event);
        return toHandle;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final boolean intercepted = super.onInterceptHoverEvent(flipXY(event));
        flipXY(event);
        return intercepted;
    }

    private static final class VerticalPageTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View view, float position) {
            final int pageWidth = view.getWidth();
            final int pageHeight = view.getHeight();
            if (position < -1) {
                view.setAlpha(0);
            }
            else if (position <= 1) {
                view.setAlpha(1);
                view.setTranslationX(pageWidth * -position);
                float yPosition = position * pageHeight;
                view.setTranslationY(yPosition);
            } else {
                view.setAlpha(0);
            }
        }
    }


}



