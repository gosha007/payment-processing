FROM openjdk:20-jdk-slim
RUN addgroup dockergroup; adduser --ingroup dockergroup dockeruser
USER dockeruser
COPY build/libs/payment-processing-0.0.1.jar payment-processing.jar
ENTRYPOINT ["java","-jar","/payment-processing.jar"]