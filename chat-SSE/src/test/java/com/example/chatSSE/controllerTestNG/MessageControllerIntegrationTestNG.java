package com.example.chatSSE.controllerTestNG;

import com.example.chatSSE.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
@ContextConfiguration
@AutoConfigureMockMvc
@Transactional
@EnableAsync
public class MessageControllerIntegrationTestNG extends AbstractTestNGSpringContextTests
{
  private MockMvc      mockMvc;

  private ObjectMapper objectMapper;
  @Autowired
  WebApplicationContext wac;

  @BeforeMethod
  public void setup()
  {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.wac)
        .build();
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
  public void send_too_long_message_should_not_send_message() throws Exception
  {
    Message message = new Message();
    message.setSenderId(135);
    message.setReceiverId(151);
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
}