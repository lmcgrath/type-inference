package com.github.lmcgrath.toylang;

public class UndefinedSymbolException extends TypeException {

    public UndefinedSymbolException(String message) {
        super(message);
    }
}
