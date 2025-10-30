import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  // These stages define the load profile
  stages: [
    // Ramp up to 2000 "virtual users" (concurrent connections) over 30 seconds
    { duration: '30s', target: 2000 },
    
    // Stay at 2000 concurrent users for 1 minute
    { duration: '1m', target: 2000 },
    
    // Ramp down to 0 users over 10 seconds
    { duration: '10s', target: 0 },
  ],
  
  // k6 will abort the test if more than 5% of requests fail
  thresholds: {
    'http_req_failed': ['rate<0.05'], 
  },
};

// This is the main function for each virtual user
export default function () {
  const url = 'http://localhost:8080/login';
  
  // This is the data we'll send. It matches our default user.
  const payload = JSON.stringify({
    username: 'user',
    password: 'password',
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // Send the POST request
  const res = http.post(url, payload, params);

  // Check if the request was successful (HTTP 200)
  check(res, {
    'is status 200': (r) => r.status === 200,
  });

  sleep(1); // Wait for 1 second before this VU sends another request
}
