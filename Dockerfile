# Utiliser l'image officielle Maven avec OpenJDK 8 comme image parent
FROM maven:3.8-openjdk-8

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier tous les fichiers du projet (y compris le dossier parent contenant logger)
COPY . /app

# Créer un script shell pour installer le logger et exécuter l'application
RUN echo '#!/bin/sh' > start.sh && \
    echo 'cd ./logger && mvn clean install' >> start.sh && \
    echo 'cd ../security-AUTH' >> start.sh && \
    echo 'mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Djava.security.auth.login.config=src/main/resources/jaas.conf -Djava.security.krb5.conf=src/main/resources/krb5.conf -Dsun.security.krb5.debug=true -Djava.security.debug=logincontext"' >> start.sh && \
    chmod +x start.sh

# Exposer le port sur lequel l'application s'exécutera (à ajuster si nécessaire)
EXPOSE 8081

# Définir le point d'entrée sur le script shell
ENTRYPOINT ["/bin/sh", "/app/start.sh"]