package net.fvogel.chronos.wiki.dto;

import lombok.Data;

import java.util.Map;

@Data
public class WikipediaQueryDto {
    Map<String, WikipediaPageDto> pages;
}
