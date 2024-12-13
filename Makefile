# Makefile for Spring Boot (Maven) Project

# Set Maven executable location
MAVEN = mvn

# Set the default target
.PHONY: all
all: clean package

# Clean the project
.PHONY: clean
clean:
	$(MAVEN) clean

# Compile and package the application
.PHONY: package
package:
	$(MAVEN) package

# Run tests
.PHONY: test
test:
	$(MAVEN) test

# Build and run the application
.PHONY: run
run:
	$(MAVEN) spring-boot:run

# Clean, build, and run the application
.PHONY: build
build: clean package run

# Show Maven dependencies
.PHONY: deps
deps:
	$(MAVEN) dependency:tree

# Install the application
.PHONY: install
install:
	$(MAVEN) install

# Check format
.PHONY: spotlessCheck
spotlessCheck:
	$(MAVEN) spotless:check

# Fix format
.PHONY: spotlessFix
spotlessFix:
	$(MAVEN) spotless:apply
