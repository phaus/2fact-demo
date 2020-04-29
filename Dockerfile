FROM openjdk:8-jre-alpine

RUN apk --no-cache add \
    ttf-dejavu \
    bash \
    unzip

WORKDIR /app

ARG APP_VERSION=2.5.10

COPY target/universal/2fact-demo-${APP_VERSION}.zip /app

RUN unzip /app/2fact-demo-${APP_VERSION}.zip

RUN rm /app/2fact-demo-${APP_VERSION}.zip

RUN chmod +x /app/2fact-demo-${APP_VERSION}/bin/2fact-demo

CMD /app/2fact-demo-${APP_VERSION}/bin/2fact-demo