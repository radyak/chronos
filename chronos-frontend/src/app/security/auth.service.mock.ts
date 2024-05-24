import {Observable, of} from "rxjs";
import {UserProfile} from "./user-profile.interface";

export class AuthServiceMock {

  get profile$(): Observable<UserProfile> {
    return of({
      name: 'Mock Admin'
    });
  }

  constructor() {}

  assertLoggedIn(): boolean {
    return true;
  }

  isLoggedIn(): boolean {
    return true;
  }

  login(): void {  }

  logout(): void {  }
}
