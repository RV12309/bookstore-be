
FROM openjdk:17-alpine AS builder
WORKDIR /workspace
COPY . .
FROM openjdk:17-alpine
WORKDIR /app
RUN echo "copying jar"
COPY --from=builder /workspace/postmaster/build/libs/postmaster-0.0.1.jar postmaster.jar
RUN echo "copying jar done"
RUN ls -la
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","postmaster.jar"]