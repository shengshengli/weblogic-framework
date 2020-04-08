package com.weblogic.framework.entity;

import lombok.Data;
import javax.naming.Context;
import java.net.URLClassLoader;

/**
 * Title: ContextPojo
 * Desc: 内容对象
 * Date:2020/4/3 11:30
 * @version 1.0.0
 */
@Data
public class ContextPojo {


    /**
     * context
     */
    private Context context;

    /**
     * url class loader
     */
    private URLClassLoader urlClassLoader;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public URLClassLoader getUrlClassLoader() {
        return urlClassLoader;
    }

    public void setUrlClassLoader(URLClassLoader urlClassLoader) {
        this.urlClassLoader = urlClassLoader;
    }
}
