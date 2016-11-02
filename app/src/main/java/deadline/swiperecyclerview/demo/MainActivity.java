package deadline.swiperecyclerview.demo;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import deadline.swiperecyclerview.OnLoadListener;
import deadline.swiperecyclerview.R;
import deadline.swiperecyclerview.SwipeRecyclerView;

public class MainActivity extends AppCompatActivity {

    private SwipeRecyclerView recyclerView;
    private List<String> data;

    private RecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (SwipeRecyclerView) findViewById(R.id.swipeRecyclerView);
        recyclerView.getSwipeRefreshLayout()
                .setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));

        data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(String.valueOf(i));
        }
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        for (int i = 0; i < 10; i++) {
                            data.add(String.valueOf(i));
                        }
                        recyclerView.complete();
                        adapter.notifyDataSetChanged();

                    }
                }, 1500);

            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            data.add(String.valueOf(i));
                        }
                        recyclerView.complete();
                        adapter.notifyDataSetChanged();
                    }
                }, 1500);
            }
        });
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_item, parent, false);
            return new ItemViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {

            holder.tv.setText("my position is" + data.get(position));
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