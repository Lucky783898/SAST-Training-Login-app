# Spring Boot Login API

## 📖 About This Project
This is a robust, session-based REST API built with **Java** and **Spring Boot**. It serves as a backend authentication system, allowing users to register, securely log in, view their profile, change their password, and log out.

The project uses an in-memory **H2 Database** for quick setup and testing, and is built using **Maven**. It demonstrates core backend concepts including Data Transfer Objects (DTOs), input validation, custom exception handling, and stateless-style JSON responses while leveraging Spring's internal `HttpSession` for session management.

## ⚙️ Core Technologies
*   **Java 17+**
*   **Spring Boot** (Spring Web, Spring Data JPA, Spring Validation)
*   **Database:** H2 Database (In-Memory)
*   **Build Tool:** Maven
*   **Lombok:** Boilerplate code reduction

---

## 🚀 Detailed Functionalities & How to Use

### 1. User Registration
Allows new users to create an account.
*   **Validations:** Requires a first name, a properly formatted email, and a password of at least 6 characters. Last name is optional.
*   **Unique Constraint:** The system checks the database to ensure the provided email is not already registered. Throws a custom `UserAlreadyExistsException` if a duplicate is found.

### 2. User Login
Authenticates the user and initiates a session.
*   **Security:** Verifies that the email exists and the plain-text password matches the database record.
*   **Session Issuance:** On a successful login, Spring Boot creates an `HttpSession`, saves the User ID in the server's memory, and issues a `JSESSIONID` cookie to the client.

### 3. Get User Profile (Protected)
Retrieves the logged-in user's details.
*   **Session Verification:** Intercepts the `JSESSIONID` cookie from the request. If the cookie is missing or invalid, it blocks access and returns a `401 Unauthorized` JSON response.
*   **Data Protection:** The `User` model uses `@JsonIgnore` on the password field, ensuring sensitive data is never leaked in the API response.

### 4. Change Password (Protected)
Allows an authenticated user to update their password.
*   **Re-authentication:** The user must provide their *old* password alongside their *new* password. The system verifies the old password before applying the update.
*   **Validations:** The new password must also pass the minimum length requirement.

### 5. Logout
Ends the user's active session.
*   **Invalidation:** Calls `session.invalidate()` on the server side, wiping the session memory and rendering the client's `JSESSIONID` cookie useless for future requests.

---

## 📡 API Endpoints

| HTTP Method | Endpoint | Description | Auth Required? |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Register a new user | ❌ No |
| **POST** | `/api/auth/login` | Log in and receive session cookie | ❌ No |
| **GET** | `/api/auth/profile` | Retrieve the logged-in user's data | ✅ Yes |
| **POST** | `/api/auth/change-password` | Update the user's password | ✅ Yes |
| **POST** | `/api/auth/logout` | Invalidate the current session | ✅ Yes |

*(Note: All endpoints return a standardized JSON structure: `{ "success": boolean, "message": string, "data": object }`)*

---

## 🧪 Testing with Postman

This repository includes a Postman Collection JSON file to easily test all API endpoints, including both happy paths and negative test scenarios (validation errors, wrong passwords, unauthorized access).

### How to use the Postman Collection:
1.  **Locate the File:** Find the `.postman_collection.json` file included in this project.
2.  **Import to Postman:** Open the Postman application, click **Import** (top left), and drag-and-drop the JSON file.
3.  **Run the Tests in Order:**
    *   Because this API relies on sessions, you **must** run the requests in a specific order to test the happy flows.
    *   **Step 1:** Run **Register** to create a user.
    *   **Step 2:** Run **Login**. *Crucial step:* This tells Postman to save the `JSESSIONID` cookie.
    *   **Step 3:** Run **Get Profile** or **Change Password**. Postman will automatically attach the cookie saved from Step 2, allowing these protected requests to succeed.
    *   **Step 4:** Run **Logout** to destroy the session.

*Tip: If you want to test the "Unauthorized" negative scenarios, you will need to manually clear the `JSESSIONID` cookie in Postman by clicking the "Cookies" link directly below the "Send" button.*

---

## 🛠️ How to Run the Application Locally

1.  Clone or download this repository.
2.  Ensure you have **Java** and **Maven** installed on your machine.
3.  Navigate to the project root directory in your terminal.
4.  Run the application using Maven:
    ```bash
    mvn spring-boot:run