import LogoComp from '@/components/Logo';
import RightContent from '@/components/RightContent';
import addTheme from '@/utils/addTheme';
import { initEnv, InitSystemInfo } from '@/utils/help';
import { history,RunTimeLayoutConfig } from '@umijs/max';
import { ConfigProvider } from 'antd';
import Cookies from 'js-cookie';
import qs from 'query-string';
import defaultProps from './components/defaultLayout';
import ApmLayout from './layout/apmLayout';
import { useModel } from '@umijs/max';
import './global.less';

export async function getInitialState() {
  addTheme();
  const initData = await InitSystemInfo();
  const initEnvData = initEnv(initData);
  return initEnvData;
}



export const layout: RunTimeLayoutConfig = () => {
  const {collapse, setCollapse} = useModel('global');
  let routerProps = defaultProps.routerBody;
  return {
    ...routerProps,
    menu: {
      locale: false,
    },
    token: {
      header: {
        colorBgHeader: '#0a1724',
        colorTextMenu: "rgba(255, 255, 255, 0.65)",
      },
      sider:{
        colorTextMenu: "rgba(255, 255, 255, 0.65)",
        colorTextMenuTitle: "rgba(255, 255, 255, 0.65)"
      },
      pageContainer:{
        paddingBlockPageContainerContent: 12,
        paddingInlinePageContainerContent: 20
      }
    },
    layout: 'mix',
    menuHeaderRender: false,
    headerTitleRender: () => <LogoComp />,
    rightContentRender: () => <RightContent />,
    collapsed: collapse,
    childrenRender: (children: any, props:any) => {
      
      if (
        props.location.pathname.includes('/app/') &&
        !props.location.pathname.includes('/app/home') &&
        !props.location.pathname.includes('/app/callLink')
      ) {
        setCollapse(true)
        return (
          <ConfigProvider>
            <ApmLayout children={children} />
          </ConfigProvider>
        );
      }
      setCollapse(false)
      return <ConfigProvider>{children}</ConfigProvider>;
    },
  };
};

export function onRouteChange(props: any) {
  const { location, action } = props;
  if (!action || action === 'POP' || action === 'REPLACE') {
    return;
  }

  const searhData = {
    ...qs.parse(location.search),
    tenant: Cookies.get('loginTenant'),
  };
  history.replace({
    pathname: props.location.pathname,
    search: qs.stringify(searhData),
  });
}