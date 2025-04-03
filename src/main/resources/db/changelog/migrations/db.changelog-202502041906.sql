--liquibase formatted sql
--changeset wesley:202502041906
--comment: set unblock_reason nullable

ALTER TABLE board.BLOCKS MODIFY COLUMN unblocked_reason VARCHAR(255) NULL;

--rollback ALTER TABLE board.BLOCKS MODIFY COLUMN unblocked_reason VARCHAR(255) NOT NULL;
