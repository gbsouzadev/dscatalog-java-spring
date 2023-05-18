package com.devsuperior.dscatalog.services.exceptions;

import java.io.Serializable;

public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String msg){
        super(msg);
    }


}
