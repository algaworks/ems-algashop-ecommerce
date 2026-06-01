FROM eclipse-temurin:21-jre
ENV HOMEDIR=/app \
    JAR_NAME=algashop-ecommerce.jar
WORKDIR ${HOMEDIR}
ADD build/libs/$JAR_NAME $JAR_NAME
CMD java $JAVA_OPTS -jar $JAR_NAME