package com.baozs.demos.sqlbritedemo2017;

import android.app.Application;

import com.baozs.devkit.common.AppCommon;
import com.facebook.stetho.Stetho;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.baozs.demos.sqlbritedemo2017.models.DatabaseManager;
import com.baozs.demos.sqlbritedemo2017.models.V2exService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by vashzhong on 2017/4/23.
 */

public class DemoApplication extends Application {
    private static DemoApplication sApplication = null;

    private SqlBrite mSqlBrite;
    private BriteDatabase mBriteDatabase;
    private DatabaseManager mDatabaseManager = null;

    private V2exService v2exService;

    private ExecutorService mNetworkExecutor;
    private ExecutorService mDatabaseExecutor;
    private ExecutorService mDatabaseReadExecutor;


    static {
        // BasicLogcatConfigurator.configureDefaultContext();
    }

    public V2exService getV2exService() {
        return v2exService;
    }

    public Scheduler getNetworkScheduler() {
        return Schedulers.from(mNetworkExecutor);
    }

    public Scheduler getDatabaseScheduler() {
        return Schedulers.from(mDatabaseExecutor);
    }

    public Scheduler getDatabaseReadScheduler() {
        return Schedulers.from(mDatabaseReadExecutor);
    }

    public BriteDatabase getBriteDatabase() {
        return mBriteDatabase;
    }

    public static DemoApplication getApplication() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        AppCommon.init();

        Stetho.initializeWithDefaults(this);
        mDatabaseManager = new DatabaseManager(this);

        mSqlBrite = new SqlBrite.Builder().logger(new SqlBrite.Logger() {
            private Logger LOG = LoggerFactory.getLogger(SqlBrite.class);

            @Override
            public void log(String message) {
                LOG.info(message);
            }
        }).build();

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(V2exService.baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        v2exService = retrofit2.create(V2exService.class);
        mNetworkExecutor = Executors.newFixedThreadPool(5, new BasicThreadFactory.Builder().namingPattern("Net. %d").build());
        mDatabaseExecutor = Executors.newSingleThreadExecutor(new BasicThreadFactory.Builder().namingPattern("DB. %d").build());
        mDatabaseReadExecutor = Executors.newSingleThreadExecutor(new BasicThreadFactory.Builder().namingPattern("DBRead. %d").build());

        mBriteDatabase = mSqlBrite.wrapDatabaseHelper(mDatabaseManager, getDatabaseReadScheduler());
    }
}
