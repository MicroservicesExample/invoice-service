# multistage build with layerd jar, like layerd image with app user

#satge1

FROM eclipse-temurin:17 AS builder
WORKDIR workspace
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} invoice-service.jar
RUN java -Djarmode=layertools -jar invoice-service.jar extract

#stage2

FROM eclipse-temurin:17
RUN mkdir /home/app
RUN useradd app
RUN chown app /home/app
USER app
WORKDIR workspace
COPY --from=builder workspace/dependencies/ ./
COPY --from=builder workspace/spring-boot-loader/ ./
COPY --from=builder workspace/snapshot-dependencies/ ./
COPY --from=builder workspace/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]