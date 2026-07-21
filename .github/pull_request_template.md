<!--
  Merci pour cette contribution 🙌
  Remplis les sections ci-dessous, puis supprime celles qui ne s'appliquent pas.
  Une PR sans description claire sera renvoyée en l'état.
-->

## Objectif

<!-- Que fait cette PR, et surtout : pourquoi ? -->

Closes #

## Type de changement

- [ ] 🐛 Correction de bug
- [ ] ✨ Nouvelle fonctionnalité
- [ ] 🔒 Sécurité
- [ ] ♻️ Refactorisation (sans changement de comportement)
- [ ] ⬆️ Montée de version de dépendance
- [ ] 📝 Documentation
- [ ] ⚙️ CI / CD / outillage

## Détail des changements

<!--
  Liste ce qui change, fichier ou module par module.
  Explique les choix non évidents : c'est ce qui fait gagner du temps en revue.
-->

-

## Vérifications

- [ ] `mvn clean verify` passe en local
- [ ] Des tests couvrent le changement (ou justification ci-dessous si non)
- [ ] La CI est verte (build, tests, Gitleaks, Trivy, SonarCloud)
- [ ] Aucun secret, mot de passe, token ou URL interne dans le diff
- [ ] Commits au format Conventional Commits (`feat:`, `fix:`, `chore:`…)

<!-- Si des tests manquent, explique pourquoi ici : -->

## Impact sécurité

<!--
  À remplir dès que la PR touche à l'authentification, aux autorisations,
  aux données personnelles, aux dépendances ou à la configuration.
  Sinon : « Aucun ».
-->

- **Surface d'exposition modifiée ?**
- **Données personnelles concernées (RGPD) ?**
- **Nouvelle dépendance introduite ?**

> ⚠️ Une faille de sécurité ne se signale **jamais** dans une PR ou une issue
> publique, mais via un *security advisory* privé.

## Impact base de données

- [ ] Aucun
- [ ] Migration / modification de schéma <!-- décris-la et précise si elle est réversible -->

## Déploiement et retour arrière

<!--
  Variable d'environnement à ajouter ? Secret à créer ? Ordre de déploiement ?
  Comment revenir en arrière si ça se passe mal ?
-->

- **Prérequis au déploiement :**
- **Procédure de rollback :**

## Points d'attention pour la revue

<!-- Ce sur quoi tu veux un avis, ou ce que tu assumes comme dette technique. -->

-
