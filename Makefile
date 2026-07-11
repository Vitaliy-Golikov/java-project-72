# Hexlet project action requires `setup` target
.PHONY: setup build test

setup:
	cd app && chmod +x gradlew

build: setup
	cd app && ./gradlew build -x test

test: setup
	cd app && ./gradlew test

run: setup
	cd app && ./gradlew run

checkstyle: setup
	cd app && ./gradlew checkstyleMain checkstyleTest