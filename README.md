# Notification Service

A lightweight microservice that handles asynchronous email notifications for the Aqtilink platform. Built with Spring Boot 3.4, it consumes notification events from RabbitMQ and sends notifications to users.

## Overview

The Notification Service is responsible for:
- Receiving notification events from RabbitMQ message queue
- Processing notification requests asynchronously
- Sending real email notifications via SendGrid
- Handling activity notifications (new activities, friend invitations)
- Managing notification delivery without blocking other services

## Architecture

### Technology Stack

- **Framework**: Spring Boot 3.4.12
- **Java Version**: 17
- **Message Queue**: RabbitMQ (AMQP)
- **Email Provider**: SendGrid API
- **Message Format**: JSON

### Project Structure

```
notification-service/
├── src/main/java/com/aqtilink/notification_service/
│   ├── NotificationServiceApplication.java   # Main application class
│   ├── config/                               # RabbitMQ configuration
│   ├── controller/                           # REST API endpoints
│   ├── dto/                                  # Data Transfer Objects
│   ├── listener/                             # Message queue listeners
│   └── service/                              # Business logic
└── src/main/resources/
    └── application.yml                       # Application configuration
```

## API Endpoints

### Send Notification (Direct HTTP)

#### Create Notification
```
POST /api/v1/notifications
Content-Type: application/json

Request Body:
{
  "email": "user@example.com",
  "subject": "New Activity Alert",
  "message": "Your friend has created a new activity!"
}

Response: 200 OK
```

## Message Queue System

### RabbitMQ Architecture

The service uses a direct exchange pattern for notification delivery:

```
Publisher → Exchange (notification-exchange) 
         → Queue (notification-queue) 
         → Listener (NotificationListener)
         → Service (NotificationService)
```

### Queue Configuration

- **Exchange**: `notification-exchange` (DirectExchange)
- **Queue**: `notification-queue`
- **Routing Key**: `notification.routingkey`
- **Message Format**: JSON (Jackson2JsonMessageConverter)

## Data Models

### NotificationEventDTO

```java
{
  "id": "UUID (optional)",
  "email": "string (required)",
  "subject": "string (required)",
  "message": "string (required)"
}
```

**Example Event (from Activity Service)**:
```json
{
  "email": "user@example.com",
  "subject": "Your friend created a new activity!",
  "message": "Your friend has created a new activity: Morning Run. Join them now!"
}
```

## Key Components

### NotificationListener
- Consumes messages from `notification-queue`
- Automatically deserializes JSON to NotificationEventDTO
- Processes incoming notification events
- Logs received notifications to console

### NotificationService
- Handles the actual notification sending logic via SendGrid API
- Sends real emails from `noreply@em5604.aqtilink.live`
- Logs email delivery status and response details
- Supports plain text email content

### RabbitMQConfig
- Configures the direct exchange and queue
- Sets up message converter for JSON serialization/deserialization
- Configures the RabbitListener container factory

## Configuration

### Environment Variables

```yaml
# RabbitMQ Configuration
SPRING_RABBITMQ_HOST: localhost
SPRING_RABBITMQ_PORT: 5672
SPRING_RABBITMQ_USERNAME: guest
SPRING_RABBITMQ_PASSWORD: guest

# SendGrid Configuration
SENDGRID_API_KEY: <your-sendgrid-api-key>

# Server
server.port: 8082
spring.application.name: notification-service

# Actuator
management.endpoints.web.exposure.include: health,info
```

### Application Properties

- **Port**: 8082
- **RabbitMQ Host**: localhost (configurable)
- **RabbitMQ Port**: 5672
- **Actuator Endpoints**: /health, /info

## Dependencies

### Core Framework
- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-actuator` - Monitoring and health checks

### Message Queue
- `spring-boot-starter-amqp` - RabbitMQ integration

### Email Provider
- `sendgrid-java` (v4.10.1) - SendGrid API client

### Development
- `spring-boot-devtools` - Live reload support
- `spring-boot-starter-test` - Testing framework

## Running the Service

### Prerequisites
- Java 17+
- RabbitMQ 3.12+
- SendGrid API Key (from [SendGrid Dashboard](https://app.sendgrid.com/settings/api_keys))
- Docker and Docker Compose (optional)

### Local Development

1. **Start RabbitMQ**:
   ```bash
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
   ```

2. **Set up SendGrid API Key**:
   ```bash
   export SENDGRID_API_KEY=your-api-key-here
   ```
   
   Alternatively, create a `sendgrid.env` file:
   ```bash
   SENDGRID_API_KEY=your-api-key-here
   ```

3. **Install dependencies**:
   ```bash
   ./mvnw clean install
   ```

4. **Run the service**:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or with environment file:
   ```bash
   e SENDGRID_API_KEY=your-api-key-here \
  -export $(cat sendgrid.env | xargs) && ./mvnw spring-boot:run
   ```

   Service will start at `http://localhost:8082`
   
   RabbitMQ Management UI: `http://localhost:15672` (guest/guest)

