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

## Overrides

You can override the following environment settings:

| Name           | Purpose                        | Default |
| ---------------|--------------------------------|------|
| `SECRET`       | Cookie encryption              | see [application.conf](https://github.com/phaus/2fact-demo/blob/master/conf/application.conf#L11) |
| `PORT`         | HTTP Port                      | 9000 |
| `DB_DRIVER`    | The Database Driver            | "org.h2.Driver" |
| `DATABASE_URL` | The Access URL of the database | jdbc:h2:mem:play" |
| `DB_USER`      | The username of the database user | "sa" |
| `DB_PASS`      | The password of the database user | empty |

If you want to use e.g. Postgres as a Database you would set:

* `DB_DRIVER` to "org.postgresql.Driver"
* `DATABASE_URL` to e.g. "jdbc:postgresql://database.example.com/playdb"
* `DB_USER` to "pg_user"
* `DB_PASS` to "pg_pass"
