package com.example.chatSSE.service;

import com.example.chatSSE.model.Message;
import com.example.chatSSE.model.User;
import com.example.chatSSE.repository.MessageRepository;

import java.util.List;

import com.example.chatSSE.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * A Class that helps us validate and manipulate the data returned by the repositories
 */
@Service
public class MessageService {
    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final EmitterService emitterService;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          EmitterService emitterService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.emitterService = emitterService;
    }

    /**
     * When the given user logins, we subscribe him to an emitter,
     * and we return information whether the user has logged in or not
     */
    public SseEmitter subscribe(String username) {
        final SseEmitter emitter = new SseEmitter(300000L);
        emitterService
                .getSseSessions()
                .put(username, emitter);
        emitter.onCompletion(() ->
                emitterService
                        .getSseSessions()
                        .remove(username));

        emitter.onTimeout(emitter::complete);
        emitterService
                .getSseSessions()
                .put(username, emitter);

        userRepository.setUserStatus(username);
        return emitter;
    }


    public void deleteMessagesFromPendingTable(Integer id){
         messageRepository.deleteMessageFromPendingTable(id);
    };

    /**
     * Here we validate that the user is online,
     * and then he can get his pending messages.
     * When he gets them, we delete them.
     */
    public List<Message> getPendingMessage(String username) {

        if (userRepository.findByUsername(username).isPresent() &&
            userRepository.findByUsername(username).get().getIsOnline() == 1) {
            User user = userRepository.findByUsername(username).get();
            List<Message> messages = messageRepository.findPendingMessagesById(user.getUserId());
            return messages;

        } else {
            return null;
        }
    }
}