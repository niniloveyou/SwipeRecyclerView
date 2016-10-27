package deadline.swiperecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @auther deadline
 * @time   2016/10/22
 */
public abstract class BaseFooterView extends FrameLayout{

    public BaseFooterView(Context context) {
        super(context);
    }

    public BaseFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 网络不好的时候想要展示的UI
     */
    public abstract void onNetUnAvailable();

    /**
     * 正常的loading的View
     */
    public abstract void onLoading();

    /**
     * 没有更多数据
     */
    public abstract void onNoMore();

    /**
     *  错误时展示的View
     */
    public abstract void onError();

    /**
     * 其他的任何提示
     */
    public abstract void onHint();
}
