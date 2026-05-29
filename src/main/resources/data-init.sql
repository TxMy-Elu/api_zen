-- =============================================
-- DATA INIT - Données d'initialisation
-- INSERT IGNORE : ignore silencieusement si la ligne existe déjà
-- Permet de relancer l'app sans erreur sur une BDD déjà peuplée
-- =============================================

-- Roles
INSERT IGNORE INTO role (libelle) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- Utilisateurs (mot de passe: 'password' encodé en BCrypt)
INSERT IGNORE INTO user (nom, prenom, email, password, role_id) VALUES
  ('Dupont', 'Jean', 'admin@cesizen.fr', '$2a$10$dKOCtfFh/KjniYmErSmcP.yLPrAC8EqjxDEWvMUdPZE8edFL.aJya', 1),
  ('Durand', 'Marie', 'user@cesizen.fr', '$2a$10$dKOCtfFh/KjniYmErSmcP.yLPrAC8EqjxDEWvMUdPZE8edFL.aJya', 2),
  ('Martin', 'Paul', 'paul@cesizen.fr', '$2a$10$dKOCtfFh/KjniYmErSmcP.yLPrAC8EqjxDEWvMUdPZE8edFL.aJya', 2),
  ('Petit', 'Anne', 'anne@cesizen.fr', '$2a$10$dKOCtfFh/KjniYmErSmcP.yLPrAC8EqjxDEWvMUdPZE8edFL.aJya', 2),
  ('Bernard', 'Luc', 'luc@cesizen.fr', '$2a$10$dKOCtfFh/KjniYmErSmcP.yLPrAC8EqjxDEWvMUdPZE8edFL.aJya', 2);

-- Exercices de respiration
INSERT IGNORE INTO exercice_respiration (nom, duree_inspiration, duree_apnee, duree_expiration, duree_session, description) VALUES
  ('Respiration carrée', 4, 0, 4, 120, 'Cycle simple: inspirez 4s, retenez 0s, expirez 4s.'),
  ('Respiration profonde lente', 6, 2, 6, 300, 'Inspiration profonde, courte apnée, expiration longue.'),
  ('Cohérence cardiaque', 5, 0, 5, 300, 'Rythme 5/5 pour calmer le système nerveux.'),
  ('Box breathing', 4, 4, 4, 240, 'Respiration militaire pour réduire le stress.'),
  ('Respiration alternée', 4, 0, 4, 120, 'Technique yogique pour équilibrer les hémisphères.');

-- Participations (exercer)
INSERT IGNORE INTO exercer (id_utilisateur, id_exercice, completed_at) VALUES
  (1, 1, NOW()),
  (1, 2, NOW()),
  (1, 3, NOW()),
  (2, 2, NOW()),
  (2, 4, NOW()),
  (3, 1, NOW()),
  (3, 5, NOW()),
  (4, 3, NOW()),
  (5, 1, NOW());

-- Catégories
INSERT IGNORE INTO categorie (libelle, description) VALUES
  ('Tuto Vidéo', 'Tutoriels en format vidéo pour apprendre visuellement'),
  ('Tuto PDF', 'Guides téléchargeables en format PDF'),
  ('Cours', 'Cours complets et structurés sur différents sujets'),
  ('Article', 'Articles informatifs et éducatifs'),
  ('Infographie', 'Contenu visuel résumant des informations clés'),
  ('Podcast', 'Contenus audio à écouter');

-- Articles
INSERT IGNORE INTO article (titre, contenu, type_media, media_url, date_publication, est_publie, id_categorie) VALUES
  ('Introduction à la respiration carrée', 'Découvrez les bases de la respiration carrée en vidéo.', 'video', '/uploads/tuto_respiration_carree.mp4', NOW(), TRUE, 1),
  ('Guide complet de méditation', 'Téléchargez notre guide PDF pour débuter la méditation.', 'file', '/uploads/guide_meditation.pdf', NOW(), TRUE, 2),
  ('Cours: Gestion du stress', 'Un cours complet en 5 modules pour apprendre à gérer votre stress au quotidien.', 'text', NULL, NOW(), TRUE, 3),
  ('Les bienfaits de la pleine conscience', 'Article détaillé sur les avantages de la pleine conscience.', 'text', NULL, NOW(), TRUE, 4),
  ('Techniques de relaxation', 'Infographie présentant 10 techniques de relaxation rapide.', 'image', '/uploads/infographie_relaxation.png', NOW(), TRUE, 5),
  ('Podcast: Bien-être au travail', 'Écoutez notre podcast sur le bien-être en entreprise.', 'audio', '/uploads/podcast_bienetre.mp3', NOW(), TRUE, 6),
  ('Vidéo: Exercice de cohérence cardiaque', 'Tutoriel vidéo pour pratiquer la cohérence cardiaque.', 'video', '/uploads/coherence_cardiaque.mp4', NOW(), TRUE, 1),
  ('PDF: Journal de gratitude', 'Template PDF pour tenir un journal de gratitude quotidien.', 'file', '/uploads/journal_gratitude.pdf', NOW(), FALSE, 2),
  ('Cours: Introduction au yoga', 'Cours débutant pour découvrir les postures de base du yoga.', 'text', NULL, NOW(), TRUE, 3),
  ('Les 7 chakras expliqués', 'Article complet sur les 7 chakras et leur signification.', 'text', NULL, NOW(), FALSE, 4),
  ('Box Breathing pour débutants', 'Apprenez la technique de box breathing en 5 minutes.', 'text', NULL, NOW(), TRUE, 4),
  ('Méditation guidée - 10 min', 'Séance de méditation relaxante pour commencer votre journée.', 'audio', '/uploads/meditation_10min.mp3', NOW(), TRUE, 6);

-- Consultations d'articles
INSERT IGNORE INTO consulter (id_utilisateur, id_article, viewed_at) VALUES
  (1, 1, NOW()),
  (1, 2, NOW()),
  (1, 3, NOW()),
  (1, 4, NOW()),
  (2, 1, NOW()),
  (2, 4, NOW()),
  (2, 5, NOW()),
  (3, 1, NOW()),
  (3, 5, NOW()),
  (3, 6, NOW()),
  (4, 2, NOW()),
  (4, 7, NOW()),
  (5, 1, NOW()),
  (5, 3, NOW()),
  (5, 11, NOW()),
  (5, 12, NOW());
