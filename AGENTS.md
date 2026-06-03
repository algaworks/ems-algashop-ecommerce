# AGENTS.md

This file provides guidance to AI coding agents when working with the Ecommerce WebApp only.
The monorepo root instructions still apply; in particular, prefix commands with `rtk` as described in the root `AGENTS.md`.

## Repository Structure

This is the **Ecommerce WebApp** project. It is a Spring Boot Web MVC application with Thymeleaf pages, not a backend microservice.

```
apps/ecommerce/
├── src/main/java/com/algaworks/algashop/ecommerce/
│   ├── EcommerceWebAppApplication.java
│   ├── application/
│   │   ├── client/             RestClient-based API clients
│   │   ├── controller/         Spring MVC controllers
│   │   ├── model/              form, page, filter, and client DTO models
│   │   ├── service/            web application services
│   │   └── properties/         algashop.* configuration properties
│   └── infraestructure/        security, OAuth2, RestClient, session, and web config
├── src/main/resources/
│   ├── templates/              Thymeleaf HTML templates
│   ├── static/                 CSS, JS, images, fonts, favicon
│   ├── db/migration/           Flyway migrations
│   └── application*.yml        profile-based configuration
├── src/test/java/              JUnit/Mockito tests
├── build.gradle
├── settings.gradle
└── Dockerfile
```

## Technology Stack

- **Java 21**, Gradle, Spring Boot 3.2.1
- **Spring Web MVC + Thymeleaf** for server-rendered pages
- **Spring Security OAuth2 Client** for login and backend API calls
- **Spring Session + Redis** for web session storage and OAuth2 authorized client state
- **Flyway + PostgreSQL driver** for the OAuth2 authorized-client schema
- **Thymeleaf Layout Dialect** and `thymeleaf-extras-springsecurity6`
- **Lombok** for boilerplate reduction
- **JUnit 5**, Mockito, AssertJ, and Spring Security Test

## Build Commands

All commands run from the Ecommerce project directory:

```bash
cd apps/ecommerce

rtk ./gradlew build          # compile + test
rtk ./gradlew bootJar        # build build/libs/algashop-ecommerce.jar
rtk ./gradlew dockerBuild    # build algaworks/algashop-ecommerce:dev
```

## Test Commands

```bash
cd apps/ecommerce

rtk ./gradlew test
```

To run a single test class:

```bash
rtk ./gradlew test --tests "com.algaworks.algashop.ecommerce.application.controller.MyAccountDetailsControllerTest"
```

Existing focused tests cover controller behavior and account-profile flows, for example `MyDataControllerTest`, `CustomerProfileControllerTest`, and `CustomerProfileRequiredInterceptorTest`.

## Running Locally

The app runs on port `9080` from `src/main/resources/application-base.yml`.

Profiles are grouped in `src/main/resources/application.yml`:
- `development` = `base` + `development-env`
- `docker` = `base` + `development-env` + `docker-env`
- `production` = `base` + `production-env`
- `flyway` = `base` + `production-env`

Development settings use:
- API gateway URL: `http://api.algashop.local:9999`
- Authorization server URL: `http://auth.algashop.local:8081`
- OAuth2 redirect URI: `http://algashop.local:9080/login/oauth2/code/algashop-ecommerce-web`
- Redis database `3` for sessions/authorized clients

Required hostnames are maintained in the monorepo `etc/hostnames`; Ecommerce uses at least:
```
127.0.0.1 algashop-ecommerce
127.0.0.1 algashop.local
127.0.0.1 auth.algashop.local
```

The root Docker Compose files provide dependencies such as Redis, the authorization server, gateway-ecommerce, and backend microservices. This project has its own `Dockerfile`, but the current `docker-compose.services.yml` only defines `algashop-gateway-ecommerce`, not an Ecommerce WebApp service.

## Architecture

### MVC Web Layer
Controllers live in `application/controller/` and return Thymeleaf views from `src/main/resources/templates`.
Examples:
- `ProductCatalogController` renders catalog/product pages.
- `ShoppingCartController` handles shopping cart routes.
- `MyAccountController`, `MyDataController`, `MyAddressController`, and `CustomerProfileController` handle account flows.

Form backing objects live under `application/model/form`, page view models under `application/model/page`, and API DTOs under `application/model/client`.

### API Clients
Outbound API calls are in `application/client/` and use Spring `RestClient`.
- The primary `restClient` bean uses client credentials through `OAuth2ClientCredentialsTokenInterceptor`.
- The `userAuthenticatedRestClient` bean uses the logged-in user's token through `OAuth2UserTokenInterceptor`.

Use `EcommerceProperties` (`algashop.*`) for gateway, auth, and payment URLs instead of hardcoding new base URLs.

### OAuth2 Security Model
`EcommerceWebSecurityConfig` defines protected routes and OAuth2 login/logout behavior.
Protected account, checkout, order, address, details, and shopping-cart add routes require authentication.

OAuth2 client registrations are defined in `application-base.yml` and environment-specific profile files:
- `algashop-ecommerce-web` uses authorization code flow for browser users.
- `backend` uses client credentials flow for machine-to-machine product/category/user/shipping calls.

### Customer Profile Completion
Authenticated users must have a customer profile before accessing key account, checkout, and cart routes.
`CustomerProfileRequiredInterceptor` checks `/api/v1/customers/me` through `CustomerRestClient` and is registered in `EcommerceWebMvcConfig`.

If `/api/v1/customers/me` returns 404, users are redirected to `/my-account/complete-your-profile`; users who already have a profile are redirected away from that page to `/my-account`.

### Templates and Static Assets
Keep HTML changes in `src/main/resources/templates`.
Reusable Thymeleaf fragments live in `templates/fragments/`; shared layouts live in `templates/layout/`.
Static CSS, JS, images, and fonts live under `src/main/resources/static`.

## Working with Submodules

`apps/ecommerce` is its own Git submodule (`ems-algashop-ecommerce`). When modifying Ecommerce files, commits belong in this submodule repository. After committing inside the submodule, update the submodule reference from the monorepo root.

```bash
# From the monorepo root
rtk git submodule update --init --recursive

# Update the Ecommerce submodule reference
rtk git submodule update --remote apps/ecommerce
```
