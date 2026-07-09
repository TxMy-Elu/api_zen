# Veille technologique — CESIZen

> Bloc de compétences 3 — Trois fiches de veille
> Version 1.0 — Juillet 2026

---

## Méthodologie

La veille s'appuie sur des sources officielles et des bases de vulnérabilités, matérialisée dans le projet par Dependabot et les scans Trivy/OWASP. Chaque fiche suit la même trame : **source datée**, **résumé**, **impact sur CESIZen**, **action prise**. Les trois sujets correspondent à des évènements réels du projet.

---

## Fiche 1 — Vulnérabilités Jackson (CVE-2026-54512 / 54513)

| Champ | Contenu |
|---|---|
| Sujet | Désérialisation Jackson (jackson-databind) |
| Source | Base d'avis GitHub / NVD — remontée par Trivy en CI, juillet 2026 |
| Références | CVE-2026-54512, CVE-2026-54513 |
| Sévérité | CRITICAL / HIGH |

**Résumé.** Deux vulnérabilités de jackson-databind, bibliothèque de sérialisation JSON transitive de Spring Boot. Exploitables lors de la désérialisation de données non fiables.

**Impact sur CESIZen.** L'API désérialise du JSON entrant. La version héritée du parent Spring Boot était vulnérable. Le scan Trivy de la CI a **fait échouer le build**, révélant la CVE avant toute mise en production.

**Action prise.** Forçage des versions corrigées par import de BOM dans `pom.xml` : `com.fasterxml.jackson:jackson-bom:2.21.4` et `tools.jackson:jackson-bom:3.1.4` en `<dependencyManagement>`. Point technique : un `<dependencyManagement>` déclaré dans le projet **prime sur le BOM du parent** Spring Boot — seul moyen propre de forcer une version transitive.

---

## Fiche 2 — Spring Boot 4 & Spring Security 6

| Champ | Contenu |
|---|---|
| Sujet | Montée de version majeure du socle backend |
| Source | spring.io/blog, notes de version Spring Boot 4.0.x — 2026 |
| Nature | Évolution de framework |

**Résumé.** Spring Boot 4 s'appuie sur Spring Framework 7, Spring Security 6 et Hibernate 7. Plusieurs changements de comportement impactent la configuration.

**Impact sur CESIZen.**

- Hibernate 7 ne détecte plus le dialect : ajout explicite de `spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect`.
- Spring Security 6 permet de configurer nativement les en-têtes (`permissionsPolicy`, `contentSecurityPolicy`) — exploité pour le durcissement HTTP.
- Tomcat embarqué mis à jour : occasion de forcer 11.0.22 pour corriger des CVE.

**Action prise.** Configuration adaptée et testée ; en-têtes de sécurité posés via la nouvelle API. La montée de version a été l'occasion de renforcer la posture de sécurité.

---

## Fiche 3 — Vulnérabilité Next.js 16 (contournement de middleware)

| Champ | Contenu |
|---|---|
| Sujet | Contournement de middleware Next.js |
| Source | Base d'avis GitHub — détectée par `pnpm audit` / Trivy, juillet 2026 |
| Références | GHSA-36qx-fr4f-26g5 (et apparentées) |
| Sévérité | HIGH — corrigée en 16.2.5+ |

**Résumé.** Next.js 16.2.2 portait plusieurs vulnérabilités HIGH, dont un contournement de middleware permettant, dans certaines configurations, d'atteindre des routes normalement protégées. Corrigé à partir de la 16.2.5.

**Impact sur CESIZen.** Le front s'appuie sur le routage pour protéger l'espace d'administration. Le scan de la CI web a remonté **huit vulnérabilités HIGH** dans Next.js ; le step Trivy bloquant aurait fait échouer toute livraison.

**Action prise.** Montée de Next.js en `16.2.10` (et de l'outillage : eslint-config-next, postcss), build et tests revérifiés. Les huit CVE HIGH ont disparu. Une CVE moderate résiduelle (postcss du toolchain interne de Next) subsiste sous le seuil bloquant, suivie en issue. Cet épisode illustre la valeur d'un scan de dépendances **bloquant** en CI.

---

## Synthèse

Les trois fiches montrent une veille outillée : les vulnérabilités ne sont pas découvertes manuellement mais remontées automatiquement par la chaîne CI (Trivy, audit npm, OWASP), puis traitées par mise à jour ciblée. La veille sur les frameworks permet d'anticiper les changements de comportement et de transformer chaque montée de version en occasion de durcissement.
