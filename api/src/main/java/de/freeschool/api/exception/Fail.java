package de.freeschool.api.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fail {

    private String identifier;
    private Object[] vars = null;

    public Fail(String identifier) {
        this.identifier = identifier;
    }
}
