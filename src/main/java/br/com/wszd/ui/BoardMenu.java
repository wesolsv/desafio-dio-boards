package br.com.wszd.ui;

import br.com.wszd.persistence.entity.BoardColumnEntity;
import br.com.wszd.persistence.entity.BoardEntity;
import br.com.wszd.persistence.entity.CardEntity;
import br.com.wszd.service.BoardColumnQueryService;
import br.com.wszd.service.BoardQueryService;
import br.com.wszd.service.CardQueryService;
import br.com.wszd.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static br.com.wszd.persistence.config.ConnectionConfig.getConnection;
import static br.com.wszd.persistence.entity.BoardColumnKindEnum.INITIAL;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;

    public void execute() {
        try{
            System.out.printf("Bem vindo %s, selecione a operação \n", entity.getId());
            var option = -1;
            while (true){
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Ver board");
                System.out.println("7 - Ver coluna com cards");
                System.out.println("8 - Ver card");
                System.out.println("9 - Voltar para o menu anterior");
                System.out.println("0 - Exit");

                option = scanner.nextInt();

                switch (option){
                    case 1 -> createCard();
                    case 2 -> moveCardToNexColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando para o menu anterior");
                    case 0 -> System.exit(0);
                    default -> System.out.println("Invalid Option");
                }
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            System.exit(0);
        }

    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Informe o titulo do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());

        try(var connection = getConnection()){
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNexColumn() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() {
        try(var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b ->{
                    System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                    b.columns().forEach(c ->{
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount());
                    });
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showColumn() throws  SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)){
            System.out.printf("Escolha uma coluna %s\n", entity.getName());
            entity.getBoardColumns().forEach( c -> System.out.printf("%s - %s [%s]\n",c.getId() ,c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()){
           var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s \n", ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws  SQLException  {
        System.out.println("Informe o id do card para visualizar");
        var selectedCardId = scanner.nextLong();

        try(var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(c -> {
                                System.out.printf("Card %s - %s \n", c.id(), c.title());
                                System.out.printf("Descrição %s \n", c.description());
                                System.out.println(c.blocked() ? "Está bloqueado. Motivo " + c.blockReason() : "Não está bloqueado");
                                System.out.printf("Já foi bloqueado %s vezes \n", c.blocksAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                    },
                    () -> System.out.println("Não existe um card com o id informado"));
        }
    }
}
