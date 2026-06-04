package net.fvogel.chronos.data.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetaInfo {
    String createAuthor;
    String createDate = LocalDateTime.now().toString();
    String lastUpdateAuthor;
    String lastUpdateDate;
    Integer version = 1;

    public void update(String author) {
        this.version++;
        this.lastUpdateDate = LocalDateTime.now().toString();
        this.lastUpdateAuthor = author;
    }
}
