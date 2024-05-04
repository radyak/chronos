import {Injectable} from "@angular/core";
import {Notification} from "./notifcation.model";
import {faCheckCircle, faExclamationCircle, faWarning} from "@fortawesome/free-solid-svg-icons";

@Injectable({
  providedIn: "root"
})
export class NotificationService {
  notifications: Notification[] = [];

  public message(text: string, heading?: string) {
    this.notifications.push({
      heading: heading,
      text: text
    });
  }

  public error(text: string, heading?: string) {
    this.notifications.push({
      heading: heading,
      text: text,
      classname: 'bg-danger text-light',
      delay: 15000,
      icon: faWarning
    });
  }

  public warn(text: string, heading?: string) {
    this.notifications.push({
      heading: heading,
      text: text,
      classname: 'bg-warning text-dark',
      delay: 12000,
      icon: faExclamationCircle
    });
  }

  public success(text: string, heading?: string) {
    this.notifications.push({
      heading: heading,
      text: text,
      classname: 'bg-success text-light',
      delay: 7000,
      icon: faCheckCircle
    });
  }

  public show(toast: Notification) {
    this.notifications.push(toast);
  }

  public remove(toast: Notification) {
    this.notifications = this.notifications.filter((t) => t !== toast);
  }

  public clear() {
    this.notifications.splice(0, this.notifications.length);
  }

}
