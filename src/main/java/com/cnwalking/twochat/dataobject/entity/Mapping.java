package com.cnwalking.twochat.dataobject.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * mapping
 * @author 
 */
@Data
public class Mapping implements Serializable {
    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名id
     */
    private String myUserId;

    /**
     * 朋友id
     */
    private String friendUserId;

    private static final long serialVersionUID = 1L;
}