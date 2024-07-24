package com.tpe.service;

import com.tpe.dto.ContactMessageRequest;
import com.tpe.dto.ContactMessageResponse;
import com.tpe.entity.ContactMessage;
import com.tpe.exceptions.ResourceNotFoundException;
import com.tpe.mapper.ContactMessageMapper;
import com.tpe.messages.Messages;
import com.tpe.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import com.tpe.payload.bussiness.ResponseMessage;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.exceptions.ConflictException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ContactMessageMapper contactMessageMapper;

    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {

        ContactMessage contactMessage = contactMessageMapper.requestToContactMessage(contactMessageRequest);
        ContactMessage savedData = contactMessageRepository.save(contactMessage);

        return ResponseMessage.<ContactMessageResponse>builder()
                .message(SuccessMessages.CONTACTMESSAGE_CREATE)
                .httpStatus(HttpStatus.CREATED) // 201
                .object(contactMessageMapper.contactMessageToResponse(savedData))
                .build();
    }

    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return contactMessageRepository.findAll(pageable).map(contactMessageMapper::contactMessageToResponse);
    }

    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return contactMessageRepository.findByEmailEquals(email, pageable).
                map(contactMessageMapper::contactMessageToResponse);
    }

    public String deleteById(Long id) {
        getContactMessageById(id);
        contactMessageRepository.deleteById(id);
        return Messages.CONTACT_MESSAGE_DELETED_SUCCESSFULLY;
    }

    public ContactMessage getContactMessageById(Long id){
        return contactMessageRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(Messages.NOT_FOUND_MESSAGE));
    }

    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        if (Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }
        return contactMessageRepository.findBySubjectEquals(subject, pageable). // Derived
                map(contactMessageMapper::contactMessageToResponse);
    }

    public List<ContactMessage> searchBetweenDates(String beginDateString, String endDateString) {

        try {
            LocalDate beginDate = LocalDate.parse(beginDateString);
            LocalDate endDate = LocalDate.parse(endDateString);
            return contactMessageRepository.findMessagesBetweenDates(beginDate, endDate);
        } catch (DateTimeParseException e) {
            throw new ConflictException(Messages.WRONG_DATE_MESSAGE);
        }
    }


    public List<ContactMessage> searchBetweenTimes(String startHourString, String startMinuteString, String endHourString, String endMinuteString) {

        try {
            int startHour = Integer.parseInt(startHourString);
            int startMinute = Integer.parseInt(startMinuteString);
            int endHour = Integer.parseInt(endHourString);
            int endMinute = Integer.parseInt(endMinuteString);

            return contactMessageRepository.findMessagesBetweenTimes(startHour,startMinute,endHour,endMinute);
        } catch (NumberFormatException e) {
            throw new ConflictException(Messages.WRONG_TIME_MESSAGE);
        }
    }
}
