export interface NodeDialogData {
  mode: 'add' | 'edit';
  node?: TreeNodeResponse;
  parentId?: number;
}