# Centurion University ERP: Student Module

## Runtime choice

The project currently uses `jakarta.servlet` 6.1, Jakarta Persistence 3.2 and Hibernate 7.1. It must therefore run on Tomcat 10.1+.
Tomcat 9 uses the older `javax.servlet` namespace and is not compatible with the current dependencies without a planned migration.

## Setup

1. Start MySQL.
2. Edit `Backend/src/main/resources/db.properties` and replace `CHANGE_ME` with the local database password.
3. Run `Backend/database/schema.sql` in MySQL.
4. Run `mvn clean package` inside `Backend`.
5. Deploy `Backend/target/Backend.war` to Tomcat 10.1+.
6. Open `/Backend/students/list`.

## Implemented routes

| Method | URL | Purpose |
|---|---|---|
| GET | `/students/list` | Display all students |
| GET | `/students/add` | Display add form |
| POST | `/students/add` | Save a student |
| GET | `/students/details?id=1` | Display one student |
| GET | `/students/edit?id=1` | Display edit form |
| POST | `/students/update` | Update a student |
| POST | `/students/delete` | Delete a student |

## Layer explanation

`JSP -> Servlet -> Service -> DAO -> Hibernate -> MySQL`

- JSP collects and displays data.
- Servlet handles HTTP requests and forwards or redirects.
- Service validates input and applies application rules.
- DAO opens Hibernate sessions and transactions.
- Hibernate maps `Student` to the `students` table.
- MySQL stores the data.

`SessionFactory` is the application-level Hibernate resource. A `Session` is opened for a database operation. A write operation starts a `Transaction`, commits when successful, rolls back on failure, and closes the session.

## Manual CRUD test

Create a student with admission number `CUTM-001`, first name `Asha`, department ID `1`, course ID `1`, semester `1`, and a valid email. Verify that it appears in the list. Open View, then Edit and change the phone number. Finally use Delete and verify the row disappears.

Also test an empty admission number, invalid email, text in a numeric field, a duplicate admission number, and a non-existing student ID. The expected result is a readable validation or not-found error and no partial database write.

## Viva points

- `@Entity` marks a Java class for ORM mapping; `@Table` selects the table.
- `@Id` and `@GeneratedValue` define the primary key.
- A DAO isolates database code; a Service isolates validation and business rules.
- `doGet()` normally displays data or a form; `doPost()` processes submitted data.
- Forward keeps the current request inside the server; redirect starts a new request.
- A primary key identifies a row; a foreign key links related tables.
- Hibernate reduces manual JDBC mapping, but still needs sessions and transactions.