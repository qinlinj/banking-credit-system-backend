package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class CreditCardController {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint for adding a credit card to a user
     *
     * @param payload Payload for adding a credit card to a user
     * @return ResponseEntity with the saved credit card ID or bad request if user ID is invalid
     */
    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        Optional<User> optionalUser = userRepository.findById(payload.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            CreditCard creditCard = new CreditCard();
            creditCard.setIssuanceBank(payload.getCardIssuanceBank());
            creditCard.setNumber(payload.getCardNumber());
            creditCard.setUser(user);
            CreditCard savedCard = creditCardRepository.save(creditCard);
            return new ResponseEntity<>(savedCard.getId(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint for getting all credit cards of a user
     *
     * @param userId ID of the user
     * @return ResponseEntity with a list of CreditCardView or bad request if user ID is invalid
     */
    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        List<CreditCard> creditCards = creditCardRepository.findAllByUserId(userId);

        if (creditCards.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<CreditCardView> cards = creditCards.stream()
                .map(card -> CreditCardView.builder()
                        .issuanceBank(card.getIssuanceBank())
                        .number(card.getNumber())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(cards);
    }

    /**
     * Endpoint for getting user ID of a credit card
     *
     * @param creditCardNumber Credit card number
     * @return ResponseEntity with the user ID or bad request if credit card number is invalid
     */
    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        Optional<CreditCard> optionalCard = creditCardRepository.findByNumber(creditCardNumber);
        if (optionalCard.isPresent()) {
            CreditCard creditCard = optionalCard.get();
            return new ResponseEntity<>(creditCard.getUser().getId(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint for updating the balance of a credit card
     *
     * @param payload Array of UpdateBalancePayload for updating the balance of credit cards
     *
     * @return ResponseEntity with a success message or bad request if credit card number is invalid
     */
    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<String> updateBalance(@RequestBody UpdateBalancePayload[] payload) {
        for (UpdateBalancePayload transaction : payload) {
            Optional<CreditCard> creditCardOptional = creditCardRepository.findByNumber(transaction.getCreditCardNumber());

            if (creditCardOptional.isPresent()) {
                CreditCard creditCard = creditCardOptional.get();
                List<BalanceHistory> balanceHistoryList = creditCard.getBalanceHistory();
                BalanceHistory newBalanceHistory = new BalanceHistory();
                newBalanceHistory.setCreditCard(creditCard);
                newBalanceHistory.setDate(transaction.getTransactionTime());
                newBalanceHistory.setBalance(balanceHistoryList.isEmpty() ? transaction.getTransactionAmount() : balanceHistoryList.get(0).getBalance() + transaction.getTransactionAmount());

                if (balanceHistoryList.isEmpty()) {
                    balanceHistoryList.add(newBalanceHistory);
                } else {
                    balanceHistoryList.set(0, newBalanceHistory);
                }
                creditCard.setBalanceHistory(balanceHistoryList);
                creditCardRepository.save(creditCard);
            } else {
                return ResponseEntity.badRequest().body("Credit card number not found.");
            }
        }
        return ResponseEntity.ok("Balance history updated.");
    }


}

