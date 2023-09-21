import { notification } from 'antd';
import request, { RequestOptionsInit, ResponseError } from 'umi-request';


const statusCodeMessage: any = {
  400: '发出的请求有错误。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。看到此消息可能是监控在发布，请稍等约30秒。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};


/**
 * 异常处理程序
 * 示例约定的返回错误格式为 { resultCode: string, resultMsg: string, isBizError: boolean }, 实际情况需要根据项目调整
 * @param {*} error
 */
async function errorHandler(error: ResponseError<any>) {
  const { response, data, request } = error;
  if (!response) {
    return;
  }
  const msg =
    data?.message || response?.statusText || statusCodeMessage[response.status];
 
  if (response.status === 401) {
    notification['error']({
        message: 'Request Error',
        description: msg,
      });
  }
}

/**
 * 配置request请求时的默认参数
 */
export default async function MRequest<T>(
  url: string,
  options: RequestOptionsInit = {},
): Promise<T> {
  let headers: any = {
    'Content-Type': 'application/json',
    ...options.headers,
  };
  if (options.requestType === 'form') {
    headers = {
      ...options.headers,
    };
  }
  const params: any = (options.params && { ...options.params }) || {};
  request.interceptors.request.use((url, options) => {
    return {
      url,
      options: { ...options },
    };
  });
  request.interceptors.response.use(async (response, options) => {
    return response;
  });
  return request(url, {
    ...options,
    params,
    credentials: 'include',
    headers,
    errorHandler,
  }).then((result) => {
    if (result) {
      const { data, message, success, resultCode, result: resData } = result;
      if (!success) {
        if (message?.indexOf('io.grpc.StatusRuntimeException') < 0) {
          notification['error']({
            message: 'Request Error',
            description: resultCode + ', ' + message,
          });
        }
      }
      return data || resData;
    }
    return {};
  });
}
