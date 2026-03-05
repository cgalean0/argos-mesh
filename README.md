
# 🛡️ Argos-Mesh: Distributed Defense System

**Argos-Mesh** es un ecosistema de microservicios de alto rendimiento diseñado para la venta de productos con una capa de seguridad activa contra ataques de denegación de servicio (DoS) y fuerza bruta. Utiliza una arquitectura orientada a eventos (**EDA**) para desacoplar la lógica de negocio de la mitigación de amenazas.

## 🚀 Key Features

-   **Reactive Security**: Detección automática de anomalías de tráfico mediante el algoritmo de **Sliding Window**.
    
-   **High Concurrency**: Implementado con **Java 21 Virtual Threads** para manejar miles de peticiones simultáneas con baja sobrecarga de CPU.
    
-   **Asynchronous Communication**: Integración robusta mediante **RabbitMQ** para la propagación de eventos de venta y alertas de seguridad.
    
-   **Distributed Blocking**: Uso de **Redis** como L2 Cache para el baneo atómico de IPs, protegiendo la base de datos relacional (**PostgreSQL**).
    
-   **Full Observability**: Microservicio dedicado de notificaciones para el monitoreo de alertas críticas.
    

## 🏗️ Architecture

El sistema se divide en tres componentes núcleo:

1.  **`argos-orders`**: Punto de entrada de la API. Valida existencias en DB y publica eventos de venta. Actúa como ejecutor del bloqueo consultando Redis en tiempo real ($O(1)$).
    
2.  **`argos-sentinel`**: El cerebro analítico. Consume eventos de RabbitMQ, analiza patrones de tráfico y gestiona la lista negra en Redis.
    
3.  **`argos-notify`**: El canal de salida. Informa sobre actividades sospechosas y mitigaciones exitosas.
    

## 🛠️ Tech Stack

-   **Runtime**: Java 21 (Spring Boot 4.0.3)
    
-   **Messaging**: RabbitMQ (Topic Exchange)
    
-   **Storage**: PostgreSQL (Relational) & Redis (In-Memory Key-Value)
    
-   **Orchestration**: Docker & Docker Compose
    
-   **Testing**: Locust (Load Testing) & JUnit 5
    

## 📈 Performance & Stress Testing

El sistema fue sometido a pruebas de estrés utilizando **Locust**, simulando ataques de bots desde una misma IP.

-   **Throughput**: ~300 Requests Per Second (RPS).
    
-   **Efficiency**: Bloqueo del **99.7%** del tráfico malicioso tras superar el umbral de 50 peticiones/10s.
    
-   **Latency**: Promedio de respuesta de **3.8ms** durante el estado de bloqueo.
    

> **Nota**: Gracias al chequeo preventivo en Redis, el sistema evita el "Database Exhaustion", rechazando el tráfico antes de que llegue a la capa de persistencia de PostgreSQL.

## 📦 How to Run

1.  Clonar el repositorio.
    
2.  Asegurarse de tener Docker instalado.
    
3.  Ejecutar la orquestación completa:
    
    Bash
    
    ```bash
    docker-compose up --build
    
    ```
    
4.  Acceder a la documentación de la API vía Swagger: `http://localhost:8080/swagger-ui.html`
    

----------

## 📖 Detailed Documentation

Para una inmersión profunda en las decisiones de diseño y diagramas técnicos, visita:

👉 [https://cgalean0-argos-mesh.mintlify.app/](https://cgalean0-argos-mesh.mintlify.app/introduction)
