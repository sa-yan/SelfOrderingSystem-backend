package com.sayan.selforderingsystem.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.models.OrderItem;
import com.sayan.selforderingsystem.repositories.OrderRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final OrderRepository orderRepository;

    // "From" address for outgoing mail; must be a verified sender in the SMTP provider (Brevo)
    @Value("${app.mail.from}")
    private String fromAddress;

    public EmailService(JavaMailSender javaMailSender, OrderRepository orderRepository) {
        this.javaMailSender = javaMailSender;
        this.orderRepository = orderRepository;
    }

    // Runs on a background thread so payment confirmation never waits on SMTP
    @Async
    public void sendBillMail(String orderId){
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
        String htmlBody = ""
                + "<div style=\"font-family:Arial,Helvetica,sans-serif;max-width:480px;margin:auto;"
                + "border:1px solid #eee;border-radius:12px;overflow:hidden\">"
                + "<div style=\"background:#ea580c;padding:22px;text-align:center\">"
                + "<h1 style=\"color:#ffffff;margin:0;font-size:22px;letter-spacing:-0.5px\">Food Corner</h1>"
                + "<p style=\"color:#ffedd5;margin:4px 0 0;font-size:12px\">Fresh food, straight to your table</p>"
                + "</div>"
                + "<div style=\"padding:24px;color:#1f2428\">"
                + "<p style=\"margin:0 0 12px\">Hi there,</p>"
                + "<p style=\"margin:0 0 12px\">Thanks for your order! Your payment for "
                + "<b>Order #" + order.getOrderNumber() + "</b> was successful.</p>"
                + "<p style=\"margin:0 0 12px\">Your itemised invoice is attached as a PDF.</p>"
                + "<div style=\"background:#faf7f2;border-radius:10px;padding:16px;margin:18px 0\">"
                + "<p style=\"margin:0;font-size:13px;color:#6b7280\">Table " + order.getTableNumber() + " &middot; Total Paid</p>"
                + "<p style=\"margin:6px 0 0;font-size:22px;font-weight:bold;color:#ea580c\">Rs "
                + String.format("%,.2f", order.getTotalAmount()) + "</p>"
                + "</div>"
                + "<p style=\"margin:16px 0 0;font-size:13px;color:#6b7280\">We hope you enjoyed your meal. "
                + "See you again soon! 🍽️</p>"
                + "</div>"
                + "<div style=\"background:#faf7f2;padding:14px;text-align:center;font-size:11px;color:#9aa3ad\">"
                + "Food Corner &middot; This is an automated order confirmation.</div>"
                + "</div>";


        try {
            ByteArrayOutputStream invoicePdf = generateInvoicePdf(order);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

            mimeMessageHelper.setFrom(fromAddress);
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

    // Brand palette — mirrors the Food Corner frontend design tokens
    private static final Color BRAND = new Color(234, 88, 12);    // #ea580c
    private static final Color INK = new Color(31, 36, 40);
    private static final Color MUTED = new Color(107, 114, 128);
    private static final Color LINE = new Color(230, 226, 217);
    private static final Color ZEBRA = new Color(250, 247, 242);
    private static final Color PAID_GREEN = new Color(22, 163, 74);

    private ByteArrayOutputStream generateInvoicePdf(Order order) throws DocumentException {
        Document document = new Document(PageSize.A5, 36, 36, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font brandFont = new Font(Font.HELVETICA, 24, Font.BOLD, Color.WHITE);
        Font taglineFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(255, 237, 213));
        Font labelFont = new Font(Font.HELVETICA, 8, Font.BOLD, MUTED);
        Font valueFont = new Font(Font.HELVETICA, 11, Font.BOLD, INK);
        Font thFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        Font tdFont = new Font(Font.HELVETICA, 10, Font.NORMAL, INK);
        Font tdMuted = new Font(Font.HELVETICA, 10, Font.NORMAL, MUTED);
        Font totalMuted = new Font(Font.HELVETICA, 11, Font.NORMAL, MUTED);
        Font totalValue = new Font(Font.HELVETICA, 15, Font.BOLD, BRAND);

        // ---- Header band ----
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell band = new PdfPCell();
        band.setBackgroundColor(BRAND);
        band.setBorder(Rectangle.NO_BORDER);
        band.setPadding(14f);
        Paragraph brandName = new Paragraph("Food Corner", brandFont);
        brandName.setAlignment(Element.ALIGN_CENTER);
        Paragraph tagline = new Paragraph("Fresh food, straight to your table", taglineFont);
        tagline.setAlignment(Element.ALIGN_CENTER);
        band.addElement(brandName);
        band.addElement(tagline);
        header.addCell(band);
        document.add(header);

        Paragraph invLabel = new Paragraph("TAX INVOICE / RECEIPT",
                new Font(Font.HELVETICA, 9, Font.BOLD, MUTED));
        invLabel.setAlignment(Element.ALIGN_CENTER);
        invLabel.setSpacingBefore(10f);
        invLabel.setSpacingAfter(8f);
        document.add(invLabel);

        // ---- Meta: receipt/table on the left, date/time on the right ----
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
        String date = order.getOrderDate().format(dateFmt);
        String time = order.getOrderDate().format(timeFmt);

        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(100);
        meta.addCell(metaCell("RECEIPT NO.", "#" + order.getOrderNumber(), labelFont, valueFont, Element.ALIGN_LEFT));
        meta.addCell(metaCell("DATE", date, labelFont, valueFont, Element.ALIGN_RIGHT));
        meta.addCell(metaCell("TABLE", String.valueOf(order.getTableNumber()), labelFont, valueFont, Element.ALIGN_LEFT));
        meta.addCell(metaCell("TIME", time, labelFont, valueFont, Element.ALIGN_RIGHT));
        meta.setSpacingAfter(10f);
        document.add(meta);

        // ---- Items ----
        PdfPTable items = new PdfPTable(new float[]{5f, 1.2f, 2f, 2f});
        items.setWidthPercentage(100);
        addHeaderCell(items, "ITEM", thFont, Element.ALIGN_LEFT);
        addHeaderCell(items, "QTY", thFont, Element.ALIGN_CENTER);
        addHeaderCell(items, "PRICE", thFont, Element.ALIGN_RIGHT);
        addHeaderCell(items, "AMOUNT", thFont, Element.ALIGN_RIGHT);

        double subtotal = 0;
        boolean alt = false;
        for (OrderItem item : order.getItems()) {
            Color bg = alt ? ZEBRA : Color.WHITE;
            double lineTotal = item.getPrice() * item.getQuantity();
            subtotal += lineTotal;
            addBodyCell(items, item.getName(), tdFont, bg, Element.ALIGN_LEFT);
            addBodyCell(items, String.valueOf(item.getQuantity()), tdMuted, bg, Element.ALIGN_CENTER);
            addBodyCell(items, money(item.getPrice()), tdMuted, bg, Element.ALIGN_RIGHT);
            addBodyCell(items, money(lineTotal), tdFont, bg, Element.ALIGN_RIGHT);
            alt = !alt;
        }
        document.add(items);

        // ---- Totals (right-aligned) ----
        PdfPTable totals = new PdfPTable(new float[]{1.3f, 1f});
        totals.setWidthPercentage(55f);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totals.setSpacingBefore(8f);
        totals.addCell(totalCell("Subtotal", totalMuted, Element.ALIGN_LEFT, false));
        totals.addCell(totalCell(money(subtotal), totalMuted, Element.ALIGN_RIGHT, false));
        totals.addCell(totalCell("Total", new Font(Font.HELVETICA, 12, Font.BOLD, INK), Element.ALIGN_LEFT, true));
        totals.addCell(totalCell(money(order.getTotalAmount()), totalValue, Element.ALIGN_RIGHT, true));
        document.add(totals);

        // ---- Payment status + PAID badge ----
        PdfPTable pay = new PdfPTable(new float[]{2.4f, 1f});
        pay.setWidthPercentage(100);
        pay.setSpacingBefore(10f);
        PdfPCell payInfo = new PdfPCell(new Phrase("Payment: Paid online via Razorpay",
                new Font(Font.HELVETICA, 9, Font.NORMAL, MUTED)));
        payInfo.setBorder(Rectangle.NO_BORDER);
        payInfo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pay.addCell(payInfo);
        PdfPCell paidBadge = new PdfPCell(new Phrase("PAID", new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE)));
        paidBadge.setBackgroundColor(PAID_GREEN);
        paidBadge.setBorder(Rectangle.NO_BORDER);
        paidBadge.setHorizontalAlignment(Element.ALIGN_CENTER);
        paidBadge.setVerticalAlignment(Element.ALIGN_MIDDLE);
        paidBadge.setPadding(7f);
        pay.addCell(paidBadge);
        document.add(pay);

        // ---- Footer ----
        Paragraph sep = new Paragraph(new Chunk(new LineSeparator(0.5f, 100, LINE, Element.ALIGN_CENTER, -4)));
        sep.setSpacingBefore(16f);
        document.add(sep);

        Paragraph thanks = new Paragraph("Thank you for dining with Food Corner!",
                new Font(Font.HELVETICA, 10, Font.BOLD, INK));
        thanks.setAlignment(Element.ALIGN_CENTER);
        thanks.setSpacingBefore(10f);
        document.add(thanks);

        Paragraph note = new Paragraph(
                "This is a computer-generated invoice and does not require a signature.\nSave paper, save nature.",
                new Font(Font.HELVETICA, 8, Font.NORMAL, MUTED));
        note.setAlignment(Element.ALIGN_CENTER);
        note.setSpacingBefore(4f);
        document.add(note);

        document.close();
        return out;
    }

    private PdfPCell metaCell(String label, String value, Font labelFont, Font valueFont, int align) {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        c.setPaddingBottom(6f);
        Paragraph l = new Paragraph(label, labelFont);
        l.setAlignment(align);
        Paragraph v = new Paragraph(value, valueFont);
        v.setAlignment(align);
        c.addElement(l);
        c.addElement(v);
        return c;
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(BRAND);
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(align);
        c.setPadding(6f);
        table.addCell(c);
    }

    private void addBodyCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColor(LINE);
        c.setBorderWidth(0.5f);
        c.setHorizontalAlignment(align);
        c.setPadding(6f);
        table.addCell(c);
    }

    private PdfPCell totalCell(String text, Font font, int align, boolean topBorder) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBorder(topBorder ? Rectangle.TOP : Rectangle.NO_BORDER);
        c.setBorderColor(LINE);
        c.setBorderWidth(0.8f);
        c.setHorizontalAlignment(align);
        c.setPadding(6f);
        return c;
    }

    private String money(double amount) {
        return "Rs " + String.format("%,.2f", amount);
    }

}
