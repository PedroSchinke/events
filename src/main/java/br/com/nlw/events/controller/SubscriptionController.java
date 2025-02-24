package br.com.nlw.events.controller;

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicadorNotFoundException;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping({"/subscription/{prettyName}", "/subscription/{prettyName}/{userId}"})
    public ResponseEntity<?> createSubscription(
            @PathVariable String prettyName,
            @RequestBody User subscriber,
            @PathVariable(required = false) Integer userId
    ) {
        try {
            SubscriptionResponse res = subscriptionService.createNewSubscription(prettyName, subscriber, userId);

            if (res != null) {
                return ResponseEntity.ok(res);
            }
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
        } catch (SubscriptionConflictException e) {
            return ResponseEntity.status(409).body(new ErrorMessage(e.getMessage()));
        } catch (UserIndicadorNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
        }

        return ResponseEntity.badRequest().build();
    }
}
