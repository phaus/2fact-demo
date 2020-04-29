# 2fact Demo

This is a demo application for 2 factor authentication
The Demo App currently covers normal registration/login and 2 Factor Authentication with Google Auth & Yubikey

## Building

This file will be packaged with your application, when using:

```bash
sbt update
sbt dist
```

After that, you can use `docker build -t foo/2fact-demo .` to create an Docker Image.

## Running

As default, the Application binds onto Port 9000. This can be change with setting the environment variable `PORT` (heroku pattern).
