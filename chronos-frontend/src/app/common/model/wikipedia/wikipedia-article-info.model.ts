import { WikipediaImage } from "./wikipedia-article-image.model";

export interface WikipediaArticleInfo {
  title: string;
  qid: string;
  image?: WikipediaImage;
}
