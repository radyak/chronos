import {Injectable} from '@angular/core';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {Entry} from "../../../model/entry.model";
import {AdminAddTagsModal} from "../components/admin-add-tags-modal/admin-add-tags-modal";
import {Tag} from "../../../model/tag.model";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import {faTags} from "@fortawesome/free-solid-svg-icons";
import {NotificationService} from "../../../ui-components/notifications/notification.service";

export interface BulkAction {
  id: string,
  label: string,
  icon: IconDefinition
}

interface InternalBulkAction extends BulkAction {
  action: (selectedEntries: Array<Entry>) => Promise<any> | void
}

@Injectable({
  providedIn: null
})
export class AdminBulkActionService {

  private bulkActions: Array<InternalBulkAction> = [
    {
      id: 'addTags',
      label: 'Add Tags',
      icon: faTags,
      action: async (selectedEntries: Array<Entry>) => {
        const modalRef = this.modal.open(AdminAddTagsModal, { ariaLabelledBy: 'modal-basic-title' })
        modalRef.componentInstance.selectedEntries = selectedEntries;
        return modalRef.result.then((selectedTags?: Array<Tag>) => {
          if (selectedTags) {
            this.notificationService.success(
              `Successfully added ${selectedTags.length} tags to ${selectedEntries.length} entries`
            )
          }
        })
      }
    }
  ];

  constructor(private modal: NgbModal,
              private notificationService: NotificationService) {
  }

  public getAvailableActions(): Array<BulkAction> {
    return this.bulkActions.map(({id, label, icon}) => ({id, label, icon}));
  }

  public bulkAction(selectedEntries: Array<Entry>, id: string): void {
      const bulkAction = this.bulkActions.find(b => b.id === id);
      if (!bulkAction) {
        this.notificationService.error(
          `The selected action is not available`
        )
      } else {
        bulkAction.action(selectedEntries);
      }
  }

}
