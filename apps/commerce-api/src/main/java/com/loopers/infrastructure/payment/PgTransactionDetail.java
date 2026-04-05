package com.loopers.infrastructure.payment;

record PgTransactionDetail(String transactionId, String orderId, String status, Long amount) {
}
