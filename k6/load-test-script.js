import http from 'k6/http';
import {check, sleep} from 'k6';
import {Counter} from 'k6/metrics';

const BASE_URL = 'http://host.docker.internal:8080';

// -----------------------
// k6 옵션 및 시나리오 설정
// -----------------------
export const options = {
    discardResponseBodies: true,
    scenarios: {
        // [Peak Test] - 쿠폰 발급 및 주문의 피크 상황 시뮬레이션 (각각 10초 준비 시간 적용)
        peak_coupon_issue: {
            executor: 'shared-iterations',
            exec: 'issueCoupon',
            vus: 300,
            iterations: 300,
            maxDuration: '30s',
            startTime: '10s',
        },
        peak_product_order: {
            executor: 'shared-iterations',
            exec: 'orderProduct',
            vus: 300,
            iterations: 300,
            maxDuration: '30s',
            startTime: '20s',
        },

        // [Load Test] - 정상 운영 시 예상되는 평균 부하 (10초 준비 후 시작)
        load_coupon_issue: {
            executor: 'constant-vus',
            exec: 'issueCoupon',
            vus: 100,
            duration: '1m',
            startTime: '40s',
        },
        load_product_order: {
            executor: 'constant-vus',
            exec: 'orderProduct',
            vus: 100,
            duration: '1m',
            startTime: '50s',
        },

        // [Endurance Test] - 장기간 부하 테스트 (10초 준비 후 시작, 실행 시간을 3분으로 단축)
        endurance_coupon_issue: {
            executor: 'constant-vus',
            exec: 'issueCoupon',
            vus: 50,
            duration: '3m',
            startTime: '1m50s',
        },
        endurance_product_order: {
            executor: 'constant-vus',
            exec: 'orderProduct',
            vus: 50,
            duration: '3m',
            startTime: '2m',
        },

        // [Stress Test] - 시스템 한계 부하 테스트 (10초 준비 후 시작, Endurance Test 종료 후 시작)
        stress_coupon_issue: {
            executor: 'ramping-vus',
            exec: 'issueCoupon',
            startVUs: 10,
            stages: [
                {duration: '30s', target: 300},
                {duration: '30s', target: 300},
                {duration: '30s', target: 500},
                {duration: '30s', target: 0},
            ],
            startTime: '5m10s',  // endurance 테스트 종료 후 10초 준비
        },
        stress_product_order: {
            executor: 'ramping-vus',
            exec: 'orderProduct',
            startVUs: 10,
            stages: [
                {duration: '30s', target: 300},
                {duration: '30s', target: 300},
                {duration: '30s', target: 500},
                {duration: '30s', target: 0},
            ],
            startTime: '5m20s',  // stress_coupon_issue 이후 10초 준비
        },
    },
};

// -----------------------
// 커스텀 메트릭 (Counters)
// -----------------------
export let issue_success200 = new Counter('issue_success200');
export let issue_failure400 = new Counter('issue_failure400');
export let issue_other = new Counter('issue_other');

export let order_success200 = new Counter('order_success200');
export let order_failure400 = new Counter('order_failure400');
export let order_other = new Counter('order_other');

// -----------------------
// 공통 헬퍼 함수: 응답 상태 처리 및 메트릭 기록
// -----------------------
function handleResponse(res, type) {
    if (res.status === 200) {
        if (type === 'issue') {
            issue_success200.add(1);
        } else {
            order_success200.add(1);
        }
    } else if (res.status === 400) {
        if (type === 'issue') {
            issue_failure400.add(1);
        } else {
            order_failure400.add(1);
        }
    } else {
        if (type === 'issue') {
            issue_other.add(1);
        } else {
            order_other.add(1);
        }
        console.error(`${type} - unexpected status code: ${res.status}`);
    }

    check(res, {
        [`${type}: status is either 200 or 400`]: (r) => r.status === 200 || r.status === 400,
    });
}

// -----------------------
// 시나리오 함수: 쿠폰 발급
// -----------------------
export function issueCoupon() {
    const userId = __VU;
    const url = `${BASE_URL}/api/v1/coupons/issue`;
    const payload = JSON.stringify({
        userId: userId,
        couponId: 1,
        issuedAt: new Date().toISOString(),
    });
    const params = {headers: {'Content-Type': 'application/json'}};

    const res = http.post(url, payload, params);
    handleResponse(res, 'issue');

    sleep(0.1);
}

// -----------------------
// 시나리오 함수: 상품 주문
// -----------------------
export function orderProduct() {
    const userId = __VU;
    const url = `${BASE_URL}/api/v1/order`;
    const payload = JSON.stringify({
        userId: userId,
        couponId: null,
        orderItems: [{productId: 1, quantity: 1}],
        withCoupon: false,
    });
    const params = {headers: {'Content-Type': 'application/json'}};

    const res = http.post(url, payload, params);
    handleResponse(res, 'order');

    sleep(0.1);
}
