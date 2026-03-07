import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkDragDrop, CdkDrag, CdkDropList, CdkDragPlaceholder } from '@angular/cdk/drag-drop';
import { TreeNodeResponse } from '../../model/tree-node-response';
import { TreeService } from '../../services/tree.service';

@Component({
  selector: 'app-tree-node',
  standalone: true,
  imports: [CommonModule, CdkDrag, CdkDropList, CdkDragPlaceholder],
  templateUrl: './tree-node.component.html',
  styleUrls: ['./tree-node.component.scss']
})
export class TreeNodeComponent {
  @Input({ required: true }) node!: TreeNodeResponse;
  @Input() level: number = 0;
  @Input() dropListIds: string[] = [];
  @Output() nodeSelected = new EventEmitter<TreeNodeResponse>();
  @Output() nodeDrop = new EventEmitter<CdkDragDrop<TreeNodeResponse>>();

  private treeService = inject(TreeService);
  
  isExpanded = true;
  isDropTarget = false;

  get isSelected(): boolean {
    return this.treeService.selectedNode()?.id === this.node.id;
  }

  get matchesFilter(): boolean {
    return this.treeService.checkMatchesFilter(this.node, this.treeService.filterText());
  }

  get hasFilter(): boolean {
    return this.treeService.filterText().trim().length > 0;
  }

  get dropListId(): string {
    return `drop-list-${this.node.id}`;
  }

  get connectedDropLists(): string[] {
    return this.dropListIds.filter(id => id !== this.dropListId);
  }

  toggleExpand(event: Event): void {
    event.stopPropagation();
    this.isExpanded = !this.isExpanded;
  }

  onSelect(): void {
    this.treeService.selectNode(this.node);
    this.nodeSelected.emit(this.node);
  }

  onChildSelected(node: TreeNodeResponse): void {
    this.nodeSelected.emit(node);
  }

  onDragEnter(): void {
    this.isDropTarget = true;
  }

  onDragExit(): void {
    this.isDropTarget = false;
  }

  async onDrop(event: CdkDragDrop<TreeNodeResponse>): Promise<void> {
    this.isDropTarget = false;
    const draggedNode = event.item.data as TreeNodeResponse;
    
    // Don't drop on self
    if (draggedNode.id === this.node.id) return;
    
    // Move the dragged node to be a child of this node
    await this.treeService.moveNode(draggedNode.id, this.node.id);
    this.nodeDrop.emit(event);
  }

  onChildDrop(event: CdkDragDrop<TreeNodeResponse>): void {
    this.nodeDrop.emit(event);
  }
}
