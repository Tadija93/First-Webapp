# Shopify Product Synchronization App

## Description

This Spring Boot application facilitates the synchronization of product data between a Product Information Management (PIM) system and a Shopify store. It automates the process of updating or creating products in Shopify based on data from the PIM, ensuring consistency and reducing manual effort.

## Features

* **Product Synchronization:** Fetches product data from a PIM system and updates or creates corresponding products in a Shopify store.
* **Shopify API Integration:** Uses the Shopify Admin API to interact with Shopify product data.
* **Configurable:** Shopify and PIM system credentials (API URLs, access tokens) are configurable.
* **Error Handling:** Robust error handling to manage issues like product not found errors and API exceptions.
* **Logging:** Detailed logging for monitoring and debugging.

## Technologies Used

* Java 17
* Spring Boot
* Spring Web (RestTemplate)
* Jackson (for JSON processing)
* JUnit 5 (for testing)
* Mockito (for mocking in unit tests)

## Setup and Installation

### 1. Prerequisites

* Java 17 or later
* Maven (for building the project)
* Shopify API access credentials (API key, access token)
* PIM system access details (API URL, credentials)

### 2. Configuration

* Copy the `application.properties` or `application.yml` file and rename it to `application-local.properties` or `application-local.yml`.
* Update the configuration file with your Shopify and PIM system details:
  
```bash
shopify.store.url=https://your-store.myshopify.com
shopify.access.token=YOUR_SHOPIFY_ACCESS_TOKEN
pim.store.url=https://your-pim-api.com
pim.access.token=YOUR_PIM_API_TOKEN
```

* Alternatively, you can set these properties as environment variables.

⚠️ **Important:** Never commit real access tokens or secrets to version control. Use environment variables or a secret manager in production environments.

### 3. Building the Application

Use Maven to build the application:

```bash
mvn clean install
```

### 4. Running the Application

Run the application using the generated JAR file:

```bash
java -jar target/shopify-api-<version>.jar
```

## Usage

* You can test it locally using `curl` or a tool like Postman:

```bash
curl http://localhost:8081/shopify/products
curl http://localhost:8081/shopify/receiver/products
```

### Sample Response

```json
{
  "status": "success",
  "message": "Products synchronized. Created: 0, Updated: 16"
}
```

## Error Handling

The application handles common error scenarios:

* `ProductNotFoundException`: Thrown when a product is not found in Shopify.
* `ShopifyApiException`: Thrown when there's an error communicating with the Shopify API.
* `PimApiException`: Thrown when fetching data from the PIM system fails.

All exceptions are logged with meaningful messages to help with troubleshooting.

## Monitoring

* Logging is enabled to the console by default.
* You can configure file-based logging or external logging tools (e.g., Logstash, ELK stack) in `application.properties`.

Example:

```properties
logging.level.root=INFO
logging.file.name=logs/sync-app.log
```

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch: `git checkout -b feature/your-feature`.
3. Commit your changes: `git commit -m 'Add your feature'`.
4. Push to the branch: `git push origin feature/your-feature`.
5. Open a pull request.

Please follow the existing coding style and add tests where applicable.

## Contact

For support or questions, feel free to reach out:

**Author:** Aleksandar Tadic  
**GitHub:** [github.com/Tadija93](https://github.com/Tadija93)  
**Email:** aleksandartadic93@yahoo.com
