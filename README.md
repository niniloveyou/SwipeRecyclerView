#SwipeRecyclerView
SwipeRefreshLayout + RecyclerView 实现的下拉刷新，上拉加载更多

#ScreenShot
![](https://github.com/niniloveyou/SwipeRecyclerView/blob/master/swipeRecyclerView.gif)

#导航

1. 支持自动下拉刷新

          设置自动下拉刷新，切记要在recyclerView.setOnLoadListener()之后调用
          因为在没有设置监听接口的情况下，setRefreshing(true),调用不到OnLoadListener
          mSwipeRecyclerView.setRefreshing(true);
          
2. 支持emptyView
          
          mSwipeRecyclerView.setEmptyView(View emptyView);
3. 

#Usage
    <deadline.swiperecyclerview.SwipeRecyclerView
        android:id="@+id/swipeRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
        
