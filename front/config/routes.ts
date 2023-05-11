import alarmRoutes from './routes/alarmRoutes';
import userRoutes from './routes/userRoutes';
import infraRoutes from './routes/infraRoutes';
import dashboardRoutes from './routes/dashboardRoutes';
import interationRoutes from './routes/interationRoutes';
import applicationRoutes from './routes/applicationRoutes';
import logMonitorRoutes from './routes/logMonitorRoutes';

export default [
  {
    path: '/console/overview',
    name: 'overview',
    icon: 'home',
    component: '@/pages/overview',
  },
  {
    name: '总览',
    path: '/',
    redirect: '/console/overview',
  },
  { path: '/', redirect: '/console/overview' },
  ...userRoutes,
  ...alarmRoutes,
  ...infraRoutes,
  ...dashboardRoutes,
  ...interationRoutes,
  ...applicationRoutes,
  ...logMonitorRoutes,
];
