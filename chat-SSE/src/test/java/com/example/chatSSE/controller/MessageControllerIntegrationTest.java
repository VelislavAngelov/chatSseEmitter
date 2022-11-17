package com.example.chatSSE.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.chatSSE.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@EnableAsync

public class MessageControllerIntegrationTest
{
  @Autowired
  private MockMvc mockMvc;

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
  public void send_message_should_send_message() throws Exception
  {
    Message message = new Message();
    message.setSenderId(135);
    message.setReceiverId(151);
    message.setMessage("bla bla bla");

    mockMvc
        .perform(put("/api/v1/message/newMessage")
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("Allan", "12345"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void send_too_long_message_should_not_send_message() throws Exception
  {
    Message message = new Message();
    message.setSenderId(4);
    message.setReceiverId(3);
    message.setMessage("blablablaablablablaablablablaablablablaablablablaa"
        + "             blablablaablablablaablablablaablablablaablablablaa"
        + "             blablablaablablablaablablablaablablablaablablablaa"
        + "             blablablaablablablaablablablaablablablaablablablaa"
        + "             blablablaablablablaablablablaablablablaablablablaa"
        + "             blablablaablablablaablablablaablablablaablablablaaa");

    mockMvc
        .perform(put("/api/v1/message/newMessage")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void send_message_with_null_id_should_not_send_message() throws Exception
  {
    Message message = new Message();
    message.setSenderId(null);
    message.setReceiverId(null);
    message.setMessage("bla bla bla");

    mockMvc
        .perform(put("/api/v1/message/newMessage")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void send_message_with_negative_id_should_not_send_message() throws Exception
  {
    Message message = new Message();
    message.setSenderId(-3);
    message.setReceiverId(-5);
    message.setMessage("bla bla bla");

    mockMvc
        .perform(put("/api/v1/message/newMessage")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void user_subscribe_should_subscribe_user() throws Exception
  {
    mockMvc
        .perform(get("/api/v1/message/emitter")
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("John", "12345678")))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void user_getPendingMessage_should_get_current_user_messages() throws Exception
  {
    mockMvc
        .perform(get("/api/v1/message/pendingMessage")
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("Mick", "password")))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void aSyncTest() throws Exception
  {
    MvcResult result = mockMvc
        .perform(get("/api/v1/message/emitter")
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("Mick", "password")))
        .andDo(print())
        .andExpect(MockMvcResultMatchers
            .request()
            .asyncStarted())
        .andExpect(status().isOk())
        .andReturn();
    try {
      mockMvc
          .perform(asyncDispatch(result))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk());
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    Message message = new Message("asdasd", 135, 151);
    mockMvc
        .perform(put("/api/v1/message/newMessage")
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("Allan", "12345"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
        .andExpect(status().isOk());
  }
}