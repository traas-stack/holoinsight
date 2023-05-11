// @ts-ignore
/* eslint-disable */

declare namespace API {

    type ApiList = {
        tenant:string,
        start: number,
        end: number,
        termParams?: any
    };

    type AppTopo = {
        serviceName: string,
        depth?: Number,
        category: string,
        time?: any
    };

    type ApiComponentTraceIds = {
        serviceName?: string | null,
        start?: number,
        end?: number,
        address: string,
    }

    type ApiCallLink = {
        serviceName?: string,
        serviceInstanceName?: string,
        endpointName?: string,
        traceIds?: string[],
        traceState:'ALL'|'SUCCESS'|'ERROR',
        queryOrder: 'BY_START_TIME' | 'BY_DURATION',
        paging:{
            pageNum: number,
            pageSize: stirng
        }
        tags?:{
            key: string,
            value: string,
            List:Array 
        },
        minTraceDuration?: string,
        maxTraceDuration?:string,
        duration?:{
            start?:number,
            end?:number,
        }
    }
    type ApiCallLinkDetail = {
        traceIds: list<string>
    }
  }
  