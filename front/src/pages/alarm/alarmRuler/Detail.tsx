import AlarmSubscribe from '@/components/AlarmSubscribe';
import CommonFormList from '@/pages/alarm/component/CommonFormList';
import CommonTriggerList from '@/pages/alarm/component/CommonTriggerList';
import AlarmTemplate from '@/pages/alarm/component/AlarmTemplate';
import { NAMES } from '@/pages/alarm/component/const';
import PQlPanel from '@/pages/alarm/component/PQLPanel';
import {
  alarmRuleCreate,
  alarmRuleQueryById,
  alarmRuleUpdate,
  alarmSubscribeQueryById,
  alarmSubscribeSubmit,
  pageQueryAlarmWebHook,
} from '@/services/alarm/api';
import {
  Button,
  Card,
  Col,
  Form,
  Input,
  message,
  Radio,
  Row,
  Select,
  Space,
  TimePicker,
  Tooltip
} from 'antd';
import qs from 'query-string';
import { UnorderedListOutlined } from '@ant-design/icons';
import ProForm, {
  ProFormCheckbox,
  // ProFormRadio,
  // ProFormList,
  // ProFormText,
  ProFormSelect,
} from '@ant-design/pro-form';
import React, { useEffect, useRef, useState } from 'react';

import { queryById } from '@/services/customplugin/api';

import { querySchema } from '@/services/tenant/api';
import moment from 'moment';
import type { FormInstance } from 'antd/es/form';
import { history } from 'umi';
import './Detail.less';

import AlarmCallForm from '@/components/Alarm/alarmCallBackForm';
import CommonBreadcrumb from '@/components/CommonBreadcrumb';
import { buildQpl } from '@/pages/alarm/component/BuildPql';
import { alarmWebHookCreate } from '@/services/alarm/api';
import { useModel } from '@umijs/max';
import $i18n from '@/i18n';
import AlarmPanel from '@/pages/alarm/alarmHistory/AlarmPanel';
// import './index.less';
import { debounce } from 'lodash';
import { useSearchParams } from '@umijs/max';

const { Option } = Select;
const { TextArea } = Input;

const comPutationRuler: any = {
  EQ: $i18n.get({ id: 'holoinsight.pages.alarm.Detail.Equal', dm: '等于' }),
  NEQ: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.NotEqual',
    dm: '不等于',
  }),
  GT: $i18n.get({ id: 'holoinsight.pages.alarm.Detail.Greater', dm: '大于' }),
  LT: $i18n.get({ id: 'holoinsight.pages.alarm.Detail.Less', dm: '小于' }),
  LTE: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.LessThanOrEqual',
    dm: '小于等于',
  }),
  GTE: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.GreaterThanOrEqual',
    dm: '大于等于',
  }),
  NULL: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.NoData',
    dm: '无数据',
  }),
};

const periodObj: any = {
  Current: "最近N个周期",
  PeriodValue: "最近N个周期同环比差值",
  PeriodAbs: "最近N个周期同环比绝对值",
  PeriodRate: "最近N个周期同环比涨跌百分比"
}

const timeOptions: any = {
  "MINUTE": "1分钟",
  "FIVE_MINUTE": "5分钟",
  "QUARTER_HOUR": "15分钟",
  "HALF_HOUR": "半小时",
  "HOUR": "1小时",
  "DAY": "1天",
  "WEEK": "1周",
}

