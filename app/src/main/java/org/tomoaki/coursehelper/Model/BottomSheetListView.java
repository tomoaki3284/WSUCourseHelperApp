package org.tomoaki.coursehelper.Model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * This class is for ListView inside of bottom sheet
 * Problem that I encounter was that ListView cannot be scroll, when it is inside of bottom sheet
 * Instead, it will collapsed bottom sheet, rather than scrolling up/down
 *
 * Solution is to extend ListView class, then override onInterceptTouchEvent.
 * Detail Explanation is here:
 *      StackOverFLow: https://stackoverflow.com/questions/40570985/listview-in-bottomsheet
 *      Documentation: https://developer.android.com/training/gestures/viewgroup#java
 */
public class BottomSheetListView extends ListView {
    public BottomSheetListView (Context context, AttributeSet p_attrs) {
        super (context, p_attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (canScrollVertically(this)) {
            //parent is bottom sheet
            //making this touch event disallow would make child ListView overtake touch event
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }

    public boolean canScrollVertically (AbsListView view) {
        boolean canScroll = false;

        if (view != null && view.getChildCount() > 0) {
            boolean isOnTop = view.getFirstVisiblePosition() != 0 || view.getChildAt(0).getTop() != 0;
            boolean isAllItemsVisible = isOnTop && view.getLastVisiblePosition() == view.getChildCount();

            if (isOnTop || isAllItemsVisible) {
                canScroll = true;
            }
        }

        return  canScroll;
    }
}
