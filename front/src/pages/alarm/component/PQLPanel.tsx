import PQlEditor from '@/components/PQLEditor';
import $i18n from '@/i18n';
import PQlChart from '@/pages/alarm/component/PQLChart';
import { alarmQueryPqlParse } from '@/services/alarm/api';
import { Button, Form, message, Space } from 'antd';
import { useRequest } from 'ahooks';
import React, { useState } from 'react';
export default function PQlPanel(props: any) {
  const { form } = props;
  const [pql, setPql] = useState<
    | {
        pql: string;
        startTime: number;
        endTime: number;
        refresh: number;
      }
    | undefined
  >(undefined);
  const [pqlDetail, setPqlDetail] = useState<
    | {
        exprs: string[];
        metrics: string[];
        rawPql: string;
      }
    | undefined
  >(undefined);
  const pqlParse = useRequest(alarmQueryPqlParse, {
    manual: true,
    onSuccess: (data: {
      exprs: string[];
      metrics: string[];
      rawPql: string;
    }) => {
      if (data.metrics) {
        setPqlDetail(data);
      } else {
        message.error(
          $i18n.get({
            id: 'holoinsight.pages.alarm.PQlPanel.TheResolutionIsNotSuccessful',
            dm: '未解析成功，请确认输入的语句是否正确。',
          }),
        );
      }
    },
  });

  function parseQpl() {
    const pql = form.getFieldValue('pql');

    pqlParse.run({
      pql,
    });
  }
  function rangePql() {
    const pql = form.getFieldValue('pql');

    const now = Date.now();
    setPql((res) => {
      const flag = res?.refresh ?? 0;
      return {
        pql,
        startTime: now - 30 * 60 * 1000,
        endTime: now,
        refresh: flag + 1,
      };
    });
  }
  return (
    <>
      <h6 className="pql-panel-title">
        {$i18n.get({
          id: 'holoinsight.pages.alarm.PQlPanel.EnterAPqlExpression.1',
          dm: '输入PQL表达式',
        })}
      </h6>
      <Form.Item name={'pql'}>
        <PQlEditor width={'100%'} height={200} />
      </Form.Item>
      <Space>
        <Button type={'primary'} onClick={parseQpl} loading={pqlParse.loading}>
          {$i18n.get({
            id: 'holoinsight.pages.alarm.PQlPanel.PqlExpressionParsing',
            dm: 'PQL表达式解析',
          })}
        </Button>
        <Button type={'primary'} onClick={rangePql}>
          {$i18n.get({
            id: 'holoinsight.pages.alarm.PQlPanel.TrendChartPreview',
            dm: '趋势图预览',
          })}
        </Button>
      </Space>
      {pqlDetail && (
        <div className={'pql-text-split'}>
          <h5>
            {$i18n.get({
              id: 'holoinsight.pages.alarm.PQlPanel.PqlExpressionParsingResult',
              dm: 'PQL表达式解析结果',
            })}
          </h5>
          {pqlDetail?.exprs.map((item, index) => {
            return <p key={index}>{item}</p>;
          })}
        </div>
      )}

      <div style={{ marginTop: 30 }}>{pql && <PQlChart {...pql} />}</div>
    </>
  );
}
