import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TreeNodeResponse } from '../../model/tree-node-response';

@Component({
  selector: 'app-delete-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './delete-confirm-dialog.component.html',
  styleUrls: ['./delete-confirm-dialog.component.scss']
})
export class DeleteConfirmDialogComponent {
  @Input() nodesToDelete: TreeNodeResponse[] = [];
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  get nodeCount(): number {
    return this.nodesToDelete.length;
  }

  get hasChildren(): boolean {
    return this.nodeCount > 1;
  }

  onConfirm(): void {
    this.confirm.emit();
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onBackdropClick(event: Event): void {
    if ((event.target as HTMLElement).classList.contains('dialog-backdrop')) {
      this.onCancel();
    }
  }
}
