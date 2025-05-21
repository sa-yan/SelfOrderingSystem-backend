package com.sayan.selforderingsystem.controllers;

import com.razorpay.RazorpayException;
import com.sayan.selforderingsystem.services.RazorPayService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class RazorPayController {

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
        razorPayService.confirmPayment(orderId, razorpayPaymentId);
        return ResponseEntity.ok("Payment confirmed and order updated.");
    }


}
