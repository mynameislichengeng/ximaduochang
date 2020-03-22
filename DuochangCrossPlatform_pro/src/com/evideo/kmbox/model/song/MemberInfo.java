package com.evideo.kmbox.model.song;

import java.io.Serializable;

public class MemberInfo implements Serializable {

    /** [描述变量作用] */
    private static final long serialVersionUID = 1L;

    public String id = null;
    public String name = null;
    public String picpath = null;
    public MemberInfo(String id, String name, String picpath) {
        super();
        this.id = id;
        this.name = name;
        this.picpath = picpath;
    }
    
}
