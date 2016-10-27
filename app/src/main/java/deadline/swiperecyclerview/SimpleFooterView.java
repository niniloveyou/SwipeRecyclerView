package deadline.swiperecyclerview;

import android.content.Context;
import android.util.AttributeSet;
/**
 * @author deadline
 */
public class SimpleFooterView extends BaseFooterView{


    public SimpleFooterView(Context context) {
        super(context);
    }

    public SimpleFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onNoMore() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onHint() {

    }

    @Override
    public void onNetUnAvailable() {

    }

}
