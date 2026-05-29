# ── Étape 1 : Build ──────────────────────────────────────────────────────────
# On utilise une image Maven avec JDK 21 pour compiler le projet
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# On copie d'abord uniquement le pom.xml pour profiter du cache Docker :
# si le pom ne change pas, Maven ne re-télécharge pas toutes les dépendances
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline -B

# Ensuite on copie le code source et on compile
# -DskipTests : on ne lance pas les tests ici (la CI s'en charge séparément)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Étape 2 : Runtime ────────────────────────────────────────────────────────
# Image finale beaucoup plus légère : juste le JRE (pas le compilateur)
FROM eclipse-temurin:21-jre

WORKDIR /app

# Sécurité : on crée un utilisateur non-root dédié
# Un conteneur qui tourne en root = risque majeur si compromis
RUN groupadd --system cesizen && useradd --system --gid cesizen cesizen

# On copie uniquement le JAR compilé depuis l'étape builder
COPY --from=builder /app/target/*.jar app.jar

# On donne la propriété du fichier à notre user non-root
RUN chown cesizen:cesizen app.jar

# On bascule sur le user non-root
USER cesizen

# Port exposé par Spring Boot
EXPOSE 8080

# Lancement de l'application
# -Djava.security.egd : optimisation du démarrage (entropie aléatoire)
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
