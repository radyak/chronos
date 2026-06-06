import { inject, Injectable } from '@angular/core';
import { AdminConfirmService } from './admin-confirm.service';
import { catchError, firstValueFrom, from, Observable, tap } from 'rxjs';
import { NotificationService } from 'src/app/common/components/notifications/notification.service';
import { AdminDataClient } from '../clients/admin-data.client';
import { EntryDTO } from 'src/app/common/model/data/entry.dto';

@Injectable({
  providedIn: 'root',
})
export class AdminDataService {

  // Injected Dependencies
  private adminDataClient = inject(AdminDataClient);
  private confirmService = inject(AdminConfirmService);
  private notificationService = inject(NotificationService);

  public save(entry: EntryDTO): Observable<EntryDTO> {
    const entryKey = entry.attributes['key'];
    return this.adminDataClient.saveEntry(entry).pipe(
      catchError((err: any, caught: Observable<EntryDTO>) => {
        this.notificationService.error(`Error while saving entry "${entryKey}"`);
        throw new Error("Entry not saved");
      }),
      tap(() => {
        this.notificationService.success(`Entry "${entryKey}" saved successfully`);
      })
    );
  }

  public delete(entry: EntryDTO): Observable<void> {
    const entryKey = entry.attributes['key'];
    return from(
      this.confirmService.confirm(
        `Confirm Delete ${entryKey}`,
        `Do you want to delete data entry ${entryKey}?`
      ).then(
        () =>
          firstValueFrom(this.adminDataClient.deleteEntry(entryKey)).then(
            () => {
              this.notificationService.success(`Entry "${entryKey}" deleted successfully`);
            },
            (err) => {
              this.notificationService.error(`Error while deleting entry "${entryKey}"`);
              throw new Error("Entry not deleted");
            }
          ),
        () => {
          // Nothing todo
        }
      )
    );
  }

}
