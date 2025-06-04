# Home Vault file server

## Starting

### File server

To start the file-server:

1. run `./gradlew :file-server:runApp`

### Auth server

To start the auth-server:

1. start docker engine on your machine
2. create `.env` file in project root directory and add definitions for
    - PG_DB_NAME
    - PG_USERNAME
    - PG_PASSWORD
3. run `./gradlew :database:composeUp`
    - if you just created a new db, run `./gradlew :database:flywayMigrate`
4. run `./gradlew :auth-server:runApp`

Example for `.env`:
```
PG_DB_NAME=hv_database
PG_USERNAME=postgres
PG_PASSWORD=postgres
```

## Building jar

To build `.jar` run:

`./gradlew :file-server:deployJar`
`./gradlew :auth-server:deployJar`

Then the `.jar` files will be in `file-server/deploy` and `auth-server/deploy` (if all tests passed)

## Verification

To run **sonarqube** verification:

1. start docker engine on your machine
2. run sonarqube container in docker with `./gradlew :verification:composeUp`
3. create `.env` file in project root and load your access token in variable `SONARQUBE_TOKEN`
4. run `./gradlew jacocoTestReport sonar`

Example for `.env`:
```
SONARQUBE_TOKEN=sqa_169bad6484545d5435341636d8024cf46efc5a9c5
```

## Reserved ports for services:
- **File server**: `8080`
- **Auth server**: `8090`
- **Database**: `5433`
- **SonarQube**: `9000`