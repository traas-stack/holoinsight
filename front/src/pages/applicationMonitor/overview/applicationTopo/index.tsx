import images from '@/pages/assets/topo-blue/icons';
import { getTopoDataDetail } from '@/services/application/api';
import React, { useEffect, useState } from 'react';
import moment from 'moment';
import G6 from '@antv/g6';
import ToolTips from './ToolTips';

const imgList = {
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
const getFormatData = (data: any) => {
  const { nodes = [], calls = [] } = data || {};
  const newNodes = nodes
    // .filter((item: any) => item.type !== 'USER')
    .map((item: any) => {
      return {
        ...item,
        label:
          item.name.length > 26
            ? item.name.substring(0, 26) + '...'
            : item.name,
        img: images[imgList[item.type]] || images['INSTANCE'],
        type: 'image',
      };
    });
  const newCalls = calls.map((item: any) => ({
    ...item,
    label: `${item.metric ? item.metric.totalCount : 0}次`,
    labelCfg: {
      style: {
        fill: window.holoTheme === 'holoLight' ? '#000' : '#fff',
      },
      refY: item.destId === item.sourceId ? -30 : 20, // refY 默认是顺时针方向向下，所以需要设置负值
    },
    source: item.sourceId,
    target: item.destId,
  }));

  return {
    nodes: newNodes.concat(
      newNodes.map((item, idx) => {
        return {
          ...item,
          label: idx,
        };
      }),
    ),
    edges: newCalls,
  };
};

const TopoGraph: React.FC<any> = () => {
  // 边 tooltip 坐标
  const [hoverNode, setHoverNode] = useState(false);
  const [nodeTooltipX, setNodeToolTipX] = useState(0);
  const [nodeTooltipY, setNodeToolTipY] = useState(0);
  const [curNode, setCurNode] = useState({});
  const [showNodeTooltip, setShowNodeTooltip] = useState(false);
  const [edgeTooltipX, setEdgeToolTipX] = useState(0);
  const [edgeTooltipY, setEdgeToolTipY] = useState(0);
  const [curEdge, setCurEdge] = useState({});
  const [showEdgeTooltip, setShowEdgeTooltip] = useState(false);
  const [hoverEdge, setHoverEdge] = useState(false);
  const fetchData = async () => {
    // 查询最近15min的数据
    const nowBeginTime = moment(new Date()).format('x');
    const data = await getTopoDataDetail({
      start: nowBeginTime - 15 * 60 * 1000,
      end: nowBeginTime,
      category: 'tenant',
      depth: 8,
      // termParams: {
      //   app_id: appId,
      //   env_id: envId,
      // },
    });
    return data;
  };

  const initGraph = async () => {
    const resData = await fetchData();
    const formatData = getFormatData(resData);
    const container = document.getElementById('TopoGraph');
    let width = 800;
    let height = 800;
    if (container) {
      // width = parseInt(getComputedStyle(container)['width']);
      // height = parseInt(getComputedStyle(container)['height']);
      width = container.scrollWidth;
      height = container.scrollHeight || 500;
    }

    // 创建 G6 图实例
    const graph = new G6.Graph({
      container: 'TopoGraph', // 指定图画布的容器 id
      // 画布宽高
      width,
      height,
      // plugins: [tooltip],
      layout: {
        type: 'dagre',
        preventOverlap: true,
        rankdir: 'LR',
        nodesep: 25,
        ranksep: 90,
        fill: window.holoTheme === 'holoLight' ? '#000' : '#fff',
        labelCfg: {
          fill: window.holoTheme === 'holoLight' ? '#000' : '#fff',
        },
      },
      modes: {
        default: ['drag-node', 'zoom-canvas'],
      },
      defaultNode: {
        size: 30,
        type: 'image',
        labelCfg: {
          style: {
            fill: window.holoTheme === 'holoLight' ? '#000' : '#fff',
          },
        },
      },
      defaultEdge: {
        style: {
          endArrow: true,
        },
        fill: window.holoTheme === 'holoLight' ? '#000' : '#fff',
        labelCfg: {
          fill: window.holoTheme === 'holoLight' ? '#000' : '#fff',
        },
      },
    });

    // 读取数据
    graph.data(formatData);
    // 关闭局部渲染
    graph.get('canvas').set('localRefresh', false);
    // 渲染图
    // 监听 node 上面 mouse 事件
    graph.on('node:mouseenter', (evt) => {
      const { item } = evt;
      const model = item.getModel();
      const { x, y } = model;
      const point = graph.getCanvasByPoint(x, y);
      setCurNode(model);
      setNodeToolTipX(point.x - 75);
      setNodeToolTipY(point.y + 15);
      setHoverNode(true);
    });
    graph.on('node:mouseleave', function (event) {
      setTimeout(() => {
        setHoverNode(false);
      });
    });
    graph.on('edge:mouseenter', function (event) {
      const { item } = event;
      const model = item.getModel();
      const { x, y } = event;
      const point = graph.getCanvasByPoint(x, y);
      setEdgeToolTipX(point.x - 75);
      setEdgeToolTipY(point.y + 15);
      setCurEdge(model);
      setHoverEdge(true);
      graph.setItemState(event.item, 'active', true);
    });
    graph.on('edge:mouseleave', function (event) {
      setTimeout(() => {
        setHoverEdge(false);
      }, 100);
      graph.setItemState(event.item, 'active', false);
    });
    graph.render();
  };

  useEffect(() => {
    initGraph();
  }, []);
  return (
    <div>
      <div id="TopoGraph" style={{ position: 'relative', height: '900px' }}>
        <ToolTips
          data={curNode}
          x={nodeTooltipX}
          y={nodeTooltipY}
          show={hoverNode}
          allShow={showNodeTooltip}
          setAllShow={setShowNodeTooltip}
        ></ToolTips>
        <ToolTips
          data={curEdge}
          x={edgeTooltipX}
          y={edgeTooltipY}
          show={hoverEdge}
          allShow={showEdgeTooltip}
          setAllShow={setShowEdgeTooltip}
        ></ToolTips>
      </div>
    </div>
  );
};

export default TopoGraph;
