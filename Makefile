run:
	docker-compose up -d
	gradlew bootRun
test:
	gradlew test
