#!/bin/bash

rabbitmq-server -detached

rabbitmqctl stop_app

rabbitmqctl reset

rabbitmqctl join_cluster rabbit@rabbitmq1 --ram

rabbitmqctl stop

sleep 2s

rabbitmq-server
