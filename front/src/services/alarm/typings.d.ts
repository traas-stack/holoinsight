

declare namespace API {
  type AlarmRule = {
    id: number;
    ruleName: string;
    ruleType: string;
    alarmLevel: string;
    ruleDescribe: string;
    status: number;
    isMerge: number;
    recover: number;
    noticeType: string;
    alarmContent: string;
    tenant: string;
    rule: any;
    timeFilter: any;
    extra: any;
  };

  type FolderModel = {
    id?: any;
    name: string;
    tenant?: string;
    parentFolderId: any;
    extInfo?: string;
  };
}

declare namespace API {
  type AlarmGroup = {
    id: number;
    groupName: string;
    groupInfo: any;
    tenant: string;
  };
}
