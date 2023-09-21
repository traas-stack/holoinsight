export type Tfile = {
  id: number,
  parentFolderId: number
  name: string,
  creator: string,
  gmtModified: string,
  pluginType: string,
  tenant?: string,
  status?: string,
}

export type TNewFolderProp = {
  parentId: number,
  file?: Tfile,
  type: string,
  visible: boolean,
  handleClose: Function,
  refesh: Function,
  setMoveTo?: Function,
  setReadymove?: Function,
  readyMove?: any[],
  refss?: any,
  setVisible?: Function,
}

export type TpopType = {
  type: string,
  way?: string
}
