// @ts-ignore
/* eslint-disable */

declare namespace API {
 
  type Tenant = {
    id?: string;
    name?: string;
  };
  type SysConfig = {
    data?: any;
    success?: boolean;
    message?: string;
  };

  type MetaTable = {
    id?: Int16Array;
    name?: string;
    tenant?: string;
    status?: string;
    config?: object;
    tableSchema?: object;
  }
}



