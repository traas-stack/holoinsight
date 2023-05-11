
import request from '../request';

export async function queryById(id: any, options?: {}) {
    return request(`/webapi/folder/query/${id}`, {
        method: 'GET',
        ...(options || {}),
    });
}
export async function deleteById(
    id: number,
    options?: any,
) {
    return request(`/webapi/folder/delete/${id}`, {
        method: 'Delete',
        ...(options || {}),
    });
}

export async function create(params: API.FolderModel, options?: {}) {
    return request(`/webapi/folder/create`, {
        method: 'POST',
        data: JSON.stringify(params),
        ...(options || {}),
    });
}
export async function updateParentFolderId(params: any, options?: {}) {
    return request(`/webapi/folder/updateParentFolderId`, {
        method: 'POST',
        data: JSON.stringify(params),
        ...(options || {}),
    });
}


// /webapi/folder/update?tenantId=1
export async function update(params: API.FolderModel, options?: {}) {
    return request(`/webapi/folder/update`, {
        method: 'POST',
        data: JSON.stringify(params),
        ...(options || {}),
    });
}

export async function queryByParentFolderId(parentFolderId: any, options?: {}) {
    return request(`/webapi/folder/queryByParentFolderId/${parentFolderId}`, {
        method: 'GET',
        ...(options || {}),
    });
}


export async function getQueryPath(params: API.TqueryPath, options?: {}) {
    return request(`/webapi/folder/path`, {
        method: 'POST',
        data: params,
        ...(options || {}),
    })
}

export async function getFolderSeach(data: any, options?: {}) {
    return request('/webapi/search/configSearch', {
        method: "POST",
        data: JSON.stringify(data),
        ...(options || {})
    })
}