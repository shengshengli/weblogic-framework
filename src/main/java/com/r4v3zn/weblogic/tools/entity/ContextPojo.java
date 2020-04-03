package com.r4v3zn.weblogic.tools.entity;

import lombok.Data;

import javax.naming.Context;
import java.net.URLClassLoader;

/**
 * Title: ContextPojo
 * Descrption: TODO
 * Date:2020/4/3 11:30
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
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
}
