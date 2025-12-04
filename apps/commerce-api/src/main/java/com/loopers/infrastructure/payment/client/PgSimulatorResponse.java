package com.loopers.infrastructure.payment.client;

import java.util.List;

public class PgSimulatorResponse {

    public record RequestTransaction(String transactionKey, String status, String description) {

    }

    public record GetTransaction(String transactionKey, String orderId, Long amount, String paymentCardType, String cardNo, String status, String description) {

    }

    public record GetTransactions(String orderUid, List<Transaction> transactions) {

        public record Transaction(String transactionKey, String status, String description) {

        }

    }
}
