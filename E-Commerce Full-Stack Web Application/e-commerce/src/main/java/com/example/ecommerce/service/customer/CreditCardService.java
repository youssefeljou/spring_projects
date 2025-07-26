package com.example.ecommerce.service.customer;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.mapper.CreditCardMapper;
import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CreditCardRepository;

import com.example.ecommerce.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final CustomerRepository customerRepository;
    private final CreditCardMapper creditCardMapper;

    //--Create--
    @Transactional
    public CreditCardViewDTO addCard(Long customerId, CreditCardCreateDTO dto) {

        CreditCard card = creditCardMapper.toEntity(dto);

        //proxy get reference only, not database retrieval
        Customer customer = customerRepository.getReferenceById(customerId);
        card.setCustomer(customer);

        if (card.isExpired())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card expired");

        creditCardRepository.save(card);
        return creditCardMapper.toDto(card);
    }

    //--Get -> list --
    @Transactional(readOnly = true)
    public List<CreditCardViewDTO> listCards(Long customerId) {
        return creditCardRepository.findByCustomerId(customerId).stream()
                .map(creditCardMapper::toDto)
                .toList();
    }

    //--Get -> one --
    @Transactional(readOnly = true)
    public CreditCardViewDTO getCard(Long customerId, Long cardId) {
        CreditCard card = creditCardRepository.findByIdAndCustomerId(cardId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));
        return creditCardMapper.toDto(card);
    }

    //--Update--
    @Transactional
    public CreditCardViewDTO updateCard(Long customerId, Long cardId, CreditCardUpdateDTO dto) {

        CreditCard card = creditCardRepository.findByIdAndCustomerId(cardId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        if (dto.cardHolderName() != null)
            card.setCardHolderName(dto.cardHolderName());

        if (dto.expiration() != null) {
            YearMonth exp = YearMonth.parse("20" + dto.expiration().substring(3) + "-" +
                    dto.expiration().substring(0, 2));
            if (YearMonth.now().isAfter(exp))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card expired");
            card.setExpiration(exp);
        }
        return creditCardMapper.toDto(card);
    }

    //--Delete--
    @Transactional
    public void deleteCard(Long customerId, Long cardId) {
        CreditCard card = creditCardRepository.findByIdAndCustomerId(cardId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));
        creditCardRepository.delete(card);
    }
}
