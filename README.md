# Home Vault file server

## Starting

To start the server run:

`./gradlew :service:runApp`

## Building jar

To run tests and build `.jar` file run:

`./gradlew :service:deployJar`

Then the `.jar` file will be in `service/deploy` (if all tests passed)

## Verification

To run **sonarqube** verification:

1. Start sonarqube container in docker with `./gradlew :verification:composeUp`
2. Create `.env` file in project root and load your access token in variable `SONARQUBE_TOKEN`
3. Run gradle task `./gradlew jacocoTestReport sonar`
