import request from '../request';

export async function queryMenu(app:string,options?: {}) {
    return request(`/webapi/displaymenu/query/apm/${app}`, {
        method: 'GET',
        ...(options || {}),
        params: {
        }
    });
}