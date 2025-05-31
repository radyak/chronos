package net.fvogel.chronoswiki.wikipedia.dto;

import lombok.Data;

import java.util.Map;

@Data
public class WikipediaQueryDto {
    Map<String, WikipediaPageDto> pages;
}
