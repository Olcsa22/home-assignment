import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TreePanelComponent } from '../tree-panel/tree-panel.component';
import { ContentPanelComponent } from '../content-panel/content-panel.component';
import { NodeDialogComponent, NodeDialogData, NodeDialogResult } from '../node-dialog/node-dialog.component';
import { DeleteConfirmDialogComponent } from '../delete-confirm-dialog/delete-confirm-dialog.component';
import { TreeService } from '../../services/tree.service';
import { TreeNodeResponse } from '../../model/tree-node-response';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [
    CommonModule,
    TreePanelComponent,
    ContentPanelComponent,
    NodeDialogComponent,
    DeleteConfirmDialogComponent
  ],
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent {
  private treeService = inject(TreeService);

  readonly selectedNode = this.treeService.selectedNode;
  readonly isLoading = this.treeService.isLoading;
  readonly error = this.treeService.error;
  
  showNodeDialog = signal(false);
  nodeDialogData = signal<NodeDialogData>({ mode: 'add' });
  isCreatingRoot = signal(false);
  
  showDeleteDialog = signal(false);
  nodesToDelete = signal<TreeNodeResponse[]>([]);

  onNewClick(): void {
    const selected = this.selectedNode();
    if (selected) {
      this.isCreatingRoot.set(false);
      this.nodeDialogData.set({ mode: 'add', parentId: selected.id });
    } else {
      this.isCreatingRoot.set(true);
      this.nodeDialogData.set({ mode: 'add' });
    }
    this.showNodeDialog.set(true);
  }

  onEditClick(): void {
    const selected = this.selectedNode();
    if (selected) {
      this.nodeDialogData.set({ mode: 'edit', node: selected });
      this.showNodeDialog.set(true);
    }
  }

  onDeleteClick(): void {
    const selected = this.selectedNode();
    if (selected) {
      const nodes = this.treeService.getNodeToDelete(selected.id);
      this.nodesToDelete.set(nodes);
      this.showDeleteDialog.set(true);
    }
  }

  async onNodeDialogSave(result: NodeDialogResult): Promise<void> {
    const data = this.nodeDialogData();
    
    if (data.mode === 'add') {
      if (this.isCreatingRoot()) {
        await this.treeService.addRootNode(result.name, result.content);
      } else if (data.parentId !== undefined) {
        await this.treeService.addNode(data.parentId, result.name, result.content);
      }
    } else if (data.mode === 'edit' && data.node) {
      await this.treeService.editNode(data.node.id, result.name, result.content);
    }
    
    this.showNodeDialog.set(false);
    this.isCreatingRoot.set(false);
  }

  onNodeDialogCancel(): void {
    this.showNodeDialog.set(false);
    this.isCreatingRoot.set(false);
  }

  async onDeleteConfirm(): Promise<void> {
    const selected = this.selectedNode();
    if (selected) {
      await this.treeService.deleteNode(selected.id);
    }
    this.showDeleteDialog.set(false);
  }

  onDeleteCancel(): void {
    this.showDeleteDialog.set(false);
  }

  get canDelete(): boolean {
    const selected = this.selectedNode();
    return selected !== null;
  }

  get canEdit(): boolean {
    return this.selectedNode() !== null;
  }

  get newButtonLabel(): string {
    return this.selectedNode() ? 'New Child' : 'New Root';
  }
}
