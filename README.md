# payment-processing

Application is written on Spring Boot framework.

## Requirements

Java 20

## Running the application locally

You can execute the `main` method in the `lt.paymentprocessing.PaymentProcessingApp` class from your IDE.

Alternatively you can use Maven Wrapper to run command in cmd or terminal:

```
.\mvnw spring-boot:run
```

Or you can `package` project with Maven command `mvn package` and run built `jar` located in `target` directory:

```
java -jar .\target\payment-processing-0.0.1.jar
```

## Accessing application in-memory DB

Go to browser and type:
```
http://localhost:8080/h2-console
```
Click `Connect` with prefilled user `sa` and empty password field

## Calling rest API endpoints
### Create payment:
```
POST /payments
```
Request body example
```
{
    "type":"TYPE2",
    "amount":"400",
    "currency":"USD",
    "deptor_iban":"LT121000011101001000",
    "creditor_iban":"EE382200221020145685",
    "details":"some details"
}
```
Success response example
```
{
    "id": 1,
    "message": "Payment successfully created"
}
```
### Get all valid payments:
```
GET /payments
```
Optional query parameters `amountFrom` and `amountTill`
```
GET /payments?amountFrom=10&amountTill=1000
```
Success response example
```
{
    "id_list": [
        1,
        2
    ]
}
```

### Get specific payment by id:
```
GET /payments/{paymentId}
```
Request example
```
GET /payments/1
```
Success response example
```
{
    "id": 1,
    "cancellation_fee": 0.00
}
```
### Cancel specific payment by id:
```
DELETE /payments/{paymentId}
```
Request example
```
DELETE /payments/1
```
Success response example
```
{
    "id": 1,
    "message": "Payment successfully cancelled"
}
```