# Chat Application

Full-stack real-time chat application built with Spring Boot and React.

## Features

* Real-time messaging (WebSockets)
* Room-based chat system
* Optional password-protected rooms
* Secure user authentication for login and registration

## Tech Stack

* Backend: Spring Boot, WebSocket (STOMP), JPA
* Frontend: React, TypeScript, Vite

## Running locally

### Backend

```
cd chat-backend
mvn spring-boot:run
```

### Frontend

```
cd chat-frontend
npm install
npm run dev
```

## Build for production

### Backend

```
cd chat-backend
mvn clean package
```

### Frontend

```
cd chat-frontend
npm run build
```