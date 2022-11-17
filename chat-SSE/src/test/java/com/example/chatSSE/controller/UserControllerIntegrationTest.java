package com.example.chatSSE.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.chatSSE.dto.UserDTO;
import com.example.chatSSE.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserControllerIntegrationTest
{

  @Autowired
  private MockMvc      mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  Logger log = Logger.getLogger(UserControllerIntegrationTest.class.getName());

  @Test
  void test_wires()
  {
    assertNotNull(mockMvc);
    log.info("Test Wiring - OK");
  }

  @Test
  public void create_user_should_return_successfully() throws Exception
  {
    User user = new User();
    user.setUserName("Mick");
    user.setPassword("password");

    mockMvc
            .perform(post("/api/v1/user/newUser")
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("Allan", "12345"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
            .andDo(print())
            .andExpect(status().isOk());
  }

  @Test
  public void create_user_with_too_short_or_too_long_username_should_fail() throws Exception
  {
    User user = new User();
    user.setUserName("J");
    user.setPassword("password");
    mockMvc
        .perform(post("/api/v1/user/newUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void create_user_with_too_short_or_too_long_password_should_fail() throws Exception
  {
    User user = new User();
    user.setUserName("John");
    user.setPassword("passwordasdafasdasdasfgddsgf3wef");
    mockMvc
        .perform(post("/api/v1/user/newUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void get_all_users_should_return_list_userDto() throws Exception
  {
    List<UserDTO> dtoList = new ArrayList<>();

    mockMvc
        .perform(get("/api/v1/user/allUsers")
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("Linda", "password"))

            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dtoList)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void delete_user_by_id() throws Exception
  {
    Integer id = 150;
    mockMvc
        .perform(delete("/api/v1/user/userById/{id}", id)
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("Linda", "password"))

            .contentType(MediaType.ALL_VALUE)
            .content(objectMapper.writeValueAsString(id)))
        .andDo(print())
        .andExpect(status().isOk());
  }
}