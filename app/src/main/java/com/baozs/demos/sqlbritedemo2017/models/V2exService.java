package com.baozs.demos.sqlbritedemo2017.models;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by vashzhong on 2017/4/23.
 */

public interface V2exService {
    String baseUrl = "https://www.v2ex.com/api/";
    @GET("topics/latest.json")
    Observable<List<V2PostItem>> listV2Posts();

}
