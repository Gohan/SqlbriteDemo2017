package com.baozs.demos.sqlbritedemo2017.listview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.baozs.demos.sqlbritedemo2017.R;

public class ListViewActivity extends AppCompatActivity {

    private MyAutoPullDownListView mListView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mListView = (MyAutoPullDownListView) findViewById(R.id.listview);
        mListView.setOverscrollHeader(ContextCompat.getDrawable(this, R.drawable.rockman));
        mAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mAdapter);
        mListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        for (int i = 0; i < 100; i++) {
            mAdapter.add("Hello");
            mAdapter.add("World");
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.overScrollDown();
            }
        });
    }

    public static class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
        }
    }
}
