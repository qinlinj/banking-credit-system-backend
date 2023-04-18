# Banking-credit-system-backend

## Project Summary

A Spring Boot program that manages user creation/deletion and adding credit cards to their profiles. Users may have zero or more credit cards associated with them. Also, create two APIs: one to get all credit cards for a user and another to find a user by their credit card number.

## Models

Following is a component overview of the models. You can find more hints in the source code files as well

- `User`: each user has their name, date of birth, and some (or none) credit cards associated with them
- `CreditCard`: each credit card has issance bank, card number, who the card belongs to, and a list of balance history
- `BalanceHistory`: credit card balance for a specific date.

## **Testing**:

POST, http://localhost:8080/user, Content-Type as application/json

{ "name": "Justin Jia", "email": "[john.smith@example.com](mailto:john.smith@example.com)" }

POST,  http://localhost:8080/credit-card, Content-Type as application/json

{ "userId": 1, "cardIssuanceBank": "Bank of America", "cardNumber": "123456" }

GET,  http://localhost:8080/credit-card:all?userId=1

POST, http://localhost:8080/credit-card:update-balance, Content-Type as application/json

[ { "creditCardNumber": "123456", "transactionAmount": 500.00, "transactionTime": "2023-04-24T08:00:00Z" } ]