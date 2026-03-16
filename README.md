
# Coupon Management API

A Spring Boot REST API to manage and apply different types of coupons for an e-commerce platform.

## Features

- **Cart-wise coupons**: Discount on the entire cart when cart value exceeds a threshold.
- **Product-wise coupons**: Discount on specific products.
- **Buy X Get Y (BxGy) coupons**: Buy certain products, get others for free.
- **CRUD operations**: Create, retrieve, update, delete, and apply coupons to a shopping cart.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- REST APIs

## Project Structure

```
coupon-management-api/
├── src/
│   ├── main/
│   │   ├── java/com/monkcommerce/coupons/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── strategy/
│   │   │   └── CouponApplication.java
│   └── resources/
│       └── application.properties
├── pom.xml
└── README.md
```

## Coupon Types

### 1. Cart-wise Coupons

- **Condition:** `cart_total > threshold`
- **Discount:** `cart_total * discountPercentage`
- **Example:** 10% off on carts above 1000

### 2. Product-wise Coupons

- **Discount applies only to a specific product**
- **Example:** 20% off on Product ID 1

### 3. Buy X Get Y (BxGy)

- **Buy**: Certain products (e.g., [X, Y, Z])
- **Get**: Other products for free (e.g., [A, B])
- **Repetition limit**: Defines how many times coupon can be applied

## Database

- Uses MySQL
- Coupon details stored in the `coupons` table
- Fields: `id`, `name`, `type`, `description`, `active`, `expiration_date`, `details_json`
- `details_json` stores coupon rules dynamically

**Example:**
```json
{
  "threshold": 1000,
  "discountPercentage": 10
}
```

## How To Run The Project

1. **Clone Repository**
    ```sh
    git clone https://github.com/your-username/coupon-management-api.git
    cd coupon-management-api
    ```

2. **Create MySQL Database**
    ```sql
    CREATE DATABASE coupon_db;
    ```

3. **Update Database Configuration**

    Edit application.properties:
    ```
    spring.datasource.url=jdbc:mysql://localhost:3306/coupon_db
    spring.datasource.username=root
    spring.datasource.password=root
    ```

4. **Install Dependencies**
    ```sh
    mvn clean install
    ```

5. **Run the Application**
    - Option 1: Run `CouponApplication.java` from your IDE
    - Option 2: Use Maven
      ```sh
      mvn spring-boot:run
      ```

6. **Server Starts**
    - Visit: [http://localhost:8080](http://localhost:8080)

## API Endpoints

- **Create Coupon:** `POST /coupons`
- **Get All Coupons:** `GET /coupons`
- **Get Coupon By ID:** `GET /coupons/{id}`
- **Update Coupon:** `PUT /coupons/{id}`
- **Delete Coupon:** `DELETE /coupons/{id}`
- **Find Applicable Coupons:** `POST /applicable-coupons`
- **Apply Coupon:** `POST /apply-coupon/{couponId}`

**Example: Create Coupon Request**
```json
{
  "type": "CART_WISE",
  "name": "10% off above 1000",
  "description": "Cart discount",
  "active": true,
  "detailsJson": "{\"threshold\":1000,\"discountPercentage\":10}"
}
```

**Example: Cart Request**
```json
{
  "items": [
    { "productId": 1, "quantity": 3, "price": 100 },
    { "productId": 2, "quantity": 2, "price": 200 }
  ]
}
```

## Implemented Cases

- Cart Wise: Discount when cart value exceeds threshold, percentage-based, ignores expired/inactive coupons
- Product Wise: Discount only to target product, supports multiple quantity
- BXGY: Buy set, get set, repetition limit, discount based on free product price

## Assumptions

- Cart request contains productId, quantity, and price
- Product service is external (not included)
- Only one coupon applied at a time
- Product price in request is trusted

## Limitations

- Coupon stacking not supported
- Category-based, user-specific coupons not implemented
- Coupon usage tracking not implemented

## Future Improvements

- Add Swagger documentation
- Add authentication
- Add coupon usage limits
- Add category/brand-based coupons
- Add coupon stacking engine
- Add Redis caching
- Add coupon priority system

---

You can copy and paste this into your README.md for a professional and clear project overview!
