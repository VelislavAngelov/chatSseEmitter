package com.example.chatSSE.repository;

import com.example.chatSSE.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO
{

  User registration(User user);

  Optional<User> findUserById(Integer id);

  int deleteById(Integer id);

  List<User> readAllOnlineUsers();

  List<User> readAllUsers();

  Optional<User> findByUsername(String username);

  void setUserStatus(String username);
}