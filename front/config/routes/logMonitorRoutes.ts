export default [
  {
    path: '/log',
    name: 'log',
    icon: 'cluster',
    component: './logMonitor',
  },
  {
    path: '/log/:id',
    name: 'log',
    icon: 'cluster',
    component: './logMonitor',
  },
  {
    path: '/log/:type/:action/:parentId',
    component: './logMonitor/LogMonitorEdit',
  },
  {
    path: '/log/metric/:id',
    component: './logMonitor/Metric',
  },
  {
    path: '/log/:type/:action/:parentId/:id',
    component: './logMonitor/LogMonitorEdit',
  },
];
