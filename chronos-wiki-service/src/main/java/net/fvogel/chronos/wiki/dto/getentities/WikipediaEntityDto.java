package net.fvogel.chronos.wiki.dto.getentities;

import lombok.Data;

import java.util.Map;

@Data
public class WikipediaEntityDto {
    String type;
    String id;
    Map<String, WikipediaSiteLinkDto> sitelinks;
}
