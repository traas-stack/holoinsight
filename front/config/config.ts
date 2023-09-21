import { defineConfig } from '@umijs/max';
import routes from './routes';
export default defineConfig({
  antd: {
    dark: true,
  },
  access: {},
  model: {},
  title: 'HoloInsight',
  initialState: {},
  favicons: ['../src/assets/holoinsight.png'],
  request: {},
  layout: {
    menuHeaderRender: true,
    fixHeader: true,
    siderWidth: 256,
    splitMenus: true,
  },
  externals: {
    react: 'React',
    'react-dom': 'ReactDOM',
    
  },
  headScripts: [
    '/src/assets/react.production.min.js',
    '/src/assets/react-dom.production.min.js',
    '/src/assets/react-router.min.js',
    '/src/assets/holoinsight-magi.min.js',
  ],
  styles: [
   
  ],
  proxy: {
    '/webapi/': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
  esbuildMinifyIIFE: true,
  reactRouter5Compat: {},
  routes,
  npmClient: 'tnpm',
});
