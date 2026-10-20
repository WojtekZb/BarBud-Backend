# BarBud Backend

[Nederlands](#nederlands) | [English](#english)

---

## Nederlands

BarBud is een mobiele app die je helpt cocktails te maken met wat je thuis hebt staan. Je legt je eigen bar aan met de ingrediënten die je in huis hebt. Later gaat BarBud met behulp van AI drankjes voorstellen die je met die ingrediënten kunt maken.

Dit is een persoonlijk project. Ik heb het gebouwd voor het semester Full Stack Development bij Fontys Hogescholen en werk er nu verder aan om security by design toe te passen en de app veilig te maken.

Deze repository bevat de **REST API**. De Flutter-app staat in [BarbudFrontend](https://github.com/WojtekZb/BarbudFrontend).

### Wat de API doet

- Gebruikers registreren en inloggen
- Stateless authenticatie met JWT: een access-token (1 uur geldig) en een refresh-token (30 dagen geldig)
- Bars aanmaken, ophalen, bekijken en bijwerken per gebruiker
- De lijst met beschikbare ingrediënten leveren
- De database is al voorbereid op drankjes en hun ingrediënten, zodat een admin later eigen originele drankjes kan toevoegen

### Endpoints

| Methode | Endpoint | Omschrijving | Inloggen nodig |
|---|---|---|---|
| POST | `/auth/register` | Account aanmaken | Nee |
| POST | `/auth/login` | Inloggen, geeft access- en refresh-token terug | Nee |
| POST | `/auth/refresh` | Nieuwe access-token ophalen | Nee |
| GET | `/bar/ingredients` | Alle ingrediënten ophalen | Ja |
| POST | `/bar/create` | Nieuwe bar aanmaken | Ja |
| POST | `/bar/my-bars` | Bars van de gebruiker ophalen | Ja |
| POST | `/bar/details` | Details en ingrediënten van een bar ophalen | Ja |
| PUT | `/bar/update` | Bar bijwerken | Ja |

### Technologie

| Onderdeel | Technologie |
|---|---|
| Taal en framework | Java 21, Spring Boot 3 |
| Beveiliging | Spring Security, JWT (jjwt), BCrypt voor wachtwoorden |
| Database | PostgreSQL 17 met Spring Data JPA |
| Databasemigraties | Flyway |
| Containers | Docker en Docker Compose |
| Testen | Unit-tests, integratietests, k6-loadtests |
| Codekwaliteit | JaCoCo (testdekking), SonarQube |
| CI/CD | GitHub Actions |

### Database

| Tabel | Inhoud |
|---|---|
| `users` | Gebruikers met gehashte wachtwoorden en refresh-tokens |
| `bars` | Bars, gekoppeld aan een gebruiker |
| `ingredients` | Alle ingrediënten met categorie |
| `bar_ingredients` | Welke ingrediënten in welke bar staan |
| `drinks` | Drankjes met beschrijving, stappen en alcoholpercentage |
| `drink_ingredients` | Welke ingrediënten in welk drankje gaan, met hoeveelheid en eenheid |

### Deployment

De backend draait in Docker op mijn eigen Raspberry Pi en is niet bereikbaar vanaf internet.

- Bij elke push en pull request bouwt GitHub Actions het project, draait de tests, meet de testdekking met JaCoCo en analyseert de code met SonarQube.
- Bij een push naar `main` haalt een self-hosted runner op de Raspberry Pi de nieuwste versie op en herstart de containers met Docker Compose.

### Beveiliging

Al aanwezig:

- Wachtwoorden worden gehasht met BCrypt
- Stateless authenticatie met kortlevende access-tokens en refresh-tokens
- Alle endpoints behalve registreren, inloggen en verversen vereisen een geldige token
- Geheimen zoals de JWT-sleutel komen uit omgevingsvariabelen
- De server staat niet open voor verbindingen van buitenaf

Op dit moment onderzoek ik hoe ik security by design verder kan toepassen in zowel de backend als de app.

### Gepland

- AI die op basis van de ingrediënten in je bar drankjes voorstelt
- Een admin die eigen, originele drankjes kan toevoegen
- Security by design toepassen in de hele applicatie (in onderzoek)
- Een downloadpagina voor de app, zodra de backend veilig van buitenaf bereikbaar is

### Projectstructuur

```
src/main/java/com/barbud/barbudbackend/
├── controllers/     REST-endpoints
├── services/        Businesslogica en JWT
├── repositories/    Databasetoegang
├── interfaces/      Interfaces voor de repositories
├── requests/        Request-DTO's
├── responses/       Response-DTO's
├── security/        JWT-filter
└── config/          Spring Security-configuratie
init/                Databaseschema en startdata (ingrediënten)
performance-tests/   k6-loadtest
```

---

## English

BarBud is a mobile app that helps you make cocktails with what you have at home. You set up your own bar with the ingredients you have. Later on, BarBud will use AI to suggest drinks you can make with those ingredients.

This is a personal project. I built it for the Full Stack Development semester at Fontys University of Applied Sciences and I'm now continuing to work on it to apply security by design and make the app secure.

This repository contains the **REST API**. The Flutter app lives in [BarbudFrontend](https://github.com/WojtekZb/BarbudFrontend).

### What the API does

- Register and log in users
- Stateless authentication with JWT: an access token (valid for 1 hour) and a refresh token (valid for 30 days)
- Create, list, view and update bars per user
- Provide the list of available ingredients
- The database is already prepared for drinks and their ingredients, so an admin can add their own original drinks later

### Endpoints

| Method | Endpoint | Description | Login required |
|---|---|---|---|
| POST | `/auth/register` | Create an account | No |
| POST | `/auth/login` | Log in, returns an access and refresh token | No |
| POST | `/auth/refresh` | Get a new access token | No |
| GET | `/bar/ingredients` | Get all ingredients | Yes |
| POST | `/bar/create` | Create a new bar | Yes |
| POST | `/bar/my-bars` | Get the user's bars | Yes |
| POST | `/bar/details` | Get a bar's details and ingredients | Yes |
| PUT | `/bar/update` | Update a bar | Yes |

### Tech stack

| Part | Technology |
|---|---|
| Language and framework | Java 21, Spring Boot 3 |
| Security | Spring Security, JWT (jjwt), BCrypt for passwords |
| Database | PostgreSQL 17 with Spring Data JPA |
| Database migrations | Flyway |
| Containers | Docker and Docker Compose |
| Testing | Unit tests, integration tests, k6 load tests |
| Code quality | JaCoCo (test coverage), SonarQube |
| CI/CD | GitHub Actions |

### Database

| Table | Contents |
|---|---|
| `users` | Users with hashed passwords and refresh tokens |
| `bars` | Bars, linked to a user |
| `ingredients` | All ingredients with a category |
| `bar_ingredients` | Which ingredients are in which bar |
| `drinks` | Drinks with description, steps and alcohol percentage |
| `drink_ingredients` | Which ingredients go into which drink, with amount and unit |

### Deployment

The backend runs in Docker on my own Raspberry Pi and is not reachable from the internet.

- On every push and pull request, GitHub Actions builds the project, runs the tests, measures test coverage with JaCoCo and analyses the code with SonarQube.
- On a push to `main`, a self-hosted runner on the Raspberry Pi pulls the latest version and restarts the containers with Docker Compose.

### Security

Already in place:

- Passwords are hashed with BCrypt
- Stateless authentication with short-lived access tokens and refresh tokens
- Every endpoint except register, login and refresh requires a valid token
- Secrets such as the JWT key come from environment variables
- The server is closed to connections from outside

I'm currently researching how to apply security by design further across both the backend and the app.

### Planned

- AI that suggests drinks based on the ingredients in your bar
- An admin who can add their own original drinks
- Applying security by design across the whole application (being researched)
- A download page for the app, once the backend can be reached safely from outside

### Project structure

```
src/main/java/com/barbud/barbudbackend/
├── controllers/     REST endpoints
├── services/        Business logic and JWT
├── repositories/    Database access
├── interfaces/      Repository interfaces
├── requests/        Request DTOs
├── responses/       Response DTOs
├── security/        JWT filter
└── config/          Spring Security configuration
init/                Database schema and seed data (ingredients)
performance-tests/   k6 load test
```
