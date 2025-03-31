--liquibase formatted sql
--changeset wesley:202503311533
--comment: boards table create

CREATE TABLE BOARDS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL

) ENGINE=InnoDB;


--rollback DROP TABLE BOARDS
