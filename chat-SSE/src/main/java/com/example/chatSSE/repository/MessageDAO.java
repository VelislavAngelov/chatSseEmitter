package com.example.chatSSE.repository;

import com.example.chatSSE.model.Message;

import java.util.List;

public interface MessageDAO
{
  void createPending(Message message);

  void deleteMessageFromPendingTable(Integer id);

  List<Message> findPendingMessagesById(Integer id);
}