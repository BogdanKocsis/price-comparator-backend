# Price Comparator Backend Application

This Java Spring Boot application compares grocery product prices across different supermarket chains, identifies the best discounts, recommends product substitutes, and allows users to set custom price alerts.

## Project Structure

```
src/
├── main/
│   ├── java/com/example/pricecomparator/
│   │   ├── controller/         # REST Controllers
│   │   ├── service/            # Business logic services
│   │   ├── model/              # DTOs and entity classes
│   │   └── repository/         # CSV data repository
│   └── data/              # CSV files
└── test/
    └── java/com/example/pricecomparator/ # Unit and integration tests
```

## Build & Run

1. Prerequisites:
* Java 17+
* Maven 
* IDE (e.g., IntelliJ IDEA)

2. Commands

* Clone the repository : 'https://github.com/BogdanKocsis/price-comparator-backend.git' AND 'cd pricecomparator'
* Compile and build the project with Maven: 'mvn clean install'
* Run the app: Open the PriceComparatorApplication.java
* The app will run on: http://localhost:8080
* Test all endpoints using Postman

## Features

- Optimize shopping lists for best prices
- Track current discounts and special offers
- View price history trends
- Get substitute product recommendations
- Set price alerts for specific products

## Assumptions & Simplifications

* Data is loaded from local .csv files (no external database).
* All endpoints are public (no authentication).
* CSV files must follow the naming convention: store_YYYY-MM-DD.csv/store_discounts_YYYY-MM-DD.csv.
* Discounts are considered active if the current date falls between fromDate and toDate.
* Data is loaded from local .csv files (no external database).
* Date-driven operations: most features (e.g., best discounts, new discounts, basket optimization) use data files corresponding to the date query parameter. For example, /best-discounts?date=2025-05-08 reads from lidl_2025-05-08.csv, etc.
* Simplified error handling: missing or malformed CSV files will throw an exception, no fallback data source is provided.
* In-memory storage for price alerts: alerts are stored in a thread-safe list and are lost when the application restarts.

## API Usage & Examples

### 1. Optimize Shopping List
```http
POST http://localhost:8080/api/prices/optimize-basket?date=2025-05-08
Content-Type: application/json

[
  "Lapte Zuzu",
  "Rosii Cherry"
]
```
### 2. Top Active Discounts
```http
GET http://localhost:8080/api/prices/best-discounts?limit=5&date=2025-05-08
```
### 3. New Discounts (last 24h)
```http
GET http://localhost:8080/api/prices/new-discounts?date=2025-05-08
```
### 4. Price History(optional filter by category and/or store name)
```http
GET http://localhost:8080/api/prices/price-history?productName=iaurt grecesc&category=lactate&store=Lidl
```
### 5. Product Recommendations (value per unit)
```http
GET http://localhost:8080/api/prices/recommendations?productName=cafea măcinată&date=2025-05-08
```
### 6. Custom Price Alerts
```http
POST http://localhost:8080/api/alerts
Content-Type: application/json

{
  "productName": "lapte zuzu",
  "targetPrice": 11.00
}
```

## Testing

Automated tests cover:
- Services: discount retrieval, basket optimization, price alerts, recommendations, price history
- Controllers: all /api/prices/* and /api/alerts endpoints

To run all tests:

```bash
mvn test
```

## Author

**Kocsis Bogdan**  
📧 bogdankocsis@gmail.com  
🔗 [GitHub](https://github.com/BogdanKocsis)
