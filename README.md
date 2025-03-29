# BrokerAge

## How to Run the Application

### Build the Project
```sh
mvn clean install
```

### Run the Application
```sh
mvn spring-boot:run
```

## Data Initialization
The class `DataInitializer` automatically creates sample data for testing. For production, this class should be deleted or conditions should be set for testing.

## API Usage

### Place an Order
```sh
curl --location 'http://localhost:8080/api/orders' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--header 'Cookie: JSESSIONID=5A0FF4D5258EB658A62EF9862D87CA11' \
--data '{
           "customer": {
             "id": 1
           },
           "asset": {
             "assetName": "BTC"
           },
           "orderSideType": "SELL",
           "size": 1,
           "price": 150.0,
           "orderStatus": "PENDING",
           "createDate": "2023-10-01T10:00:00"
         }'
```

### List Orders
```sh
curl --location 'http://localhost:8080/api/orders?customerId=1&startDate=2023-10-01T00%3A00%3A00&endDate=2023-10-31T23%3A59%3A59' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--header 'Cookie: JSESSIONID=5A0FF4D5258EB658A62EF9862D87CA11'
```

### Delete a Specific Order
```sh
curl --location --request DELETE 'http://localhost:8080/api/orders/2' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--header 'Cookie: JSESSIONID=5A0FF4D5258EB658A62EF9862D87CA11'
```

### List Assets of a Customer
```sh
curl --location 'http://localhost:8080/api/orders/assets?customerId=1' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--header 'Cookie: JSESSIONID=5A0FF4D5258EB658A62EF9862D87CA11'
```

