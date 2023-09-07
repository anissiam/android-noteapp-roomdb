package com.vision.mynotes.enteties;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "note_table")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "date_time")
    private String date;

    @ColumnInfo(name = "note_text")
    private String text;

    @ColumnInfo(name = "note_image")
    private String image;

    @ColumnInfo(name = "note_color")
    private String color;

    public Note() {
    }

    public Note(int id, String title, String date, String text, String image, String color) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.text = text;
        this.image = image;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", text='" + text + '\'' +
                ", image='" + image + '\'' +
                ", color=" + color +
                '}';
    }
}
