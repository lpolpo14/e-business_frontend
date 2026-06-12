package unipi.d3fender.controllers;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import unipi.d3fender.services.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class StripeController {

    private final UserService userService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.basic-price-id}")
    private String basicPriceId;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @PostMapping("/checkout")
    public RedirectView createCheckoutSession(
            @RequestParam String plan,
            @RequestParam(required = false) String acceptedTerms
    ) throws Exception {

        if (!"BASIC".equalsIgnoreCase(plan)) {
            return new RedirectView("/pricing?invalidPlan");
        }

        if (acceptedTerms == null) {
            return new RedirectView("/pricing?termsRequired");
        }

        Stripe.apiKey = stripeSecretKey;

        var currentUser = userService.getCurrentUser();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(appBaseUrl + "/subscription/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(appBaseUrl + "/pricing?paymentCancelled")
                        .setClientReferenceId(String.valueOf(currentUser.getId()))
                        .setCustomerEmail(currentUser.getEmail())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(basicPriceId)
                                        .setQuantity(1L)
                                        .build()
                        )
                        .putMetadata("plan", "BASIC")
                        .putMetadata("userId", String.valueOf(currentUser.getId()))
                        .build();

        Session session = Session.create(params);

        return new RedirectView(session.getUrl());
    }

    @GetMapping("/success")
    public String subscriptionSuccess(@RequestParam("session_id") String sessionId) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        Session session = Session.retrieve(sessionId);

        var currentUser = userService.getCurrentUser();

        boolean belongsToCurrentUser =
                String.valueOf(currentUser.getId()).equals(session.getClientReferenceId());

        boolean completed =
                "complete".equalsIgnoreCase(session.getStatus());

        boolean paid =
                "paid".equalsIgnoreCase(session.getPaymentStatus());

        if (belongsToCurrentUser && completed && paid) {
            userService.selectSubscription("BASIC");
            return "redirect:/dashboard?paymentSuccess";
        }

        return "redirect:/pricing?paymentNotVerified";
    }
}