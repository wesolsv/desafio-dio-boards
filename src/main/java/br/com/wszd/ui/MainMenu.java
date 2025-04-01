package br.com.wszd.ui;

import br.com.wszd.persistence.entity.BoardColumnEntity;
import br.com.wszd.persistence.entity.BoardColumnKindEnum;
import br.com.wszd.persistence.entity.BoardEntity;
import br.com.wszd.service.BoardQueryService;
import br.com.wszd.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.wszd.persistence.config.ConnectionConfig.getConnection;
import static br.com.wszd.persistence.entity.BoardColumnKindEnum.INITIAL;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute() throws SQLException {
        System.out.println("Welcome to the system my friend, choose your option:");
        var option = -1;

        while (true){
            System.out.println("1 - Create new board");
            System.out.println("2 - Select board");
            System.out.println("3 - Delete board");
            System.out.println("4 - Exit");

            option = scanner.nextInt();

            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid Option");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Informe o nome do board");
        entity.setName(scanner.next());

        System.out.println("Seu board terá colunas além do padrão? Se sim informe quantas, se não digite '0'");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna de inicial do board");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for(int i = 0 ; i<additionalColumns; i++){
            System.out.println("Informe o nome da coluna de tarefa pendente do board");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, INITIAL, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna do final do board");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, INITIAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, INITIAL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.println("Não foi possível encontrar o board com o id informado"));
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id para exclusão: ");
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            if(service.delete(scanner.nextLong())){
                System.out.println("Exclusão efetuada com sucesso!");
            } else {
                System.out.println("Ocorreu um erro durante a exclusão!");
            }
        }
    }


    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }

}
