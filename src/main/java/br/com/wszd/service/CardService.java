package br.com.wszd.service;

import br.com.wszd.dto.BoardColumnInfoDTO;
import br.com.wszd.dto.CardDetailsDTO;
import br.com.wszd.exception.CardBlockedException;
import br.com.wszd.exception.CardFinishedException;
import br.com.wszd.exception.EntityNotFoundException;
import br.com.wszd.persistence.dao.BlockDAO;
import br.com.wszd.persistence.dao.CardDAO;
import br.com.wszd.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static br.com.wszd.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.wszd.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow( () -> new EntityNotFoundException("O card não foi encontrado com o id informado"));
            if(dto.blocked()){
                throw new CardBlockedException("O card tem um bloqueio, não pode ser movido");
            }
           var curentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            if(curentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Card já finalizado");
            }
            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == curentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("O card não pode ser movido pois está cancelado"));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card não foi encontrado com o id informado"));
            if (dto.blocked()) {
                throw new CardBlockedException("O card tem um bloqueio, não pode ser movido");
            }
            var curentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            if(curentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Card já finalizado");
            }
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == curentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("O card não pode ser movido pois está cancelado"));
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card não foi encontrado com o id informado"));
            if (dto.blocked()) {
                throw new CardBlockedException("O card já está bloqueado");
            }

            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst().orElseThrow();
            if(currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                throw new IllegalStateException("O card está na coluna final ou cancel, não pode ser bloqueado");
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, id);
            connection.commit();
        }catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException {

        try{
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card não foi encontrado com o id informado"));
            if (!dto.blocked()) {
                throw new CardBlockedException("O card não está bloqueado");
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, id);
            connection.commit();
        }catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

    }
}
