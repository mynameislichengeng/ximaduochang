package com.evideo.kmbox.model.datacenter;
/*
* Copyright (C) 2014-2016 福建星网视易信息系统有限公司
* All rights reserved by 福建星网视易信息系统有限公司
*
* Modification History:
* DateAuthorVersionDescription
* -----------------------------------------------
* 2017/4/24王凯1.0[修订说明]
*
*/

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

class OkHttpClientFactory {

    /** [连接超时] */
    private static final int TIME_OUT_CON = 10 * 1000;
    /** [读取超时] */
    private static final int TIME_OUT_SO = 10 * 1000;

    private static final OkHttpClientFactory ourInstance = new OkHttpClientFactory();

    private OkHttpClient client;

    public static OkHttpClientFactory getInstance() {
        return ourInstance;
    }

    private OkHttpClientFactory() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .allEnabledCipherSuites()
                .build();

        TrustNoCheckValidityManagerWrapper trustNoCheckValidityManagerWrapper = new TrustNoCheckValidityManagerWrapper();

        client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .connectTimeout(TIME_OUT_CON, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT_SO,TimeUnit.MILLISECONDS)
                .sslSocketFactory(createSSLSocketFactory(trustNoCheckValidityManagerWrapper), trustNoCheckValidityManagerWrapper)
                .build();
    }



    private static SSLSocketFactory createSSLSocketFactory(TrustManager trustManager) {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{trustManager},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    private static class TrustNoCheckValidityManagerWrapper implements X509TrustManager {

        X509TrustManager manager;

        public TrustNoCheckValidityManagerWrapper() {
            try {
                TrustManagerFactory e = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                e.init((KeyStore)null);
                TrustManager[] trustManagers = e.getTrustManagers();
                if(trustManagers.length == 1 && trustManagers[0] instanceof X509TrustManager) {
                    this.manager = (X509TrustManager)trustManagers[0];
                } else {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
            } catch (GeneralSecurityException var3) {
                throw new AssertionError();
            }
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
            X509Certificate[] certificates = new X509Certificate[x509Certificates.length];
            for(int i=0;i<x509Certificates.length;i++){
                certificates[i] = new NoCheckValidityX509CertificateWrapper(x509Certificates[i]);
            }
            manager.checkClientTrusted(certificates,s);
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
            X509Certificate[] certificates = new X509Certificate[x509Certificates.length];
            for(int i=0;i<x509Certificates.length;i++){
                certificates[i] = new NoCheckValidityX509CertificateWrapper(x509Certificates[i]);
            }
            manager.checkClientTrusted(certificates,s);
        }


        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return manager.getAcceptedIssuers();
        }
    }

    private static class NoCheckValidityX509CertificateWrapper extends X509Certificate {
        X509Certificate x509Certificate;

        public NoCheckValidityX509CertificateWrapper(X509Certificate other) {
            x509Certificate = other;
        }

        @Override
        public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {

        }

        @Override
        public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {

        }

        @Override
        public int getVersion() {
            return x509Certificate.getVersion();
        }

        @Override
        public BigInteger getSerialNumber() {
            return x509Certificate.getSerialNumber();
        }

        @Override
        public Principal getIssuerDN() {
            return x509Certificate.getIssuerDN();
        }

        @Override
        public Principal getSubjectDN() {
            return x509Certificate.getSubjectDN();
        }

        @Override
        public Date getNotBefore() {
            return x509Certificate.getNotBefore();
        }

        @Override
        public Date getNotAfter() {
            return x509Certificate.getNotAfter();
        }

        @Override
        public byte[] getTBSCertificate() throws CertificateEncodingException {
            return x509Certificate.getTBSCertificate();
        }

        @Override
        public byte[] getSignature() {
            return x509Certificate.getSignature();
        }

        @Override
        public String getSigAlgName() {
            return x509Certificate.getSigAlgName();
        }

        @Override
        public String getSigAlgOID() {
            return x509Certificate.getSigAlgOID();
        }

        @Override
        public byte[] getSigAlgParams() {
            return x509Certificate.getSigAlgParams();
        }

        @Override
        public boolean[] getIssuerUniqueID() {
            return x509Certificate.getIssuerUniqueID();
        }

        @Override
        public boolean[] getSubjectUniqueID() {
            return x509Certificate.getSubjectUniqueID();
        }

        @Override
        public boolean[] getKeyUsage() {
            return x509Certificate.getKeyUsage();
        }

        @Override
        public int getBasicConstraints() {
            return x509Certificate.getBasicConstraints();
        }

        @Override
        public byte[] getEncoded() throws CertificateEncodingException {
            return x509Certificate.getEncoded();
        }

        @Override
        public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
            x509Certificate.verify(key);
        }

        @Override
        public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
            x509Certificate.verify(key,sigProvider);
        }

        @Override
        public String toString() {
            return x509Certificate.toString();
        }

        @Override
        public PublicKey getPublicKey() {
            return x509Certificate.getPublicKey();
        }

        @Override
        public Set<String> getCriticalExtensionOIDs() {
            return x509Certificate.getCriticalExtensionOIDs();
        }

        @Override
        public byte[] getExtensionValue(String oid) {
            return x509Certificate.getExtensionValue(oid);
        }

        @Override
        public Set<String> getNonCriticalExtensionOIDs() {
            return x509Certificate.getNonCriticalExtensionOIDs();
        }

        @Override
        public boolean hasUnsupportedCriticalExtension() {
            return x509Certificate.hasUnsupportedCriticalExtension();
        }
    }

    public OkHttpClient getClient(){
        return client;
    }


}
