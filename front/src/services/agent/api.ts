
import request from '../request';

export const listFiles = async (param: any, options?: {}) => {
    return await request(`/webapi/agent/listFiles`, {
        method: 'POST',
        data: param,
        ...(options || {}),
    });
};

export const previewFile = async (param: any, options?: {}) => {
    return await request(`/webapi/agent/previewFile`, {
        method: 'POST',
        data: param,
        ...(options || {}),
    });
};

export const inspect = async (param: any, options?: {}) => {
    return await request(`/webapi/agent/inspect`, {
        method: 'POST',
        data: param,
        ...(options || {}),
    });
};

export const getVMagent = async (options?: {}) => {
    return await request(`/webapi/agent/vmAgent`, {
        method: "get",
        ...(options || {})
    });
}
