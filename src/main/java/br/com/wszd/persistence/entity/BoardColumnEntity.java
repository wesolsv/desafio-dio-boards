package br.com.wszd.persistence.entity;

import lombok.Data;

@Data
public class BoardColumnEntity {

    private Long id;
    private String name;
    private int order;
    private BoardColumnEnum kind;
    private BoardEntity board = new BoardEntity();
}
