# Task 1 — Java Backend & REST API

Simple Spring Boot 3 service that manages **Task** documents in MongoDB and can **execute a safe shell command** for a task. Designed to be straightforward to run and review.

---

## Requirements
- Java 17+
- Maven 3.9+
- MongoDB (local or cloud). Quick local run:
  ```bash
  docker run -d --name mongo -p 27017:27017 mongo:7
