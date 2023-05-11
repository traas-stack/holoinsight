
import request from '@/services/request';
import * as api from './api';
import * as folderApi from './folderApi';
export default {
  api,
  folderApi,
};


export async function queryByParentFolderId(
    parentFolderId: any,
    options?: Record<string, unknown>,
  ) {
    return request(
      `/webapi/customPlugin/queryByParentFolderId/${parentFolderId}`,
      {
        method: 'GET',
        ...(options || {}),
      },
    );
  }