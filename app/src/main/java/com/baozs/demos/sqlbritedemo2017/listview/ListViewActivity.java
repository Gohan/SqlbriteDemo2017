package com.baozs.demos.sqlbritedemo2017.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baozs.demos.sqlbritedemo2017.R;

import java.util.ArrayList;
import java.util.Locale;

public class ListViewActivity extends AppCompatActivity {

    private MyAutoPullDownListView mListView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mListView = (MyAutoPullDownListView) findViewById(R.id.listview);
        mListView.setOverscrollHeader(ContextCompat.getDrawable(this, R.drawable.rockman));
        mAdapter = new MyAdapter(this, R.layout.widget_comment_container_layout);
        mListView.setAdapter(mAdapter);
        mListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        for (int i = 0; i < 100; i++) {
            mAdapter.add(String.format(Locale.getDefault(), "Hello (%d)", i));
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.overScrollDown();
            }
        });
    }

    public static class MyAdapter extends BaseAdapter {
        private final Context mContext;
        private final int mResource;
        private ArrayList<String> mItems = new ArrayList<>();

        public void add(String str) {
            mItems.add(str);
        }

        public MyAdapter(@NonNull Context context, @LayoutRes int resource) {
            mContext = context;
            mResource = resource;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mTitle.setText(getItem(position).toString());
            viewHolder.mComments.removeAllViews();
            if (position % 9 == 0) {
                for (int i = 0; i < 30 * position; i++) {
                    TextView child = new TextView(mContext);
                    viewHolder.mComments.addView(child);
                    child.setText("Comments " + i);
                }
            }
            return convertView;
        }
    }

    public static class ViewHolder {
        private final View mViewItem;
        private final LinearLayout mComments;
        private final TextView mTitle;
        public LinearLayout mContainer;
        public ViewHolder(View view) {
            mViewItem = view;
            mComments = (LinearLayout) view.findViewById(R.id.comments);
            mTitle = (TextView) view.findViewById(R.id.title);
        }
    }
}
