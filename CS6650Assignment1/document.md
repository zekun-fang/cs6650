# CS6650 Assignment 1 Submission

## 1. Git Repository

- Repository URL: `https://github.com/zekun-fang/cs6650.git`
- The repository contains:
  - `server/` - Java Servlet API implementation
  - `client` - Initial multithreaded client implementation and Enhanced client with performance metrics
  - `out/artifacts/server_war/server_war.war` - WAR file for easy deployment
  - `Client README.md` - Instructions for running the client

## 2. Client Design Description

### 2.1 Overview

The client is a **multi-threaded Java HTTP client** that generates and sends **200K POST requests** to a remote server. It ensures efficient event generation and optimized request handling.

### 2.2 Major Components

#### - `MultiThreadedLiftRideClient.java`

- Manages thread pool execution.
- Dynamically scales threads based on workload.
- Ensures proper synchronization of request execution.

#### - `HttpWorker.java`

- Sends HTTP POST requests to the server.
- Handles retries and failures.
- Logs request latency for analysis.

#### - `EventGenerator.java`

- Produces ski lift ride events asynchronously.
- Uses a **BlockingQueue** to efficiently distribute events.

#### - `ClientLatencyAnalyzer.java`

- Analyzes request latencies.
- Computes mean, median, p99, min, max latencies.

## 3. Client Part 1 - Load Test Execution

### 3.1 Execution Details

- **Number of Initial Threads**: 200
- **Requests per Thread**: 1000
- **Total Requests Sent**: 200,000

### 3.2 Results

```
======= Client 1 Output =======
Successful requests: 200000
Failed requests: 0
Total response time: 39594 ms
Throughput: 5051 requests per second
```

**Screenshot:** *![445be0c407116a55d7518a97fee1970](C:\Users\Zekun Fang\OneDrive\文档\WeChat Files\wxid_cmbuee6ut4ie11\FileStorage\Temp\445be0c407116a55d7518a97fee1970.png)*

### 3.3 Throughput Comparison

#### Little’s Law Calculation

Little’s Law states that:

L= λ W

Where:

- L is the **average number of concurrent requests** in the system.
- λ is the **throughput (requests per second)**.
- W is the **average response time (seconds)**.

Using the single-thread request latency, we estimate W as: 0.03869 s

Given that **200 threads** were actively sending requests, the predicted throughput is:

λ = 200/0.03869 =  5169 requests/sec

This results in:

- **Actual Throughput:** 5051 requests per second
- **Predicted Throughput (Little’s Law):** 5169 requests per second
- **Difference:** -2% deviation

This comparison validates the client’s efficiency and adherence to theoretical performance expectations.

## 4. Client Part 2 - Performance Metrics

### 4.1 Execution Details

The client logs each request's latency and computes statistics upon completion.

### 4.2 Results

```
======= Client 2 Output =======
Mean Response Time: 38.69 ms
Median Response Time: 33 ms
Min Response Time: 12 ms
Max Response Time: 1661 ms
Response Time at 99th Percentile: 289 ms
Throughput per individual thread: 25.85 requests/sec
Estimated overall throughput with 200 threads: 5169.81 requests/sec
```

**Screenshot:** *![c1c392d6ce407eb43254f8f7e7f127d](C:\Users\Zekun Fang\OneDrive\文档\WeChat Files\wxid_cmbuee6ut4ie11\FileStorage\Temp\c1c392d6ce407eb43254f8f7e7f127d.png)*

## 5. Server Deployment on EC2

- **Base URL:** `http://35.86.138.226:8080/server_war/`
- **Deployed using:** Apache Tomcat
- **Tested using:** Postman & Client

**Screenshot of EC2 Server Running:** *![0cd9688acf24a4d08d46fdb6f145706](C:\Users\Zekun Fang\OneDrive\文档\WeChat Files\wxid_cmbuee6ut4ie11\FileStorage\Temp\0cd9688acf24a4d08d46fdb6f145706.png)*

## 6. Additional Testing

### 6.1 Postman API Test

- Verified API functionality manually using Postman.

**Screenshot of Postman Test:** *![16b0cf38b42ce71b27462815d68ad97](C:\Users\Zekun Fang\OneDrive\文档\WeChat Files\wxid_cmbuee6ut4ie11\FileStorage\Temp\16b0cf38b42ce71b27462815d68ad97.png)*

## 7. Plot of throughput over time

![output](C:\Users\Zekun Fang\Downloads\output.png)

## 8. Conclusion

- Successfully implemented a high-performance multithreaded client.
- Achieved **optimal throughput** and **low latency**.
- Deployed and tested successfully on AWS EC2.
- Additional enhancements with **latency analysis** and plot of throughput over time