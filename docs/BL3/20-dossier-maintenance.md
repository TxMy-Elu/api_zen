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

Sources ciblées : **spring.io/blog** et GitHub Releases (backend), **nextjs.org/blog** et changelog Vercel (front), **flutter.dev** et pub.dev (mobile), et pour la sécurité **OWASP**, **CVE Mitre** et la base d'avis GitHub. Le suivi passe par le Watch des dépôts critiques et par Dependabot, qui matérialise les nouvelles versions.

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

### 7.1 Maintenance préventive

Anticiper les défaillances avant qu'elles ne surviennent. **Incluse** dans le forfait de maintenance.

- Mises à jour hebdomadaires des dépendances (Dependabot) sur les trois dépôts.
- Scans de sécurité nocturnes (Trivy, Gitleaks, OWASP Dependency Check).
- Revue mensuelle des journaux de connexion à la recherche d'anomalies.
- Vérification de la disponibilité via la sonde externe.
- Audit SonarCloud trimestriel.

### 7.2 Maintenance corrective

Correction des anomalies constatées.

| Criticité | Définition | Délai de prise en charge (SLA) | Délai de résolution visé |
|---|---|---|---|
| Bloquante | Service indisponible ou faille de sécurité exploitable | 4 heures ouvrées | 24 heures |
| Majeure | Fonctionnalité inutilisable, sans contournement | 1 jour ouvré | 72 heures |
| Mineure | Anomalie avec contournement possible | 3 jours ouvrés | Prochaine version |
| Cosmétique | Défaut d'affichage sans impact fonctionnel | — | Selon disponibilité |

**Dans le périmètre** : bugs introduits par les développements réalisés sur CESIZen.

**Hors périmètre** : pannes de l'hébergeur, failles de composants tiers non encore corrigées en amont, indisponibilités de services externes (Vercel, Supabase), et toute anomalie causée par une modification effectuée par le client.

### 7.3 Maintenance évolutive

Ajout de fonctionnalités ou adaptation à un nouveau besoin. **Facturée au temps passé**, hors forfait.

| Élément | Valeur indicative |
|---|---|
| Tarif journalier moyen (TJM) — développeur | 450 € HT / jour |
| Tarif journalier moyen (TJM) — chef de projet | 600 € HT / jour |
| Unité de facturation minimale | 0,5 jour |
| Forfait de maintenance préventive + corrective | 350 € HT / mois |

**Mode de contractualisation.**

1. Le besoin est formalisé par une **issue GitHub** (modèle « Fonctionnalité »), avec ses critères d'acceptation.
2. Un **chiffrage** en jours-homme est produit et soumis au client.
3. Après validation écrite (bon de commande), la tâche est affectée à une **milestone**.
4. La livraison suit le processus standard : branche dédiée → pull request → CI verte → revue → merge → déploiement.
5. La recette est prononcée par le client sur la base des critères d'acceptation de l'issue.

Le contrat de maintenance est conclu pour une durée de **12 mois**, reconductible tacitement, résiliable avec un préavis de 2 mois. Les évolutions sont priorisées conjointement lors d'un **comité de pilotage trimestriel**.

> *Les montants ci-dessus sont donnés à titre d'hypothèse pédagogique et n'engagent aucune partie.*

---

## 8. Conclusion

La maintenance repose sur l'automatisation (Dependabot, scans, CI) et une traçabilité complète (issues, milestones, journaux d'audit). La supervision est opérationnelle, avec une amélioration identifiée — la sonde externe — pour couvrir l'indisponibilité totale.
