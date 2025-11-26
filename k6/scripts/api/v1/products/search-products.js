import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

const BASE_URL = 'http://localhost:8080';

export let options = {
    stages: [
        { duration: '5s', target: 50 },   // 0 → 50 VU
        { duration: '10s', target: 100 }, // 50 → 100 VU
        { duration: '10s', target: 200 }, // 100 → 200 VU
        { duration: '5s', target: 0 },    // 감소
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% 요청이 500ms 이하
        http_req_failed: ['rate<0.01'], // 실패율 1% 미만
    },
    ext: {
        'experimental-prometheus-rw': {
            port: 9911,
            // default: bind to 0.0.0.0 (ok)
        }
    }
};

export default function () {
    const productId = randomIntBetween(1, 100000);
    const brandId = randomIntBetween(1, 5000);
    const keyword = '';
    const sort = 'LIKES_DESC';
    const page = 5;
    const size = 20;


    const url = `${BASE_URL}/api/v1/products` +
            `?brandId=${brandId}` +
            `&keyword=${keyword}` +
            `&sort=${sort}` +
            `&page=${page}` +
            `&size=${size}`;

    const res = http.get(url, {

    });

    check(res, {
        'status is 200 or 404': r => r.status === 200 || r.status === 404,
        'response time < 500ms': r => r.timings.duration < 500,
    });

    sleep(1);
}