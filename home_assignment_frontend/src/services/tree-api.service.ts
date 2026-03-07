import { Injectable } from '@angular/core';
import { TreeNodeResponse } from '../model/tree-node-response';

export interface CreateTreeNodeRequest {
  id: number;
  name: string;
  content: string;
  parentId?: number;
}

export interface UpdateTreeNodeRequest {
  id: number;
  name: string;
  content: string;
}

export interface ReorganizeTreeNodeRequest {
  targetId: number;
  newParentId: number | null;
}

export interface SingleValueResponse {
  content: string;
}

@Injectable({
  providedIn: 'root'
})
export class TreeApiService {
  private readonly baseUrl = 'http://localhost:8080/nodes';

  async listTree(): Promise<TreeNodeResponse[]> {
    const response = await fetch(`${this.baseUrl}/listTree`);
    if (!response.ok) {
      throw new Error('Failed to load tree');
    }
    return response.json();
  }

  async create(request: CreateTreeNodeRequest): Promise<TreeNodeResponse> {
    const response = await fetch(`${this.baseUrl}/create`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request)
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || 'Failed to create node');
    }
    return response.json();
  }

  async update(request: UpdateTreeNodeRequest): Promise<TreeNodeResponse> {
    const response = await fetch(`${this.baseUrl}/update`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request)
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || 'Failed to update node');
    }
    return response.json();
  }

  async delete(id: number): Promise<void> {
    const response = await fetch(`${this.baseUrl}/delete/${id}`, {
      method: 'DELETE'
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || 'Failed to delete node');
    }
  }

  async reorganize(request: ReorganizeTreeNodeRequest): Promise<TreeNodeResponse[]> {
    const response = await fetch(`${this.baseUrl}/reorganize`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request)
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || 'Failed to reorganize tree');
    }
    return response.json();
  }

  async loadContentById(id: number): Promise<string> {
    const response = await fetch(`${this.baseUrl}/contentById/${id}`);
    if (!response.ok) {
      throw new Error('Failed to load content');
    }
    const data: SingleValueResponse = await response.json();
    return data.content;
  }

  async findByContent(content: string): Promise<TreeNodeResponse[]> {
    const response = await fetch(`${this.baseUrl}/byContent?content=${encodeURIComponent(content)}`);
    if (!response.ok) {
      throw new Error('Failed to search');
    }
    return response.json();
  }
}
