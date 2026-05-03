# Mail Notifier Service

Microservicio para **enviar notificaciones por correo electrónico** vía **SMTP (Gmail)**, diseñado con foco en **Clean Code**, **observabilidad** y **resiliencia** ante fallos de red y abuso (spam).

- **Stack:** Java 21, Spring Boot 4
- **Mail:** Spring Mail + Gmail SMTP
- **Resiliencia:** Resilience4j (Circuit Breaker + Rate Limiter)
- **Documentación:** OpenAPI / Swagger UI (arquitectura por interfaces)
- **Infraestructura:** Docker (Dockerfile + docker-compose)
- **Logs:** SLF4J

---

## Demo

> TODO: Agrega aquí un GIF mostrando el flujo desde Swagger.

```/dev/null/README-demo.gif#L1-1
[PLACEHOLDER: demo.gif]
```

---

## Arquitectura

Flujo de alto nivel (request → envío por Gmail):

```/dev/null/architecture.mmd#L1-10
%% TODO: Reemplaza este diagrama con el definitivo si cambian componentes
flowchart LR
  U[User / Client] --> C[Controller]
  C --> RL[Resilience4j Rate Limiter]
  RL --> CB[Resilience4j Circuit Breaker]
  CB --> G[Gmail SMTP]
```

Notas de diseño:
- La **documentación OpenAPI** está desacoplada del controlador usando una interfaz (`SendMailResource`).
- El **Rate Limiter** protege el endpoint contra spam: **2 requests/min**.
- El **Circuit Breaker** evita cascadas de fallos cuando el proveedor SMTP (o la red) está inestable.

---

## Características principales

- 🛡️ **Resiliencia**: Circuit Breaker + Rate Limiter (Resilience4j)
- 🧭 **API documentada**: Swagger UI / OpenAPI (contratos vía interfaces)
- 🐳 **Docker ready**: build y ejecución reproducible
- 🧾 **Validación**: DTOs con Bean Validation (`@NotBlank`, `@Email`)
- 🧵 **Trazabilidad**: logging con SLF4J
- ⚙️ **Configuración por entorno**: variables `.env` (no se versiona)

---

## Configuración (Variables de entorno)

El proyecto está preparado para cargar configuración desde un archivo `.env` (ver `docker-compose.yml`).

Crea un archivo `.env` en la raíz del repo con estas variables:

- `CORREO_PRUEBA`: correo que actúa como **remitente** y **destinatario** (actualmente el servicio envía al mismo `send.mail`).
- `PASSWORD`: contraseña de la cuenta Gmail.
- `MAIL_HOST`: host SMTP.
- `MAIL_PORT`: puerto SMTP.

Ejemplo recomendado:

```/dev/null/.env.example#L1-10
CORREO_PRUEBA=tu-cuenta@gmail.com
PASSWORD=tu-app-password
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
```

### Gmail: credenciales seguras

Para Gmail normalmente necesitas un **App Password** (no tu contraseña real), dependiendo de la configuración de seguridad de tu cuenta.

---

## Instalación y ejecución con Docker

### 1) Construir el JAR

Este `Dockerfile` asume que ya existe el JAR en `target/*.jar`.

```/dev/null/commands.sh#L1-3
./mvnw -DskipTests clean package
```

### 2) Build de la imagen

```/dev/null/commands.sh#L5-7
docker build -t mail-notifier-service:local .
```

### 3) Ejecutar con Docker Compose

```/dev/null/commands.sh#L9-12
docker compose up --build
```

La API quedará disponible en:
- `http://localhost:5002/api/v1`

---

## Endpoints

### Endpoint principal

- **Base path:** `/api/v1`
- **Enviar correo (POST):** `/api/v1/send/mail`

Payload esperado:

```/dev/null/request.json#L1-5
{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "message": "Hola, esto es una prueba"
}
```

Respuestas relevantes (alto nivel):
- `200 OK`: envío exitoso.
- `400 Bad Request`: validación fallida (`@NotBlank`, `@Email`).
- `429 Too Many Requests`: Rate Limiter (2/min).
- `503 Service Unavailable`: Circuit Breaker abierto.
- `500 Internal Server Error`: fallo inesperado al enviar (SMTP, etc.).

### Swagger / OpenAPI

- Swagger UI (por defecto springdoc): `http://localhost:5002/api/v1/swagger-ui.html`
- Atajo adicional: `http://localhost:5002/api/v1/documentation` (redirecciona a Swagger UI)

---

## Convenciones y buenas prácticas

- **Separación de responsabilidades**: controller → servicio → integración SMTP.
- **Contratos de API** en interfaces para evitar acoplar documentación a implementación.
- **Resiliencia por defecto**: limitación de tasa y circuit breaker para robustez.

---
