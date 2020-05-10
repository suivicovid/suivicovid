package org.suivicovid.sync;

import java.io.Serializable;

public class Info implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {STARTING};

    private final Status status;

    public Info(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    
}