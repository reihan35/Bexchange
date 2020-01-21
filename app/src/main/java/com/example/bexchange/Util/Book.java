package com.example.bexchange.Util;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private String desc;
    private String imgLink;
    private boolean isChecked = false;

    public Book(String isbn, String title, String author, String desc, String imgLink) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.desc = desc;
        this.imgLink = imgLink;
    }

    public String getIsbn(){return  isbn;}

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDesc() {
        return desc;
    }

    public String getImgLink() {
        return imgLink;
    }

    public String toString(){
        return imgLink;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked(){ return isChecked; }
}
