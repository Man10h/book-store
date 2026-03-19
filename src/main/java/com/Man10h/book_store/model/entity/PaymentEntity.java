package com.Man10h.book_store.model.entity;

import com.Man10h.book_store.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "provider")
    private String provider; // VNPAY, MOMO, PAYPAL...

    @Column(name = "transaction_id")
    private String transactionId;
    // mã transaction từ gateway (vnp_TransactionNo)

    @Column(name = "payment_url")
    private String paymentUrl;
    // url redirect sang gateway

    @Column(name = "txn_ref")
    private String txnRef;
    // vnp_TxnRef (reference payment)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private OrderEntity orderEntity;
}