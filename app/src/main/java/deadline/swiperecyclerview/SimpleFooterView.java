package deadline.swiperecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author deadline
 */
public class SimpleFooterView extends BaseFooterView{

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    private TextView mText;
    private CircleImageView mCircleView;
    private MaterialProgressDrawable mProgress;

    public SimpleFooterView(Context context) {
        this(context, null);
    }

    public SimpleFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 180));
        //View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_footer_view, this);
       // mCircleView = (CircleImageView) view.findViewById(R.id.footer_view_circle);
        /*ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.footer_view_progressbar);
        mText = (TextView) view.findViewById(R.id.footer_view_tv);*/
        createProgressView();
    }


    private void createProgressView() {
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT);
        mProgress = new MaterialProgressDrawable(getContext(), mCircleView);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mProgress.setColorSchemeColors(Color.BLUE);
        mProgress.updateSizes(MaterialProgressDrawable.DEFAULT);
        mProgress.setAlpha(255);
        mProgress.setArrowScale(0f);
        mProgress.setProgressRotation(1000);
        mProgress.showArrow(true);
        mProgress.start();
        mCircleView.setImageDrawable(mProgress);
        LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(mCircleView, params);
    }

    @Override
    public void onLoadingMore() {
        mText.setText("加载中...");
    }

    @Override
    public void onNoMore(CharSequence message) {
        mText.setText("-- the end --");
    }

    @Override
    public void onError(CharSequence message) {
        mText.setText("-- 啊哦，好像哪里不对劲！ --");
    }

    @Override
    public void stopLoadingMore(boolean animationEnable) {

    }

    @Override
    public void onNetChange(boolean isAvailable) {
        mText.setText("-- 网络连接不通畅！ --");
    }

}
