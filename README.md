# Mobile Number Portability (MNP) System

A Mobile Number Portability system built with Java, Spring Boot, Angular, and MySQL. The application allows recipient operators to submit porting requests, donor operators to accept or reject them, and agents to look up a phone number's current operator.

## Tech Stack

- **Backend:** Java 17, Spring Boot, Spring Data JPA
- **Frontend:** Angular 21, TypeScript
- **Database:** MySQL 8.4
- **Deployment:** Docker Compose for the backend and database
- **Build tools:** Maven and npm

## Prerequisites

Install the following:

- Docker Desktop with Docker Compose
- Node.js and npm compatible with the Angular project
- Git

## Project Structure

```text
mnp-system/
├── backend/       # Spring Boot REST API
├── frontend/      # Angular web application
├── database/      # Database schema and seed data
├── docker-compose.yml
├── .env           # Local database credentials (not committed)
└── README.md
```

## Configuration

Create a `.env` file in the project root with the following variables:

```dotenv
MYSQL_DATABASE=mnp_db
MYSQL_USER=mnp_user
MYSQL_PASSWORD=replace_with_a_local_password
MYSQL_ROOT_PASSWORD=replace_with_a_local_root_password
```

Use your own local development passwords. Do not commit `.env` or real credentials to version control.

## Database

The database schema and initial data are provided in `database/schema.sql`.

The schema defines operators, phone numbers, and porting requests. It also includes initial operator and phone-number records.

The Docker Compose configuration uses a persistent volume for MySQL data. Restarting the containers does not automatically reset the database.

## Running the Backend and Database

From the project root, run:

```bash
docker compose up --build -d
```

Check the container status:

```bash
docker compose ps
```

View backend logs:

```bash
docker compose logs -f backend
```

The Spring Boot API is available at:

`http://localhost:8080`

Stop the containers with:

```bash
docker compose down
```

This preserves the database volume. Do not use `docker compose down -v` unless you intentionally want to remove the persistent database volume.

## Running the Frontend

The Angular frontend runs separately from Docker.

Open another terminal:

```bash
cd frontend
npm install
npm start
```

Open the application at:

`http://localhost:4200`

The frontend communicates with the backend at `http://localhost:8080`. Keep the backend and database containers running while using the frontend.

## Authentication

The application uses a mock organization header for identifying the current operator. This is for the task and is not production-grade authentication.

Supported organization values:

- `vodafone`
- `orange`
- `etisalat`

Include the `organization` header in requests that require an operator context.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/porting-requests` | Create a porting request |
| `GET` | `/api/porting-requests` | List porting requests, with pagination |
| `POST` | `/api/porting-requests/{id}/accept` | Accept a pending request as the donor |
| `POST` | `/api/porting-requests/{id}/reject` | Reject a pending request as the donor |
| `GET` | `/api/phone-numbers/{number}` | Look up a phone number and its current operator |

## API Testing with Postman

A Postman collection is included in the `postman/` directory:

`postman/MNP_System_API.postman_collection.json`

### Import the collection

1. Start the backend and MySQL database using Docker Compose:

   ```bash
   docker compose up --build -d
   ```

2. Open Postman.
3. Click **Import** and select `postman/MNP_System_API.postman_collection.json`.
4. Make sure the backend is running at `http://localhost:8080`.
5. Run the requests in the collection to test the API.

### Included test scenarios

The collection includes requests to test:

- Looking up a phone number and its current operator.
- Creating a porting request.
- Preventing duplicate pending requests for the same phone number.
- Verifying that only the donor operator can accept or reject a request.
- Accepting and rejecting porting requests.
- Listing porting requests with pagination.

The collection uses variables for the base URL, operator header, phone numbers, and request IDs. Successful request creation automatically saves the returned request ID for subsequent accept or reject requests.

### Selecting the operator

The collection uses the `organization` request header to simulate authentication. Supported values are:

- `vodafone`
- `orange`
- `etisalat`

Change the collection's `organization` variable when testing requests as a different operator.

**Note:** Test data is stored in the database and persists between runs. Some scenarios may need different phone numbers or updated variables when repeated. The automatic cancellation timeout can be verified by creating a pending request and waiting for the configured timeout.


### Create a Porting Request

Example request:

```http
POST /api/porting-requests
Content-Type: application/json
organization: orange
```

```json
{
  "phoneNumber": "01012345678"
}
```

### List Requests

```http
GET /api/porting-requests?page=0&size=20
organization: orange
```

The endpoint returns a paginated response.

### Accept or Reject a Request

```http
POST /api/porting-requests/{id}/accept
organization: vodafone
```

```http
POST /api/porting-requests/{id}/reject
organization: vodafone
```

Only the donor operator can decide a request.

### Look Up a Phone Number

```http
GET /api/phone-numbers/01012345678
organization: vodafone
```

The response includes the phone number, its status, and its current operator.

## Business Rules

- Each porting request contains one phone number.
- A new request is rejected if that number already has a pending request.
- The recipient operator must differ from the phone number's current operator.
- Only the donor operator can accept or reject a request.
- Accepting a request updates the phone number's current operator.
- Rejecting a request leaves the current operator unchanged.
- Expired pending requests are automatically cancelled according to the configured timeout.
- Operators can view all requests involving them. Unrelated operators can view accepted requests only.
- Phone numbers are validated against the supported operator ranges.

## Configuration Properties

The backend supports the following environment/configuration properties:

| Property | Purpose |
|---|---|
| `DB_URL` | Database JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `mnp.porting-request.timeout-minutes` | Pending-request timeout; defaults to 2 minutes |
| `mnp.porting-request.cleanup-interval-ms` | Cleanup interval; defaults to 10,000 milliseconds |

## Manual Test Scenarios

Test the following scenarios through the API or frontend:

1. Create a valid porting request.
2. Attempt to create another request for the same number while the first is pending; confirm it is rejected.
3. Attempt to accept or reject a request as an operator other than the donor; confirm it is forbidden.
4. Accept a request as the donor and verify that the current operator changes.
5. Reject a request as the donor and verify that the current operator remains unchanged.
6. Leave a request pending beyond the configured timeout and verify that it becomes `CANCELLED`.
7. Verify that an operator sees all related requests and that unrelated operators see accepted requests only.
8. Look up a phone number and verify its current operator.
9. Verify pagination on the request-list endpoint.

## Notes

The `organization` header is a mock authentication mechanism for this task. Do not use it as a replacement for real authentication and authorization in production.
