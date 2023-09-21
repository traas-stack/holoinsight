export default [
  {
    path: '/infra/server',
    component: './infra',
  },
  {
    path: '/infra/metric/:type/:namespace/:hostname',
    component: './infra/Metric',
  },
  {
    path: '/infra/metric/:type/:hostname',
    component: './infra/Metric',
  },
  {
    path: '/infra/agent',
    component: './infra',
  },
];
