export default [
      {
        path: "/dashboard",
        component: './dashboard',
      },
      {
        path: '/m/dashboard/create',
        component: './dashboardMagi',
      },
      {
        path: '/m/dashboard/:mode/:id',
        component: './dashboardMagi',
      },
  ];
  