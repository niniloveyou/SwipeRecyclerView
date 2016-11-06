package deadline.swiperecyclerview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeRecyclerView recyclerView;
    private List<String> data;
    private RecyclerViewAdapter adapter;
    private int pagerSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_main);

        recyclerView = (SwipeRecyclerView) findViewById(R.id.swipeRecyclerView);

        //set color
        recyclerView.getSwipeRefreshLayout()
                .setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        //set layoutManager
        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.getRecyclerView().setLayoutManager(new GridLayoutManager(this, 3));
        //recyclerView.getRecyclerView().setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        //禁止下拉刷新
       // recyclerView.setRefreshEnable(false);

        //禁止加载更多
        //recyclerView.setLoadMoreEnable(false);

        //设置emptyView
        /*TextView textView = new TextView(this);
        textView.setText("empty view");
        recyclerView.setEmptyView(textView);*/

        //设置footerView
         //recyclerView.setFooterView(new SimpleFooterView(this));

        //由于SwipeRecyclerView中对GridLayoutManager的SpanSizeLookup做了处理，因此对于使用了
        //GridLayoutManager又要使用SpanSizeLookup的情况，可以这样使用！
        /*recyclerView.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 3;
            }
        });*/

        //设置去除footerView 的分割线
       /* recyclerView.getRecyclerView().addItemDecoration(new RecyclerView.ItemDecoration() {
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
        });*/

        //设置noMore
       // recyclerView.onNoMore("-- end --");

        //设置网络处理
        //recyclerView.onNetChange(true);

        //设置错误信息
        //recyclerView.onError("error");

        data = new ArrayList<>();
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        for (int i = 0; i < pagerSize; i++) {
                            data.add(String.valueOf(i));
                        }

                        recyclerView.complete();
                        adapter.notifyDataSetChanged();

                    }
                }, 1000);

            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < pagerSize; i++) {
                            data.add(String.valueOf(i));
                        }

                        if(data.size() > 20){
                            recyclerView.onNoMore("-- the end --");
                        }else {
                            recyclerView.stopLoadingMore();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, 1000);
            }
        });

        //设置自动下拉刷新，切记要在recyclerView.setOnLoadListener()之后调用
        //因为在没有设置监听接口的情况下，setRefreshing(true),调用不到OnLoadListener
        recyclerView.setRefreshing(true);
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.demo_layout_item, parent, false);
            return new ItemViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {

            holder.tv.setText("my position is" + data.get(position));

            //for test item click listener
            holder.tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "快来咬我啊，我是" + position + "号", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public ItemViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv);
        }
    }
}