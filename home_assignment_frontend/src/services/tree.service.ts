import { Injectable, signal, inject } from '@angular/core';
import { TreeNodeResponse } from '../model/tree-node-response';
import { TreeApiService } from './tree-api.service';

@Injectable({
  providedIn: 'root'
})
export class TreeService {
  private apiService = inject(TreeApiService);
  private nextId = 1;
  
  readonly treeData = signal<TreeNodeResponse[]>([]);
  readonly selectedNode = signal<TreeNodeResponse | null>(null);
  readonly filterText = signal<string>('');
  readonly isLoading = signal(false);
  readonly error = signal<string | null>(null);

  constructor() {
    this.loadTree();
  }

  async loadTree(): Promise<void> {
    this.isLoading.set(true);
    this.error.set(null);
    try {
      const data = await this.apiService.listTree();
      this.treeData.set(data);
      data.forEach(node => this.updateNextId(node));
    } catch (err) {
      this.error.set(err instanceof Error ? err.message : 'Failed to load tree');
      console.error('Failed to load tree:', err);
    } finally {
      this.isLoading.set(false);
    }
  }

  private updateNextId(node: TreeNodeResponse): void {
    if (node.id >= this.nextId) {
      this.nextId = node.id + 1;
    }
    node.children?.forEach(child => this.updateNextId(child));
  }

  selectNode(node: TreeNodeResponse | null): void {
    this.selectedNode.set(node);
  }

  clearSelection(): void {
    this.selectedNode.set(null);
  }

  async setFilter(text: string): Promise<void> {
    this.filterText.set(text);
    if (text.trim()) {
      try {
        const filteredData = await this.apiService.findByContent(text);
        this.treeData.set(filteredData);
      } catch (err) {
        console.error('Failed to filter:', err);
      }
    } else {
      await this.loadTree();
    }
  }

  async addRootNode(name: string, content: string): Promise<void> {
    this.error.set(null);
    try {
      const newNode = await this.apiService.create({
        id: this.nextId++,
        name,
        content
      });
      await this.loadTree();
      this.selectedNode.set(newNode);
    } catch (err) {
      this.error.set(err instanceof Error ? err.message : 'Failed to create node');
      console.error('Failed to create root node:', err);
    }
  }

  async addNode(parentId: number, name: string, content: string): Promise<void> {
    this.error.set(null);
    try {
      const newNode = await this.apiService.create({
        id: this.nextId++,
        name,
        content,
        parentId
      });
      await this.loadTree();
      // Find and select the newly created node
      const roots = this.treeData();
      for (const root of roots) {
        const found = this.findNode(root, newNode.id);
        if (found) {
          this.selectedNode.set(found);
          break;
        }
      }
    } catch (err) {
      this.error.set(err instanceof Error ? err.message : 'Failed to create node');
      console.error('Failed to create node:', err);
    }
  }

  async editNode(nodeId: number, name: string, content: string): Promise<void> {
    this.error.set(null);
    try {
      const node = this.findNodeById(nodeId);
      if (node) {
        await this.apiService.update({
          id: nodeId,
          name,
          content
        });
        await this.loadTree();
        // Re-select the edited node
        const roots = this.treeData();
        for (const root of roots) {
          const found = this.findNode(root, nodeId);
          if (found) {
            this.selectedNode.set(found);
            break;
          }
        }
      }
    } catch (err) {
      this.error.set(err instanceof Error ? err.message : 'Failed to edit node');
      console.error('Failed to edit node:', err);
    }
  }

  async deleteNode(nodeId: number): Promise<TreeNodeResponse[]> {
    const nodesToDelete = this.getNodeToDelete(nodeId);
    this.error.set(null);
    try {
      await this.apiService.delete(nodeId);
      await this.loadTree();
      this.selectedNode.set(null);
      return nodesToDelete;
    } catch (err) {
      this.error.set(err instanceof Error ? err.message : 'Failed to delete node');
      console.error('Failed to delete node:', err);
      return [];
    }
  }

  getNodeToDelete(nodeId: number): TreeNodeResponse[] {
    const roots = this.treeData();
    
    for (const root of roots) {
      const node = this.findNode(root, nodeId);
      if (node) {
        return this.collectAllNodes(node);
      }
    }
    
    return [];
  }

  async moveNode(nodeId: number, newParentId: number | null): Promise<void> {
    if (nodeId === newParentId) return;
    
    const node = this.findNodeById(nodeId);
    if (!node) return;

    // Check if newParent is a descendant of node (would create cycle)
    if (newParentId !== null && this.isDescendant(node, newParentId)) return;

    this.error.set(null);
    try {
      await this.apiService.reorganize({
        targetId: nodeId,
        newParentId: newParentId
      });
      await this.loadTree();
    } catch (err) {
      this.error.set(err instanceof Error ? err.message : 'Failed to move node');
      console.error('Failed to move node:', err);
    }
  }

  private findNodeById(nodeId: number): TreeNodeResponse | null {
    const roots = this.treeData();
    for (const root of roots) {
      const found = this.findNode(root, nodeId);
      if (found) return found;
    }
    return null;
  }

  private isDescendant(node: TreeNodeResponse, targetId: number): boolean {
    if (!node.children) return false;
    for (const child of node.children) {
      if (child.id === targetId) return true;
      if (this.isDescendant(child, targetId)) return true;
    }
    return false;
  }

  private findNode(node: TreeNodeResponse, id: number): TreeNodeResponse | null {
    if (node.id === id) return node;
    if (node.children) {
      for (const child of node.children) {
        const found = this.findNode(child, id);
        if (found) return found;
      }
    }
    return null;
  }

  private collectAllNodes(node: TreeNodeResponse): TreeNodeResponse[] {
    const nodes: TreeNodeResponse[] = [node];
    node.children?.forEach(child => {
      nodes.push(...this.collectAllNodes(child));
    });
    return nodes;
  }

  checkMatchesFilter(node: TreeNodeResponse, filter: string): boolean {
    // When using backend filtering, use the matchesFilter property from backend
    if (node.matchesFilter !== undefined) {
      return node.matchesFilter;
    }
    // Fallback to local check
    if (!filter.trim()) return true;
    const lowerFilter = filter.toLowerCase();
    return node.name.toLowerCase().includes(lowerFilter) ||
           node.content.toLowerCase().includes(lowerFilter);
  }
}
