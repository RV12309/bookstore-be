FROM openjdk:17-alpine AS builder

WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
RUN echo "listing build/libs"
RUN chmod +x ./build/libs
RUN ls -la build/libs

FROM openjdk:17-alpine
WORKDIR /app
RUN echo "copying jar"
COPY --from=builder /workspace/build/libs/bookstore.jar .
RUN echo "copying jar done"
RUN ls -la
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","bookstore.jar"]