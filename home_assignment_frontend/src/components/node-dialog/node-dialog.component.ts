import { Component, inject, signal, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TreeNodeResponse } from '../../model/tree-node-response';

export interface NodeDialogData {
  mode: 'add' | 'edit';
  node?: TreeNodeResponse;
  parentId?: number;
}

export interface NodeDialogResult {
  name: string;
  content: string;
}

@Component({
  selector: 'app-node-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './node-dialog.component.html',
  styleUrls: ['./node-dialog.component.scss']
})
export class NodeDialogComponent {
  @Input() data: NodeDialogData = { mode: 'add' };
  @Output() save = new EventEmitter<NodeDialogResult>();
  @Output() cancel = new EventEmitter<void>();

  name = signal('');
  content = signal('');

  ngOnInit(): void {
    if (this.data.mode === 'edit' && this.data.node) {
      this.name.set(this.data.node.name);
      this.content.set(this.data.node.content);
    }
  }

  get title(): string {
    if (this.data.mode === 'edit') {
      return 'Edit Node';
    }
    return this.data.parentId !== undefined ? 'Add Child Node' : 'Add Root Node';
  }

  get isValid(): boolean {
    return this.name().trim().length > 0 && this.content().trim().length > 0;
  }

  onSave(): void {
    if (this.isValid) {
      this.save.emit({
        name: this.name().trim(),
        content: this.content().trim()
      });
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onBackdropClick(event: Event): void {
    if ((event.target as HTMLElement).classList.contains('dialog-backdrop')) {
      this.onCancel();
    }
  }

  updateName(event: Event): void {
    this.name.set((event.target as HTMLInputElement).value);
  }

  updateContent(event: Event): void {
    this.content.set((event.target as HTMLTextAreaElement).value);
  }
}
