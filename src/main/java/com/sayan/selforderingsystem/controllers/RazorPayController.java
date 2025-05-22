package com.sayan.selforderingsystem.controllers;

import com.razorpay.RazorpayException;
import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.repositories.OrderRepository;
import com.sayan.selforderingsystem.services.EmailService;
import com.sayan.selforderingsystem.services.OrderService;
import com.sayan.selforderingsystem.services.RazorPayService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class RazorPayController {

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private RazorPayService razorPayService;

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestParam double amount
            , @RequestParam String currency){

        try {
            return new ResponseEntity<>(razorPayService.createOrder(amount, currency, "1324")
                    , HttpStatus.OK);
        } catch (RazorpayException e) {
            return new ResponseEntity<>(e.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestParam String orderId,
                                                 @RequestParam String razorpayPaymentId) {
        Optional<com.sayan.selforderingsystem.models.Order> orderOptional = orderRepository.findById(orderId);
        if(orderOptional.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order with ID " + orderId + " not found"
            );
        }

        emailService.senBillMail(orderId);
        razorPayService.confirmPayment(orderId, razorpayPaymentId);
        return ResponseEntity.ok("Payment confirmed and Bill is sent to your email.");
    }



}
