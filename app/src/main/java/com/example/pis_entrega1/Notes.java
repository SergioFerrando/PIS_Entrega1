package com.example.pis_entrega1;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.EditText;

import java.util.Date;

public abstract class Notes {
    private String date;
    private String name;
    private Date deathLine;
    private String content;
    private String id;
    private int type;
    private boolean checked;

    public Notes(){
        this.name = "";
    }

    public Notes(String name) {
        this.name = name;
    }


    public Notes(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    abstract void saveNote();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDeathLine() {
        return deathLine;
    }

    public void setDeathLine(Date deathLine) {
        this.deathLine = deathLine;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
