import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-user-mgmt-delete-dialog',
  templateUrl: './user-management-delete-dialog.component.html',
})
export class UserMgmtDeleteDialogComponent {
  user: User;

  constructor(
    private userService: UserService,
    public activeModal: NgbActiveModal,
    private eventManager: JhiEventManager,
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(login) {
    this.userService.delete(login).subscribe(response => {
      this.eventManager.broadcast({ name: 'userListModification', content: 'Deleted a user' });
      this.activeModal.close(true);
    });
  }
}
