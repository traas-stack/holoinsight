import {
  DownsampleSelect,
  DownsampleSelectPromuthus,
  FillPolicySelect, 
  FillPolicyPromuthus,
} from '@/MagiContainer/DataSource/dataSourceSelect';
import React, { useEffect } from 'react';
import {
  EditorEvents, 
  DSMetricEditor
} from '@/Magi'
import DsSelector from './selector';

import $i18n from '../../i18n';
async function updateTargetsSource(panel: any) {
  return false;
}

const Editor = ({ panel }: any) => {
  useEffect(() => {
    updateTargetsSource(panel).then((res) => {
      if (res) {
        panel.emit(EditorEvents.UPDATE_TARGETS, panel.model.targets);
      }
    });
  }, []);

  return (
    <DSMetricEditor
      panel={panel}
      DsSelector={DsSelector}
      loadDimsValueMap={() => {
        return Promise.resolve({});
      }}
      metricMode="single"
      aggregatorTypes={[
        { label: 'none', value: 'none' },
        { label: 'avgBy', value: 'avg' },
        { label: 'sumBy', value: 'sum' },
        { label: 'countBy', value: 'count' },
        { label: 'minBy', value: 'min' },
        { label: 'maxBy', value: 'max' },
      ]}
      dsConfigMap={{
        func: () => false,
        limit: () => false,
        filter: () => true,
        customList: [
          {
            id: 'fillPolicy',
            title: $i18n.get({
              id: 'holoinsight.MagiContainer.DataSource.editor.ZeroOrNot',
              dm: '是否补零',
            }),
            editor: panel?.model?.targets?.[0]?.type === 'PROMETHEUS' ? FillPolicyPromuthus : FillPolicySelect,
            show: (t) => true,
          },
          {
            id: 'downsample',
            title:panel?.model?.targets?.[0]?.type === 'PROMETHEUS' ? '步长' : $i18n.get({
              id: 'holoinsight.MagiContainer.DataSource.editor.PrecisionReduction',
              dm: '降精度',
            }),
            editor: panel?.model?.targets?.[0]?.type === 'PROMETHEUS' ? DownsampleSelectPromuthus : DownsampleSelect,
            show: (t) => true,
          },
        ],
      }}
      getFilterTypes={() => {
        return [
          {
            label: $i18n.get({
              id: 'holoinsight.MagiContainer.DataSource.editor.Whitelist',
              dm: '白名单',
            }),
            value: 'literal_or',
          },
          {
            label: $i18n.get({
              id: 'holoinsight.MagiContainer.DataSource.editor.Blacklist',
              dm: '黑名单',
            }),
            value: 'not_literal_or',
          },
          {
            label: $i18n.get({
              id: 'holoinsight.MagiContainer.DataSource.editor.RegularExpressionMatching',
              dm: '正则表达式匹配',
            }),
            value: 'regexp',
          },
          {
            label: $i18n.get({
              id: 'holoinsight.MagiContainer.DataSource.editor.RegularExpressionExclusion',
              dm: '正则表达式排除',
            }),
            value: 'not_regexp_match',
          },
          {
            label: $i18n.get({
              id: 'holoinsight.MagiContainer.DataSource.editor.Wildcard',
              dm: '通配符',
            }),
            value: 'wildcard',
          },
        ];
      }}
    />
  );
};

export default Editor;
