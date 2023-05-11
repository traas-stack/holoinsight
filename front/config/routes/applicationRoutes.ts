export default [
  {
    path: '/app/home',
    icon: 'home',
    component: './applicationMonitor',
  },
  {
    path: '/app/callLink',
    icon: 'home',
    component: './applicationMonitor/callLink',
  },
  {
    path: '/apm/detailLinkDetail',
    icon: 'home',
    component: './applicationMonitor/callLink/callLinkDetail',
  },
  {
    path: '/app/overview',
    component: './applicationMonitor/overview',
  },
  {
    path: '/app/dashboard/:template',
    component: './applicationMonitor/magiDashboard',
  },
  {
    path: '/app/interfacemetric',
    component: './applicationMonitor/APIMonitor',
  },
  {
    path: '/app/servermetric',
    component: './applicationMonitor/standAloneMonitor',
  },
  {
    path: '/app/traceinfra',
    component: '../components/App/componentMonitor',
  },
  {
    path: '/app/tracedetail',
    component: './applicationMonitor/callLink',
  },
  {
    path: '/app/rule',
    component: './applicationMonitor/rule',
  },
  {
    path: '/app/logRetrieval',
    component: './applicationMonitor/logRetrieval',
  },
];
