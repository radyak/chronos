package net.fvogel.chronosbackend.general.wikipedia.dto.getentities;

import lombok.Data;

import java.util.Map;

@Data
public class WikipediaEntityResultDto {
    Map<String, WikipediaEntityDto> entities;
    Integer success;
}