### Docker Deployment

```bash
docker build -t notification-service:latest .
docker run -d \
  --name notification-service \
  -p 8082:8082 \
  -e SPRING_RABBITMQ_HOST=rabbitmq \
  --link rabbitmq:rabbitmq \
  notification-service:latest
```

### Docker Compose

The service is typically run alongside other services via docker-compose:

```yaml
services:
  notification-service:
    build: ./notification-service
    container_name: notification-service
    ports:
      - "8082:8082"
    environment:
      SENDGRID_API_KEY: ${SENDGRID_API_KEY}
    env_file:
      - sendgrid.env
      SPRING_RABBITMQ_HOST: rabbitmq
    depends_on:
      - rabbitmq
```

## Message Flow Examples

### Activity Creation Notification

1. **Activity Service** publishes event to RabbitMQ:
   ```json
   {
     "email": "friend@example.com",
     "subject": "Your friend created a new activity!",
     "message": "Your friend has created a new activity: Morning Run. Join them now!"
   }
   ```
 via SendGrid API

4. User receives email at their inbox from `noreply@em5604.aqtilink.live`
2. **NotificationListener** receives the message from the queue

3. **NotificationService** processes and sends the email

### Friend Request Notification (Future Enhancement)

```json
{
  "email": "user@example.com",
  "subject": "New friend request",
  "message": "John Doe sent you a friend request"
}
```

## Integration Points

### Activity Service Integration
- **Event**: New activity created
- **Queue**: notification-queue
- HTML email templates
- SMS notifications via SendGrid or TwilioficationEventDTO with email, subject, message

### Future Integrations
- User Service for friend request notifications
- Email providers (SendGrid, AWS SES, etc.)
- SMS notifications
- Push notifications

## Error Handling

### Standard HTTP Status Codes
- `200 OK` - Notification sent successfully
- `400 Bad Request` - Invalid notification data
- `500 Internal Server Error` - Server-side error

### Queue Error Handling
- **Dead Letter Queue**: Can be configured for failed messages
- **Retry Policy**: Can be configured for message redelivery
- **Logging**: All processing is logged to console

## DSendGrid Configuration

The service uses SendGrid for email delivery. Current configuration:

- **From Address**: `noreply@em5604.aqtilink.live`
- **Content Type**: `text/plain`
- **API Key**: Configured via `SENDGRID_API_KEY` environment variable

To customize the sender email or enable HTML templates, modify the `NotificationService` class.

### Enabling HTML Email Templates

To use HTML email templates instead of plain text:

1. Update `NotificationService.send()` method:
   ```java
   Content content = new Content("text/html", dto.getMessage());
   ```

2. Format message with HTML in the DTO:
   ```java
   String htmlMessage = "<html><body><h1>Hello</h1><p>" + message + "</p></body></html>";
   email-provider:
     api-key: ${EMAIL_API_KEY}
   ```

### Adding New Event Types

1. Create new message listener for specific event type
2. Publish events from other services to appropriate routing key
3. Configure separate queues if needed

### Testing

Run tests with:
```bash
./mvnw test
```

## Performance Considerations

- **Asynchronous Processing**: All notifications are processed asynchronously via message queue
- **No Blocking**: Other services are not blocked waiting for notification delivery
- **Scalability**: Multiple instances can consume from the same queue for horizontal scaling
- **Reliability**: Messages are persisted in RabbitMQ until processed

## Monitoring

### Actuator Endpoints
- `/actuator/health` - Service health status
- `/actuator/info` - Service information

### RabbitMQ Management
- Access Management UI at `http://localhost:15672`
- Monitor queue lengths and message rates
- View consumer information

### Logs
Monitor service logs for notification processing:
```bash
docker logs notification-service
```

## References

- [Spring Boot AMQP Documentation](https://docs.spring.io/spring-boot/3.4.x/reference/messaging/amqp.html)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/3.4.x/reference/)

