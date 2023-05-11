
import request from '../request';



/** 获取当前所有插件信息 */
export async function getAllProductInfo(options?: {}) {
    return request('/webapi/integration/product/listAll', {
        method: 'GET',
        ...(options || {}),
    });
}

/** 查看应用监控相关的interation */
export async function getAppInteration(options?: {}) {
    return request('/webapi/integration/plugin/list/app', {
        method: 'GET',
        ...(options || {}),
    });
}

/** 获取当前所有插件信息 */
export async function getQueryProductByName(name: string, options?: {}) {
    return request(`/webapi/integration/product/queryByName/${name}`, {
        method: 'GET',
        ...(options || {}),
    });
}

export async function queryProductById(id: any, options?: {}) {
    return request(`/webapi/integration/product/queryById/${id}`, {
        method: 'GET',
        ...(options || {}),
    });
}

/** 获取当前所有插件信息 */
export async function getAllPlugin(options?: {}) {
    return request('/webapi/integration/plugin/list', {
        method: "GET",
        ...(options || {})
    })
}

/** 创建实例 */
export async function createPlugin(data: any, options?: {}) {
    return request('/webapi/integration/plugin/create', {
        method: "post",
        data,
        ...(options || {})
    })
}

/** 删除实例 */
export async function deletePlugin(id: number, options?: {}) {
    return request(`/webapi/integration/plugin/delete/${id}`, {
        method: "delete",
        ...(options || {})
    })
}

/** 按照产品类型查看租户实例详情*/
export async function queryEgByType(type: any, options?: {}) {
    return request(`/webapi/integration/plugin/listByType/${type}`, {
        method: "get",
        ...(options || {})
    })
}

export async function queryPluginById(id: any, options?: {}) {
    return request(`/webapi/integration/plugin/queryById/${id}`, {
        method: "get",
        ...(options || {})
    })
}

/** 按照产品名称查看租户实例详情*/
export async function updatePlugin(data: any, options?: {}) {
    return request(`/webapi/integration/plugin/update`, {
        method: "post",
        data: data,
        ...(options || {})
    })
}

export async function updateProduct(data: any, options?: {}) {
    return request(`/webapi/integration/product/update`, {
        method: "post",
        data: data,
        ...(options || {})
    })
}

export async function getProductReceived(options?: {}) {
    return request(`/webapi/integration/product/dataReceived`, {
        method: "get",
        ...(options)
    });
}
