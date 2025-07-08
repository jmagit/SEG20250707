export type ModoCRUD = 'list' | 'add' | 'edit' | 'view' | 'delete';

export class CancelOperationArg {
  public isCancel = false

  public cancel() { this.isCancel = true }
}
