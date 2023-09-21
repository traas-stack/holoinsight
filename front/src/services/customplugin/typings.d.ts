// @ts-ignore
/* eslint-disable */

declare namespace API {

  type FolderModel = {
    id?: any;
    name: string;
    tenant?: string;
    parentFolderId: any;
    extInfo?: string;
  };
  type TqueryPath = {
    requests: {
      folderId?: number,
      customPluginId?: number,
      includePluginName: boolean
    }[]
  }
}



