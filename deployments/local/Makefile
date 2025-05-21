
MAKEFILE_DIR := $(dir $(realpath $(lastword $(MAKEFILE_LIST))))
COMPOSE_FILE := docker-compose.yml

.PHONY: up down restart logs reset-minio reset-db

up:
	docker compose up -d

down:
	docker compose down

restart: down up

#logs:
#	docker compose logs -f

reset-minio:
	sudo rm -rf $(MAKEFILE_DIR)/.volumes/minio
	sudo mkdir -p $(MAKEFILE_DIR)/.volumes/minio

reset-db:
	sudo rm -rf $(MAKEFILE_DIR)/.volumes/lordes-postgres
	sudo mkdir -p $(MAKEFILE_DIR)/.volumes/lordes-postgres

restart-all: reset-minio reset-db
