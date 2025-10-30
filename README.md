Virtual Threads vs. Platform Threads Demo

This project demonstrates the powerful difference in performance between traditional platform threads and Java 21's virtual threads for I/O-bound applications.

Prerequisites

Java 21+: Virtual threads are a final feature in Java 21.

Check: java -version

Maven: To build and run the Spring Boot app.

Check: mvn -version

Docker & Docker Compose: To easily run the Postgres database.

Check: docker -v and docker-compose -v

k6: A modern, easy-to-use load testing tool.

Install on Kubuntu: sudo gpg -k ... (Follow official k6.io instructions: https://www.google.com/search?q=https://k6.io/docs/getting-started/installation/)

The "Request Forgery" / Load Test

You mentioned "request forge with diff IPs." For this demo, you don't need different IPs. The bottleneck you want to expose is server-side thread exhaustion due to concurrency, not network-level IP blocking. Our k6 script will simulate 2,000 users all hitting your server at the same time. This is the perfect test.

Step 1: Start the Database

In your project's root directory (where docker-compose.yml is):

docker-compose up -d


This will start a Postgres database in the background. You only need to do this once.

Step 2: Run TEST 1 (Classic Platform Threads)

Open src/main/resources/application.properties.

Make sure the "TEST 1" lines are uncommented and the "TEST 2" line is commented:

# --- TEST 1: PLATFORM THREADS (The "Old Way") ---
# Uncomment these lines for the first test

spring.threads.virtual.enabled=false
server.tomcat.threads.max=200 
server.tomcat.threads.min-spare=10


# --- TEST 2: VIRTUAL THREADS (The "New Way") ---
# Uncomment this line for the second test.
# This one line enables virtual threads for all web requests.
#
# spring.threads.virtual.enabled=true


Run the Spring Boot application:

mvn spring-boot:run


Your app is now running, limited to a pool of 200 platform threads.

In a new terminal, run the load test:

k6 run load-test.js


!!! OBSERVE THE RESULTS !!!
You will see the k6 output struggling. Because your app can only handle 200 concurrent requests (and each request takes 200ms), it cannot handle the 2,000 concurrent users.

http_req_failed will spike.

http_req_duration (especially p(95) and p(99)) will be extremely high (e.g., 5-10+ seconds) instead of the ~200ms we expect.

The checks will fail.

Why? The 200 platform threads are all blocked waiting for Thread.sleep(). The server is saturated.

Stop the Spring Boot app (press CTRL+C in its terminal).

Step 3: Run TEST 2 (Virtual Threads)

Open src/main/resources/application.properties again.

Comment out the "TEST 1" lines and uncomment the "TEST 2" line:

# --- TEST 1: PLATFORM THREADS (The "Old Way") ---
# Uncomment these lines for the first test
# 
# spring.threads.virtual.enabled=false
# server.tomcat.threads.max=200 
# server.tomcat.threads.min-spare=10


# --- TEST 2: VIRTUAL THREADS (The "New Way") ---
# Uncomment this line for the second test.
# This one line enables virtual threads for all web requests.
#
spring.threads.virtual.enabled=true


Run the Spring Boot application again:

mvn spring-boot:run


Your app is now running, and it will create a new virtual thread for every request.

In your other terminal, run the exact same load test:

k6 run load-test.js


!!! OBSERVE THE NEW RESULTS !!!
It will be a night-and-day difference.

http_req_failed will be 0% or very close to it.

http_req_duration (all percentiles) will be stable and low, very close to our 200ms Thread.sleep() delay.

The checks will all pass.

Why? Even with 2,000 concurrent requests, the app just creates 2,000 lightweight virtual threads. When each one hits Thread.sleep() or the database, it "unmounts" from the underlying OS carrier thread, freeing it to do other work. The app handles the load effortlessly.

This simple, repeatable demo clearly and dramatically shows the power of virtual threads for I/O-bound workloads.