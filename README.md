# Crypto Money

## Overview

Crypto Money is a Java-based application designed to manage a crypto wallet.

## Prerequisites

- Java 21 or higher
- Spring Boot 3.5.0 or higher
- Maven 3.9.9 or higher
- OpenAPI(Swagger) 3.1.1 or higher
- Docker and Docker Compose
- Lombok
- Mapstruct

## Setup Instructions

### Clone the repository

```sh
git clone https://github.com/diegodourado555/cryptomoney.git
```

## Docker

1. Navigate to `cryptomoney/docker` directory
2. Run the following command `docker compose up`
3. Access the application at `http://localhost:8080`
4. Database should be available on port `5432`
5. The tables and dummy data will be created automatically(1-schema.sql, 2-data.sql)

## Swagger

Available at `http://localhost:8080/swagger-ui/index.html`