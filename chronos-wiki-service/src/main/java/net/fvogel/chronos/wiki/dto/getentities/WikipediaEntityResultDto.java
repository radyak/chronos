package net.fvogel.chronos.wiki.dto.getentities;

import lombok.Data;

import java.util.Map;

@Data
public class WikipediaEntityResultDto {
    Map<String, WikipediaEntityDto> entities;
    Integer success;
}
