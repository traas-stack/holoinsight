import { Spin } from 'antd';
import React from 'react';
import classnames from 'classnames';
import images from '@/pages/assets/topo-blue/icons';
import { isEqual, pick } from 'lodash';
import $i18n from '../../i18n';
import styles from './linkTpChart.less';


function uuid() {
  return `${'xxxx-xxxx-xxxx'.replace(/x/g, () =>
    Math.floor(Math.random() * 16).toString(16),
  )}`;
}

function COLLAPSE_ICON(x: any, y: any, r: any) {
  return [
    ['M', x, y],
    ['a', r, r, 0, 1, 0, r * 2, 0],
    ['a', r, r, 0, 1, 0, -r * 2, 0],
    ['M', x + 2, y],
    ['L', x + 2 * r - 2, y],
  ];
}
function EXPAND_ICON(x: any, y: any, r: any) {
  return [
    ['M', x, y],
    ['a', r, r, 0, 1, 0, r * 2, 0],
    ['a', r, r, 0, 1, 0, -r * 2, 0],
    ['M', x + 2, y],
    ['L', x + 2 * r - 2, y],
    ['M', x + r, y - r + 2],
    ['L', x + r, y + r - 2],
  ];
}

const ICON_MAP = {
  MongoDB: 'MONGODB',
  Mysql: 'MYSQL',
  Elasticsearch: 'ELASTICSEARCH',
  RocketMQ: 'RocketMQ',
  Kafka: 'Kafka',
  ActiveMQ: 'ActiveMQ',
  RabbitMQ: 'RabbitMQ',
  PostgreSQL: 'PostgreSQL',
  ORACLE: 'ORACLE',
  Memcached: 'Memcached',
  Redis: 'Redis',
  Zookeeper: 'Zookeeper',
  SqlServer: 'SqlServer',
  SQLite: 'SQLite',
  HBase: 'HBase',
  Mariadb: 'Mariadb',
  InfluxDB: 'InfluxDB',
  ClickHouse: 'ClickHouse',
};

export default class LinkGraph extends React.PureComponent {
  constructor(props: any) {
    super(props);
    this.state = {
      visible: false,
      loading: true,
      fullscreen: false,
      sortType: 'errorGrowth',
      dataApp: '',
      data: undefined,
    };
    this.graph = null;
  }
  container = React.createRef();
  onClose = () => {
    this.setState({
      visible: false,
    });
  };

  componentDidMount() {
    const fromType = this.props.fromType;
    G6.registerNode('onestep-box', {
      drawShape: function drawShape(cfg: any, group: any) {
        const r = 4;
        const {
          level,
          name,
          collapsed,
          data = {},
          nodeType,
          children,
          serviceName,
        } = cfg;
        const alarm = data.errorCount;
        const { totalCount, successRate, avgLatency } = data;
        const color = !alarm ? 'rgba(77,203,156,1)' : 'rgba(227,54,54,1)';
        const lightContentColor = !alarm ? '#edf9f5' : '#fbe8e8'
        const shape = group.addShape('rect', {
          attrs: {
            x: 0,
            y: 0,
            cursor: level !== 0 ? 'pointer' : 'defalut',
            width: level > 0 ? 216 : 200,
            height: fromType === 'api' ? 110 : 90,
          },
          name: 'box',
        });
        group.addShape('rect', {
          attrs: {
            x: 0,
            y: 0,
            width: 200,
            height: fromType === 'api' ? 110 : 90,
            stroke: color,
            radius: r,
            fill: window?.holoTheme === 'holoLight'
              ? lightContentColor : null
          },
          name: 'box',
        });
        group.addShape('rect', {
          attrs: {
            x: 0,
            y: 0,
            width: 200,
            height: fromType === 'api' ? 40 : 20,
            fill: color,
            radius: [r, r, 0, 0],
          },
          name: 'title-box',
        });
        group.addShape('image', {
          attrs: {
            x: 4,
            y: 2,
            height: 16,
            width: 16,
            style: {
              cursor: 'pointer',
            },

            img: images[ICON_MAP[nodeType]] || images['INSTANCE'],
          },
          name: 'node-icon',
        });
        const mainTitle =
          name?.length > 22 ? name.substring(0, 22) + '...' : name;
        const subTitle =
          serviceName?.length > 24
            ? serviceName.substring(0, 24) + '...'
            : serviceName;
        group.addShape('text', {
          attrs: {
            textBaseline: 'top',
            y: 4,
            x: 24,
            width: 170,
            lineHeight: 20,
            text:
              fromType === 'api'
                ? $i18n.get(
                  {
                    id: 'holoinsight.components.Chart.linkTpChart.ServiceSubtitle',
                    dm: '服务：{subTitle}',
                  },
                  { subTitle: subTitle },
                )
                : mainTitle,
            style: {
              cursor: 'pointer',
              display: 'inline-block',
            },
            fill: '#fff',
          },
          name: 'title',
        });
        if (fromType === 'api') {
          group.addShape('text', {
            attrs: {
              textBaseline: 'top',
              y: 24,
              x: 24,
              width: 170,
              lineHeight: 20,
              text:
                fromType === 'api'
                  ? $i18n.get(
                    {
                      id: 'holoinsight.components.Chart.linkTpChart.InterfaceMaintitle',
                      dm: '接口：{mainTitle}',
                    },
                    { mainTitle: mainTitle },
                  )
                  : subTitle,
              style: {
                cursor: 'pointer',
                display: 'inline-block',
              },
              fill: '#fff',
            },
            name: 'title',
          });
        }

        if (level > 0 && (!children || children.length > 0)) {
          group.addShape('marker', {
            attrs: {
              x: 204,
              y: 40,
              r: 5,
              lineAppendWidth: 10,
              cursor: 'pointer',
              symbol: collapsed ? EXPAND_ICON : COLLAPSE_ICON,
              stroke: '#666',
              lineWidth: 1,
            },
            name: 'collapse-icon',
          });
        }

        let height = fromType === 'api' ? 50 : 30;
        [
          {
            title: $i18n.get({
              id: 'holoinsight.components.Chart.linkTpChart.NumberOfRequests',
              dm: '请求数量：',
            }),
            value: `${totalCount || Number(totalCount) === 0 ? totalCount : '-'
              }次`,
          },
          {
            title: $i18n.get({
              id: 'holoinsight.components.Chart.linkTpChart.AverageResponseTime',
              dm: '平均响应时间：',
            }),
            value: `${avgLatency || Number(totalCount) === 0
              ? avgLatency.toFixed(1)
              : '-'
              }ms`,
          },
          {
            title: $i18n.get({
              id: 'holoinsight.components.Chart.linkTpChart.RequestSuccessRate',
              dm: '请求成功率：',
            }),
            value: `${successRate || Number(successRate) === 0
              ? Math.floor(successRate * 1000) / 1000
              : '-'
              }%`,
          },
        ].forEach((item, index) => {

          group.addShape('text', {
            attrs: {
              textBaseline: 'top',
              y: height,
              x: 10,
              lineHeight: 20,
              text: item.title,
              fill:
                window?.holoTheme === 'holoLight'
                  ? 'rgba(122,132,153,1)'
                  : 'rgba(255, 255, 255, 0.85)',
            },
            name: `index-title-${index}`,
          });
          group.addShape('text', {
            attrs: {
              textBaseline: 'top',
              y: height,
              x: 120,
              lineHeight: 20,
              text: item.value,
              fill:
                window?.holoTheme === 'holoLight'
                  ? '#000'
                  : 'rgba(255, 255, 255, 0.85)',
            },
            name: `index-title-${index}`,
          });
          height = height + 20;
        });
        return shape;
      },
    });
    this.initChart();
  }

