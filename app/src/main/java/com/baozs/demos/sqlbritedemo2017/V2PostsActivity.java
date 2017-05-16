package com.baozs.demos.sqlbritedemo2017;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikitakozlov.pury.annotations.MethodProfiling;
import com.squareup.sqlbrite.BriteDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.baozs.demos.sqlbritedemo2017.models.V2PostItem;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class V2PostsActivity extends AppCompatActivity {

    public static Logger LOG = LoggerFactory.getLogger(V2PostsActivity.class);
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter = new PostAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v2ex_posts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pull_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_posts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mPostAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @MethodProfiling(profilerName = "V2PostsActivity::onRefresh()")
            @Override
            public void onRefresh() {
                LOG.info("Pulldown onRefresh()");
                DemoApplication.getApplication()
                        .getV2exService()
                        .listV2Posts()
                        .subscribeOn(DemoApplication.getApplication().getNetworkScheduler())
                        .observeOn(DemoApplication.getApplication().getDatabaseScheduler())
                        .map(new Func1<List<V2PostItem>, Boolean>() {
                            @Override
                            public Boolean call(List<V2PostItem> v2PostItems) {
                                BriteDatabase.Transaction transaction = DemoApplication.getApplication().getBriteDatabase().newTransaction();
                                try {
                                    SQLiteDatabase readableDB = DemoApplication.getApplication().getBriteDatabase().getReadableDatabase();

                                    Iterator<V2PostItem> iterator = v2PostItems.iterator();
                                    ContentValues values = new ContentValues();
                                    while (iterator.hasNext()) {
                                        boolean replace = false;
                                        V2PostItem item = iterator.next();
                                        Cursor cursor = readableDB.rawQuery("select id, last_modified from post_table where id = ?;",
                                                new String[]{String.valueOf(item.id)});
                                        if (cursor.moveToFirst()) {
                                            if (cursor.getLong(1) == item.last_modified) {
                                                cursor.close();
                                                continue;
                                            }
                                            replace = true;
                                            LOG.info("replace post id = {}", item.id);
                                        }
                                        cursor.close();

                                        values.put("id", item.id);
                                        values.put("title", item.title);
                                        values.put("content", item.content);
                                        values.put("last_modified", String.valueOf(item.last_modified));
                                        values.put("created", item.created);

                                        if (replace) {
                                            DemoApplication.getApplication().getBriteDatabase().update("post_table", values, "id = ?", String.valueOf(item.id));
                                        } else {
                                            DemoApplication.getApplication().getBriteDatabase().insert("post_table", values);
                                        }
                                        values.clear();
                                    }
                                    transaction.markSuccessful();
                                } finally {
                                    transaction.end();
                                }
                                return true;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .timeout(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        LOG.error("onCompleted");
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOG.error("onError");
                        if (e instanceof TimeoutException) {
                            LOG.error("TimeoutException onError", new Throwable("TimeoutException", e));
                        } else {
                            LOG.error("Unknown Error", new Throwable("Other errors", e));
                        }
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        LOG.error("onNext");
                    }
                });
            }
        });

        DemoApplication.getApplication().getBriteDatabase()
                .createQuery("post_table", "select count(*) from post_table;")
                .mapToOneOrDefault(new Func1<Cursor, Integer>() {
            @Override
            public Integer call(Cursor cursor) {
                return cursor.getInt(0);
            }
        }, 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                setTitle(String.format(Locale.getDefault(), "Post Count: %d", integer));
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable e) {
                LOG.error("cursor read count ERROR!!!", new Throwable("Joke error!", e));
            }
        });

        DemoApplication.getApplication().getBriteDatabase()
                .createQuery("post_table", "select " + V2PostItem.SELECT_ALL_ITEM + " from post_table order by last_modified desc;")
                .mapToList(new Func1<Cursor, V2PostItem>() {
                    @Override
                    public V2PostItem call(Cursor cursor) {
                        return V2PostItem.createFromCursor(cursor);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<V2PostItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LOG.error("Error!", e);
                    }

                    @Override
                    public void onNext(List<V2PostItem> v2PostItems) {
                        mPostAdapter.setData(v2PostItems);
                    }
                });
    }

    public static class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public ArrayList<V2PostItem> mPostItems = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new PostItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((PostItemViewHolder) holder).bindData(mPostItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mPostItems.size();
        }

        public void setData(List<V2PostItem> data) {
            mPostItems = new ArrayList<>(data);
            notifyDataSetChanged();
        }
    }

    public static class PostItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTitle;
        private final TextView mTime;

        public PostItemViewHolder(View itemView) {
            super(itemView);
            mTitle = ((TextView) itemView.findViewById(R.id.title));
            mTime = ((TextView) itemView.findViewById(R.id.time));
        }

        public void bindData(V2PostItem item) {
            mTitle.setText(String.format(Locale.getDefault(), "(%d): %s", item.id, item.title));
            mTime.setText(String.valueOf(item.last_modified));
        }
    }

}
