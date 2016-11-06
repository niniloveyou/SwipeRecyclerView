#SwipeRecyclerView
SwipeRefreshLayout + RecyclerView 实现的下拉刷新，上拉加载更多

#ScreenShot
![](https://github.com/niniloveyou/SwipeRecyclerView/blob/master/swipeRecyclerView.gif)

#导航

###1. 支持自动下拉刷新

          设置自动下拉刷新，切记要在recyclerView.setOnLoadListener()之后调用
          因为在没有设置监听接口的情况下，setRefreshing(true),调用不到OnLoadListener
          mSwipeRecyclerView.setRefreshing(true);
          
###2. 支持emptyView
          
          mSwipeRecyclerView.setEmptyView(View emptyView);
          
###3. 支持禁止上拉加载更多/下拉刷新
          
          //禁止下拉刷新
        mSwipeRecyclerView.setRefreshEnable(false);

        //禁止加载更多
        mSwipeRecyclerView.setLoadMoreEnable(false);
        
###4.支持自定义footer view

       //设置footerView
       //但是自定义的footerView必须继承BaseFooterView
       mSwipeRecyclerView.setFooterView(new SimpleFooterView(this));
       
###5.支持GridLayoutManager的SpanSizeLookup
          
         //由于SwipeRecyclerView中对GridLayoutManager的SpanSizeLookup做了处理，因此对于使用了
        //GridLayoutManager又要使用SpanSizeLookup的情况，可以这样使用！
        mSwipeRecyclerView.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 3;
            }
        });
        
###6.关于footerView的分割线 获取childCount - 1 不包含footerView即可 

         //设置去除footerView 的分割线
        mSwipeRecyclerView.getRecyclerView().addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(0xFFEECCCC);

                Rect rect = new Rect();
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                final int childCount = parent.getChildCount() - 1;
                for (int i = 0; i < childCount; i++) {
                    final View child = parent.getChildAt(i);

                    //获得child的布局信息
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int top = child.getBottom() + params.bottomMargin;
                    final int itemDividerHeight = 1;//px
                    rect.set(left + 50, top, right - 50, top + itemDividerHeight);
                    c.drawRect(rect, paint);
                }
            }
        });
        
###7.需要对SwipeRefreshLayout或RecyclerView做其他的设置

          mSwipeRecyclerView.getSwipeRefreshLayout()
          mSwipeRecyclerView.getRecyclerView()
          
          
          
###8.可能存在的问题

     由于Recycler.Adapter中关于数据集更新的方法全是final的，无法重写，并且自定义的DataObserver也没法实现的方法 如：notifyItemMoved方法
     因此使用除SwipeRecyclerView中DataObserver的方法之外的更新数据集的方法，可能会有问题所以更新数据集建议采用DataObserver中有的方法。
          
          
#Usage

由于并没有放到jCenter
         
    所以如果需要使用：请自行把layout目录下layout_swipe_recyclerview, layout_footer_view copy跟正常一样控件使用，没有自定义属性
        
    <deadline.swiperecyclerview.SwipeRecyclerView
        android:id="@+id/swipeRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
        
    mSwipeRecyclerView = (SwipeRecyclerView) findViewById(R.id.swipeRecyclerView);   
    

#...
      如果你觉得还可以star一下吧！
      
#about me
我的博客：http://www.jianshu.com/users/25e80ace21b8/latest_articles
