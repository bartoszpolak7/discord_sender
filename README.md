# Discord webhook and bot

This is a Java 21 application built with Spring Boot. It serves the web client
and HTTP API from one server and connects to Discord using JDA.
The repository uses one of my older projects refactored, the intention being to learn CI/CD pipelines.

## Configuration

Set these environment variables before running the application:

| Variable              | Required               | Default | Purpose                      |
| --------------------- | ---------------------- | ------- | ---------------------------- |
| `DISCORD_BOT_TOKEN`   | Yes                    | —       | Discord bot token            |
| `DISCORD_WEBHOOK_URL` | When sending a webhook | —       | Discord incoming webhook URL |

The bot requires the Discord `MESSAGE_CONTENT` and `GUILD_MESSAGES` gateway
intents to be enabled for the application in the Discord Developer Portal.

## Build and run

**Run docker container**

### Option 1: Docker

```bash
docker compose up --build
```

### Option 2: Run locally

```bash
# Linux/macOS
./gradlew bootRun

# Windows (PowerShell)
.\gradlew.bat bootRun
```

The server listens on port 8080 by default. Open `http://localhost:8080` to use
the bundled web client.

## HTTP API

- `POST /webhook/send` — send `{"message":"..."}` to the configured webhook.
- `GET /webhook/config` — report whether a webhook is configured (the URL is
  kept private).
- `GET /bot/messages` — return the bot's recent message history.

```

```
