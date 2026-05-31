package test.sn;

import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class CertificateRevocationVerifier {
    private CertificateRevocationVerifier() {
    }

    public static boolean isRevoked(X509Certificate finalCertificate, List<X509Certificate> authorities)
            throws GeneralSecurityException {
        Objects.requireNonNull(finalCertificate, "finalCertificate must not be null");
        Objects.requireNonNull(authorities, "authorities must not be null");

        Set<TrustAnchor> trustAnchors = buildTrustAnchors(authorities);
        CertPath certPath = buildCertificationPath(finalCertificate, authorities, trustAnchors);

        PKIXParameters parameters = new PKIXParameters(trustAnchors);
        parameters.setRevocationEnabled(true);

        CertPathValidator validator = CertPathValidator.getInstance("PKIX");
        PKIXRevocationChecker revocationChecker = (PKIXRevocationChecker) validator.getRevocationChecker();
        parameters.addCertPathChecker(revocationChecker);

        try {
            validator.validate(certPath, parameters);
            return false;
        } catch (CertPathValidatorException exception) {
            if (exception.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
                return true;
            }

            throw exception;
        }
    }

    public static List<X509Certificate> buildCertificationPath(
            X509Certificate finalCertificate,
            List<X509Certificate> authorities
    ) throws GeneralSecurityException {
        Objects.requireNonNull(finalCertificate, "finalCertificate must not be null");
        Objects.requireNonNull(authorities, "authorities must not be null");

        CertPath certPath = buildCertificationPath(finalCertificate, authorities, buildTrustAnchors(authorities));
        List<X509Certificate> certificationPath = new ArrayList<>(certPath.getCertificates().size());

        for (Certificate certificate : certPath.getCertificates()) {
            certificationPath.add((X509Certificate) certificate);
        }

        return certificationPath;
    }

    private static CertPath buildCertificationPath(
            X509Certificate finalCertificate,
            List<X509Certificate> authorities,
            Set<TrustAnchor> trustAnchors
    ) throws GeneralSecurityException {
        X509CertSelector targetSelector = new X509CertSelector();
        targetSelector.setCertificate(finalCertificate);

        List<X509Certificate> certificates = new ArrayList<>(authorities.size() + 1);
        certificates.add(finalCertificate);
        certificates.addAll(authorities);

        CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certificates));

        PKIXBuilderParameters parameters = new PKIXBuilderParameters(trustAnchors, targetSelector);
        parameters.addCertStore(certStore);
        parameters.setRevocationEnabled(false);

        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
        PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder.build(parameters);

        return result.getCertPath();
    }

    private static Set<TrustAnchor> buildTrustAnchors(List<X509Certificate> authorities) throws GeneralSecurityException {
        Set<TrustAnchor> trustAnchors = new HashSet<>();

        for (X509Certificate authority : authorities) {
            if (isSelfSigned(authority)) {
                trustAnchors.add(new TrustAnchor(authority, null));
            }
        }

        if (trustAnchors.isEmpty()) {
            throw new CertificateException("No self-signed root authority found");
        }

        return trustAnchors;
    }

    private static boolean isSelfSigned(X509Certificate certificate) throws GeneralSecurityException {
        if (!certificate.getSubjectX500Principal().equals(certificate.getIssuerX500Principal())) {
            return false;
        }

        certificate.verify(certificate.getPublicKey());
        return true;
    }
}