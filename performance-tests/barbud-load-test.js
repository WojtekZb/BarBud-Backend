import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 5 },
    { duration: '1m', target: 10 },
    { duration: '30s', target: 0 },
  ],

  thresholds: {
    http_req_failed: ['rate<0.05'],       // Less than 5% failed requests
    http_req_duration: ['p(95)<800'],     // 95% of requests below 800ms
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const TEST_USER = {
  email: __ENV.TEST_EMAIL || 'admin@example.com',
  password: __ENV.TEST_PASSWORD || 'BorekILolek1!',
};

export function setup() {
  const loginRes = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify(TEST_USER),
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );

  check(loginRes, {
    'login status is 200': (res) => res.status === 200,
    'login has access token': (res) => {
      try {
        const body = res.json();
        return body.accessToken !== undefined && body.accessToken !== null;
      } catch {
        return false;
      }
    },
  });

  const body = loginRes.json();

  return {
    accessToken: body.accessToken,
    userId: body.userId,
  };
}

export default function (data) {

  const MY_BARS_TEST = {
    userId: data.userId
  }

  const authHeaders = {
    headers: {
      Authorization: `Bearer ${data.accessToken}`,
      'Content-Type': 'application/json',
    },
  };

  const ingredientsRes = http.get(`${BASE_URL}/bar/ingredients`, authHeaders);

  check(ingredientsRes, {
    'ingredients status is 200': (res) => res.status === 200,
    'ingredients response is fast': (res) => res.timings.duration < 500,
  });

  const myBarsRes = http.post(
    `${BASE_URL}/bar/my-bars`,
      JSON.stringify(MY_BARS_TEST),
      authHeaders
  );

  check(myBarsRes, {
    'my bars status is 200 or 204': (res) => res.status === 200 || res.status === 204,
    'my bars response is fast': (res) => res.timings.duration < 700,
  });

  sleep(1);
}