  componentDidUpdate(prevProps: any) {
    const keys = ['app', 'start', 'end'];
    if (
      !isEqual(pick(prevProps, keys), pick(this.props, keys)) ||
      JSON.stringify(prevProps.treeNode) !== JSON.stringify(this.props.treeNode)
    ) {
      this.initChart();
    }
  }

  componentWillUnmount() {
    if (this.graph) {
      this.graph.destroy();
    }
  }

  initChart() {
    const _this = this;
    function backNodeOrEdgeTooltip(data: any, node: any) {
      let message = '';
      if (node) {
        if (_this.props.fromType === 'api') {
          message += $i18n.get(
            {
              id: 'holoinsight.components.Chart.linkTpChart.DivServiceNameSpanNodeservicename',
              dm: '<div>服务名称：<span>{nodeServiceName}</span></div>',
            },
            { nodeServiceName: node.serviceName },
          );
          message += $i18n.get(
            {
              id: 'holoinsight.components.Chart.linkTpChart.DivInterfaceNameSpanNodename',
              dm: '<div>接口名称：<span>{nodeName}</span></div>',
            },
            { nodeName: node.name },
          );
        } else {
          message += $i18n.get(
            {
              id: 'holoinsight.components.Chart.linkTpChart.DivServiceNameSpanNodename',
              dm: '<div>服务名称：<span>{nodeName}</span></div>',
            },
            { nodeName: node.name },
          );
        }
      }
      if (data && Object.keys(data).length) {
        const {
          totalCount,
          errorCount,
          successRate,
          avgLatency,
          p95Latency,
          p99Latency,
        } = data;
        const errorGrowth = (errorCount / totalCount).toFixed(1);
        message += `<div>平均响应时间 <span>${avgLatency ? avgLatency.toFixed(1) : '-'
          }</span></div>`;
        message += `<div>P95响应时间 <span>${p95Latency ? p95Latency.toFixed(1) : '-'
          }</span></div>`;
        message += `<div>P99响应时间 <span>${p99Latency ? p99Latency.toFixed(1) : '-'
          }</span></div>`;
        message += `<div>总次数 <span>${totalCount ? totalCount : '-'
          }</span></div>`;
        if (Number(errorGrowth) > 0.3 && errorCount > 10) {
          message += `<div>错误数 <span style="color: red">${errorCount ? errorCount : '-'
            }</span> 大于 <span style="color: red">10</span></div>`;
        } else {
          message += `<div>错误数 <span style="color: red">${errorCount ? errorCount : '-'
            }</span></div>`;
        }
        if (successRate < 99) {
          message += `<div >成功率 <span style="color: red">${successRate ? Math.floor(successRate * 1000) / 1000 : '-'
            }%</span> 小于 <span style="color: red">99%</span></div>`;
        } else {
          message += `<div>成功率 ${successRate ? Math.floor(successRate * 1000) / 1000 : '-'
            }%</div>`;
        }
        return message;
      } else {
        return (message += $i18n.get({
          id: 'holoinsight.components.Chart.linkTpChart.NoData',
          dm: '无数据',
        }));
      }
    }
    const { centerOpen, handleChangeCheck } = this.props;
    if (!this.graph) {
      const graph = new G6.TreeGraph({
        container: this.container.current, 
        height: this.container.current.clientHeight,
        width: this.container.current.clientWidth,
        animate: false, 
        modes: {
          default: [
            'drag-canvas',
            'zoom-canvas',
            {
              type: 'tooltip', 
              formatText(model: any) {
                return backNodeOrEdgeTooltip(model.data, model);
              },
              offset: 10,
            },
            {
              type: 'edge-tooltip',
              formatText(model: any) {
                const nodeTarget = graph.findById(model.target);
                return backNodeOrEdgeTooltip(
                  nodeTarget?.getModel()?.edgeMetric || {},
                  null,
                );
              },
            },
          ],
        },
        layout: {
          type: 'mindmap',
          direction: 'H', 
          getHeight: () => {
            return this.props.fromType === 'api' ? 110 : 90;
          }, 
          getWidth: () => {
            return 216; 
          },
          getVGap: () => {
            return 8;
          },
          getHGap: () => {
            return 30;
          },
          getSide: (d) => {
            if (d.data.level < 0) {
              return 'left';
            }
            return 'right';
          },
        },
        defaultNode: {
          type: 'onestep-box',
          anchorPoints: [
            [0, 0.5],
            [1, 0.5],
          ],
        },
        defaultEdge: {
          type: 'cubic-horizontal',
          size: 1,
          color: '#e2e2e2',
        },
        plugins: [
          new G6.Minimap({
            size: [160, 90],
          }),
        ],
      });

      graph.edge((node: any) => {
        const nodeType = graph.findById(node.target);
        const level = nodeType.getModel()?.level;
        const color =
          nodeType.getModel().edgeMetric.errorCount > 0 ? 'rgba(227,54,54,1)' : '#AAA';
        return {
          type: 'cubic-horizontal',
          size: 1,
          style: {
            startArrow:
              level < 0
                ? {
                  path: 'M 0,0 L 6,3 L 6,-3 Z',
                  fill: '#AAA',
                }
                : false,
            endArrow:
              level > 0
                ? {
                  path: 'M 0,0 L 6,3 L 6,-3 Z',
                  fill: '#AAA',
                }
                : false,
          },
          color: color,
        };
      });

      graph.on('node:click', (event: any) => {
        const { item } = event;
        const elName = event.target.get('name');
        const node = item.getModel();
        const { name } = node;
        handleChangeCheck && handleChangeCheck(name);
        if (elName === 'collapse-icon') {
        } else {
          if (node.level !== 0 || centerOpen) {
            this.setState({
              visible: true,
              dataApp: name,
            });
          }
        }
      });
      this.graph = graph;
    }
    this.setState({ loading: true });
    const res = this.props.treeNode;
    const data = Array.isArray(res) ? res[0] : res;
    data.id = uuid();
    this.rootId = data.id;
    (data.children || []).forEach((item: any) => {
      item.id = uuid();
    });
    this.setState({ loading: false });
    if (this.graph) {
      this.graph.data(data || []);
      this.graph.render();
      this.graph.changeSize(
        this.container.current.clientWidth,
        this.container.current.clientHeight,
      );
      this.graph.focusItem(data.id);
    }
  }

  // 缩放
  handleChange = (value: any) => {
    if (this.graph) {
      this.graph.zoomTo(value * 1);
    }
  };

  // 改变传入的层数
  sortTypeChange = (value: any) => {
    this.setState(
      {
        sortType: value,
      },
      () => {
        this.initChart(value);
      },
    );
  };

  toggleFullscreen() {
    const { fullscreen } = this.state;
    this.setState(
      {
        fullscreen: !fullscreen,
      },
      () => {
        if (this.graph) {
          setTimeout(() => {
            this.graph.changeSize(
              this.container.current.clientWidth,
              this.container.current.clientHeight,
            );
            if (this.rootId) this.graph.focusItem(this.rootId);
          }, 200);
        }
      },
    );
  }

  render() {
    const { loading, fullscreen, } = this.state;
    const { fromType } = this.props;
    return (
      <>
        <Spin spinning={loading}>
          <div
            className={classnames(styles.chartWrap, {
              [styles.fullscreen]: fullscreen,
            })}
            style={{ height: fromType === 'app' ? 600 : 300 }}
          >
            <div className={styles.container} ref={this.container} />
          </div>
        </Spin>
      </>
    );
  }
}
