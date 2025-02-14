# Client README

## Overview

This client is a multithreaded Java application designed to send 200,000 POST requests to the server. It dynamically scales threads to optimize request throughput and ensures proper event handling.

## Prerequisites

- Java 17
- Maven (for dependency management)
- An active server running on an accessible endpoint
- Apache Tomcat (if running the server locally)

## Setup & Configuration

### 1. Clone the Repository

```sh
git clone https://github.com/zekun-fang/cs6650.git
cd /CS6650Assignment1/client  
```

### 2. Modify Server URL and Number of Threads

Update `MultiThreadedLiftRideClient.java`:

```java
private static final String SERVER_URL = "http://your-server-ip:8080/server_war_exploded";
private static final int INITIAL_THREADS = 200;
```

Replace `your-server-ip` with the actual server address

and modify your client number of threads with Parameter `INITIAL_THREADS`.

### 3. Compile & Build

Use Maven to build the project:

```sh
mvn clean install
```

### 4. Run the Client

Execute the client using:

```sh
java -jar target/client.jar
```

### 5. Verify Logs & Performance Metrics

After execution, check output of MultiThreadedLiftRideClient and generated `request_logs.csv` for latency analysis and statistics.

## Expected Output

```
======= Client 1 Output =======
Successful requests: 200000
Failed requests: 0
Total response time: 39594 ms
Throughput: 5051 requests per second
```

For performance analysis, refer to `ClientLatencyAnalyzer.java`.

## Troubleshooting

### Common Issues & Fixes

- **Connection Refused**: Ensure the server is running and reachable.
- **Slow Throughput**: Check network latency and adjust thread configurations.
- **CSV File Missing**: Verify file write permissions.

For further support, refer to the course documentation or post in the discussion forum.