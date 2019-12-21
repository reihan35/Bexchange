package com.example.bexchange;

class NotABookException extends Exception {

    public NotABookException(){
        super("The barcode registered is not a book");
    }
}
