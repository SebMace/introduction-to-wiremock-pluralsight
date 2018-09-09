package com.flights.service;

import com.flights.gateway.PayBuddyFraudCheckResponse;
import com.flights.gateway.PayBuddyGateway;
import com.flights.gateway.PayBuddyPaymentResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.flights.gateway.PayBuddyPaymentResponse.PaymentResponseStatus.SUCCESS;

public class BookingService {

    private final PayBuddyGateway payBuddyGateway;

    public BookingService(PayBuddyGateway payBuddyGateway) {
        this.payBuddyGateway = payBuddyGateway;
    }

    public BookingResponse payForBooking(final String bookingId,
                                         final String creditCardNumber,
                                         final LocalDate creditCardExpiry,
                                         final BigDecimal amount) {

        final PayBuddyPaymentResponse payBuddyPaymentResponse = payBuddyGateway.makePayment(creditCardNumber, creditCardExpiry, amount);

        if (payBuddyPaymentResponse.getPaymentResponseStatus() == SUCCESS) {
            return new BookingResponse(bookingId, payBuddyPaymentResponse.getPaymentId(), BookingResponse.BookingResponseStatus.SUCCESS);
        }

        throw new RuntimeException("Unsupported response status: " + payBuddyPaymentResponse.getPaymentResponseStatus());
    }

    public BookingResponse payForBookingWithFraudCheck(final String bookingId,
                                                       final String creditCardNumber,
                                                       final LocalDate creditCardExpiry,
                                                       final BigDecimal amount) {

        final PayBuddyFraudCheckResponse payBuddyFraudCheckResponse = payBuddyGateway.fraudCheck(creditCardNumber);

        if (!payBuddyFraudCheckResponse.isBlacklisted()) {
            return payForBooking(bookingId, creditCardNumber, creditCardExpiry, amount);
        }

        throw new RuntimeException("Unsupported response status: " + payBuddyFraudCheckResponse.isBlacklisted());
    }
}