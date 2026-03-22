GitHub Organization Access Reporter
A production-ready Spring Boot microservice designed to generate comprehensive, user-centric access reports for GitHub Organizations. This service aggregates repository permissions across an entire organization, providing a clear view of which users have access to specific internal assets.
Key Features
GraphQL-Powered: Utilizes GitHub's GraphQL API to solve the "N+1 query problem," fetching repositories and collaborators in optimized batches.

Scalable Pagination: Implements Cursor-based Pagination to reliably handle organizations with 100+ repositories and thousands of users.

Modern Java Stack: Built with Java 21 and Spring Boot 3.4.2, leveraging Records for immutable data modeling and WebClient for non-blocking I/O.

Robust Error Handling: Centralized @ControllerAdvice to provide structured JSON error responses for invalid tokens (401) or missing organizations (404).
Getting Started
Prerequisites
Java 21 installed.

Maven 3.8+ installed.

A GitHub Personal Access Token (PAT).

Setup & Installation
Clone the Repository:

Bash
git clone https://github.com/akshaybisht6396/git-hub-access-report.git
cd git-hub-access-report
Configure Authentication:
Open src/main/resources/application.properties and add your GitHub PAT:

Properties
github.token=your_fine_grained_access_token_here
Running the Project
Run the following command in the root directory:

Bash
mvn spring-boot:run
The application will be available at http://localhost:8080.
Authentication Configuration
The service uses GitHub Fine-grained Personal Access Tokens for secure API communication.

Required Scopes: The token must have Metadata: Read and Administration: Read permissions (or equivalent) for the target organization.

Implementation: Authentication is handled via a centralized WebClient bean in the GitHubConfig class. This bean automatically injects the Authorization: Bearer header into every outgoing request, ensuring security and reducing code duplication.
API Usage
The service exposes a single, optimized REST endpoint.

Endpoint
GET /api/v1/github/report/{orgName}
