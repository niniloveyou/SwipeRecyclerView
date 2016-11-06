package deadline.swiperecyclerview.footerView;

/**
 * @author deadline
 * @time 2016/10/22
 */
public interface FooterViewListener {

    /**
     * 网络不好的时候想要展示的UI
     */
    void onNetChange(boolean isAvailable);

    /**
     * 正常的loading的View
     */
    void onLoadingMore();

    /**
     * 没有更多数据
     */
    void onNoMore(CharSequence message);

    /**
     *  错误时展示的View
     */
    void onError(CharSequence message);
}
