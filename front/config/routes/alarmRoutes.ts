export default [
  {
    path: '/alarm/rule',
    component: './alarm/alarmSubscription',
  },
  {
    path: '/alarms/new',
    component: './alarm/alarmRuler/Detail',
  },
  { path: '/alarm/details/:id', component: './alarm/alarmDetails' },
  {
    path: '/alarms/new/:sourceType/:sourceId',
    component: './alarm/alarmRuler/Detail',
  },
  {
    path: '/alarm/view/:id',
    component: './alarm/alarmRuler/Detail',
  },
  {
    path: '/alarm/group',
    component: './alarm/alarmGroup',
  },
  {
    path: '/alarm/alarmRobot',
    component: './alarm/alarmRobot',
  },
  {
    path: '/alarm/history',
    component: './alarm/alarmHistory',
  },
  {
    path: '/alarm/alarmCallback',
    component: './alarm/alarmCallback',
  },
];
