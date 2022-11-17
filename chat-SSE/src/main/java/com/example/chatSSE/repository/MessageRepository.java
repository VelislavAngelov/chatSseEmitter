package com.example.chatSSE.repository;

import com.example.chatSSE.model.Message;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepository implements MessageDAO {

    /**
     * An Object that helps us to run queries
     */
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;


    public MessageRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    /**
     * Sql query to delete pending messages from the database by user id
     */
    @Override
    public void deleteMessageFromPendingTable(Integer id) {
        String sql = "DELETE "
                + "   FROM PENDINGMESSAGES "
                + "   WHERE receiver_id = :user_id";

        namedParameterJdbcOperations.update(sql, new MapSqlParameterSource("user_id", id));
    }

    /**
     * Query to insert a message in the database when the receiver is offline
     */
    @Override
    public void createPending(Message message) {

        String sql = "INSERT INTO pendingMessages(sender_id, receiver_id, message) "
                + "   VALUES (:sender_id, :receiver_id, :message)";

        namedParameterJdbcOperations.update(sql, new MapSqlParameterSource("sender_id", message.getSenderId())
                .addValue("receiver_id", message.getReceiverId())
                .addValue("message", message.getMessage()));
    }

    /**
     * Query to find pending messages by receiver id
     */
    @Override
    public List<Message> findPendingMessagesById(Integer id) {

        String sql = "SELECT *"
                + "   FROM pendingMessages p"
                + "   WHERE p.receiver_id = :user_id";

        return namedParameterJdbcOperations.query(sql,
                new MapSqlParameterSource("user_id", id),
                (rs, rowNum) -> {
                    Message currentMessage = new Message();
                    currentMessage.setSenderId(rs.getInt("sender_id"));
                    currentMessage.setReceiverId(rs.getInt("receiver_id"));
                    currentMessage.setMessage(rs.getString("message"));
                    return currentMessage;
                });
    }
}