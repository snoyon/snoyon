# cert-mgmt

Petit projet Java/Maven de manipulation de certificats X.509.

Le projet contient deux utilitaires principaux dans le package `test.sn` :

- `CertificateLoader` : charge des certificats X.509 depuis des fichiers PEM.
- `CertificateRevocationVerifier` : construit une chaine de certification PKIX et verifie la revocation du certificat final et des autorites intermediaires.

## Ressources de test

Les certificats de test sont places dans `src/test/resources`.

- `cert-claude.cer` : certificat final pour `claude.ai`.
- `mabanque.cer` : certificat final pour `mabanque.bnpparibas`.
- `bundle.txt` : bundle PEM contenant les autorites intermediaires et racines necessaires aux deux chaines.

Le fichier `bundle.txt` contient uniquement les autorites, pas les certificats finaux.

## CertificateLoader

Classe : `src/main/java/test/sn/CertificateLoader.java`

Cette classe utilise `CertificateFactory` avec le type `X.509`.

### `loadAuthorities(String bundleLocation)`

Charge un bundle PEM contenant plusieurs certificats concatenes et renvoie une `List<X509Certificate>`.

Cette methode est adaptee a un fichier comme `bundle.txt`, contenant par exemple :

```text
-----BEGIN CERTIFICATE-----
...
-----END CERTIFICATE-----
-----BEGIN CERTIFICATE-----
...
-----END CERTIFICATE-----
```

### `loadCertificate(String certificateLocation)`

Charge un certificat final unique et renvoie un `X509Certificate`.

Exemples de fichiers attendus :

- `cert-claude.cer`
- `mabanque.cer`

## CertificateRevocationVerifier

Classe : `src/main/java/test/sn/CertificateRevocationVerifier.java`

Cette classe utilise l'algorithme standard `PKIX` de Java avec :

- `CertPathBuilder` pour construire le chemin de certification ;
- `CertPathValidator` pour valider ce chemin ;
- `PKIXRevocationChecker` pour activer la verification de revocation.

### `buildCertificationPath(X509Certificate finalCertificate, List<X509Certificate> authorities)`

Construit le chemin de certification du certificat final vers une racine de confiance presente dans la liste des autorites.

La methode renvoie une `List<X509Certificate>` issue du `CertPath` construit par Java.

Point important : en PKIX, la racine utilisee comme `TrustAnchor` ne fait pas partie du `CertPath`. Par exemple, pour :

```text
certificat final -> autorite intermediaire -> autorite racine
```

la liste retournee contient generalement :

```text
certificat final -> autorite intermediaire
```

La racine est utilisee comme ancre de confiance, mais elle n'est pas incluse dans le chemin retourne.

### `isRevoked(X509Certificate finalCertificate, List<X509Certificate> authorities)`

Construit puis valide le chemin de certification avec la verification de revocation activee.

La methode renvoie :

- `true` si Java detecte explicitement une revocation (`CertPathValidatorException.BasicReason.REVOKED`) ;
- `false` si le chemin est valide et qu'aucune revocation n'est detectee ;
- une exception si le chemin ne peut pas etre construit, si la validation echoue pour une autre raison, ou si le statut de revocation ne peut pas etre determine.

La verification de revocation s'applique aux certificats presents dans le `CertPath`, typiquement :

- le certificat final ;
- les autorites intermediaires.

Les certificats racines utilises comme `TrustAnchor` ne sont pas verifies par le mecanisme PKIX standard. La bonne pratique habituelle consiste a gerer la confiance envers les racines via le magasin de confiance ou une liste locale d'autorites acceptees. 
Si une racine n'est plus fiable, elle doit etre retiree de cette liste.

cf https://www.rfc-editor.org/rfc/rfc5280.html section 6.3

## Exemple d'utilisation

```java
List<X509Certificate> authorities = CertificateLoader.loadAuthorities(
        "src/test/resources/bundle.txt"
);

X509Certificate certificate = CertificateLoader.loadCertificate(
        "src/test/resources/cert-claude.cer"
);

List<X509Certificate> certificationPath = CertificateRevocationVerifier.buildCertificationPath(
        certificate,
        authorities
);

boolean revoked = CertificateRevocationVerifier.isRevoked(
        certificate,
        authorities
);
```

## Tests

Les tests unitaires sont dans `src/test/java/test/sn`.

Execution :

```bash
mvn test
```

## References

- Oracle Java PKI API Programmer's Guide : https://docs.oracle.com/en/java/javase/11/security/java-pki-programmers-guide.html
- Oracle Java Security Developer's Guide : https://docs.oracle.com/en/java/javase/23/security/security-developer-guide.pdf
- Javadoc `CertificateFactory` : https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/security/cert/CertificateFactory.html
- Javadoc `CertPathBuilder` : https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/security/cert/CertPathBuilder.html
- Javadoc `CertPathValidator` : https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/security/cert/CertPathValidator.html
- Javadoc `PKIXBuilderParameters` : https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/security/cert/PKIXBuilderParameters.html
- Javadoc `PKIXRevocationChecker` : https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/security/cert/PKIXRevocationChecker.html
- Javadoc `TrustAnchor` : https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/security/cert/TrustAnchor.html