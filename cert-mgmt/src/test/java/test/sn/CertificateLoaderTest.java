package test.sn;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CertificateLoaderTest {
    @Test
    void loadAuthoritiesReturnsAllBundleCertificatesInOrder() throws Exception {
        List<X509Certificate> authorities = CertificateLoader.loadAuthorities(resourcePath("bundle.txt"));

        assertAll(
                () -> assertEquals(4, authorities.size()),
                () -> assertEquals("CN=E8,O=Let's Encrypt,C=US", subject(authorities.get(0))),
                () -> assertEquals("CN=ISRG Root X1,O=Internet Security Research Group,C=US", subject(authorities.get(1))),
                () -> assertEquals("CN=DigiCert Global G3 TLS ECC SHA384 2020 CA1,O=DigiCert Inc,C=US", subject(authorities.get(2))),
                () -> assertEquals("CN=DigiCert Global Root G3,OU=www.digicert.com,O=DigiCert Inc,C=US", subject(authorities.get(3)))
        );
    }

    @Test
    void loadCertificateReturnsClaudeFinalCertificate() throws Exception {
        X509Certificate certificate = CertificateLoader.loadCertificate(resourcePath("cert-claude.cer"));

        assertAll(
                () -> assertNotNull(certificate),
                () -> assertEquals("CN=claude.ai", subject(certificate)),
                () -> assertEquals("CN=E8,O=Let's Encrypt,C=US", certificate.getIssuerX500Principal().getName())
        );
    }

    @Test
    void loadCertificateReturnsMabanqueFinalCertificate() throws Exception {
        X509Certificate certificate = CertificateLoader.loadCertificate(resourcePath("mabanque.cer"));

        assertAll(
                () -> assertNotNull(certificate),
                () -> assertEquals("CN=mabanque.bnpparibas,O=BNP PARIBAS SA,L=Montreuil,C=FR,2.5.4.5=#130b3636322030343220343439,2.5.4.15=#0c1450726976617465204f7267616e697a6174696f6e,1.3.6.1.4.1.311.60.2.1.1=#13055061726973,1.3.6.1.4.1.311.60.2.1.2=#0c0ec38e6c652d64652d4672616e6365,1.3.6.1.4.1.311.60.2.1.3=#13024652", subject(certificate)),
                () -> assertEquals("CN=DigiCert Global G3 TLS ECC SHA384 2020 CA1,O=DigiCert Inc,C=US", certificate.getIssuerX500Principal().getName())
        );
    }

    private static String resourcePath(String resourceName) throws URISyntaxException {
        URL resource = CertificateLoaderTest.class.getClassLoader().getResource(resourceName);
        assertNotNull(resource, () -> "Missing test resource: " + resourceName);
        return Path.of(resource.toURI()).toString();
    }

    private static String subject(X509Certificate certificate) {
        return certificate.getSubjectX500Principal().getName();
    }
}