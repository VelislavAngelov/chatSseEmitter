package com.example.chatSSE.dto;

/**
 * Data transfer object used to return specific information
 */
public class UserDTO
{

  /**
   * The unique username of the userDTO
   */
  private String userName;

  public UserDTO(String userName) {
    this.userName = userName;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  @Override
  public String toString()
  {
    return "userName: " + userName;
  }
}