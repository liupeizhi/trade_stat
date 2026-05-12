export default [
  {
    path: '/analysis',
    name: 'analysis',
    icon: 'lineChart',
    routes: [
      {
        path: '/analysis/dashboard',
        name: 'dashboard',
        component: './analysis/dashboard',
      },
      {
        path: '/analysis/score',
        name: 'score',
        component: './analysis/score',
      },
      {
        path: '/analysis/backtest',
        name: 'backtest',
        component: './analysis/backtest',
      },
      {
        component: './404',
      },
    ],
  },
  {
    path: '/position',
    name: 'position',
    icon: 'wallet',
    routes: [
      {
        path: '/position/current',
        name: 'current',
        component: './position/current',
      },
      {
        path: '/position/history',
        name: 'history',
        component: './position/history',
      },
      {
        path: '/position/timeline',
        name: 'timeline',
        component: './position/timeline',
      },
      {
        component: './404',
      },
    ],
  },
  {
    path: '/trade',
    name: 'trade',
    icon: 'stock',
    routes: [
      {
        path: '/trade/details',
        name: 'details',
        component: './trade/details',
      },
      {
        path: '/trade/upload',
        name: 'upload',
        component: './trade/upload',
      },
      {
        component: './404',
      },
    ],
  },
  {
    path: '/',
    redirect: '/analysis/dashboard',
  },
  {
    component: './404',
  },
];
