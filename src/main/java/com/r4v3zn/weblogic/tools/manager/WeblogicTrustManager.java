package com.r4v3zn.weblogic.tools.manager;

import weblogic.security.SSL.TrustManager;

import java.security.cert.X509Certificate;

/**
 * Title: WeblogicTrustManager
 * Desc: TODO
 * Date:2020/4/3 20:59
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class WeblogicTrustManager implements TrustManager {

    @Override
    public boolean certificateCallback(X509Certificate[] x509Certificates, int i) {
        return true;
    }
}
