package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.ErrorException;
import com.Man10h.book_store.exception.business.CartNotFoundException;
import com.Man10h.book_store.exception.business.ItemNotFoundException;
import com.Man10h.book_store.exception.business.PaymentNotFoundException;
import com.Man10h.book_store.model.entity.*;
import com.Man10h.book_store.model.enums.OrderStatus;
import com.Man10h.book_store.model.enums.PaymentStatus;
import com.Man10h.book_store.repository.*;
import com.Man10h.book_store.service.PaymentService;
import com.Man10h.book_store.util.VNPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VNPayUtil vnPayUtil;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public String checkout(UserEntity userEntity) {
        //query detail
        List<CartEntity> cartEntityList = cartRepository.findByUserEntity(userEntity);
        if(cartEntityList.isEmpty()){
            throw new CartNotFoundException("Cart not found");
        }
        CartEntity cartEntity = cartEntityList.getFirst();
        List<ItemEntity> itemEntityList = cartEntity.getItemEntityList();
        if(itemEntityList.isEmpty()){
            throw new ItemNotFoundException("Item not found");
        }
        double totalPrice = 0.0;
        for(ItemEntity itemEntity : itemEntityList){
            totalPrice+=(itemEntity.getQuantity() * itemEntity.getBookEntity().getPrice());
        }

        //command order
        OrderEntity orderEntity = OrderEntity.builder()
                .orderItemEntityList(new ArrayList<>())
                .userEntity(userEntity)
                .status(OrderStatus.CREATED)
                .totalPrice(totalPrice)
                .build();
        orderRepository.save(orderEntity);

        //command order item
        for(ItemEntity itemEntity : itemEntityList){
            OrderItemEntity orderItemEntity = OrderItemEntity.builder()
                    .orderEntity(orderEntity)
                    .bookEntity(itemEntity.getBookEntity())
                    .quantity(itemEntity.getQuantity())
                    .price(itemEntity.getBookEntity().getPrice())
                    .build();
            orderItemRepository.save(orderItemEntity);
        }

        //command payment
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .orderEntity(orderEntity)
                .amount(totalPrice)
                .status(PaymentStatus.PENDING)
                .provider("VNPAY")
                .createdAt(LocalDateTime.now())
                .txnRef(UUID.randomUUID().toString())
                .build();
        paymentRepository.save(paymentEntity);

        //clear cart
        itemRepository.deleteByCartEntity(cartEntity);

        try{
            return vnPayUtil.createPaymentUrl(paymentEntity);
        } catch (Exception e) {
            throw new ErrorException("Cannot create payment url");
        }
    }

    @Transactional
    public String callback(Map<String, String> params) {

        if(!vnPayUtil.verifySignature(params)){
            throw new ErrorException("Invalid VNPay signature");
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        PaymentEntity payment = paymentRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if(payment.getStatus() != PaymentStatus.PENDING){
            return "redirect:/payment-result";
        }

        OrderEntity order = payment.getOrderEntity();

        payment.setTransactionId(params.get("vnp_TransactionNo"));

        if("00".equals(responseCode)){

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            order.setStatus(OrderStatus.PAID);

        }else{

            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);

        }

        return "redirect:/payment-result";
    }
}
