import { Component, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TreeNodeComponent } from '../tree-node/tree-node.component';
import { TreeService } from '../../services/tree.service';
import { TreeNodeResponse } from '../../model/tree-node-response';

@Component({
  selector: 'app-tree-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, TreeNodeComponent],
  templateUrl: './tree-panel.component.html',
  styleUrls: ['./tree-panel.component.scss']
})
export class TreePanelComponent {
  private treeService = inject(TreeService);
  
  filterValue = signal('');
  private filterTimeout: ReturnType<typeof setTimeout> | null = null;

  readonly treeData = this.treeService.treeData;
  readonly dropListIds = computed(() => {
    const roots = this.treeData();
    const ids: string[] = [];
    roots.forEach(root => {
      ids.push(...this.collectDropListIds(root));
    });
    return ids;
  });

  get isEmpty(): boolean {
    return this.treeData().length === 0;
  }

  onFilterInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.filterValue.set(value);
    
    if (this.filterTimeout) {
      clearTimeout(this.filterTimeout);
    }
    
    this.filterTimeout = setTimeout(() => {
      this.treeService.setFilter(value);
    }, 300);
  }

  onNodeSelected(node: TreeNodeResponse): void {
    this.treeService.selectNode(node);
  }

  onTreeContainerClick(event: Event): void {
    // Only clear selection if clicking directly on the container, not on a node
    if ((event.target as HTMLElement).classList.contains('tree-container')) {
      this.treeService.clearSelection();
    }
  }

  private collectDropListIds(node: TreeNodeResponse): string[] {
    const ids: string[] = [`drop-list-${node.id}`];
    node.children?.forEach(child => {
      ids.push(...this.collectDropListIds(child));
    });
    return ids;
  }
}
