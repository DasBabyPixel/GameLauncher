/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.data.Files;
import gamelauncher.engine.util.GameException;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DasBabyPixel
 */
class KeyManagment {

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Path sslDirectory;
    private boolean loaded;
    PrivateKey privateKey;
    X509Certificate certificate;

    public KeyManagment(Path sslDirectory) {
        this.sslDirectory = sslDirectory;
    }

    public boolean loaded() {
        try {
            lock.lock();
            return loaded;
        } finally {
            lock.unlock();
        }
    }

    public void load() {
        try {
            lock.lock();
            try {
                loadFromFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (this.privateKey == null || this.certificate == null) {
                generateNew();
            }
            loaded = true;
        } finally {
            lock.unlock();
        }
    }

    private void generateNew() {
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            privateKey = ssc.key();
            certificate = ssc.cert();
            X509EncodedKeySpec x509 = new X509EncodedKeySpec(certificate.getEncoded());
            PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(privateKey.getEncoded());

            Files.createDirectories(sslDirectory);
            Path pkey = sslDirectory.resolve("key");
            Path pcert = sslDirectory.resolve("cert");

            Files.write(pkey, pkcs8.getEncoded());
            Files.write(pcert, x509.getEncoded());
        } catch (CertificateException | GameException ex) {
            ex.printStackTrace();
        }
    }

    private void loadFromFile() throws Exception {
        Files.createDirectories(sslDirectory);
        Path pkey = sslDirectory.resolve("key");
        Path pcert = sslDirectory.resolve("cert");
        if (!Files.exists(pkey) || !Files.exists(pcert)) {
            return;
        }
        KeyFactory keyfac = KeyFactory.getInstance("RSA");
        CertificateFactory certfac = CertificateFactory.getInstance("X.509");
        PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(Files.readAllBytes(pkey));
        PrivateKey privateKey = keyfac.generatePrivate(pkcs8);
        this.certificate = (X509Certificate) certfac.generateCertificate(new ByteArrayInputStream(Files.readAllBytes(pcert)));
        this.privateKey = privateKey;
    }
}
