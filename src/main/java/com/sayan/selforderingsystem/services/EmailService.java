package com.sayan.selforderingsystem.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.models.OrderItem;
import com.sayan.selforderingsystem.repositories.OrderRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender javaMailSender;
    private OrderRepository orderRepository;

    public void senBillMail(String orderId){
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if(orderOptional.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order with ID " + orderId + " not found"
            );
        }

        Order order = orderOptional.get();

        if(order.getEmail() == null || order.getEmail().isBlank()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email not provided for order with ID " + orderId
            );
        }

        String subject = "Order #" + order.getOrderNumber() + " - Your Bill";
        String htmlBody = "<p>Dear Customer,</p><p>Please find attached the invoice for your recent order.</p><p>Thank you! 🍽️</p>";


        try {
            ByteArrayOutputStream invoicePdf = generateInvoicePdf(order);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

            mimeMessageHelper.setTo(order.getEmail());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlBody, true);
            mimeMessageHelper.addAttachment("Invoice_Order_" + order.getOrderNumber() + ".pdf",
                    new ByteArrayResource(invoicePdf.toByteArray()));

            javaMailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String generateHtmlBill(Order order) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><body>");
        sb.append("<h2 style='color: #2c3e50;'>🧾 Your Order Bill</h2>");
        sb.append("<p><strong>Order No:</strong> ").append(order.getOrderNumber()).append("</p>");
        sb.append("<p><strong>Table:</strong> ").append(order.getTableNumber()).append("</p>");

        sb.append("<table style='border-collapse: collapse; width: 100%;'>");
        sb.append("<thead><tr style='background-color: #f2f2f2;'>");
        sb.append("<th style='border: 1px solid #ddd; padding: 8px;'>Item</th>");
        sb.append("<th style='border: 1px solid #ddd; padding: 8px;'>Qty</th>");
        sb.append("<th style='border: 1px solid #ddd; padding: 8px;'>Price</th>");
        sb.append("</tr></thead><tbody>");

        for (OrderItem item : order.getItems()) {
            sb.append("<tr>");
            sb.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(item.getName()).append("</td>");
            sb.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(item.getQuantity()).append("</td>");
            sb.append("<td style='border: 1px solid #ddd; padding: 8px;'>₹")
                    .append(item.getPrice() * item.getQuantity()).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table>");
        sb.append("<p style='margin-top: 16px; font-size: 18px;'><strong>Total: ₹")
                .append(order.getTotalAmount()).append("</strong></p>");
        sb.append("<p>Thank you for ordering! 🍽️</p>");
        sb.append("</body></html>");

        return sb.toString();
    }

    private ByteArrayOutputStream generateInvoicePdf(Order order) throws DocumentException {
        Document document = new Document(PageSize.A6); // Small receipt-style page
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font smallFont = new Font(Font.HELVETICA, 9);
        Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);

        // Centered welcome text
        Paragraph welcome = new Paragraph("WELCOME!!!\n\n", titleFont);
        welcome.setAlignment(Element.ALIGN_CENTER);
        document.add(welcome);

        // Original Receipt title
        Paragraph receiptTitle = new Paragraph("Original Receipt\n\n", boldFont);
        receiptTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(receiptTitle);

        // Date & Time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String date = order.getOrderDate().format(dateFormatter);
        String time = order.getOrderDate().format(timeFormatter);
        Paragraph dateTime = new Paragraph("Date: " + date + "    Time: " + time + "\n", smallFont);
        document.add(dateTime);

        // Table number & Receipt No.
        Paragraph info = new Paragraph("Table: #" + order.getTableNumber() +
                "\nReceipt No.: " + order.getOrderNumber() + "\n\n", smallFont);
        document.add(info);

        // Item table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{4, 1, 2, 2});

        Stream.of("Item", "Qty", "Price", "Subtotal")
                .forEach(col -> {
                    PdfPCell cell = new PdfPCell(new Phrase(col, boldFont));
                    cell.setBorder(Rectangle.BOTTOM);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                });

        for (OrderItem item : order.getItems()) {
            table.addCell(new Phrase(item.getName(), smallFont));
            table.addCell(new Phrase(String.valueOf(item.getQuantity()), smallFont));
            table.addCell(new Phrase("Rs " + (int) item.getPrice(), smallFont));
            table.addCell(new Phrase("Rs " + (int) (item.getPrice() * item.getQuantity()), smallFont));

        }

        document.add(table);

        // Total
        Paragraph total = new Paragraph("\nTotal: Rs " + (int) order.getTotalAmount() + "\n", boldFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        // Mode (placeholder for now)
        Paragraph mode = new Paragraph("\nMODE: UPI\n", smallFont);
        document.add(mode);

        // Footer
        Paragraph footer = new Paragraph("\nSAVE PAPER SAVE NATURE !!\nThank you for a delicious meal. 😊", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out;
    }

}
