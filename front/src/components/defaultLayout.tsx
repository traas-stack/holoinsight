import {
  AlertOutlined,
  ApartmentOutlined,
  ClusterOutlined,
  DashboardOutlined,
  HistoryOutlined,
  HomeOutlined,
  TableOutlined,
  ThunderboltOutlined,
  UserOutlined,
} from '@ant-design/icons';
import $i18n from '../i18n';

const routerBody: any = {
  route: {
    path: '/',
    routes: [
      {
        path: '/console/overview',
        name: $i18n.get({
          id: 'holoinsight.src.components.defaultLayout.Overview',
          dm: '总览',
        }),
        icon: <HomeOutlined />,
      },

      {
        path: '/app',
        name: $i18n.get({
          id: 'holoinsight.src.components.defaultLayout.ApplicationMonitoring',
          dm: '应用监控',
        }),
        icon: <ApartmentOutlined />,
        routes: [
          {
            path: '/app/home',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.ApplicationMonitoring',
              dm: '应用列表',
            }),
          },
          {
            path: '/app/callLink',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.CallLink',
              dm: '调用链路',
            }),
          },
        ],
      },

      {
        path: '/log',
        name: $i18n.get({
          id: 'holoinsight.components.HeaderSearch.LogMonitoring',
          dm: '日志监控',
        }),
        icon: <ClusterOutlined />,
      },

      {
        path: '/infra',
        name: $i18n.get({
          id: 'holoinsight.pages.integration.agent.Infrastructure',
          dm: '基础设施',
        }),
        icon: <TableOutlined />,
        routes: [
          {
            path: '/infra/server',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.MachineList',
              dm: '机器列表',
            }),
          },
        ],
      },

      {
        path: '/integration',
        name: $i18n.get({
          id: 'holoinsight.src.components.defaultLayout.Integration',
          dm: '集成',
        }),
        icon: <ThunderboltOutlined />,
        routes: [
          {
            path: '/integration/agent',
            name: 'Agent',
          },
          {
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.IntegratedComponentLibrary',
              dm: '集成组件',
            }),
            path: '/integration/agentComp',
          },
        ],
      },
      {
        name: $i18n.get({
          id: 'holoinsight.src.components.defaultLayout.Dashboard',
          dm: '仪表盘',
        }),
        icon: <DashboardOutlined />,
        path: '/dashboard',
      },

      {
        path: '/alarm',
        name: $i18n.get({
          id: 'holoinsight.src.components.defaultLayout.AlarmManagement',
          dm: '报警管理',
        }),
        icon: <AlertOutlined />,
        routes: [
          {
            path: '/alarm/rule',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.RuleManagement',
              dm: '规则管理',
            }),
            icon: <HistoryOutlined />,
          },
          {
            path: '/alarm/group',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.NotificationGroupManagement',
              dm: '通知组管理',
            }),
            icon: <HistoryOutlined />,
          },
          {
            path: '/alarm/alarmRobot',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.DingtalkRobot',
              dm: '钉钉机器人',
            }),
            icon: <HistoryOutlined />,
          },
          {
            path: '/alarm/history',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.AlarmHistory',
              dm: '告警历史',
            }),
            icon: <HistoryOutlined />,
          },
          {
            path: '/alarm/alarmCallback',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.AlarmCallbackTemplate',
              dm: '报警回调模版',
            }),
            icon: <HistoryOutlined />,
          },
        ],
      },
      {
        path: '/user',
        name: $i18n.get({
          id: 'holoinsight.src.components.defaultLayout.UserCenter',
          dm: '个人中心',
        }),
        icon: <UserOutlined />,
        routes: [
          {
            path: '/user/alarmsub',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.UserAlarmSub',
              dm: '告警订阅',
            }),
          },
          {
            path: '/user/alarmhistory',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.UserAlarmHistory',
              dm: '告警历史',
            }),
          },
          {
            path: '/user/opsLog',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.UserOpsLog',
              dm: '操作历史',
            }),
          },
          {
            path: '/user/favorite',
            name: $i18n.get({
              id: 'holoinsight.src.components.defaultLayout.UserFavorite',
              dm: '用户收藏',
            }),
          },
        ],
      },
    ],
  },

  location: {
    pathname: '/',
  },
};

export default {
  routerBody,
};
