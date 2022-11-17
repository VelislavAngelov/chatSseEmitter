package com.example.chatSSE.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An Object that represents the messages being sent
 */
public class Message {

    /**
     * Represents the id number of the sender of the message
     */
    @NotNull
    @Min(1)
    private Integer senderId;
    /**
     * Represents the id number of the receiver of the message
     */
    @NotNull
    @Min(1)
    private Integer receiverId;
    /**
     * Represents the message that is being sent
     */

    @NotEmpty
    @Size(max = 200, message = "The maximum size of a message is 200 characters!")
    private String message;

    public Message(String message, Integer senderId, Integer receiverId) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public String toString() {
        return "\nMessage: " + message;
    }
}