const calcuRuler: any = {
  avg: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.Average',
    dm: '平均值',
  }),
  count: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.NumericalValue',
    dm: '数值',
  }),
  min: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.MinimumValue',
    dm: '最小值',
  }),
  max: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.Maximum',
    dm: '最大值',
  }),
  sum: $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.Summation',
    dm: '求和',
  }),
};
type TriggerType = 'rule' | 'ai' | 'pql';
const Detail: React.FC = (props: any) => {
  //@ts-ignore
  const { initialState } = useModel('@@initialState');
  const [templateShow, setTemplateShow] = useState(false);
  const [selectValue, setSelectValue] = useState('days');
  const [form] = Form.useForm();
  const [proForm] = Form.useForm();
  const settingFormRef = React.createRef<FormInstance>();
  const formRef = React.createRef<FormInstance>();
  const triggerType: TriggerType = Form.useWatch('triggerType', form) || 'rule';
  let hookFormRef = useRef<HTMLFormElement>(null);
  const [status, setStatus] = useState<any>([]); //  保存 or 编辑 状态
  const [showGroup, setShowGroup] = useState([]); //  集群显示的ID的list
  const [moreCondition, setMoreCondition] = useState<any>([]); //更多条件
  const [numberCp, setNumberCp] = useState<any>([]); //数值条件
  const [saveRulerList, setSaveRulerList] = useState<any>([]); //存储保存后的单个值
  const [moreShow, setMoreShow] = useState<boolean>(false);
  const [ipDropArr, setIpDropArr] = useState<any>([]);
  const [assembleStatus, setAssembleStatus] = useState<any>([]);
  //高级配置
  const [alramHookType, setAlarmHookType] = useState('');
  const [template, setTemplate] = useState([]);
  const [json, setJson] = useState({});
  const [subscriberObj, setSubscriberObj] = useState<any>({
    userSub: [],
    dingGroupSub: [],
    userGroupSub: [],
  });
  const appName = new URLSearchParams(location.search).get('app');
  // const [tagsobj, setTagsobj] = useState({
  //   needRequest: [],
  //   needKey: []  // {key: 1-1，name:""}
  // });  //
  const { sourceType, id, sourceId } = props.match.params;
  const { from } = props.location.search;
  const [searchParams] = useSearchParams();
  const changeBreadcrumb = searchParams.get('changeBreadcrumb') === 'true'
  const title = changeBreadcrumb ? $i18n.get({
    id: 'holoinsight.src.components.defaultLayout.UserAlarmSub',
    dm: '告警订阅',
  }) : $i18n.get({
    id: 'holoinsight.pages.alarm.Detail.RuleManagement',
    dm: '规则管理',
  })
  const urlListUrl = changeBreadcrumb ? '/user/alarmsub' : '/alarm/rule'
  const urlList = [
    {
      name: `${from === 'log'
        ? $i18n.get({
          id: 'holoinsight.pages.alarm.Detail.LogMonitoring',
          dm: '日志监控',
        })
        : title
        }`,
      url: `${from === 'log' ? '/log' : urlListUrl}`,
    },

    {
      name: `${id
        ? $i18n.get({
          id: 'holoinsight.pages.alarm.Detail.EditRule',
          dm: '编辑规则',
        })
        : $i18n.get({
          id: 'holoinsight.pages.alarm.Detail.AddRule',
          dm: '新建规则',
        })
        }`,
      url: '/alarms/new',
    },
  ];

  function handleSubmitRuler() {
    formRef.current
      ?.validateFields()
      .then((res) => {
        const timeFilter: any = {};
        timeFilter.model = res.timeFilterValue === 'days' ? 'days' : 'weeks';
        timeFilter.weeks = res.timeFilterValue === 'days' ? [] : res.weeksDay;
        // timeFilter.dayTime
        timeFilter.from = res?.dayTime?.[0].format('H:mm:ss');
        timeFilter.to = res?.dayTime?.[1].format('H:mm:ss');
        const newTriggers = (res.triggers || []).map(
          (item: any, index: number) => {
            (item.datasources || []).forEach((ele: any, key: number) => {
              if (assembleStatus?.[index]?.[key] === 'true') {
                ele.groupBy = null;
              }
              ele.name = NAMES[key];
              ele.downsample = ele.downsample ? ele.downsample + 'm' : null;
              if (
                Array.isArray(ele.filters) &&
                Object.keys(ele.filters[0] || {}).length
              ) {
                const newFilter = (ele.filters || []).map((aEle: any) => {
                  const newItem = {
                    type: aEle.type,
                    name: aEle.key,
                    value: (aEle?.whites || []).join('|'),
                  };
                  return newItem;
                });
                ele.filters = newFilter;
              } else {
                delete ele.filters;
              }
            });

            if (triggerType === 'ai') {
              const triggerContent = `${item.datasources[0].metric
                } 相比历史有明显${item.type === 'ValueUp' ? '上涨' : '下跌'}`;
              return {
                datasources: item.datasources,
                type: item.type,
                query: item.query,
                tenant: initialState.currentTenant,
                triggerContent,
                aggregator: 'avg', //目前先默认写死avg
                downsample: '1', //目前先默认写死1
              };
            }
            if (triggerType === 'rule') {
              const newCompareConfigs = item.compareConfigs.map((m: any, n: number) => {
                const compareParam: any = [];
                if (m.cmp) {
                  compareParam.push({
                    cmp: m.cmp,
                    cmpValue: m.cmpValue,
                  });
                }

                if (m.cmpMore) {
                  compareParam.push({
                    cmp: m.cmpMore,
                    cmpValue: m.cmpValueMore,
                  });
                }
                let str = formatterStr(item, m.cmp, m.cmpValue, m.cmpMore, m.cmpValueMore, item.downsample);
                if (item.datasources.length > 1) {
                  str = `${item.query} ` + str;
                } else {
                  str = `${item.datasources[0].metric} ` + str;
                }
                // item.triggerContent = str;
                return {
                  compareParam,
                  triggerLevel: m.triggerLevel,
                  triggerContent: str
                }
              })
              delete item.triggerContent // 慢慢将老告警规则替换为新规则
              delete item.compareParam //慢慢的将compareParam删除出去
              item.compareConfigs = newCompareConfigs;
              return item;
            }
            return item;
          },
        );

        const downRequest = id ? alarmRuleUpdate : alarmRuleCreate;
        res.ruleType = 'rule';
        res.timeFilter = timeFilter;
        res.tenant=initialState?.currentTenant
        res.rule = {
          boolOperation: res.boolOperation,
          triggers: newTriggers,
        };
        if (id) {
          res.id = id;
        }
        if (sourceId && sourceType) {
          res.sourceId = sourceId;
          res.sourceType = sourceType;
        }
        let dingTemplate: any = {};
        (res.dingtalkTemplate || []).forEach((element: any) => {
          dingTemplate[element.key] = element.value;
        })
        res.extra = {
          notificationConfig: {
            dingtalkTemplate: Object.keys(dingTemplate).length ? {
              fieldMap: dingTemplate
            } : null
          }
        }
        // 从应用侧带应用name进来
        if (appName) {
          res.sourceType = `apm_${appName}`;
        }
        if (triggerType === 'pql') {
          res = {
            ...res,
            triggers: undefined,
            rule: {},
            triggerType: 'pql',
            ruleType: 'pql',
          };
        }
        if (triggerType === 'ai') {
          res = {
            ...res,
            triggerType: 'ai',
            ruleType: 'ai',
          };
        }
        downRequest(res).then((data) => {
          if (data) {
            settingFormRef?.current
              ?.validateFields()
              .then((backData) => {
                backData.alarmSubscribe = subscriberObj.userSub
                  .concat(subscriberObj.dingGroupSub)
                  .concat(subscriberObj.userGroupSub);
                if (id) {
                  backData.uniqueId = triggerType !== 'pql' ? `rule_${id}` : `pql_${id}`;
                } else {
                  backData.uniqueId = triggerType !== 'pql' ? `rule_${data}` : `pql_${data}`;
                }
                if (backData.way === 'hand') {
                  hookFormRef
                    .validateFields()
                    .then(async (res: any) => {
                      const downData = JSON.parse(JSON.stringify(res));
                      downData.status = 1;
                      downData.type = 1;
                      downData.requestHeaders = {};
                      res.requestHeaders.forEach((element: object) => {
                        const key = Object.keys(element);
                        downData.requestHeaders[element[key[0]]] =
                          element[key[1]];
                      });
                      downData.requestHeaders = JSON.stringify(
                        downData.requestHeaders,
                      );

                      const data = await alarmWebHookCreate(downData);
                      commonSubscribeSubmit(backData, data, id);
                    })
                    .catch((err: any) => { });
                } else {
                  commonSubscribeSubmit(backData, backData.webhook, id);
                }
              })
              .catch((res) => { });
          }
        });
      })
      .catch((res) => {
        const nameField = res?.errorFields?.[0]?.name?.[0];
        nameField && formRef?.current.scrollToField(nameField);
      });
  }

  function commonSubscribeSubmit(backData: any, hookId: any, id: any) {
    const newObj: any = {};
    if (hookId) {
      backData.alarmSubscribe.push({
        groupId: hookId,
        noticeType: ['webhook'],
      });
    }

    newObj.alarmSubscribe = backData.alarmSubscribe;
    newObj.uniqueId = backData.uniqueId;
    // console.log({ backData, newObj, hookId, id ,s});
    alarmSubscribeSubmit(newObj).then((back) => {
      if (back) {
        message.success(
          `${id
            ? $i18n.get({
              id: 'holoinsight.pages.alarm.Detail.EditSuccess',
              dm: '编辑成功',
            })
            : $i18n.get({
              id: 'holoinsight.pages.alarm.Detail.AddSuccess',
              dm: '新增成功',
            })
          }`,
        );
        if (appName) {
          history.back();
        } else {
          history.push(urlListUrl)
        }
      }
    });
  }

  useEffect(() => {
    pageQueryAlarmWebHook({
      pageNum: 1,
      pageSize: 99999,
      target: {},
    }).then((res: any) => {
      const backData = Array.isArray(res.items)
        ? res.items.map((item: any) => {
          const newObj: any = {};
          newObj.value = item.id;
          newObj.label = item.webhookName;
          return newObj;
        })
        : [];
      setTemplate(backData);
    });
    if (id) {
      alarmRuleQueryById(id).then((res: any) => {
        if (res) {
          const showGroupArr: any = [];
          setSelectValue(res?.timeFilter?.model === 'weeks' ? 'weeks' : 'days');
          res.timeFilterValue =
            res?.timeFilter?.model === 'weeks' ? 'weeks' : 'days';
          res.boolOperation = res?.rule?.boolOperation;
          res.triggers = res?.rule?.triggers;
          res.weeksDay = res?.timeFilter?.weeks;
          res.dayTime = [
            moment(res?.timeFilter?.from, 'HH:mm:ss'),
            moment(res?.timeFilter?.to, 'HH:mm:ss'),
          ];

          const newTagsObj: any = {};
          const needRequest = new Set();
          const needKey: any = [];
          const triggerContent: any = [];
          (res?.triggers || []).forEach((element: any, index: number) => {
            //兼容老逻辑
            if (triggerType === 'ai' || !element.compareConfigs) {
              triggerContent.push(element.triggerContent);
            } else {
              const str = backInitStr(element);
              triggerContent.push(str);
            }
            status.push(index);
            showGroupArr.push([]);
            assembleStatus[index] = [];
            (element?.datasources || []).forEach((item: any, key: number) => {
              needRequest.add(item.metric);
              needKey.push({
                key: `${index}_${key}`,
                id: item.metric,
              });

              item.downsample = item.downsample
                ? item.downsample.slice(0, -1)
                : null;
              if (!item.groupBy) {
                delete item.groupBy;
              }
              if (item.aggregator === 'none') {
                assembleStatus[0][index] = 'true';
              }
              //可删除
              // if (Array.isArray(item.filters) && item.filters.length) {
              //   showGroupArr[index].push(key);
              //   item.check = [1];
              // } else {
              //   item.check = [];
              // }
              item.check = Array.isArray(item.filters) && item.filters.length ? '1' : '0';

              item.filters = (item.filters || []).map((ele: any) => {
                const newItem: any = {};
                newItem.type = ele.type === 'not_literal_or' ? 'not_literal_or' : 'literal_or'
                newItem.key = ele.name;
                newItem.whites = (ele?.value || '').split('|');
                return newItem;
              });
            });
            // const [tagsobj, setTagsobj] = useState({
            //   needRequest: [],
            //   needKey:[]  // {key: 1-1，name:""}
            // });
            const backData = getTriggerData(element, res?.triggers.length, index);
            element.compareConfigs = backData;
            // if(element.compareConfigs){
            //    element.compareConfigs.map((k:any,n:number)=>{

            //    })
            // }else{
            //   //兼容老逻辑
            //   (element?.compareParam || []).forEach((item: any, key: number) => {
            //     if (key === 0) {
            //       element.cmp = item.cmp;
            //       element.cmpValue = item.cmpValue;
            //     } else {
            //       moreCondition.push(index);
            //       setMoreCondition([...moreCondition]);
            //       element.cmpMore = item.cmp;
            //       element.cmpValueMore = item.cmpValue;
            //     }
            //   });
            // }
          });
          setAssembleStatus[[...assembleStatus]];
          setSaveRulerList(triggerContent);
          setStatus([...status]);
          newTagsObj.needRequest = Array.from(needRequest);
          newTagsObj.needKey = needKey;
          reuqestBackTagArr(newTagsObj);
          setShowGroup(showGroupArr);
          if (!res.triggerType) {
            res.triggerType = res.ruleType || 'rule';
          }
          const backTemplate = res?.extra?.notificationConfig?.dingtalkTemplate?.fieldMap;
          if (Object.keys(backTemplate || {}).length) {
            setTemplateShow(true);
            res.dingtalkTemplate = Object.keys(backTemplate).map((item, index) => {
              return {
                key: item,
                value: backTemplate[item]
              }
            })
          }
          form.setFieldsValue(res);
        }
      });
      alarmSubscribeQueryById(`${triggerType !== 'pql' ? 'rule_' + id : 'pql_' + id}`).then((res) => {
        const back = res.alarmSubscribe || [];
        const arr = back.filter((item: any) => {
          return item?.noticeType
            ? item?.noticeType.includes('webhook')
            : false;
        });
        const otherArr = back.filter((item: any) => {
          return item?.noticeType
            ? !item?.noticeType.includes('webhook')
            : true;
        });
        const alarmSubscribe: any = {};
        alarmSubscribe.userSub = otherArr.filter((item: any) => {
          return item?.noticeType
            ? !item?.noticeType.includes('dingDingRobot') && item.subscriber
            : false;
        });
        alarmSubscribe.dingGroupSub = otherArr.filter((item: any) => {
          return item?.noticeType
            ? item?.noticeType.includes('dingDingRobot')
            : false;
        });
        alarmSubscribe.userGroupSub = otherArr.filter((item: any) => {
          return !item.noticeType.includes('dingDingRobot') && !item.subscriber;
        });
        setSubscriberObj(alarmSubscribe);
        const backData = {
          // alarmSubscribe: res.filter((item) => {
          //   return item.noticeType !== 'webhook'
          // }),
          webhook: arr[0]?.groupId,
          way: 'auto',
          highConfig: 'highConfig',
        };

        setMoreShow(true);
        setAlarmHookType('auto');
        proForm.setFieldsValue(backData);
      });
    }
    if (sourceId) {
      queryById(sourceId).then((res) => {
        form.setFields([
          {
            name: ['triggers', 0, 'datasources', 0, 'metric'],
            value: res?.conf?.collectMetrics?.[0]?.name,
          },
        ]);
      });
    }
  }, []);

  function getTriggerData(element: any, len: number, key: number) {
    //新逻辑
    if (element.compareConfigs) {
      return element.compareConfigs.map((k: any, n: number) => {
        let item: any = {};
        item.cmp = k.compareParam[0].cmp;
        item.cmpValue = k.compareParam[0].cmpValue;
        if (k.compareParam?.[1]) {
          if (moreCondition?.[key]) {
            moreCondition[key].push(n);
          } else {
            moreCondition[key] = [];
            moreCondition[key].push(n);
          }
          setMoreCondition([...moreCondition]);
          item.cmpMore = k.compareParam?.[1].cmp;
          item.cmpValueMore = k.compareParam?.[1].cmpValue;
        }
        item.triggerLevel = k.triggerLevel
        return item;
      })
    } else {
      //兼容老逻辑
      let backData: any = [];
      let eleItem: any = {};
      (element?.compareParam || []).forEach((item: any, key: number) => {
        if (key === 0) {
          eleItem.cmp = item.cmp;
          eleItem.cmpValue = item.cmpValue;
        } else {
          if (moreCondition?.[key]) {
            moreCondition[key].push(0);
          } else {
            moreCondition[key] = [];
            moreCondition[key].push(0);
          }
          // moreCondition.push(index);
          setMoreCondition([...moreCondition]);
          eleItem.cmpMore = item.cmp;
          eleItem.cmpValueMore = item.cmpValue;
        }
      });
      eleItem.triggerLevel = element.triggerLevel;
      backData = [eleItem];
      return backData;
    }
  }

  function handleChangeChildData(data: any) {
    setJson(data);
  }
  function reuqestBackTagArr(newTagsObj: any) {
    const backRequest = newTagsObj.needRequest.map((item: string) =>
      querySchema({ name: item }),
    );

    Promise.all(backRequest)
      .then((res) => {
        const arrChangeMap = new Map(
          res.map((item) => [item.metric, item.tags]),
        );

        newTagsObj.needKey.forEach((element: any) => {
          const keyArr = element.key.split('_');
          if (ipDropArr?.[keyArr[0]]?.[keyArr[1]]) {
            ipDropArr[keyArr[0]][keyArr[1]] = arrChangeMap
              .get(element.id)
              .map((bitem: any) => {
                return {
                  label: bitem,
                  value: bitem,
                };
              });
          } else {
            ipDropArr[keyArr[0]] = ipDropArr[keyArr[0]]
              ? ipDropArr[keyArr[0]]
              : [];
            ipDropArr[keyArr[0]][keyArr[1]] = arrChangeMap
              .get(element.id)
              .map((bitem: any) => {
                return {
                  label: bitem,
                  value: bitem,
                };
              });
          }
        });
        setIpDropArr([...ipDropArr]);
      })
      .catch((err) => { });
  }

  function getData(data: any) {
    if (data.type === 'userSub') {
      subscriberObj.userSub = data.data;
    } else if (data.type === 'dingGroupSub') {
      subscriberObj.dingGroupSub = data.data;
    } else {
      subscriberObj.userGroupSub = data.data;
    }
    setSubscriberObj(JSON.parse(JSON.stringify(subscriberObj)));
  }

  function handleChangeValue(value: string) {
    setSelectValue(value);
  }

  function formatterStr(obj: any, cmp: string, cmpValue: string, cmpMore: string, cmpMoreValue: string, downsample:string) {
    let str = '';
    str = `${obj.type === 'Current' ? `最近${obj?.stepNum}个周期` : `连续${obj?.stepNum}个周期与${timeOptions[obj.periodType]}${periodObj[obj.type]}`}${calcuRuler?.[obj?.aggregator]
      }${comPutationRuler[cmp]}${cmpValue}${cmpMore
        ? '且' + comPutationRuler?.[cmpMore] + cmpMoreValue
        : ''
      } 周期为${downsample}分钟`
    return str;
  }
  function backInitStr(obj: any) {
    let str = ""
    if (obj.compareConfigs) {
      if (obj.compareConfigs.length > 1) {
        obj.compareConfigs.forEach((item: any, index: number) => {
          const itemData = item.compareParam;
          str = str + ` 条件${index + 1}: ${formatterStr(obj, itemData[0]?.cmp, itemData[0]?.cmpValue, itemData[1]?.cmpMore, itemData[1]?.cmpValueMore, obj.downsample)}.`;
        })
      } else {
        const value = obj.compareConfigs[0].compareParam;
        str = `${formatterStr(obj, value[0]?.cmp, value[0]?.cmpValue, value[1]?.cmpMore, value[1]?.cmpValueMore, obj.downsample)}`;
      }
    }
    if (obj.compareConfigs.length > 1) {
      str = `${obj.query} ` + str;
    } else {
      str = `${obj?.datasources[0]?.metric} ` + str;
    }
    return str;
  }
  function backStr(obj: any) {
    let str = '';
    if (obj.compareConfigs) {
      if (obj.compareConfigs.length > 1) {
        obj.compareConfigs.forEach((item: any, index: number) => {
          str = str + ` 条件${index + 1}: ${formatterStr(obj, item.cmp, item.cmpValue, item.cmpMore, item.cmpValueMore, obj.downsample)}.`;
        })
      } else {
        const value = obj.compareConfigs[0];
        str = `${formatterStr(obj, value.cmp, value.cmpValue, value.cmpMore, value.cmpValueMore, obj.downsample)}`;
      }
    } else {
      str = `${formatterStr(obj, obj.cmp, obj.cmpValue, obj.cmpMore, obj.cmpValueMore, obj.downsample)}`;
    }
    if (obj.datasources.length > 1) {
      str = `${obj.query} ` + str;
    } else {
      str = `${obj?.datasources[0]?.metric} ` + str;
    }
    return str;
  }

  function commonCovert(id: number, type: string) {
    if (type === 'save') {
      const formData = form.getFieldValue('triggers');
      const obj = formData[id] || {};
      const str = backStr(obj);
      saveRulerList[id] = str;
      setSaveRulerList([...saveRulerList]);
      if (triggerType === 'ai') {
        form.setFieldValue(['triggers', id, 'triggerContent'], str);
      }
    } else if (type === 'submit') {
      const newArr = saveRulerList.filter((item: any, index: number) => index !== id);
      setSaveRulerList(newArr);
    }
  }

  function rendetCardTitle(type: string, field: any, remove: Function) {
    function handleSaveItem(types: string) {
      commonCovert(field.name, 'save');
      if (types === 'save') {
        status.push(field.name);
        setStatus([...status]);
      } else {
        setStatus(status.filter((item: any) => item !== field.name));
      }
    }
    function handleDelete() {
      commonCovert(field.name, 'delete');
      // setStatus(status.filter((item: any) => item !== field.name));
      const resStatus = status.splice(0, status.length - 1);
      setStatus(resStatus);
      if (ipDropArr[field.name]) {
        ipDropArr.splice(field.name, 1);
        setIpDropArr([...ipDropArr]);
      }
      if (assembleStatus[field.name]) {
        assembleStatus.splice(field.name, 1);
        setAssembleStatus([...assembleStatus]);
      }
      remove(field.name);
    }

    return type === 'chart' ? (
      <div>
        <Tooltip placement="top" title={saveRulerList[field.name] || ''} >
          <span className="title">{saveRulerList[field.name] || ''}</span>
        </Tooltip>


        <section>
          <Button
            type="text"
            onClick={() => {
              handleSaveItem('edit');
            }}
          >
            {$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.Edit',
              dm: '编辑',
            })}
          </Button>

          <Button
            type="text"
            onClick={() => {
              handleDelete();
            }}
          >
            {$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.Delete',
              dm: '删除',
            })}
          </Button>
        </section>
      </div>
    ) : (
      <div>
        <span className="title">
          {$i18n.get({
            id: 'holoinsight.pages.alarm.Detail.AlarmCondition',
            dm: '告警条件',
          })}
        </span>

        <section>
          <Button
            type="text"
            onClick={() => {
              handleSaveItem('save');
            }}
          >
            {$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.Save',
              dm: '保存',
            })}
          </Button>

          <Button
            type="text"
            onClick={() => {
              handleDelete();
            }}
          >
            {$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.Delete',
              dm: '删除',
            })}
          </Button>
        </section>
      </div>
    );
  }

  function handleMoreCondition(id: number, idx: number, type: string) {
    if (type === 'more') {
      if (moreCondition?.[id]) {
        moreCondition[id].push(idx);
      } else {
        moreCondition[id] = [];
        moreCondition[id].push(idx);
      }

      setMoreCondition([...moreCondition]);
    } else {
      const newMoreCondition = moreCondition[id].filter((item: any) => {
        return item !== idx;
      });
      moreCondition[id] = newMoreCondition;
      setMoreCondition([...moreCondition]);
    }
  }

  function handleChangeType(e: string, id: number, idx: number) {
    const type = e === 'NULL' ? true : false
    if (numberCp?.[id]) {
      if (!numberCp?.[id]?.[idx]) {
        numberCp[id][idx] = [];
      }
      numberCp[id][idx] = type;
    } else {
      numberCp[id] = [];
      if (!numberCp?.[id]?.[idx]) {
        numberCp[id][idx] = [];
      }
      numberCp[id][idx] = type;
    }
    setNumberCp([...numberCp]);
  }
  const debounceHandleSubmitRuler = debounce(handleSubmitRuler, 1000, { 'leading': true, 'trailing': false })
  return (
    <div>
      <CommonBreadcrumb urlList={urlList} />

      <Form
        // scrollToFirstError={true}
        labelAlign="left"
        form={form}
        ref={formRef}
      >
        <Card
          title={$i18n.get({
            id: 'holoinsight.pages.alarm.Detail.BasicInformation',
            dm: '基础信息',
          })}
          className="alarmDetail-card-detail"
          headStyle={{ textAlign: 'left' }}
          extra={
            id ? (
              <Button
                onClick={() => {
                  history.push({
                    pathname: `/alarm/details/${id}`,
                    search: qs.stringify({
                      from: 'alarm',
                    }),
                  });
                }}
                icon={<UnorderedListOutlined />}
              >
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.AlarmHistory',
                  dm: '告警历史',
                })}
              </Button>
            ) : null
          }
        >
          <Form.Item
            labelCol={{ span: 2, offset: 0 }}
            name="ruleName"
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.AlarmRuleName',
              dm: '报警规则名称',
            })}
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.AlarmRuleNameIsRequired',
                  dm: '报警规则名称为必填项',
                }),
              },
            ]}
          >
            <Input style={{ width: 624 }} />
          </Form.Item>

          <Form.Item
            labelCol={{ span: 2, offset: 0 }}
            name="alarmLevel"
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.AlarmLevel',
              dm: '报警级别',
            })}
            rules={[
              {
                required: true,
                message: $i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.RequiredAlarmLevel',
                  dm: '报警级别为必选项',
                }),
              },
            ]}
            initialValue={'1'}
          >
            <Select style={{ width: 210 }} allowClear>
              <Option value="1">
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Emergency',
                  dm: '紧急',
                })}
              </Option>

              <Option value="2">
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Serious',
                  dm: '严重',
                })}
              </Option>

              <Option value="3">
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.High',
                  dm: '高',
                })}
              </Option>

              <Option value="4">
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Medium',
                  dm: '中',
                })}
              </Option>

              <Option value="5">
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Low',
                  dm: '低',
                })}
              </Option>
            </Select>
          </Form.Item>

          <Form.Item
            className="alram-create-describle"
            labelCol={{ span: 2, offset: 0 }}
            name="ruleDescribe"
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.Description.1',
              dm: ' 描述',
            })}
          >
            <TextArea style={{ width: 624, minHeight: 88 }} />
          </Form.Item>
        </Card>

        <Card
          title={$i18n.get({
            id: 'holoinsight.pages.alarm.Detail.TriggerCondition',
            dm: '触发条件',
          })}
          className="alarmDetail-card-detail"
          headStyle={{ textAlign: 'left' }}
        >
          <Form.Item
            name="triggerType"
            initialValue={'rule'}
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.TriggerType',
              dm: '触发类型',
            })}
          >
            <Select
              placeholder={$i18n.get({
                id: 'holoinsight.pages.alarm.Detail.SelectATriggerType',
                dm: '请选择触发类型',
              })}
              style={{ width: 300 }}
              onChange={(value) => {
                const triggers = form.getFieldValue('triggers') || [];
                const newData = triggers.map((item) => {
                  return {
                    ...item,
                    type: value === 'custom' ? 'Current' : undefined,
                    datasources:
                      value === 'custom'
                        ? item.datasources
                        : item.datasources.slice(0, 1),
                  };
                });
                form.setFieldValue('triggers', newData);
              }}
            >
              <Option value="rule">
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.CustomThreshold',
                  dm: '自定义阈值',
                })}
              </Option>
              <Option value="ai">智能阈值</Option>
              <Option value="pql">
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.PqlAlert',
                  dm: 'pql告警',
                })}
              </Option>
            </Select>
          </Form.Item>

          {triggerType !== 'pql' && (
            <Form.Item name="boolOperation" initialValue={'OR'}>
              <Radio.Group>
                <Radio value="OR">
                  {$i18n.get({
                    id: 'holoinsight.pages.alarm.Detail.TriggerAnAlarmIfAny',
                    dm: '满足任意条件触发报警',
                  })}
                </Radio>

                <Radio value="AND">
                  {$i18n.get({
                    id: 'holoinsight.pages.alarm.Detail.TriggerAnAlarmIfAll',
                    dm: '满足所有条件触发报警',
                  })}
                </Radio>
              </Radio.Group>
            </Form.Item>
          )}

          {triggerType === 'pql' ? (
            <PQlPanel
              fieldAll={{}}
              setShowGroup={setShowGroup}
              form={form}
              triggerType={triggerType}
              showGroup={showGroup}
              ipDropArr={ipDropArr}
              setIpDropArr={setIpDropArr}
              assembleStatus={assembleStatus}
              setAssembleStatus={setAssembleStatus}
            />
          ) : (
            <Form.List name="triggers" initialValue={[{}]}>
              {(fields, { add: addItem, remove: removeItem }) => {
                return (
                  <>
                    {fields.map((fieldAll: any) => {
                      return !status.includes(fieldAll.name) ? (
                        <Card
                          className="alarmDetail-chart-item"
                          title={rendetCardTitle('text', fieldAll, removeItem)}
                          headStyle={{ textAlign: 'left' }}
                          key={fieldAll.key}
                        >
                          <CommonFormList
                            fieldAll={fieldAll}
                            setShowGroup={setShowGroup}
                            form={form}
                            triggerType={triggerType}
                            showGroup={showGroup}
                            ipDropArr={ipDropArr}
                            setIpDropArr={setIpDropArr}
                            assembleStatus={assembleStatus}
                            setAssembleStatus={setAssembleStatus}
                          />

                          <Row className="alarm-condition">
                            {triggerType === 'rule' && <CommonTriggerList
                              fieldAll={fieldAll}
                              moreCondition={moreCondition}
                              numberCp={numberCp}
                              handleChangeType={handleChangeType}
                              handleMoreCondition={handleMoreCondition}
                            />}

                            {triggerType === 'ai' && (
                              <Col span={3}>
                                <Form.Item
                                  labelCol={{ span: 2, offset: 1 }}
                                  name={[fieldAll.name, 'type']}
                                  rules={[
                                    {
                                      required: true,
                                      message: $i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.Required',
                                        dm: '必填项',
                                      }),
                                    },
                                  ]}
                                >
                                  <Select
                                    placeholder={$i18n.get({
                                      id: 'holoinsight.pages.alarm.Detail.PleaseSelect',
                                      dm: '请选择',
                                    })}
                                    allowClear
                                  >
                                    <Option value="ValueUp">
                                      {$i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.SingleDimensionValueIncreases',
                                        dm: '单维度值上涨',
                                      })}
                                    </Option>
                                    <Option value="ValueDown">
                                      {$i18n.get({
                                        id: 'holoinsight.pages.alarm.Detail.SingleDimensionValueDrop',
                                        dm: '单维度值下跌',
                                      })}
                                    </Option>
                                  </Select>
                                </Form.Item>
                              </Col>
                            )}
                          </Row>
                        </Card>
                      ) : (
                        <Card
                          title={rendetCardTitle('chart', fieldAll, removeItem)}
                          className="alarmDetail-chart-item"
                          key={fieldAll.key}
                        >
                          {form.getFieldValue('triggers')[fieldAll.name] && (
                            <AlarmPanel
                              data={
                                form.getFieldValue('triggers')[fieldAll.name]
                              }
                              key={fieldAll.key}
                            />
                          )}
                        </Card>
                      );
                    })}
                    <Button
                      type="dashed"
                      block
                      onClick={() => {
                        addItem();
                      }}
                    >
                      {$i18n.get({
                        id: 'holoinsight.pages.alarm.Detail.AddAlertConditions',
                        dm: '添加告警条件',
                      })}
                    </Button>
                  </>
                );
              }}
            </Form.List>
          )}
        </Card>

        <Card
          className="alarmDetail-card-detail"
          title={$i18n.get({
            id: 'holoinsight.pages.alarm.Detail.AdvancedConfiguration',
            dm: '高级配置',
          })}
          headStyle={{ textAlign: 'left' }}
        >
          <Space>
            <Form.Item
              initialValue={'days'}
              name="timeFilterValue"
              label={$i18n.get({
                id: 'holoinsight.pages.alarm.Detail.EffectiveTime',
                dm: '生效时间',
              })}
            >
              <Select
                style={{ width: 150, marginLeft: 38 }}
                onChange={handleChangeValue}
              >
                <Option value="days">
                  {$i18n.get({
                    id: 'holoinsight.pages.alarm.Detail.ByDay',
                    dm: '按天',
                  })}
                </Option>

                <Option value="weeks">
                  {$i18n.get({
                    id: 'holoinsight.pages.alarm.Detail.ByWeek',
                    dm: '按周',
                  })}
                </Option>
              </Select>
            </Form.Item>

            {selectValue === 'days' ? (
              <>
                <Form.Item
                  name="dayTime"
                  initialValue={[
                    moment('0:00:00', 'HH:mm:ss'),
                    moment('23:59:59', 'HH:mm:ss'),
                  ]}
                >
                  <TimePicker.RangePicker style={{ width: 260 }} />
                </Form.Item>
              </>
            ) : (
              <>
                <Form.Item name="weeksDay">
                  <Select
                    mode="tags"
                    style={{ width: 350 }}
                    placeholder={$i18n.get({
                      id: 'holoinsight.pages.alarm.Detail.PleaseSelectYouCanSelect',
                      dm: '请选择，可多选',
                    })}
                  >
                    <Option value={0}>
                      {$i18n.get({
                        id: 'holoinsight.pages.alarm.Detail.Sunday',
                        dm: '星期日',
                      })}
                    </Option>

                    <Option value={1}>
                      {$i18n.get({
                        id: 'holoinsight.pages.alarm.Detail.Monday',
                        dm: '星期一',
                      })}
                    </Option>

                    <Option value={2}>
                      {$i18n.get({
                        id: 'holoinsight.pages.alarm.Detail.Tuesday',
                        dm: '星期二',
                      })}
                    </Option>

                    <Option value={3}>
                      {$i18n.get({
                        id: 'holoinsight.pages.alarm.Detail.Wednesday',
                        dm: '星期三',
                      })}
                    </Option>

                    <Option value={4}>
                      {$i18n.get({
                        id: 'holoinsight.pages.alarm.Detail.Thursday',
                        dm: '星期四',
                      })}
                    </Option>

                    <Option value={5}>
                      {$i18n.get({
                        id: 'holoinsight.pages.alarm.Detail.Friday',
                        dm: '星期五',
                      })}
                    </Option>

                    <Option value={6}>
                      {$i18n.get({
                        id: 'holoinsight.pages.alarm.Detail.Saturday',
                        dm: '星期六',
                      })}
                    </Option>
                  </Select>
                </Form.Item>

                <Form.Item name="dayTime">
                  <TimePicker.RangePicker style={{ width: 260 }} />
                </Form.Item>
              </>
            )}
          </Space>

          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.EnableAlarm',
              dm: '开启报警',
            })}
            name="status"
            initialValue={1}
          >
            <Radio.Group style={{ marginLeft: 38 }}>
              <Radio value={1}>
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Yes',
                  dm: '是',
                })}
              </Radio>

              <Radio value={0}>
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.No',
                  dm: '否',
                })}
              </Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.EnableAlarmMerge',
              dm: '开启报警合并',
            })}
            name="isMerge"
            initialValue={0}
          >
            <Radio.Group style={{ marginLeft: 10 }}>
              <Radio value={1}>
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Yes',
                  dm: '是',
                })}
              </Radio>

              <Radio value={0}>
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.No',
                  dm: '否',
                })}
              </Radio>
            </Radio.Group>
          </Form.Item>

          {/* <Form.Item
               label="合并方式"
               name="mergetype"
              >
               <Select style={{ width: 200, marginLeft: 38 }} placeholder="按应用合并">
                 <Option>123</Option>
               </Select>
              </Form.Item> */}

          <Form.Item
            label={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.ReceiveRecoveryNotifications',
              dm: '接收恢复通知',
            })}
            name="recover"
            initialValue={0}
          >
            <Radio.Group style={{ marginLeft: 10 }}>
              <Radio value={1}>
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Yes',
                  dm: '是',
                })}
              </Radio>

              <Radio value={0}>
                {$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.No',
                  dm: '否',
                })}
              </Radio>
            </Radio.Group>
          </Form.Item>
          <AlarmTemplate templateShow={templateShow} />
        </Card>

        <ProForm
          submitter={{
            render: (_, dom) => null,
          }}
          layout="horizontal"
          form={proForm}
          formRef={settingFormRef}
        >
          <Card
            className="alarmDetail-card-detail"
            title={$i18n.get({
              id: 'holoinsight.pages.alarm.Detail.AlertNotificationSubscription',
              dm: '告警通知订阅',
            })}
            headStyle={{ textAlign: 'left' }}
          >
            <AlarmSubscribe
              subscriberObj={subscriberObj}
              getData={getData}
              type={id ? 'edit' : 'create'}
            />

            {/* <ProFormList
                 name="alarmSubscribe"
                 itemContainerRender={(doms) => {
                   return <ProForm.Group>{doms}</ProForm.Group>;
                 }}
                >
                 {(f, index, action) => {
                   return (
                     <>
                       <ProFormSelect
                         name="subscriber"
                         label="通知对象"
                         options={dingding}
                         showSearch
                         mode='single'
                         rules={[{ required: true, message: '通知对象为必填项' }]}
                       />
                       <ProFormRadio.Group
                         name="noticeType"
                         label="通知方式"
                         rules={[{ required: true, message: '通知方式为必填项' }]}
                         options={[
                           {
                             label: '钉钉',
                             value: 'dingding',
                           },
                           {
                             label: '钉钉群',
                             value: 'dingdingGroup',
                           },
                           {
                             label: '微信',
                             value: 'weixin',
                           },
                           {
                             label: '短信',
                              value: 'message',
                           },
                         ]}
                       />
                     </>
                   );
                 }}
                </ProFormList> */}

            <ProFormCheckbox.Group
              name="highConfig"
              layout="vertical"
              onChange={(value: []) => {
                value.length && setMoreShow(true);
                !value.length && setMoreShow(false);
              }}
              options={[
                {
                  label: $i18n.get({
                    id: 'holoinsight.pages.alarm.Detail.AdvancedConfiguration',
                    dm: '高级配置',
                  }),

                  value: 'highConfig',
                },
              ]}
            />
          </Card>

          {moreShow ? (
            <Card
              className="alarmDetail-card-detail"
              title={$i18n.get({
                id: 'holoinsight.pages.alarm.Detail.NotificationConfiguration',
                dm: '通知配置',
              })}
              headStyle={{ textAlign: 'left' }}
            >
              <ProFormSelect
                style={{ width: 250 }}
                onChange={(value) => {
                  setAlarmHookType(value);
                }}
                name="way"
                options={[
                  {
                    label: $i18n.get({
                      id: 'holoinsight.pages.alarm.Detail.ManuallySelectAnAlarmCallback',
                      dm: '手动选择报警回调模版',
                    }),

                    value: 'hand',
                  },

                  {
                    label: $i18n.get({
                      id: 'holoinsight.pages.alarm.Detail.SelectAnExistingAlarmTemplate',
                      dm: '选择已有的告警模版',
                    }),

                    value: 'auto',
                  },
                ]}
                labelAlign="right"
                labelCol={{ span: 3 }}
                wrapperCol={{ span: 12 }}
                label={$i18n.get({
                  id: 'holoinsight.pages.alarm.Detail.Type',
                  dm: '类型',
                })}
              />

              {alramHookType === 'hand' ? (
                <AlarmCallForm
                  formRef={(ele) => (hookFormRef = ele)}
                  json={json}
                  handleChangeChildData={handleChangeChildData}
                  type="alarmRuler"
                />
              ) : alramHookType === 'auto' ? (
                <ProFormSelect
                  width={200}
                  name="webhook"
                  labelAlign="right"
                  labelCol={{ span: 3 }}
                  wrapperCol={{ span: 12 }}
                  label={$i18n.get({
                    id: 'holoinsight.pages.alarm.Detail.TemplateCreated',
                    dm: '已创建模版',
                  })}
                  options={template}
                />
              ) : null}
            </Card>
          ) : null}
        </ProForm>
        <Card className="alarmDetail-card-detail" style={{ marginTop: 18 }}>
          <div className="alarm-detial-submit">
            <Button
              type="primary"
              onClick={() => {
                debounceHandleSubmitRuler()
              }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.alarm.Detail.Submit',
                dm: '提交',
              })}
            </Button>

            <Button
              style={{ marginRight: '10px' }}
              onClick={() => {
                if (appName) {
                  history.back();
                } else {
                  history.push(urlListUrl);
                }
              }}
            >
              {$i18n.get({
                id: 'holoinsight.pages.alarm.Detail.Cancel',
                dm: '取消',
              })}
            </Button>
          </div>
        </Card>
      </Form>
    </div>
  );
};

export default Detail;
