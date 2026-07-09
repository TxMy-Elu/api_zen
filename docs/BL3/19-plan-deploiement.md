# Plan de déploiement — CESIZen

> Bloc de compétences 3 — Déployer les applications
> API Spring Boot · Front Next.js · Mobile Flutter · Version 1.0 — Juillet 2026

---

## 1. Objet

Ce document décrit la gestion du code source, l'intégration continue (CI) et le déploiement continu (CD) des trois applications, ainsi que les environnements et la procédure de mise en production.

---

## 2. Gestion du code source

- **Hébergement.** Trois dépôts GitHub (org. TxMy-Elu) : `api_zen`, `cesizen_web`, `cesizen_mobile`.
- **Stratégie de branches** — modèle à deux branches longues :

  | Branche | Rôle | Protection |
  |---|---|---|
  | `main` | Production — code stable | PR obligatoire + checks verts (ruleset) |
  | `develop` / `dev` | Intégration — travail courant | CI à chaque push |
  | `feature/**` · `fix/**` · `hotfix/**` | Travail ponctuel | CI à chaque push |

- **Convention de commits.** Conventional Commits (`feat:`, `fix:`, `chore:`, `ci:`, `docs:`). Tags SemVer (`v1.0.0`).
- **Protection de main.** Ruleset GitHub : PR obligatoire, interdiction de force-push et de suppression, checks requis (build/tests + scans) avant merge.

---

## 3. Intégration continue (CI)

| Application | Étapes CI | Artefact |
|---|---|---|
| API (Maven) | `mvn clean verify` · JaCoCo · SonarCloud · service MySQL | JAR + rapports de tests |
| Web (pnpm) | `install --frozen-lockfile` · lint · build Next.js | build de production |
| Mobile (Flutter) | `flutter analyze` · `flutter test` · build APK release | APK (clé debug) |

**Séparation sécurité / CI.** Les scans (Trivy, Gitleaks, OWASP Dependency Check) sont isolés dans un workflow `security-*.yml` dédié, pour qu'un scan lent ou une base CVE indisponible ne bloque jamais la CI fonctionnelle.

---

## 4. Déploiement continu (CD)

### 4.1 API — conteneur Docker sur GHCR

Image Docker multi-stage (Alpine, user non-root) poussée sur le **GitHub Container Registry** après CI verte sur `main`. Le job de déploiement SSH est **gated** par la variable `PROD_ENABLED` : il ne s'exécute que lorsqu'un serveur cible est disponible. En l'absence de VPS, la CD existe, reste non bloquante, et ne se déclenche pas — l'image est néanmoins prête.

### 4.2 Front — Vercel

Déploiement par l'**intégration Git native de Vercel** : chaque push sur `main` est build et mis en production automatiquement. Un workflow `deploy-web.yml` fournit un **redéploiement manuel de secours** (déclenchement explicite + confirmation « PROD »), indépendant du dashboard Vercel.

### 4.3 Mobile — artefact APK

La CI produit un **APK release** signé avec la clé debug, publié comme artefact GitHub (rétention 7 jours). La publication sur les stores n'est pas automatisée dans le périmètre du projet ; l'APK est le livrable de déploiement.

---

## 5. Environnements

| Paramètre | Développement | Production |
|---|---|---|
| Déclenchement | Manuel (local) | Push sur `main` |
| API | Docker local (`localhost:8080`) | Image GHCR (déploiement gated) |
| Web | `pnpm dev` (`localhost:3000`) | Vercel |
| Mobile | Émulateur (`10.0.2.2:8080`) | APK distribué |
| Base de données | MySQL Docker | MySQL — user à droits minimaux |
| Swagger | Activé | Désactivé |
| Logs | DEBUG | WARN |
| HTTPS | Non (HTTP local) | Oui (HSTS) |

**Variables d'environnement.** Aucun secret commité. Valeurs sensibles dans : `.env` local (gitignoré), **secrets GitHub Actions** (BDD, JWT, token Vercel), **secrets Dependabot** (coffre distinct), **variables Vercel** (clés publiques Supabase, URL API).

---

## 6. Procédure de mise en production

1. Ouvrir une pull request `develop → main`.
2. Attendre les checks verts (build, tests, scans de sécurité).
3. Merger — le déploiement se déclenche (Vercel pour le web, image GHCR pour l'API).
4. Tagger la version sur `main` (`git tag -a vX.Y.Z`).

**Rollback.** Web : redéployer un déploiement Vercel antérieur. API : re-tagger sur GHCR l'image précédente et relancer. Mobile : redistribuer l'APK de la version stable précédente.

---

## 7. Conclusion

La chaîne de livraison est automatisée de bout en bout et homogène sur les trois applications : CI systématique, protection de la branche de production, déploiement piloté par des déclencheurs explicites. Principe directeur : aucun secret dans le code, aucun déploiement non maîtrisé.
