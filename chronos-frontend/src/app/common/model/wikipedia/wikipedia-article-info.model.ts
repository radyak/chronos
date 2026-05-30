import { WikipediaImage } from "./wikipedia-article-image.model";

export interface WikipediaArticleInfo {
  title: string;
  wikiqid: string;
  image?: WikipediaImage;
}
