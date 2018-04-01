package com.elsawaf.thebrilliant.noteapp;

import com.orm.SugarRecord;

/**
 * Created by The Brilliant on 25/03/2018.
 */

public class Note extends SugarRecord{

    private String title;
    private String desc;
    private long time;

    public Note() {
    }

    public Note(String title, String desc, long time) {
        this.title = title;
        this.desc = desc;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
