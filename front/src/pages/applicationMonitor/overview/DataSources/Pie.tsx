import { Pie, PieOptions } from '@antv/g2plot';
import _ from 'lodash';
import React, { memo, useEffect, useLayoutEffect, useRef } from 'react';
import $i18n from '../../../../i18n';
export interface Props {
  percent: number;
}

const PieBox: React.FC<Props> = ({ percent }) => {
  const pieRef = useRef<HTMLDivElement>(null);
  const pieIns = useRef<Pie>();
  const theme = window?.holoTheme;
  function getUpdater(): Partial<PieOptions> {
    const using = _.ceil(percent, 2);
    const unUsing = _.ceil(100 - using, 2);
    return {
      data: [
        {
          type: $i18n.get({
            id: 'holoinsight.overview.DataSources.Pie.Used',
            dm: '已使用',
          }),
          value: using,
        },
        {
          type: $i18n.get({
            id: 'holoinsight.overview.DataSources.Pie.Remaining',
            dm: '剩余',
          }),
          value: unUsing,
        },
      ],

      statistic: {
        title: false,
        content: {
          style: {
            whiteSpace: 'pre-wrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            color: theme === 'holoLight' ? '#000' : '#fff',
            fontSize: '18px',
            fontWeight: 400,
          },
          formatter: (datum, data) => {
            return ((data?.length > 0 ? data[0] : []).value || 0) + '%';
          },
          // content: `${using}%`,
        },
      },
    };
  }
  useLayoutEffect(() => {
    if (!pieRef.current) {
      return;
    }
    pieIns.current = new Pie(pieRef.current, {
      appendPadding: 10,
      data: [
        {
          type: $i18n.get({
            id: 'holoinsight.overview.DataSources.Pie.Used',
            dm: '已使用',
          }),
          value: 0,
        },
        {
          type: $i18n.get({
            id: 'holoinsight.overview.DataSources.Pie.Remaining',
            dm: '剩余',
          }),
          value: 0,
        },
      ],

      angleField: 'value',
      colorField: 'type',
      color: ['rgba(26, 106, 237, 1)', 'rgba(204, 204, 220, 0.65)'],
      autoFit: true,
      radius: 1,
      innerRadius: 0.85,
      label: false,
      // statistic: {
      //   title: false,
      //   content: {
      //     style: {
      //       whiteSpace: 'pre-wrap',
      //       overflow: 'hidden',
      //       textOverflow: 'ellipsis',
      //       color: theme === 'holoLight' ? '#000' : '#fff',
      //       fontSize: '18px',
      //       fontWeight: 400,
      //     },
      //     formatter: (datum, data) => {
      //       console.log(datum, data);
      //       return ((data?.length > 0 ? data[0] : []).value || 0) + '%';
      //     },
      //     // content: `${using}%`,
      //   },
      // },
      legend: {
        itemName: {
          style: {
            fill: theme === 'holoLight' ? '#000' : '#fff',
          },
        },
      },
      pieStyle: {
        lineWidth: 0,
      },
      interactions: [{ type: 'element-selected' }, { type: 'element-active' }],
    });
    // pieIns.current?.annotation().text({
    //   style: {
    //     fontSize: 20,
    //     fill: '#08a',
    //     textAlign: 'center',
    //   },
    // })
    pieIns.current.render();
    return () => {
      pieIns.current?.destroy();
    };
  }, []);

  useEffect(() => {
    pieIns.current?.update(getUpdater());
  }, [percent]);
  return <div style={{ width: '100%', height: 120 }} ref={pieRef} />;
};
export default memo(PieBox);
