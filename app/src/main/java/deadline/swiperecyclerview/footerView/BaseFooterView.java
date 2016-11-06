package deadline.swiperecyclerview.footerView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @auther deadline
 * @time   2016/10/22
 */
public abstract class BaseFooterView extends FrameLayout implements FooterViewListener{

    public BaseFooterView(Context context) {
        super(context);
    }

    public BaseFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
