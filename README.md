# Home Vault file server

## Starting applications

### File server

To start the file-server:

1. run `./gradlew :file-server:runApp`

### Auth server

To start the auth-server:

1. create `.env` file in project root directory and add definitions for
    - **PG_DB_NAME**
    - **PG_USERNAME**
    - **PG_PASSWORD**
2. run `./gradlew :database:composeUp`
    - if you just created a new db, run `./gradlew :database:flywayMigrate`
3. run `./gradlew :auth-server:runApp`

Example for `.env`:
```
PG_DB_NAME=hv_database
PG_USERNAME=postgres
PG_PASSWORD=postgres
```

## Building jar

To build `.jar` run:

- **File server**: `./gradlew :file-server:deployJar`
- **Auth server**: `./gradlew :auth-server:deployJar`

Then the `.jar` files will be in `file-server/deploy` and `auth-server/deploy` (if all tests passed)

## Analytics

### SonarQube

To run **sonarqube** verification:

1. run sonarqube container in docker with `./gradlew :verification:composeUp`
2. create `.env` file in project root and load your access token in variable `SONARQUBE_TOKEN`
3. run `./gradlew jacocoTestReport sonar`

Example for `.env`:
```
SONARQUBE_TOKEN=sqa_169bad6484545d5435341636d8024cf46efc5a9c5
```

### ELK

To start **ELK** stack:

1. run `./gradlew :elk:composeUp`

## Reserved ports for services:

- **File server**: `8080`
- **Auth server**: `8090`
- **Database**: `5433`
- **SonarQube**: `9000`
- **Kibana**: `5601`
- **Logstash**: `5000`
- **Elasticsearch**: `9200`