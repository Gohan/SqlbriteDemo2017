package com.baozs.demos.sqlbritedemo2017.models;

import android.database.Cursor;

/**
 * Created by vashzhong on 2017/4/23.
 */

public class V2PostItem {
    public long id;
    public String title;
    public String content;
    public long created;
    public long last_modified;

    public static final String SELECT_ALL_ITEM = "id, title, content, created, last_modified";

    public static V2PostItem createFromCursor(Cursor cursor) {
        V2PostItem item = new V2PostItem();
        item.id = cursor.getInt(0);
        item.title = cursor.getString(1);
        item.content = cursor.getString(2);
        item.created = cursor.getInt(3);
        item.last_modified = cursor.getInt(4);
        return item;
    }

    @Override
    public String toString() {
        return "V2PostItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", created=" + created +
                ", last_modified=" + last_modified +
                '}';
    }
}
