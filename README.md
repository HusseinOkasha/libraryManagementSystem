# libraryManagementSystem

It's a library management api, where librarian can manage books borrow, return, and track borrowed which book.

## Actors
### Admin (librarian)
* Admin is currently the only actor in the system
* Admin has
  * name , email, phoneNumber, password
* Admin can
  * CRUD patrons
  * CRUD books
  * CU bookRecords
  
## Entites
### Account
Encapsulates the common attributes between admin and patron.
### Admin
Represents the librarian account in the system the difference between it and patron is that contains password field.
### Patron
Represents the Patron in the system.
### Book
Represent the book in the system.
### BorrowingRecord
record the borrowing of certain book by certain patron and tracks the book status (returned / borrowed).

## How to run
* `git clone https://github.com/HusseinOkasha/libraryManagementSystem.git`
* open it with intellij and run it.
* running it will create a docker container for postgres database.

### Enpoints
All endpoints are protected with JWTs except the signup endpoint.  
#### Admin signup
* `curl -X POST -H "Content-Type: application/json" -d '{
  "name": "f30",
  "email": "e30@email.com",
  "password": "123",
  "phoneNumber": "30",
  "accountType": "ADMIN"
  }' http://localhost:8080/api/signup
  `
#### Admin Login
* `curl -X POST http://localhost:8080/api/login/admin -u e30@email.com:123 --verbose`
* it will return a token save it.

#### Create new patron
* `curl -X POST http://localhost:8080/api/patrons -H 'Authorization: Bearer token' -H 'Content-Type: application/json' -d '{"profileDto":{"name":"f15","email":"e15@email.com","phoneNumber":"15","accountType":"PATRON"} }'`
#### Gat Patron By ID
* `curl -X GET http://localhost:8080/api/patrons/id -H 'Authorization: Bearer token' -H 'Content-Type: application/json'`
* Replace id in the url with the wanted id.
#### Get All Patrons
* `curl -X GET http://localhost:8080/api/patrons -H 'Authorization: Bearer token' -H 'Content-Type: application/json'`
#### Update patron by ID
* `curl -X PUT http://localhost:8080/api/patrons/id -H 'Authorization: Bearer token' -H 'Content-Type: application/json' -d '{"name":"f13 updated","phoneNumber":"13"}'`

#### Delete patron by ID
* `curl -X DELETE http://localhost:8080/api/patrons/id -H 'Authorization: Bearer token' -H 'Content-Type: application/json'`
* Replace id in the url with the required id.

#### Add new book
 * `curl -X POST http://localhost:8080/api/books -H 'Authorization: Bearer token' -H 'Content-Type: application/json' -d '{"title":"Book4","author":"author4","publication_year":"2024","isbn":"isbn4"}'`
#### Get Book by ID
* `curl -X GET http://localhost:8080/api/books/id -H 'Authorization: Bearer token' -H 'Content-Type: application/json'` 
* Replace id in the url with the required id

#### Get all books
* `curl -X GET http://localhost:8080/api/books -H 'Authorization: Bearer token' -H 'Content-Type: application/json'`
#### Update Book by ID
 * `curl -X PUT http://localhost:8080/api/books -H 'Authorization: Bearer token' -H 'Content-Type: application/json' -d '{"title":"Book4 updated","author":"author4","publication_year":"2024","isbn":"isbn4"}'`
#### Delete Book by ID
* `curl -X DELETE http://localhost:8080/api/books -H 'Authorization: Bearer token' -H 'Content-Type: application/json'`
#### Borrow Book
 * `curl -X POSThttp://localhost:8080/api/borrow/bookId/patron/patronId -H 'Authorization: Bearer token' -H 'Content-Type: application/json'`
 * Replace book_id in the url with the required book_id, same for patronId

#### Return Book
* `curl -X PUT http://localhost:8080/api/borrow/bookId/patron/patronId -H 'Authorization: Bearer token' -H 'Content-Type: application/json'`
* Replace book_id in the url with the required book_id, same for patronId
