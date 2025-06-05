# Generic IOT Infrastructure

This repository contains an example IoT gateway and supporting modules.

## Repository Overview

This project implements a small IoT "gateway" server in Java. It shows a
simple JSON template for sending commands to the system and explains that
commands may be either general (company agnostic) or company specific.

### Core Components

**WaitablePQueue and ThreadPool**

`WaitablePQueue` is a custom priority queue with a semaphore based blocking
mechanism. Items are enqueued and dequeued with synchronisation so worker
threads can wait when the queue is empty.

`ThreadPool` wraps this queue to run tasks asynchronously. The pool supports
priorities, pausing/resuming, dynamic resizing and graceful shutdown.

**AdminDB**

`AdminDB` is a singleton service that handles database actions. It supports
multiple DBMS types (MySQL and MongoDB) through inner handler classes.
Requests are mapped to operations such as registering a company or fetching
products and are executed via `handleRequest`.

**Request Processing**

Incoming data is parsed by `RequestProcessService`. A factory maps command
names to constructors so the correct `Command` implementation can be created
and executed in a thread pool.

**Plugin System**

The system can load additional commands or HTTP handlers at runtime.
`PluginMediator` watches a resource directory for new JARs, loads classes that
implement specified interfaces and notifies listeners when new plugins appear.
`DirMonitor` uses Java's `WatchService` to track new files or directories and
trigger plugin loading when a file is created.

**Networking**

`ConnectionService` accepts TCP, UDP and HTTP connections. HTTP traffic is
routed to handlers such as `ProductHttpHandler` or `UpdateHttpHandler`, which
translate HTTP requests into JSON commands.

**GatewayServer**

`GatewayServer` ties everything together. On start-up it registers plugin
listeners, initialises the database handlers, creates network endpoints
(TCP/UDP/HTTP) and starts the server threads.

### Tests

The repository includes JUnit tests for the thread pool and queue as well as
some commented out experiments. Many test files are partially or entirely
commented.

### Learning Path

For newcomers wanting to extend or use this code:

- **Understand the command flow** – Review how `RequestProcessService` parses
  JSON and invokes command classes loaded via the plugin system.
- **Explore the HTTP handlers** – See how `ProductHttpHandler`,
  `IotsHttpHandler` and others translate REST paths into JSON for the
  `RequestProcessService`.
- **Inspect AdminDB handlers** – The MySQL and MongoDB handlers show how CRUD
  operations are implemented; these can be extended for additional DBMS types
  or operations.
- **Look into plugin loading** – Examine how `PluginMediator` and
  `DirMonitor` dynamically load new commands or handlers from JAR files.
- **Check the tests** – Although some are incomplete, the `ThreadPool` and
  `WaitablePQueue` tests demonstrate expected behaviour of the concurrency
  utilities.

Once familiar with these parts you can experiment by adding your own command
implementations (perhaps packaged as JARs) and hooking them into the gateway
via the plugin system.

## JSON Representation

Base JSON format used to register a new product:

```json
{
    "Key": "CommandName",
    "Data": {
        "Company_ID": "companyID",
        "Product_Name": "productName",
        "Description": "Description"
    }
}
```

## Command Registration

Commands implement the `Command` interface.

- `companyID = 0` – general commands
- `companyID != 0` – customized commands

## Building

The project uses Maven. To compile the core library and run tests execute:

```bash
mvn clean install
```

The `WebServer` directory contains a separate Maven module that builds a WAR
file. It can be built on its own with:

```bash
mvn -f WebServer/pom.xml clean package
```

## Installation and Running

1. Install **Java 8 (JDK 1.8)** and **Maven 3.6+** on your system.
2. Clone this repository and build the project:

   ```bash
   mvn clean package
   ```

   This compiles the code and produces `target/iot-infrastructure-1.0-SNAPSHOT.jar`.

3. (Optional) run the unit tests:

   ```bash
   mvn test
   ```

4. Start the gateway server using the built JAR:

   ```bash
   java -cp target/iot-infrastructure-1.0-SNAPSHOT.jar maingatewayserver.GatewayServer
   ```

5. To build the web module separately:

   ```bash
   mvn -f WebServer/pom.xml clean package
   ```

The server listens for TCP, UDP and HTTP connections once started.
