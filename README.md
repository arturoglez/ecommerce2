
# Ecommerce Application for AgileEngile TeamViewer Frontline Interview - README

This project is a Spring Boot application designed to simulate a simple e-commerce platform. It integrates with a PostgreSQL database to manage products, orders, and order items. The application provides a REST API with CRUD operations for these entities, defined using OpenAPI 3.0.

This application has been containerized using Docker for easy deployment and testing. The API is fully documented and accessible via Swagger UI. The project includes unit tests for the Product, Order, and OrderItem entities, which can be run using Maven.


## Authors

- Arturo González Corona


## Technologies Used

- Java 21 (JDK 21)
- Spring Boot (for building RESTful APIs)
- PostgreSQL (database)
- Docker (containerization)
- JUnit & Mockito (for testing)
- OpenAPI (for API documentation)


## Prerequisites

Before running this application, ensure that you have the following software installed on your machine:

- Docker and Docker Compose 
- Docker Desktop (recommended for managing containers and images)
- Maven


## Running the Application in Docker

This project is set up to run in Docker containers. It includes both a Dockerfile for building the application container and a docker-compose.yml file to manage the application and the PostgreSQL database.

Follow these steps to run the application:

1. Clone the Repository (or Copy the Files) https://github.com/arturoglez/ecommerce2
2. Build the Docker Containers
Run the following command in the root of your project to build the Docker containers:

`docker-compose up --build`

This command will:
- Build the application Docker image using the Dockerfile.
- Set up the PostgreSQL container from the docker-compose.yml file.
- Automatically start the application and the database in separate containers.

3. Accessing the Application
Once the containers are up and running, you can access the application via the following:
### Swagger UI (for API documentation):
Open a browser and go to: http://localhost:8080/swagger-ui/index.html

This will allow you to interact with the API and perform CRUD operations on products, orders, and order items.

### Application API:

The API is available at

http://localhost:8080/api/products

http://localhost:8080/api/orders

http://localhost:8080/api/order-items

for respective CRUD operations.

### Running Tests
The application includes unit tests for Product, Order, and OrderItem entities. These tests are written using JUnit and Mockito. You can run them either inside or outside Docker.

1. Running Tests Using Maven (Inside Docker)

To run the tests inside the Docker container, follow these steps:

- Ensure the containers are up and running (`docker-compose up --build`).
Run the tests with Maven:

`docker-compose exec ecommerce-app ./mvnw test`

This will execute the unit tests inside the container.

2. Running Tests Using Maven (Outside Docker)
   If you prefer to run the tests outside Docker (locally on your machine), simply run:

`./mvnw test`
or
`mvn test` insider the project

Ensure you have Maven installed on your local machine.

### Stopping the Containers
To stop the application and database containers, run the following command:

`docker-compose down`

This will stop and remove the containers without affecting the images or volumes.

## Dockerfile and docker-compose.yml

The project contains the following key files for Docker:

1. `Dockerfile`:

Defines how to build the application container, including setting up the Java environment, building the Spring Boot application, and running it.

2. `docker-compose.yml`:

Manages the ecommerce-app container (for your Spring Boot application) and the ecommerce-db container (for PostgreSQL). It ensures both containers are linked and can communicate with each other.

## API Endpoints

1. Product API
- GET `/api/products`: Retrieve a list of all products.
- GET `/api/products/{id}`: Retrieve a product by ID.
- POST `/api/products`: Create a new product.
- PUT `/api/products/{id}`: Update an existing product.
- DELETE `/api/products/{id}`: Delete a product by ID.
2. Order API
- GET `/api/orders`: Retrieve a list of all orders.
- GET `/api/orders/{id}`: Retrieve an order by ID.
- POST `/api/orders`: Create a new order.
- PUT `/api/orders/{id}`: Update an existing order.
- DELETE `/api/orders/{id}`: Delete an order by ID.
3. OrderItem API
- GET `/api/order-items`: Retrieve a list of all order items.
- GET `/api/order-items/{id}`: Retrieve an order item by ID.
- POST `/api/order-items`: Create a new order item.
- PUT `/api/order-items/{id}` Update an existing order item.
- DELETE `/api/order-items/{id`: Delete an order item by ID.

For further reference please check JSON formatting and pagination in:
http://localhost:8080/swagger-ui/index.html
## Conclusion
You have successfully set up and run the application in Docker, with all necessary dependencies and configurations in place. The application is now ready for testing and use. For any additional questions or issues, please reach out to me arturoglez.corona@gmail.com