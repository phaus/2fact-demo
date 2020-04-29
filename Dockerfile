FROM openjdk:8-jre-alpine

RUN apk --no-cache add \
    ttf-dejavu \
    bash \
    unzip

WORKDIR /app

ARG APP_VERSION=2.5.10

COPY target/universal/2fact-demo-$APP_VERSION.zip /app

RUN echo $APP_VERSION > /app/VERSION

RUN unzip /app/2fact-demo-$APP_VERSION.zip -d /app && \
    rm /app/2fact-demo-$APP_VERSION.zip

RUN mv /app/2fact-demo-$APP_VERSION /app/2fact-demo && \
    chmod +x /app/2fact-demo/bin/2fact-demo

CMD /app/2fact-demo/bin/2fact-demo