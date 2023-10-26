FROM eclipse-temurin:17
WORKDIR workspace
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} invoice-service.jar
ENTRYPOINT ["java" "-jar" "invoice-service.jar"]