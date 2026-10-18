# Discord webhook and bot

This is a Java 21 application built with Spring Boot. It serves the web client
and HTTP API from one server and connects to Discord using JDA.

## Configuration

Set these environment variables before running the application:

| Variable | Required | Default | Purpose |
| --- | --- | --- | --- |
| `DISCORD_BOT_TOKEN` | Yes | — | Discord bot token |
| `DISCORD_WEBHOOK_URL` | When sending a webhook | — | Discord incoming webhook URL |
| `PYTHON_SERVICE_URL` | When using chat | `http://localhost:8000` | URL of the Python chat service |

The bot requires the Discord `MESSAGE_CONTENT` and `GUILD_MESSAGES` gateway
intents to be enabled for the application in the Discord Developer Portal.

## Build and run

```powershell
.\gradlew.bat test
.\gradlew.bat bootRun
```

The server listens on port 8080 by default. Open `http://localhost:8080` to use
the bundled web client.

## HTTP API

- `POST /webhook/send` — send `{"message":"..."}` to the configured webhook.
- `GET /webhook/config` — report whether a webhook is configured (the URL is
  kept private).
- `GET /bot/messages` — return the bot's recent message history.
- `POST /chat` — forward `{"message":"...","newConversation":false}` to the
  Python chat service with the configured product and category lists.
