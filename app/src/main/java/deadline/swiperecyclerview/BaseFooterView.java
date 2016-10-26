package deadline.swiperecyclerview;

/**
 * @auther deadline
 * @time   2016/10/22
 */
public interface BaseFooterView {

    /**
     * 网络不好的时候想要展示的UI
     */
    void onNetUnAvailable();

    /**
     * 正常的loading的View
     */
    void onLoading();

    /**
     * 没有更多数据
     */
    void onNoMore();

    /**
     *  错误时展示的View
     */
    void onError();

    /**
     * 其他的任何提示
     */
    void onHint();
}
