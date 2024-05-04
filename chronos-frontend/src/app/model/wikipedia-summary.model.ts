export interface WikipediaSummary {
  pageid: number;
  title: string;
  extract: string;
  image?: {
    url: string,
    width: number,
    height: number
  };
  pageUrl: string;
}
