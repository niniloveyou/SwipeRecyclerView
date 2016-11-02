package deadline.swiperecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @auther deadline
 * @time   2016/10/22
 * 仿知乎的刷新效果
 */
public class SwipeRecyclerView extends FrameLayout
                implements SwipeRefreshLayout.OnRefreshListener{

    private View mEmptyView;
    private BaseFooterView mFootView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    private LayoutManager mLayoutManager;
    private OnLoadListener mListener;
    private DataObserver mDataObserver;
    private WrapperAdapter mWrapperAdapter;

    private boolean isEmptyViewShowing;
    private boolean isLoadingMore;
    private boolean isLoadMoreEnable;
    private boolean isRefreshEnable;
    private boolean isAutoLoadMoreEnable;

    private int lastVisiablePosition = 0;

    public SwipeRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupSwipeRecyclerView();
    }

    private void setupSwipeRecyclerView() {

        isEmptyViewShowing = false;
        isRefreshEnable = true;
        isLoadingMore = false;
        isLoadMoreEnable = true;
        isAutoLoadMoreEnable = true;

        mFootView = new SimpleFooterView(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_swipe_recyclerview, this);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        mLayoutManager = recyclerView.getLayoutManager();

        mRefreshLayout.setOnRefreshListener(this);
        recyclerView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!isLoadMoreEnable || isRefreshing() || isLoadingMore){
                    return;
                }

                int childCount = mWrapperAdapter == null ? 0 : mWrapperAdapter.getItemCount();
                if(!isAutoLoadMoreEnable && lastVisiablePosition == childCount){

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // do nothing if load more is not enable or refreshing or loading more
                if(!isLoadMoreEnable || isRefreshing() || isLoadingMore){
                    return;
                }

                //get the lastVisiablePosition
                mLayoutManager = recyclerView.getLayoutManager();
                if(mLayoutManager instanceof LinearLayoutManager){
                    lastVisiablePosition = ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
                }else if(mLayoutManager instanceof GridLayoutManager){
                    lastVisiablePosition = ((GridLayoutManager)mLayoutManager).findLastCompletelyVisibleItemPosition();
                }else if(mLayoutManager instanceof StaggeredGridLayoutManager){

                }

                // do nothing if you set do not auto load more
                // when scroll to the bottom of recyclerView
                if(!isAutoLoadMoreEnable){
                    return;
                }
                int childCount = mWrapperAdapter == null ? 0 : mWrapperAdapter.getItemCount();
                if(lastVisiablePosition == childCount - 1){

                    if(mListener != null){
                        isLoadingMore = true;
                        mListener.onLoadMore();
                    }
                }
            }
        });
    }

    /**
     * set is enable pull to refresh
     * @param refreshEnable
     */
    public void setRefreshEnable(boolean refreshEnable){
        isRefreshEnable = refreshEnable;
        mRefreshLayout.setEnabled(isRefreshEnable);
    }

    /**
     * set is auto load more when recyclerView scroll to bottom
     * @param autoLoadMoreEnable
     */
    public void setAutoLoadMoreEnable(boolean autoLoadMoreEnable) {
        isAutoLoadMoreEnable = autoLoadMoreEnable;
    }

    /**
     * set is loading more enable
     * @param loadMoreEnable
     *              if true when recyclerView scroll to bottom load more action will be trigger
     */
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        isLoadMoreEnable = loadMoreEnable;
    }

    /**
     * get is refreshing
     * @return
     */
    public boolean isRefreshing(){
        return mRefreshLayout.isRefreshing();
    }

    /**
     * get is loading more
     * @return
     */
    public boolean isLoadingMore(){
        return isLoadingMore;
    }

    /**
     * is empty view showing
     * @return
     */
    public boolean isEmptyViewShowing(){
        return isEmptyViewShowing;
    }

    /**
     * you may need set some other attributes of swipeRefreshLayout
     * @return
     *     swipeRefreshLayout
     */
    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mRefreshLayout;
    }

    /**
     * you may need set some other attributes of RecyclerView
     * @return
     *     RecyclerView
     */
    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    /**
     * set load more listener
     * @param listener
     */
    public void setOnLoadListener(OnLoadListener listener){
        mListener = listener;
    }

    /**
     * set the footer view
     * @param footerView
     *        the view to be showing when pull up
     */
    public void setFooterView(BaseFooterView footerView){
        if(footerView != null) {
            this.mFootView = footerView;
        }
    }

    /**
     * set a empty view like listview
     * @param emptyView
     *        the view to be showing when the data set size is zero
     */
    public void setEmptyView(View emptyView){
        this.mEmptyView = emptyView;
        if(isEmptyViewShowing){
            requestLayout();
            invalidate();
        }

        if(mDataObserver != null) {
            mDataObserver.onChanged();
        }
    }

    /**
     * set adapter to recyclerView
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter){
        if(adapter != null) {
            if(mDataObserver == null){
                mDataObserver = new DataObserver();
            }
            mWrapperAdapter = new WrapperAdapter(adapter);
            recyclerView.setAdapter(mWrapperAdapter);
            adapter.registerAdapterDataObserver(mDataObserver);
            mDataObserver.onChanged();
        }
    }

    /**
     * refresh or load more completed
     */
    public void complete(){
        mRefreshLayout.setRefreshing(false);
        stopLoadingMore(true);
    }

    /**
     * set refreshing
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing){
        mRefreshLayout.setRefreshing(refreshing);
    }

    /**
     * stop loading more, so the footerView will be removed
     */
    public void stopLoadingMore(boolean animationEnable){
        isLoadingMore = false;
        if(mWrapperAdapter != null) {
            mWrapperAdapter.notifyItemRemoved(mWrapperAdapter.getItemCount());
        }

    }

    /**
     * stop loading more without animation
     * {@link #stopLoadingMore(boolean animationEnable)}
     */
    public void stopLoadingMore(){
        stopLoadingMore(false);
    }
    
    /**
     * call method refresh
     */
    @Override
    public void onRefresh() {
        if(mListener != null){
            mListener.onRefresh();
        }
    }

    /**
     * {@link FooterViewListener#onNetChange(boolean isAvailable)}
     */
    public void onNetChange(boolean isAvailable) {
        if(mFootView != null){
            mFootView.onNetChange(isAvailable);
        }
    }

    /**
     * {@link FooterViewListener#onLoadingMore()}
     */
    public void onLoadingMore() {
        if(mFootView != null){
            mFootView.onLoadingMore();
        }
    }

    /**
     * {@link FooterViewListener#onNoMore(CharSequence message)}
     */
    public void onNoMore(CharSequence message) {
        if(mFootView != null){
            mFootView.onNoMore(message);
        }
    }

    /**
     * {@link FooterViewListener#onError(CharSequence message)}
     */
    public void onError(CharSequence message) {
        if(mFootView != null){
            mFootView.onError(message);
        }
    }


    private class WrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public static final int TYPE_FOOTER = 0x100;

        RecyclerView.Adapter<RecyclerView.ViewHolder> mInnerAdapter;

        public WrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter){
            this.mInnerAdapter = adapter;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(TYPE_FOOTER == viewType){
                return new FooterViewHolder(mFootView);
            }
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(isLoadMoreEnable && position == getItemCount() - 1){
                return;
            }
            mInnerAdapter.onBindViewHolder(holder, position);
        }


        @Override
        public int getItemViewType(int position) {
            if(isLoadMoreEnable && position == getItemCount() - 1){
                return TYPE_FOOTER;
            }else{
                return mInnerAdapter.getItemViewType(position);
            }
        }

        @Override
        public int getItemCount() {
            int count = mInnerAdapter == null ? 0 : mInnerAdapter.getItemCount();
            return isLoadMoreEnable ? count + 1 : count;
        }

        @Override
        public long getItemId(int position) {
           return mInnerAdapter.getItemId(position);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            mInnerAdapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            mInnerAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            mInnerAdapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mInnerAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return mInnerAdapter.onFailedToRecycleView(holder);
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            mInnerAdapter.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            mInnerAdapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            mInnerAdapter.onViewRecycled(holder);
        }
    }

    /**
     * ViewHolder of footerView
     */
    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * a inner class used to monitor the dataSet change
     * <p>
     * because wrapperAdapter do not know when wrapperAdapter.mInnerAdapter
     * <p>
     * dataSet changed, these method are final
     */
    class DataObserver extends RecyclerView.AdapterDataObserver{

        @Override
        public void onChanged() {
            super.onChanged();
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if(adapter != null && mEmptyView != null){

                int count = 0;
                if(isLoadMoreEnable){
                    count ++;
                }
                if(adapter.getItemCount() == count){
                    isEmptyViewShowing = true;
                    if(mEmptyView.getParent() == null){
                        FrameLayout.LayoutParams params = new LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params.gravity = Gravity.CENTER;

                        //// TODO: 2016/10/29 有可能多个emptyView重叠
                        addView(mEmptyView, params);
                    }

                    recyclerView.setVisibility(GONE);
                    mEmptyView.setVisibility(VISIBLE);
                }else{
                    isEmptyViewShowing = false;
                    mEmptyView.setVisibility(GONE);
                    recyclerView.setVisibility(VISIBLE);
                }
            }
            mWrapperAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            mWrapperAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            mWrapperAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            mWrapperAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
           mWrapperAdapter.notifyItemRangeRemoved(fromPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            mWrapperAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

    }
}
