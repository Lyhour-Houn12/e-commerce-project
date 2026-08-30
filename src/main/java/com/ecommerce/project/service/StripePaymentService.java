package com.ecommerce.project.service;

import com.ecommerce.project.payload.StripePaymentDto;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripePaymentService {

    PaymentIntent getPaymentIntent(StripePaymentDto stripePaymentDto) throws StripeException;
}
