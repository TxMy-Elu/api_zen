# Dossier de maintenance — CESIZen

> Bloc de compétences 3 — Maintenir les applications en condition opérationnelle
> Version 1.0 — Juillet 2026

---

## 1. Objet

Organisation de la maintenance de la plateforme : supervision, gestion des tickets, mises à jour préventives, veille, réponse à incident, et types de maintenance avec engagements de service.

---

## 2. Supervision et monitoring

- **Stack en place (API).** Spring Boot Actuator expose `/actuator/health` et `/actuator/prometheus`. Stack **Prometheus + Grafana + Loki** (compose dédié) collectant JVM, HikariCP, Tomcat, HTTP (débit, latence p50/p95/p99, erreurs).
- **Limite connue.** Cette stack tourne sur le même hôte que l'API : si le serveur tombe, la supervision tombe aussi. Une **sonde externe** (UptimeRobot / Better Stack) interrogeant `/actuator/health` de l'extérieur est recommandée pour détecter une indisponibilité totale et alerter par e-mail. Tracé en issue.
- **Journalisation applicative.** `log_connexion` (tentatives de connexion, succès/échec, IP, horodatage) et `log_activite` (audit AOP de toutes les mutations).

---

## 3. Gestion des tickets (GitHub Issues)

- **Modèles d'issue.** Trois formulaires par dépôt (`.github/ISSUE_TEMPLATE/`) : Bug, Fonctionnalité, Question — champs obligatoires (reproduction, criticité, environnement). Issues vierges désactivées ; vulnérabilités redirigées vers un advisory privé.
- **Labels.** Jeu homogène : `bug`, `enhancement`, `security`, `dependencies`, `documentation`, `question`, + un label par composant.
- **Milestones.** Milestone `BL3` par dépôt, avec barre de progression.
- **Cycle de vie.** Ouverture (formulaire) → triage (investigation citant le code) → correction ou fermeture justifiée. Une issue non reproductible ou déjà couverte est fermée en « not planned » avec explication.

---

## 4. Maintenance préventive — mises à jour

**Dependabot** est configuré sur les trois dépôts (PR hebdomadaires) :

| Dépôt | Écosystèmes | Particularités |
|---|---|---|
| `api_zen` | Maven, Docker, github-actions | Groupes `jjwt` et `jackson` (bumps solidaires) |
| `cesizen_web` | npm, github-actions | Groupes `react`/`next` ; `ignore` des majeures de react-day-picker (cassantes) |
| `cesizen_mobile` | pub, github-actions | — |

**Procédure.** Une PR Dependabot est mergée uniquement si la CI est verte. Une montée majeure cassante (ex : react-day-picker 9→10) est fermée et, si récurrente, neutralisée par une règle `ignore`. Les artefacts d'un même BOM sont groupés pour éviter les bumps partiels dangereux.

**Scans continus.** Trivy et Gitleaks à chaque push et chaque nuit ; OWASP Dependency Check (NVD) nocturne sur l'API. Toute CVE CRITICAL/HIGH corrigible fait échouer la CI.

---

## 5. Veille technologique

Sources ciblées : **spring.io/blog** et GitHub Releases (backend), **nextjs.org/blog** et changelog Vercel (front), **flutter.dev** et pub.dev (mobile), et pour la sécurité **OWASP**, **CVE Mitre** et la base d'avis GitHub. Le suivi passe par le Watch des dépôts critiques et par Dependabot, qui matérialise les nouvelles versions. → Trois fiches détaillées : [21-veille-technologique.md](21-veille-technologique.md).

---

## 6. Procédure de réponse à incident

1. **Détection** — alerte Dependabot, échec de scan, logs anormaux, ou signalement via issue.
2. **Isolation** — couper le service si nécessaire ; en cas de compromission de token, rotation du secret JWT (invalide tous les tokens).
3. **Analyse** — examiner `log_connexion` / `log_activite` pour délimiter l'impact.
4. **Correction** — patch → PR → CI verte → redéploiement.
5. **Communication** — notifier les utilisateurs si des données personnelles sont concernées.
6. **RGPD** — notification CNIL sous 72 h en cas de violation de données personnelles.

---

## 7. Types de maintenance et engagements

| Type | Périmètre | Engagement indicatif |
|---|---|---|
| Préventive | MàJ hebdomadaires (Dependabot), revue des logs, scans nocturnes | Continue |
| Corrective | Bugs introduits par les développements CESIZen | Critique < 24 h · Majeur < 72 h · Mineur : prochaine release |
| Évolutive | Nouvelles fonctionnalités via issue + PR + revue | Priorisation par milestone |

**Hors périmètre corrective** : pannes de l'hébergeur, failles de composants tiers non patchés en amont, indisponibilités de services externes (Vercel, Supabase).

---

## 8. Conclusion

La maintenance repose sur l'automatisation (Dependabot, scans, CI) et une traçabilité complète (issues, milestones, journaux d'audit). La supervision est opérationnelle, avec une amélioration identifiée — la sonde externe — pour couvrir l'indisponibilité totale.
