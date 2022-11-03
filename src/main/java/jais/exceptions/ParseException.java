package jais.exceptions;

import java.io.Serial;

public class ParseException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public ParseException(String s) {
        super(s);
    }
}
