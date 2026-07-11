.PHONY: build run test report check clean install

setup:
	.\gradlew

run:
	.\gradlew run

test:
	.\gradlew test

report:
	.\gradlew jacocoTestReport

check:
	.\gradlew check

clean:
	.\gradlew clean

install:
	chmod +x app/gradlew
	cd app && .\gradlew clean install
	cd app && .\gradlew clean compileTest