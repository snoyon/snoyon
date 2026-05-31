package test.sn;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CertificateLoader {
    private CertificateLoader() {
    }

    public static List<X509Certificate> loadAuthorities(String bundleLocation) throws IOException, CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        try (InputStream inputStream = Files.newInputStream(Path.of(bundleLocation))) {
            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(inputStream);
            List<X509Certificate> authorities = new ArrayList<>(certificates.size());

            for (Certificate certificate : certificates) {
                authorities.add((X509Certificate) certificate);
            }

            return authorities;
        }
    }

    public static X509Certificate loadCertificate(String certificateLocation) throws IOException, CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        try (InputStream inputStream = Files.newInputStream(Path.of(certificateLocation))) {
            return (X509Certificate) certificateFactory.generateCertificate(inputStream);
        }
    }
}
