### AS-IS

```bash
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
/  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: search-products.js
        output: -

     scenarios: (100.00%) 1 scenario, 200 max VUs, 1m0s max duration (incl. graceful stop):
              * default: Up to 200 looping VUs for 30s over 4 stages (gracefulRampDown: 30s, gracefulStop: 30s)



  █ THRESHOLDS

    http_req_duration
    ✗ 'p(95)<500' p(95)=664.64ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS

    checks_total.......: 4692   152.188488/s
    checks_succeeded...: 91.32% 4285 out of 4692
    checks_failed......: 8.67%  407 out of 4692

    ✓ status is 200 or 404
    ✗ response time < 500ms
      ↳  82% — ✓ 1939 / ✗ 407

    HTTP
    http_req_duration..............: avg=271.74ms min=29.49ms med=206.09ms max=1.25s p(90)=578.96ms p(95)=664.64ms
      { expected_response:true }...: avg=271.74ms min=29.49ms med=206.09ms max=1.25s p(90)=578.96ms p(95)=664.64ms
    http_req_failed................: 0.00%  0 out of 2346
    http_reqs......................: 2346   76.094244/s

    EXECUTION
    iteration_duration.............: avg=1.27s    min=1.02s   med=1.2s     max=2.26s p(90)=1.57s    p(95)=1.66s
    iterations.....................: 2346   76.094244/s
    vus............................: 19     min=10        max=199
    vus_max........................: 200    min=200       max=200

    NETWORK
    data_received..................: 385 kB 13 kB/s
    data_sent......................: 323 kB 11 kB/s




running (0m30.8s), 000/200 VUs, 2346 complete and 0 interrupted iterations
default ✓ [======================================] 000/200 VUs  30s
ERRO[0031] thresholds on metrics 'http_req_duration' have been crossed
```

### TO-BE
```bash

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: search-products.js
        output: -

     scenarios: (100.00%) 1 scenario, 200 max VUs, 1m0s max duration (incl. graceful stop):
              * default: Up to 200 looping VUs for 30s over 4 stages (gracefulRampDown: 30s, gracefulStop: 30s)



  █ THRESHOLDS

    http_req_duration
    ✓ 'p(95)<500' p(95)=61.31ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS

    checks_total.......: 5806   187.660795/s
    checks_succeeded...: 99.86% 5798 out of 5806
    checks_failed......: 0.13%  8 out of 5806

    ✓ status is 200 or 404
    ✗ response time < 500ms
      ↳  99% — ✓ 2895 / ✗ 8

    HTTP
    http_req_duration..............: avg=21.81ms min=2.41ms med=13.26ms max=1.24s p(90)=37.2ms p(95)=61.31ms
      { expected_response:true }...: avg=21.81ms min=2.41ms med=13.26ms max=1.24s p(90)=37.2ms p(95)=61.31ms
    http_req_failed................: 0.00%  0 out of 2903
    http_reqs......................: 2903   93.830398/s

    EXECUTION
    iteration_duration.............: avg=1.02s   min=1s     med=1.01s   max=2.24s p(90)=1.04s  p(95)=1.06s
    iterations.....................: 2903   93.830398/s
    vus............................: 18     min=10        max=199
    vus_max........................: 200    min=200       max=200

    NETWORK
    data_received..................: 476 kB 15 kB/s
    data_sent......................: 400 kB 13 kB/s




running (0m30.9s), 000/200 VUs, 2903 complete and 0 interrupted iterations
default ✓ [======================================] 000/200 VUs  30s
```