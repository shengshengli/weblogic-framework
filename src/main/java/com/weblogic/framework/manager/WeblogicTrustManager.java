package com.weblogic.framework.manager;

import weblogic.security.SSL.TrustManager;

import java.security.cert.X509Certificate;

/**
 * Title: WeblogicTrustManager
 * Desc: WeblogicTrustManager
 * Date:2020/4/3 20:59
 * @version 1.0.0
 */
public class WeblogicTrustManager implements TrustManager {

    @Override
    public boolean certificateCallback(X509Certificate[] x509Certificates, int i) {
        return true;
    }
}
