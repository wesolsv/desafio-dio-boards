package br.com.wszd.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.wszd.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void block(final String reason, final Long cardId ) throws SQLException {
        var sql = "INSERT INTO BLOCKS (blocked_at, blocked_reason, card_id) VALUES (?, ?, ?)";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i ++, cardId);
            statement.executeUpdate();
        }
    }

    public void unblock(final String reason, final Long cardId ) throws SQLException {
        var sql = "UPDATE BLOCKS SET unblocked_at= ?, unblocked_reason= ? WHERE card_id = ? AND unblocked_reason IS NULL";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i ++, cardId);
            statement.executeUpdate();
        }
    }
}
