import { TreeNodeResponse } from "./tree-node-response";

export interface NodeDialogData {
  mode: 'add' | 'edit';
  node?: TreeNodeResponse;
  parentId?: number;
}
