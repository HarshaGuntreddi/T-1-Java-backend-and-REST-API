# Kaiburr Assessment — Task 1: Java backend and REST API

A clean, **human-written** Spring Boot 3 application that exposes a REST API to create, search, delete and **run** “task” objects whose `command` is executed on the host (locally for Task‑1). Data is persisted in **MongoDB**.

> The project is self-contained and production‑ready for Task‑1. Task‑2 (Kubernetes) can be layered on top by swapping the `LocalCommandRunner` with a `KubernetesPodRunner` (stub included in comments).

---

## What you get

- **Endpoints**
  - `GET /api/tasks` → all tasks
  - `GET /api/tasks/{id}` → one task (404 if missing)
  - `GET /api/tasks/search?q=<substring>` → case‑insensitive partial name search (404 if none)
  - `PUT /api/tasks` → upsert a task (validates `command`)
  - `DELETE /api/tasks/{id}` → delete by id
  - `PUT /api/tasks/{id}/execute` → run the command (optionally override with request body), append a `taskExecution`

- **Safety first**
  - Commands are validated with **allow‑list + deny‑list + metacharacter checks**.
  - On Windows we run via `cmd /c`, on Linux/macOS via `bash -lc`. We **reject** dangerous tokens (e.g., `rm`, `sudo`, redirections, pipes, `&&`, `|`, etc.).
  - Stdout+stderr are captured and stored in `taskExecutions[].output` with timestamps.

- **MongoDB**
  - Document model with nested `TaskExecution` list.
  - Connection string is read from `MONGODB_URI` (or `spring.data.mongodb.uri`).

- **DX**
  - Clean services, controller advice (error JSONs), and sensible logging.
  - Minimal tests for validator and service behavior.
  - Postman collection and ready‑to‑copy curl snippets.

---

## Quickstart

### 0) Prerequisites
- Java 17+
- Maven 3.9+
- A running MongoDB (local or cloud). For local dev:
  ```bash
  docker run -d --name mongo -p 27017:27017 mongo:7
  ```

### 1) Configure env
Create a `.env` or export an env var:
```bash
export MONGODB_URI="mongodb://localhost:27017/kaiburr_tasks"
```
Spring also reads `spring.data.mongodb.uri`—use whichever you prefer.

### 2) Build & run
```bash
./mvnw spring-boot:run
# or
mvn clean package && java -jar target/tasks-api-0.0.1.jar
```

### 3) Try the API (curl)
Create a task:
```bash
curl -sS -X PUT http://localhost:8080/api/tasks   -H "Content-Type: application/json"   -d '{
    "id":"t-001",
    "name":"Print Hello",
    "owner":"Harsha Vardhan Guntreddi",
    "command":"echo Hello Kaiburr!"
  }' | jq
```

List all:
```bash
curl -sS http://localhost:8080/api/tasks | jq
```

Get by id:
```bash
curl -sS http://localhost:8080/api/tasks/t-001 | jq
```

Search by name:
```bash
curl -sS "http://localhost:8080/api/tasks/search?q=hello" | jq
```

Execute:
```bash
curl -sS -X PUT http://localhost:8080/api/tasks/t-001/execute | jq
# Optional override (validated as well):
curl -sS -X PUT http://localhost:8080/api/tasks/t-001/execute   -H "Content-Type: text/plain"   --data 'echo Hello again!' | jq
```

Delete:
```bash
curl -sS -X DELETE http://localhost:8080/api/tasks/t-001
```

### 4) Postman
Import `docs/postman/Kaiburr-Task1.postman_collection.json` and hit **Send**.

---

## Screenshots (you must add these!)

The assessment requires screenshots with **current date/time and your name** visible. Place them in `docs/screenshots/` and embed them below. Examples you should include (replace with your own):

- `mvn spring-boot:run` with system clock and your name visible.
- `curl`/Postman calls for: PUT task, GET all, GET by id, GET search, PUT execute (with output), DELETE.
- A MongoDB view showing the stored document with `taskExecutions` appended.

> Paste images in this README as Markdown: `![desc](docs/screenshots/<file>.png)`.

---

## Design notes

- **Controller ↔ Service ↔ Repository** separation (thin controller, all logic in service).
- **Validation**: carefully rejects shell metacharacters and a list of risky commands; allow list keeps things predictable. This is **not** a security product, but it’s safe and transparent for Task‑1.
- **Cross‑platform** execution: Windows uses `cmd.exe /c`, Unix uses `bash -lc`. We document this so reviewers can reproduce easily.
- **Extensibility**: For Task‑2 swap `LocalCommandRunner` with a `KubernetesPodRunner` that starts a BusyBox pod and streams logs to `TaskExecution`.

---

## Configuration

`src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/kaiburr_tasks}

logging:
  level:
    root: INFO
    com.kaiburr.tasks: DEBUG
```

---

## Project structure

```
src
 ├─ main
 │   ├─ java/com/kaiburr/tasks
 │   │   ├─ TasksApiApplication.java
 │   │   ├─ api/TaskController.java
 │   │   ├─ config/ExceptionAdvice.java
 │   │   ├─ core/CommandValidationException.java
 │   │   ├─ core/CommandValidator.java
 │   │   ├─ core/LocalCommandRunner.java
 │   │   ├─ model/Task.java
 │   │   ├─ model/TaskExecution.java
 │   │   ├─ repo/TaskRepository.java
 │   │   └─ service/TaskService.java
 │   └─ resources/application.yml
 └─ test/java/com/kaiburr/tasks
     ├─ CommandValidatorTest.java
     └─ TaskServiceTest.java
```

---

## License
MIT — do whatever you want; attribution appreciated.
