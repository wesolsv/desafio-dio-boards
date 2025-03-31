package br.com.wszd;

import br.com.wszd.persistence.migration.MigrationStrategy;

import java.sql.SQLException;

import static br.com.wszd.persistence.config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection()){
            new MigrationStrategy(connection).executeMigration();
        }
    }
}