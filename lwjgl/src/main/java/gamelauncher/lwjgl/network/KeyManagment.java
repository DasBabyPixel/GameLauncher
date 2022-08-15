package gamelauncher.lwjgl.network;

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

import gamelauncher.engine.io.Files;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * @author DasBabyPixel
 */
class KeyManagment {

	private final ReentrantLock lock = new ReentrantLock(true);

	private final LWJGLGameLauncher launcher;

	PrivateKey privateKey;

	X509Certificate certificate;

	public KeyManagment(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
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

			Path directory = launcher.getDataDirectory().resolve("ssl");
			Files.createDirectories(directory);
			Path pkey = directory.resolve("key");
			Path pcert = directory.resolve("cert");

			Files.write(pkey, pkcs8.getEncoded());
			Files.write(pcert, x509.getEncoded());

		} catch (CertificateException ex) {
			ex.printStackTrace();
		} catch (GameException ex) {
			ex.printStackTrace();
		}
	}

	private void loadFromFile() throws Exception {
		Path directory = launcher.getDataDirectory().resolve("ssl");
		Files.createDirectories(directory);
		Path pkey = directory.resolve("key");
		Path pcert = directory.resolve("cert");
		if (!Files.exists(pkey) || !Files.exists(pcert)) {
			return;
		}
		KeyFactory keyfac = KeyFactory.getInstance("RSA");
		CertificateFactory certfac = CertificateFactory.getInstance("X.509");
		PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(Files.readAllBytes(pkey));
		PrivateKey privateKey = keyfac.generatePrivate(pkcs8);
		X509Certificate cert = (X509Certificate) certfac
				.generateCertificate(new ByteArrayInputStream(Files.readAllBytes(pcert)));
		this.certificate = cert;
		this.privateKey = privateKey;

	}

}
