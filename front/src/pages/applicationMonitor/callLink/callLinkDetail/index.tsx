import { getCallLinkDetail } from '@/services/application/api';
import { getQueryString } from '../../../../utils/help';
import {
  Button,
  Card,
  Descriptions,
  message,
  Modal,
  Table,
  Tooltip,
} from 'antd';
import React, {useState } from 'react';
import { ArrowLeftOutlined, CopyOutlined } from '@ant-design/icons';
import { useRequest } from 'ahooks';
import type { ColumnsType } from 'antd/lib/table';
import copy from 'copy-to-clipboard';
import { history } from 'umi';
import $i18n from '../../../../i18n';
import styles from './index.less';

interface IProps {
  setIsComponentMonitor?: any;
  type?: string;
  traceId?: string;
}

type TableListItem = {
  endpointName?: string;
  serviceCode?: string;
  startTime?: string;
  endTime?: number;
  [key: string]: any;
};

export default (props: IProps) => {
  const { setIsComponentMonitor, type, traceId } = props;
  const urlTraceIds = getQueryString('traceIds');

  const [modal, setModal] = useState<boolean>(false);
  const [record, setRecord] = useState<TableListItem>({});
  const [detailList, setDetailList] = useState([]);

  function deleteChildren(data: any) {
    data.forEach((item: any) => {
      item.consumeTime = item.endTime - item.startTime;
      item.key = item.spanId;
      if (item.children.length === 0) {
        delete item.children;
      } else {
        deleteChildren(item.children);
      }
    });
    return data;
  }
  function getListToTree(arr: any) {
    const cArr = JSON.parse(JSON.stringify(arr));
    const newArr: any = [];
    const map: any = {};
    let rootTime = 0;
    let rootStartTime = 0;
    cArr.forEach((item: any) => {
      if (item.root) {
        rootTime = item?.endTime - item?.startTime;
        rootStartTime = item?.startTime;
      }
      item.children = [];
      const key: string = item.spanId;
      map[key] = item;
    });

    cArr.forEach((item: any) => {
      const time: number = item?.endTime - item?.startTime || 0;
      const parent = map[item.parentSpanId];
      if (parent) {
        const parentStartTime: number = item?.startTime - rootStartTime || 0;
        item.width = ((time / rootTime) * 100).toFixed(2);
        item.margin = ((parentStartTime / rootTime) * 100).toFixed(2);
        parent.children.push(item);
      } else {
        item.width = 100;
        newArr.push(item);
      }
    });
    return deleteChildren(newArr);
  }

  const getCallLinkDetailList = useRequest(
    () => {
      const params = {
        traceIds: urlTraceIds ? [urlTraceIds] : traceId,
      };
      return getCallLinkDetail(params);
    },
    {
      onSuccess: (res) => {
        if (res && res && res.spans) {
          const data = getListToTree(res.spans);
          setDetailList(data);
        }
      },
      onError: () => { },
    },
  );

  const columns: ColumnsType<TableListItem> = [
    {
      title: $i18n.get({
        id: 'holoinsight.callLink.callLinkDetail.CallMethod',
        dm: '调用方法',
      }),
      dataIndex: 'endpointName',
      key: 'endpointName',
      width: '35%',
      onCell: () => {
        return {
          style: {
            maxWidth: 160,
            overflow: 'hidden',
            whiteSpace: 'nowrap',
            textOverflow: 'ellipsis',
            cursor: 'pointer',
          },
        };
      },
      render: (text: string, record: any) => {
        return (
          <span
            onClick={() => {
              setModal(true);
              setRecord(record);
            }}
            style={{
              color: record?.error ? '#d4380d' : '#1890ff',
            }}
          >
            <Tooltip title={text}>{text}</Tooltip>
          </span>
        );
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.callLink.callLinkDetail.ServiceName',
        dm: '服务名',
      }),
      dataIndex: 'serviceCode',
      key: 'serviceCode',
    },
    {
      title: $i18n.get({
        id: 'holoinsight.callLink.callLinkDetail.TimeConsuming',
        dm: '耗时',
      }),
      dataIndex: 'consumeTime',
      key: 'consumeTime',
      render: (text: number) => {
        return text + 'ms';
      },
    },
    {
      title: $i18n.get({
        id: 'holoinsight.callLink.callLinkDetail.Timeline',
        dm: '时间轴',
      }),
      dataIndex: 'width',
      width: '30%',
      key: 'width',
      render: (width: number, record: any) => {
        return (
          <div
            style={{
              width: type === 'componentMonitor' ? width * 2.4 : width * 3,
              height: 10,
              borderRadius: 10,
              marginLeft:
                type === 'componentMonitor'
                  ? (record.margin * 2.4 || 'unset')
                  : (record.margin * 3 || 'unset'),
              backgroundColor: '#177ddc',
            }}
          ></div>
        );
      },
    },
  ];



 

  return (
    <>
      <div className={styles.callLinkTitle}>
        {type === 'componentMonitor' ? (
          <ArrowLeftOutlined
            onClick={() =>
              setIsComponentMonitor && setIsComponentMonitor(false)
            }
            style={{ marginRight: 10 }}
          />
        ) : (
          <ArrowLeftOutlined
            onClick={() => history.back()}
            style={{ marginRight: 10 }}
          />
        )}

        <Tooltip title={urlTraceIds || traceId}>
          <span className={styles.callLinkText}>{urlTraceIds || traceId}</span>
        </Tooltip>
        <CopyOutlined
          style={{ fontSize: 16, cursor: 'pointer', color: '#1890ff' }}
          onClick={() => {
            const copyText = urlTraceIds || traceId || '';
            copy(copyText.toString());
            message.success(
              $i18n.get({
                id: 'holoinsight.admin.apiKey.CopySuccessfully',
                dm: '复制成功！',
              }),
            );
          }}
        />
      </div>
      <Card>
        <div className={styles.appContainer}>
          <div className={styles.title}>
            {$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.MethodStack',
              dm: '方法栈',
            })}
          </div>
        </div>
        {detailList && detailList.length > 0 && (
          <Table
            columns={columns}
            scroll={{ x: '100%}' }}
            loading={getCallLinkDetailList.loading}
            dataSource={detailList}
            pagination={false}
            expandable={{
              defaultExpandAllRows: true,
            }}
          />
        )}
      </Card>
      <Modal
        title={$i18n.get({
          id: 'holoinsight.callLink.callLinkDetail.Details',
          dm: '详情',
        })}
        open={modal}
        onOk={() => setModal(false)}
        onCancel={() => setModal(false)}
        bodyStyle={{
          maxHeight: document.body.clientHeight * 0.65,
          overflow: 'auto',
        }}
        footer={[
          <Button key="close" type="primary" onClick={() => setModal(false)}>
            {$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.Close',
              dm: '关闭',
            })}
          </Button>,
        ]}
      >
        <Descriptions title="" column={1}>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.Service',
              dm: '服务',
            })}
          >
            {record?.serviceCode || '--'}
          </Descriptions.Item>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.Instance',
              dm: '实例',
            })}
          >
            {record?.serviceInstanceName || '--'}
          </Descriptions.Item>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.SpanId',
              dm: '跨度ID',
            })}
          >
            {record?.spanId || '--'}
          </Descriptions.Item>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.Endpoint',
              dm: '端点',
            })}
          >
            {record?.endpointName || '--'}
          </Descriptions.Item>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.SpanType',
              dm: '跨度类型',
            })}
          >
            {record?.type || '--'}
          </Descriptions.Item>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.Component',
              dm: '组件',
            })}
          >
            {record?.component || '--'}
          </Descriptions.Item>
          <Descriptions.Item label="Peer">
            {record?.peer || '--'}
          </Descriptions.Item>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.Error',
              dm: '错误',
            })}
          >
            {record?.error?.toString() || '--'}
          </Descriptions.Item>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.StartTime',
              dm: '开始时间',
            })}
          >
            {record?.startTime?.toString() || '--'}
          </Descriptions.Item>
          <Descriptions.Item
            label={$i18n.get({
              id: 'holoinsight.callLink.callLinkDetail.EndTime',
              dm: '结束时间',
            })}
          >
            {record?.endTime?.toString() || '--'}
          </Descriptions.Item>
          {record?.logs?.map((item: { data: any; time: number }) => {
            const dom = item?.data?.map((i: any) => (
              <Descriptions.Item key={i?.key} label={i?.key}>
                {i?.value || '--'}
              </Descriptions.Item>
            ));

            return dom;
          })}

          {record?.tags?.map(
            (
              item: { key: any; value: any },
              index: React.Key | null | undefined,
            ) => {
              return (
                <Descriptions.Item key={index} label={item?.key}>
                  {item?.value || '--'}
                </Descriptions.Item>
              );
            },
          )}
        </Descriptions>
      </Modal>
    </>
  );
};
