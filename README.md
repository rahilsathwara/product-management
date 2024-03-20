# product-management

    This is a RESTful API for managing products, users, roles, and categories. The API is built using Spring Boot and secured with JWT tokens.


## authentication
    
    1.1 Register API CURL
        
        curl --location 'http://localhost:8080/api/registerUser' \
        --header 'Content-Type: application/json' \
        --data-raw '{
        "name": "New Name",
        "email": "newemail@example.com",
        "password": "NewP@ssw0rd",
        "confirmPassword": "NewP@ssw0rd",
        "roles": ["ROLE_USER"]
        }'
    
    1.2 Login API CURL

        curl --location 'http://localhost:8080/api/authenticate' \
        --header 'Content-Type: application/json' \
        --data-raw '{
        "email": "alice@example.com",
        "password": "P@ssw0rd"
        }'

## Roles api
    
    2.1 Create Role CURL
    
        curl --location 'http://localhost:8080/api/roles/' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2MDU4MSwiZXhwIjoxNzEwODYyMzgxfQ.LSZjBpsxWoLwCWNv2AgjdY2-sVPwkFG2jPLcgNhu-e8' \
        --data '{
        "name": "ROLE_TEST"
        }'    
    
    2.2 Edit Role CURL

        curl --location --request PUT 'http://localhost:8080/api/roles/4' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2MDU4MSwiZXhwIjoxNzEwODYyMzgxfQ.LSZjBpsxWoLwCWNv2AgjdY2-sVPwkFG2jPLcgNhu-e8' \
        --data '{
        "name": "ROLE_TEST1"
        }'
    
    2.3 Get all roles CURL
    
        curl --location 'http://localhost:8080/api/roles/' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2MDU4MSwiZXhwIjoxNzEwODYyMzgxfQ.LSZjBpsxWoLwCWNv2AgjdY2-sVPwkFG2jPLcgNhu-e8' \
        --data ''
    
    2.4 Get Role By Id CURL

        curl --location 'http://localhost:8080/api/roles/4' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2MDU4MSwiZXhwIjoxNzEwODYyMzgxfQ.LSZjBpsxWoLwCWNv2AgjdY2-sVPwkFG2jPLcgNhu-e8' \
        --data ''
    
    2.5 Delete role CURL
    
        curl --location --request DELETE 'http://localhost:8080/api/roles/4' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2MDU4MSwiZXhwIjoxNzEwODYyMzgxfQ.LSZjBpsxWoLwCWNv2AgjdY2-sVPwkFG2jPLcgNhu-e8' \
        --data ''

## Users api
    
    3.1 Get Current loggedIn user profile CURL
        
        curl --location 'http://localhost:8080/api/profile' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDkwMjE0MiwiZXhwIjoxNzEwOTAzOTQyfQ.pOR1N3p_NWhlQRDO5y0UCYAg7-jWhPciRbckNmZBTeM'

    3.2 get all users list CURL
        
        curl --location 'http://localhost:8080/api/users' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lQGV4YW1wbGUuY29tIiwiaWF0IjoxNzEwODU3NTYxLCJleHAiOjE3MTA4NTkzNjF9.Wz1ZhtxDW7B_txD9tUC17qSJhGjJE6Nt1jGBZoEWi6I'
    
    3.3 Logout CURL

        curl --location --request POST 'http://localhost:8080/api/logout' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg4MjIxNiwiZXhwIjoxNzEwODg0MDE2fQ.WBmFJiQY2M9mzAft9D-mjntDGTCZ81U7nsfBubmti3o'

## Category api
    
    4.1 Create category CURL
        
        curl --location 'http://localhost:8080/api/category/' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2NDMyNSwiZXhwIjoxNzEwODY2MTI1fQ.N_wFZFHaZo3bjHJEt0jtOlEoN1oN5zDQI1riUuAzcgY' \
        --data '{
        "name": "Clothing",
        "description": "Category for clothing and apparel",
        "imageUrl": "https://example.com/clothing.jpg"
        }'        

    4.2 Edit Category CURL
        
        curl --location --request PUT 'http://localhost:8080/api/category/2' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2NDMyNSwiZXhwIjoxNzEwODY2MTI1fQ.N_wFZFHaZo3bjHJEt0jtOlEoN1oN5zDQI1riUuAzcgY' \
        --data '{
        "name": "Clothing",
        "description": "Category for clothing and apparel",
        "imageUrl": "https://example.com/clothing.jpg"
        }'

    4.3 Get category by id CURL
    
        curl --location 'http://localhost:8080/api/category/2' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2NDMyNSwiZXhwIjoxNzEwODY2MTI1fQ.N_wFZFHaZo3bjHJEt0jtOlEoN1oN5zDQI1riUuAzcgY' \
        --data ''

    4.4 Get all categories list CURL
        
        curl --location 'http://localhost:8080/api/category/' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg2NDMyNSwiZXhwIjoxNzEwODY2MTI1fQ.N_wFZFHaZo3bjHJEt0jtOlEoN1oN5zDQI1riUuAzcgY' \
        --data ''

## Product api
    
      Create Product CURL
        
        curl --location 'http://localhost:8080/api/products/' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDkwMjE0MiwiZXhwIjoxNzEwOTAzOTQyfQ.pOR1N3p_NWhlQRDO5y0UCYAg7-jWhPciRbckNmZBTeM' \
        --data '{
        "name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sagittis justo ac tortor tempus, in finibus ligula feugiat. Nulla facilisi. Sed bibendum neque eget nisi venenatis, ac varius nisl vestibulum. Quisque ut bibendum lacus.",
        "description": "Product Description",
        "price": 49.99,
        "weight": 1.5,
        "weightUnit": "kg",
        "brand": "Product Brand",
        "categoryId": 1,
        "expiryDate": "2024-12-31T23:59:59",
        "userId": "4",
        "inventory": 10
        }'

    5.2 Edit Product CURL

        curl --location --request PUT 'http://localhost:8080/api/products/1' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDkwMjE0MiwiZXhwIjoxNzEwOTAzOTQyfQ.pOR1N3p_NWhlQRDO5y0UCYAg7-jWhPciRbckNmZBTeM' \
        --data '{
        "name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sagittis justo ac tortor tempus",
        "description": "Product Description",
        "price": 49.99,
        "weight": 1.5,
        "weightUnit": "kg",
        "brand": "Product Brand",
        "categoryId": 2,
        "expiryDate": "2024-12-31T23:59:59",
        "userId": "user123",
        "inventory": "In Stock"
        }'

    5.3 Get all products CURL

        curl --location 'http://localhost:8080/api/products/' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg4MjczNCwiZXhwIjoxNzEwODg0NTM0fQ.FTqb0L_1rW7gO2r0sx1P5ok2wXhxXsN_Hbyq7g9-OnI' \
        --data ''

    5.4 Get product by Id CURL

        curl --location 'http://localhost:8080/api/products/1' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg3MTA1NywiZXhwIjoxNzEwODcyODU3fQ.ae8ORS5If3wu7MXafEVh7wVYRFgVrw5P8_KahUeEZlc' \
        --data ''
    
    5.5 Delete product by Id CURL
        
        curl --location --request DELETE 'http://localhost:8080/api/products/1' \
        --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg3MTA1NywiZXhwIjoxNzEwODcyODU3fQ.ae8ORS5If3wu7MXafEVh7wVYRFgVrw5P8_KahUeEZlc' \
        --data ''