import {IconDefinition} from "@fortawesome/fontawesome-svg-core";

export interface Notification {
  heading?: string;
  text: string;
  classname?: string;
  delay?: number;
  icon?: IconDefinition
}
