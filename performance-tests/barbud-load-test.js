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
    http_req_duration: ['p(95)<100'],     // 95% of requests below 100ms
  },
};

const BASE_URL = __ENV.BASE_URL;

const TEST_USER = {
  email: __ENV.TEST_EMAIL,
  password: __ENV.TEST_PASSWORD,
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
    'login has refresh token': (res) => {
      try {
        const body = res.json();
        return body.refreshToken !== undefined && body.refreshToken !== null;
      } catch {
        return false;
      }
    },
  });

  const body = loginRes.json();

  return {
    accessToken: body.accessToken,
    refreshToken: body.refreshToken,
    userId: body.userId,
  };
}

export default function (data) {

  const MY_BARS_TEST = {
    userId: data.userId
  }

  const REFRESH_TOKENS = {
    refreshToken: data.refreshToken
  }

  const authHeaders = {
    headers: {
      Authorization: `Bearer ${data.accessToken}`,
      'Content-Type': 'application/json',
    },
  };

  const refreshTokens = http.post(
      `${BASE_URL}/auth/refresh`,
      JSON.stringify(REFRESH_TOKENS),
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
  );

  check(refreshTokens,{
    "refresh status is 200 or 204": (res) => res.status === 200,
    'refresh response is fast': (res) => res.timings.duration < 500,
  })

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

  let barId = null;

  if (myBarsRes.status === 200) {
    try {
      const myBarsBody = myBarsRes.json();

      if (Array.isArray(myBarsBody) && myBarsBody.length > 0) {
        barId = myBarsBody[0].id;
      }
    } catch (e) {
      console.log(`Could not parse my-bars response: ${e}`);
    }
  }

  const BAR_DETAILS_TEST = {
    userId: data.userId,
    barId: barId
  }

  if (barId !== null) {
    const barDetailsRes = http.post(
        `${BASE_URL}/bar/details`,
        JSON.stringify(BAR_DETAILS_TEST),
        authHeaders
    );

    check(barDetailsRes, {
      'bar details status is 200': (res) => res.status === 200,
      'bar details response is fast': (res) => res.timings.duration < 700,
    });
  } else {
    console.log('No bar found, skipping bar details call');
  }

  sleep(1);
}