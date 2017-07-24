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
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import deadline.swiperecyclerview.footerView.BaseFooterView;
import deadline.swiperecyclerview.footerView.FooterViewListener;
import deadline.swiperecyclerview.footerView.SimpleFooterView;

/**
 * @auther deadline
 * @time   2016/10/22
 * SwipeRefreshLayout + recyclerView
 */
public class SwipeRecyclerView extends FrameLayout
                implements SwipeRefreshLayout.OnRefreshListener{

    private View mEmptyView;
    private BaseFooterView mFootView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    private LayoutManager mLayoutManager;
    private OnLoadListener mListener;
    private SpanSizeLookup mSpanSizeLookup;
    private DataObserver mDataObserver;
    private WrapperAdapter mWrapperAdapter;

    private boolean isEmptyViewShowing;
    private boolean isLoadingMore;
    private boolean isLoadMoreEnable;
    private boolean isRefreshEnable;
    private boolean isNotMoreData;

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
        isNotMoreData = false;

        mFootView = new SimpleFooterView(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_swipe_recyclerview, this);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        mLayoutManager = recyclerView.getLayoutManager();

        mRefreshLayout.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // do nothing if load more is not enable or refreshing or loading more
                if(!isLoadMoreEnable || isNotMoreData || isRefreshing() || isLoadingMore){
                    return;
                }

                //get the lastVisiablePosition
                mLayoutManager = recyclerView.getLayoutManager();
                if(mLayoutManager instanceof LinearLayoutManager){
                    lastVisiablePosition = ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
                }else if(mLayoutManager instanceof GridLayoutManager){
                    lastVisiablePosition = ((GridLayoutManager)mLayoutManager).findLastCompletelyVisibleItemPosition();
                }else if(mLayoutManager instanceof StaggeredGridLayoutManager){
                    int[] into = new int[((StaggeredGridLayoutManager) mLayoutManager).getSpanCount()];
                    ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(into);
                    lastVisiablePosition = findMax(into);
                }

                int childCount = mWrapperAdapter == null ? 0 : mWrapperAdapter.getItemCount();
                if(childCount > 1 && lastVisiablePosition == childCount - 1){

                    if(mListener != null){
                        isLoadingMore = true;
                        SwipeRecyclerView.this.onLoadingMore();
                        mListener.onLoadMore();
                    }
                }
            }
        });
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * set is enable pull to refresh
     * @param refreshEnable
     */
    public void setRefreshEnable(boolean refreshEnable){
        isRefreshEnable = refreshEnable;
        mRefreshLayout.setEnabled(isRefreshEnable);
    }

    public boolean getRefreshEnable(){
        return isRefreshEnable;
    }

    /**
     * set is loading more enable
     * @param loadMoreEnable
     *              if true when recyclerView scroll to bottom load more action will be trigger
     */
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        if(!loadMoreEnable){
            stopLoadingMore();
        }
        isLoadMoreEnable = loadMoreEnable;
    }

    public boolean getLoadMoreEnable(){
        return isLoadMoreEnable;
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
     * support for GridLayoutManager
     * @param spanSizeLookup
     */
    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup){
        this.mSpanSizeLookup = spanSizeLookup;
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
        if(mEmptyView != null){
            removeView(mEmptyView);
        }
        this.mEmptyView = emptyView;

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
        stopLoadingMore();
    }

    /**
     * set refreshing
     * if you want load data when first in, you can setRefreshing(true)
     * after {@link #setOnLoadListener(OnLoadListener)}
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing){
        mRefreshLayout.setRefreshing(refreshing);
        if(refreshing && !isLoadingMore && mListener != null){
            mListener.onRefresh();
        }
    }

    /**
     * stop loading more without animation
     */
    public void stopLoadingMore(){
        isLoadingMore = false;
        if(mWrapperAdapter != null) {
            mWrapperAdapter.notifyItemRemoved(mWrapperAdapter.getItemCount());
        }
    }

    /**
     * call method {@link OnLoadListener#onRefresh()}
     */
    @Override
    public void onRefresh() {
        isNotMoreData = false;
        if(mListener != null){

            //reset footer view status loading
            if(mFootView != null){
                mFootView.onReset();
            }
            mListener.onRefresh();
        }
    }

    /**
     * {@link FooterViewListener#onNetChange(boolean isAvailable)}
     * call when network is available or not available
     */
    public void onNetChange(boolean isAvailable) {
        if(mFootView != null){
            mFootView.onNetChange(isAvailable);
        }
    }

    /**
     * {@link FooterViewListener#onLoadingMore()}
     * call when you need change footer view to loading status
     */
    public void onLoadingMore() {
        if(mFootView != null){
            mFootView.onLoadingMore();
        }
    }

    /**
     * {@link FooterViewListener#onNoMore(CharSequence message)}
     * call when no more data add to list
     */
    public void onNoMore(CharSequence message) {
        isNotMoreData = true;
        if(mFootView != null){
            mFootView.onNoMore(message);
        }
    }

    /**
     * {@link FooterViewListener#onError(CharSequence message)}
     * call when you need show error message
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

        public boolean isLoadMoreItem(int position){
            return isLoadMoreEnable && position == getItemCount() - 1;
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
            if(isLoadMoreItem(position)){
                return;
            }
            mInnerAdapter.onBindViewHolder(holder, position);
        }


        @Override
        public int getItemViewType(int position) {
            if(isLoadMoreItem(position)){
                return TYPE_FOOTER;
            }else{
                return mInnerAdapter.getItemViewType(position);
            }
        }

        @Override
        public int getItemCount() {
            int count = mInnerAdapter == null ? 0 : mInnerAdapter.getItemCount();

            //without loadingMore when adapter size is zero
            if(count == 0){
                return 0;
            }
            return isLoadMoreEnable ? count + 1 : count;
        }

        @Override
        public long getItemId(int position) {
           return mInnerAdapter.getItemId(position);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && isLoadMoreItem(holder.getLayoutPosition()))
            {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            mInnerAdapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            mInnerAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        boolean isLoadMore = isLoadMoreItem(position);
                        if(mSpanSizeLookup != null && !isLoadMore){
                            return mSpanSizeLookup.getSpanSize(position);
                        }
                        return isLoadMore ? gridManager.getSpanCount() : 1;
                    }
                });
            }
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
                if(isLoadMoreEnable && adapter.getItemCount() != 0){
                    count ++;
                }
                if(adapter.getItemCount() == count){
                    isEmptyViewShowing = true;
                    if(mEmptyView.getParent() == null){
                        FrameLayout.LayoutParams params = new LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER;

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

    public interface OnLoadListener {

        void onRefresh();

        void onLoadMore();
    }
}
