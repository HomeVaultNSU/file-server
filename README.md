# Home Vault file server

## Starting

To start the server run:

`./gradlew :service:runApp`

## Verification

To run **sonarqube** verification:

1. Start sonarqube container in docker `docker-compose up -d`
2. Create `.env` file in project root and load your access token in variable `SONAR_TOKEN`
3. Run gradle task `./gradlew sonar`
