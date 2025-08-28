# Introduction

This is an assignment completed in the context of an active interview process with Paf.

I am given a legacy code base that has been migrated into a new Spring Boot application.

My task is to identify and fix all flaws in the code. 

# Prerequisites
You must have Java (11+), Docker and docker-compose installed in your system.
(https://docs.docker.com/compose/install/)

# How to run
Being in the root of the project, navigate into the /infra directory and run the command:
```
docker compose up -d
```
This will spin up the required infrastructure.

# Depedencies

My first step is to upgrade from version 2.5.4 to version 3.5.4 (https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.5.4).

I also add some dependencies that I will need, namely:

- **spring-boot-starter-validation**
- **Liquibase**
- **Lombok**

  Note that I like using Lombok during development, mainly for saving time from writing getters, setters and constructors.
  For this reason, you will see many times in my code that I implement constructor based dependency injection, by declaring the dependencies as private final fields, and then
  using the @RequiredArgsConstructor annotation of Lombok.
- **springdoc-openapi-starter-webmvc-ui**

  I use SpringDoc for generation of API docs adhering to the OpenAPI 3 specification. The documentation is exposed on the /v3/api-docs path by default, when the app is running.
- Testcontainers

# Infrastructure

The exercise came with an H2 inmemory database already configured.
I changed that and used a more advanced RDMS such as PostgreSQL, like I would do
in a real world scenario.

I chose Keycloak as an IAM solution as I have used it the most.

The configuration for PostgreSQL is:

- **DB url:** jdbc:postgresql://localhost:5439/paf
- **DB name:**  paf
- **DB username:**  paf
- **DB password:**  paf

The configuration for Keyckloak is:

- **Keycloak admin console url:**  http://localhost:8090/
- **Keycloak admin username**  admin
- **Keycloak admin password:** admin
- **Keycloak realm name:** paf-exercise
- **Keycloak client id:** paf-exercise-client
- **Keycloak client secret:** s3cr3t
- **Keycloak custom admin role:** paf-exercise-admin

I provide you with two predefined users in Keycloak,
so you don't have to mess with it.
One has the admin role and one with the default role. Their credentials are:

- **Keycloak admin user username:** testuseradmin
- **Keycloak admin user password:** testuseradmin
- **Keycloak default user username:** testuser
- **Keycloak default user password:** testuser

# Database schema

For evolving the database schema, I used Liquibase. In order to 
integrate it with the application, I first added the respective dependency 
in pom.xml. Then I added the relevant configuration properties, and finally 
I created the changelog xml that Liquibase needs. Finally, I deleted the 
```schema.sql``` DDL file that was provided with the assignment, as it was not needed anymore.


# Security architecture

Despite the fact that Keycloak has an existing integration with Spring and that in a real world scenario I would use
Spring Security to implement to Oauth2.0 protocol, for the scope of this exercise, I will write a simple
custom Oauth2.0 client, with Keycloak as the Oauth2.0 authorization server.

The concept of my implementation is the following:

When performing a login using credentials, instead of exposing the access token and the refresh to the application requesting access to a secured resource
(in this case the browser), we expose a session cookie (HttpOnly, SameSite=Strict and controlled expiration), that contains a session id. In the database we correlate this session id
with the access token, the refresh token and other authorization related information. 

So, when a request to an authorized resource is made, we read the value of this cookie and 
exchange the session id with the access and the refresh tokens. 

Subsequently, we introspect the access token with keycloak and if it is not active for any reason,
we try to renew it using the refresh token. If that is also expired, we consider the request unauthorized
and block access to any further resources.  In this case we remove both the session saved in DB and the Keycloak session.

It is obvious that for this to work in production, the frontend should be served under the same host as the backend.
If that is not possible, we could abstract the authorization mechanism described above to an API gateway,
which will proxy the frontend (that is hosted else were), handle the authorization and also proxy to the API (which also can be hosted else were).
This pattern is usually called Backend For Frontend (BFF), and can offer all the benefits and drawbacks that come 
with a centralized API Gateway.

Note here that when a request is authenticated, we add to it two headers. One is the Keyloak user id and the other is a boolean
representing whether this user is an admin or not.

After a request has been authorized by the IAM provider, I have another layer of security that handles authorization
on the controller level, using a set of fine-grained authorities.

Specifically, I have introduced an entity named ```Account```. This entity can contain information about a Keycloak user (in
the real world such information could be an address, a vat number etc). It also has a set of authorities. These authorities give 
permission to logged in users to access specific controller endpoints.

This is enforced using aspect oriented programming and two custom annotations: 

- The ```@RequiredAuthorities``` annotation. This is used on controller endpoints. It accepts an array of authority codes that
are required for access each endpoint. Then the respective aspect method retrieves the account's authorities and see if it is lacking any. If yes, then 
it prohibits further access.

- The ```@IsAdmin``` annotation. This is also used on controller endpoints and requires a user to be an admin to access the resource.

# Testing

You will find several unit and integration tests.
I used Junit, Mockito, RestClientTest and Testcontainers.

# Instructions on assessing and testing the implementation

As I said before, I have prepopulated the database with some data, so you can test without having to add data yourselves.

This data are:

1. Two users, one admin and one regular.
  Their credentials are:
   - **Admin user username:** testuseradmin
   - **Admin user password:** testuseradmin
   - **Regular user username:** testuser
   - **Regular user password:** testuser

2. The following authorities that are used for access in the respective endpoints:
   - GET_TOURNAMENTS
   - CREATE_TOURNAMENT
   - UPDATE_TOURNAMENT
   - DELETE_TOURNAMENT
   - ADD_PLAYER_INTO_TOURNAMENT
   - REMOVE_PLAYER_FROM_TOURNAMENT
   - GET_PLAYERS_IN_TOURNAMENT
   - GET_PLAYERS
   - GET_PLAYER
   - CREATE_PLAYER
   - UPDATE_PLAYER
   - DELETE_PLAYER

  I have also assigned all of the authorities to both users.
  
3. Two tournaments with names: **tournament1** and **tournament2**

4. Five players with names: **player1**, **player2**, **player3**, **player4**, **player5**
5. I added players 1 and 2 to tournament1 and players 3, 4 and 5 to tournament 2

Before testing the API, you will have to login from a user-agent that can save response cookies (Postman can do that).

The login endpoint is:

```
POST http://localhost:8080/auth/login
Content-Type: application/json
{
    "username": "testuser",
    "password": "testuser",
    "adminLogin": false
}
```

The logout endpoint is:

```
POST http://localhost:8080/auth/logout
Content-Type: application/json
```

After logging in, and after making sure that the PAFSESSION cookie has been set, you don't have to do anything else for authorizing subsequent 
requests to the API (for example you don't have to add the access token to the Authorization header).

You can find a detailed API specification at: https://paf-assignment.app.topalidis.online/swagger-ui/index.html

Finally, you will find  a Postman collection with all the requests required by the assignment and which I used during development.
This collection is in the root of the project, in a file named ```paf.postman_collection.json```

If you use it, do not forget to run the Login request first. Then, Postman will save the session cookie and you will be able to test other requests too.

# Notes

Please note that I did not do any modification to the front end code and I don't expect it to work at all.

Also, I did not include some things in my implementation, like for example an admin controller to handle account relation operations, or a logging middleware as I thought it is outside the scope of the current task.

This implementation is a simplified one, that aims to showcase my experience in different concepts related to Java and the Spring Framework.