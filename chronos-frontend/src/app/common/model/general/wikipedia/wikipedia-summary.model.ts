import { WikipediaImage } from "./wikipedia-article-image.model";

export interface WikipediaSummary {
  pageid: number;
  title: string;
  extract: string;
  image?: WikipediaImage;
  pageUrl: string;
}
