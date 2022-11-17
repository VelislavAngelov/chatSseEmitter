package com.example.chatSSE.controllerTestNG;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertNotNull;

import com.example.chatSSE.dto.UserDTO;
import com.example.chatSSE.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
@Transactional
public class UserControllerIntegrationTestNG extends AbstractTestNGSpringContextTests
{

  private MockMvc      mockMvc;
  private ObjectMapper objectMapper;
  @Autowired
  WebApplicationContext wac;


  @BeforeMethod
  public void setup()
  {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.wac).build();
    this.objectMapper = new ObjectMapper();
  }
  @Test
  void test_wires()
  {
    assertNotNull(wac);
    assertNotNull(objectMapper);
    assertNotNull(mockMvc);
  }
  @Test
  public void create_user_should_return_UserDTO() throws Exception
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
    Integer id = 143;
    mockMvc
        .perform(delete("/api/v1/user/userById/{id}", id)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("Linda", "password"))
                .contentType(MediaType.ALL_VALUE)
            .content(objectMapper.writeValueAsString(id)))
        .andDo(print())
        .andExpect(status().isOk());
  }
}