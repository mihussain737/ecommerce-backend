package com.ecommerce.sb_ecom.service.impl;
import com.ecommerce.sb_ecom.payload.StripePaymentDto;
import com.ecommerce.sb_ecom.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StripeServiceImpl implements StripeService {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey=stripeApiKey;
    }

    @Override
    public PaymentIntent paymentIntent(StripePaymentDto stripePaymentDto) throws StripeException {

        Customer customer;
        // Retrieve and check If Customer exist

        CustomerSearchParams customerSearchParams =
                CustomerSearchParams.builder()
                        .setQuery("email:'" + stripePaymentDto.getEmail()+"'")
                        .build();
        CustomerSearchResult customers = Customer.search(customerSearchParams);

        if(customers.getData().isEmpty()){
           //  create new customer in stripe
            CustomerCreateParams customerParams =
                    CustomerCreateParams.builder()
                            .setName(stripePaymentDto.getName())
                            .setEmail(stripePaymentDto.getEmail())
                            .setAddress(
                                    CustomerCreateParams.Address.builder()
                                            .setLine1(stripePaymentDto.getAddress().getStreet())
                                            .setState(stripePaymentDto.getAddress().getState())
                                            .setCity(stripePaymentDto.getAddress().getCityName())
                                            .setPostalCode(stripePaymentDto.getAddress().getPincode())
                                            .setCountry(stripePaymentDto.getAddress().getCountry())
                                            .build()
                            )
                            .build();
             customer = Customer.create(customerParams);
        }else {
            // fetch the customer that exist
            customer=customers.getData().get(0);
        }

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(stripePaymentDto.getAmount()) // amount in cents
                        .setCurrency(stripePaymentDto.getCurrency())
                        .setCustomer(customer.getId())
                        .setDescription(stripePaymentDto.getDescription())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();
        PaymentIntent paymentIntent= PaymentIntent.create(params);
        return paymentIntent;
    }
}
