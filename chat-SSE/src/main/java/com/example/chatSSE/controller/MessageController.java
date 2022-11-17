package com.example.chatSSE.controller;

import com.example.chatSSE.model.Message;
import com.example.chatSSE.service.EmitterService;
import com.example.chatSSE.service.MessageService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * A Class that helps us request and execute HTTP Methods
 */
@RequestMapping("/api/v1/message")
@RestController
@EnableAsync
public class MessageController {
    private final EmitterService emitterService;
    private final MessageService messageService;

    public MessageController(EmitterService emitterService, MessageService messageService) {
        this.emitterService = emitterService;
        this.messageService = messageService;
    }

    /**
     * A Http PUT method that provides us with the option of sending a message
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("newMessage")
    public ResponseBodyEmitter messageSend(@Valid @RequestBody Message message,@AuthenticationPrincipal UserDetails userDetails) {
        return emitterService.messageSend(message, userDetails);
    }

    /**
     * A Http GET method that provides us with the option of
     * getting all pending messages of the user
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("pendingMessage")
    public ResponseEntity<List<Message>> getPendingMessage(@AuthenticationPrincipal UserDetails userDetails) {
        if (!messageService.getPendingMessage(userDetails.getUsername()).isEmpty()) {
            List<Message> messages = messageService.getPendingMessage(userDetails.getUsername());

            messageService.deleteMessagesFromPendingTable(messages.get(0).getReceiverId());
            return new ResponseEntity<>(messages, HttpStatus.OK);
        }
        return new ResponseEntity<>(messageService.getPendingMessage(userDetails.getUsername()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * A Http GET method that provides us with the option of subscribing to an emitter.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("emitter")
    public CompletableFuture<SseEmitter> subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        return CompletableFuture.completedFuture(messageService.subscribe(userDetails.getUsername()));
    }
}