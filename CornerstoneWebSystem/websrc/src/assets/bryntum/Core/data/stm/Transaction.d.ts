export default class Transaction {
    queue  : object[] // TODO: add real action type here
    length : number
    addAction(action : object) : void
    undo() : void
    redo() : void
}
