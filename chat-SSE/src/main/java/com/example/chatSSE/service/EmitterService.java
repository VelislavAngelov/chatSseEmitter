package com.example.chatSSE.service;

import com.example.chatSSE.model.Message;
import com.example.chatSSE.model.User;
import com.example.chatSSE.repository.MessageRepository;
import com.example.chatSSE.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Class that helps us work with a Sse Emitter
 */
@Service
public class EmitterService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    /**
     * A Thread safe Map in which we keep the emitters as the values,
     * and the Usernames as the keys.
     */
    private final static ConcurrentHashMap<String, SseEmitter> sseSessions = new ConcurrentHashMap<>();

    public EmitterService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * In this method, we send a message by the receiver's emitter,
     * so if he is online,
     * and if he listens to his emitter,
     * he will get the message after 2 seconds(configurable).
     * Otherwise, the message status will be changed to pending.
     */
    public SseEmitter messageSend(Message message, UserDetails userDetails) {
        if (userRepository.findUserById(message.getReceiverId()).isPresent() &&
                userRepository.findUserById(message.getSenderId()).isPresent()) {

            User receiver = userRepository.findUserById(message.getReceiverId()).get();
            User sender = userRepository.findUserById(message.getSenderId()).get();

            if (sender
                    .getUserName()
                    .equals(userDetails.getUsername())) {

                if (sseSessions.containsKey(receiver.getUserName())) {

                    try {
                        sseSessions
                                .get(receiver.getUserName())
                                .send(sender.getUserName() + ": " + message.getMessage(),
                                        MediaType.APPLICATION_JSON);

//todo ask Redzhep about IOException

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    messageRepository.createPending(message);
                }
            }
            return null;
        }
        throw new IllegalArgumentException("No such users found!");
    }

    public ConcurrentHashMap<String, SseEmitter> getSseSessions() {
        return sseSessions;
    }
}