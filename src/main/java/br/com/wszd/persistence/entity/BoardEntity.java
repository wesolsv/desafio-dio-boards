package br.com.wszd.persistence.entity;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardEntity {

    private Long id;
    private String name;
    @ToStringExclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();
}
