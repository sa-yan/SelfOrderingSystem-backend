package com.sayan.selforderingsystem.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.sayan.selforderingsystem.models.OrderStatus;
import com.sayan.selforderingsystem.repositories.OrderRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RazorPayService {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    public String createOrder(double amount,
                               String currency,
                               String orderId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

        JSONObject orderReq = new JSONObject();

        orderReq.put("amount", amount*100);
        orderReq.put("currency", currency);
        orderReq.put("receipt", orderId);

        Order order = razorpayClient.orders.create(orderReq);
        return order.toString();
    }

    public void confirmPayment(String orderId, String razorpayPaymentId) {
        com.sayan.selforderingsystem.models.Order order1 =
                orderService.getOrderById(orderId);

        if (order1!=null) {
            order1.setPaymentSuccessful(true);
            order1.setRazorpayPaymentId(razorpayPaymentId);
            orderRepository.save(order1);
        }
    }

}
