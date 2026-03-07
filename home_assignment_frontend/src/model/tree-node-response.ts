export interface TreeNodeResponse {
  name: string;
  content: string;
  id: number;
  parentId?: number;
  children?: TreeNodeResponse[];
  isRoot: boolean;
  matchesFilter?: boolean;
}