import {
    createDashboard,
    DashboardComponent,
    DashboardEvents,
    PanelEvents,
  } from '../../Magi';
import {
  userFavoriteCreate,
  userFavoriteDeleteById,
  userFavoriteQueryById,
} from '@/services/tenant/favoriteApi';
import { Button, Drawer, Space } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import { memo } from 'react';


import MagiRefeshContainer from '../../MagiContainer/MagiRefeshContainer'

import VariablesEditor from '@/MagiContainer/variables';

import { getQuery, getQueryString } from '../../utils/help';
import React, { useEffect, useReducer, useState } from 'react';


import { StarOutlined } from '@ant-design/icons';
import styles from './index.less';


import { PageContainer } from '@ant-design/pro-components';
import $i18n from '../../i18n';
import './index.less';
import UserInfo from './userInfo';


const initConfig = {
  title: $i18n.get({
    id: 'holoinsight.pages.dashboardMagi.NewLargePlate',
    dm: '新大盘',
  }),
  version: '0.0.0',
  variables: [],
  groupType: 'collapse',
  panels: [],
};


const DashboardMagi: React.FC<any> = (props) => {
  const {
    getTitle,
    mode,
    dashboardConfig,
    onChangeMode,
    getChangeDashboardConfig,
    hasTimePicker,
    hasFavirate,
    allowEdit,
    start,
    end,
    updateFilter,
    hasWorkSpace,
    getNewWorkSpace,
    workspace,
    id,
    reload,
  } = props;

  const [dashboard, setDashboard] = useState<null | any>(null);
  const [_ignored, forceUpdate] = useReducer((x) => x + 1, 0);
  const [isAddPanel, setIsAddPanel] = useState(false);
  const range = decodeURIComponent(decodeURI(getQueryString('range') || ''));
  const [visible, setVisible] = useState(false);
  const [favorite, setFavorite] = useState(false);

  async function updateBoard(){
    await reload && reload();
  }
  function getVars(source: any) {
    const vars: any = {};
    const keys = Object.keys(source);
    keys.forEach((item) => {
        if(item === 'workspace'){
            vars["workspace"] = source[item];
        }
    });
    return vars;
  }
  useEffect(() => {
    if (!hasTimePicker) {
      dashboard?.context.update({ starttime: start, endtime: end });
    }
  }, [start, end]);
  useEffect(()=>{
    if(dashboard && mode === 'preview'){
      updateBoard()
    }
  },[mode])

  const initDashboard = async () => {
    const vars = getVars(getQuery());
    if (dashboardConfig && Object.keys(dashboardConfig).length) {
      return createDashboard(
        { ...dashboardConfig, groupType: dashboardConfig.groupType },
        { editable: mode !== 'preview' },
        {},
        vars,
      );
    }else{
      initConfig.title = hasFavirate.name;
      return createDashboard(
        { ...initConfig, groupType: initConfig.groupType },
        { editable: mode !== 'preview' },
        {},
        vars,
      );
    }
  };
  
  const saveDashboard = async (type: string) => {
    if (!dashboard) return;
    const config = dashboard.toJSON();
    const isEditPanel = dashboard.current?.mode === 'edit';
    if (dashboard.current) {
      dashboard.blur();
    }
    if (type !== 'save') {
      getChangeDashboardConfig(config);
    }
    if (isEditPanel && isAddPanel) {
      setIsAddPanel(false);
    }
  };
 
  const cancelEditPanel = () => {
    if (!dashboard) return;
    const { current } = dashboard;
    dashboard.blur();
    if (!current) return;
    if (isAddPanel) {
      dashboard.removePanel(current.id);
      setIsAddPanel(false);
    } else {
      current.restore();
    }
  };
  function syncVars() {
  }
  const addPanel = () => {
    if (!dashboard) return;
    const panel = dashboard.addPanel({
      title: $i18n.get({
        id: 'holoinsight.pages.dashboardMagi.commonDashboard.Unnamed',
        dm: '未命名',
      }),
      type: 'line_chart_panel',
      showTimeInfo: true,
      grid: {
        x: 0,
        y: Infinity,
        h: 6,
        w: 12,
      },
      targets: [],
    });
    panel.focus('edit');
    setIsAddPanel(true);
  };

  function addCollapse() {
    if (!dashboard) return;
    dashboard.addPanel({
      title: $i18n.get({
        id: 'holoinsight.pages.dashboardMagi.commonDashboard.Unnamed',
        dm: '未命名',
      }),
      grid: {
        x: 0,
        y: 0,
        w: 24,
        h: 1,
      },
      type: 'collapse',
      options: {
        collapse: dashboard.groupType === 'tab' ? true : false,
      },
    });
  }

  const onEditVariables = () => {
    setVisible(true);
  };
  const onChangeTab = () => {
    if (!dashboard) {
      return;
    }
    dashboard?.changeType();
  };
  const getActionBtns = () => {
    if (mode === 'preview') {
      return (
        /* 预览大盘界面 */
        <Space size={8}>
          <Button onClick={() => onChangeMode('update')}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.commonDashboard.EditDashboard',
              dm: '编辑大盘',
            })}
          </Button>
        </Space>
      );
    } else if (mode === 'update' || mode === 'create') {
      return dashboard?.current?.mode === 'edit' ? (
        /* 编辑面板界面 */
        <Space size={8}>
          <Button onClick={cancelEditPanel}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.commonDashboard.Cancel',
              dm: '取消',
            })}
          </Button>
          <Button onClick={() => saveDashboard('save')}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.commonDashboard.SavePanel',
              dm: '保存面板',
            })}
          </Button>
        </Space>
      ) : (
        /* 编辑大盘界面 */
        <Space size={8}>
          {/* <Button onClick={addCollapse}>编辑占位符</Button> */}
          <Button onClick={onChangeTab}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.commonDashboard.SwitchGroupStyle',
              dm: '切换分组样式',
            })}
          </Button>
          <Button onClick={onEditVariables}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.commonDashboard.EditPlaceholder',
              dm: '编辑占位符',
            })}
          </Button>
          <Button onClick={addPanel}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.commonDashboard.CreateAPanel',
              dm: '新建面板',
            })}
          </Button>
          <Button onClick={addCollapse}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.commonDashboard.CreateAGroup',
              dm: '新建分组',
            })}
          </Button>
          <Button onClick={() => saveDashboard('quit')}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.commonDashboard.ExitEditAndSave',
              dm: '退出编辑并保存',
            })}
          </Button>
        </Space>
      );
    }
  };

  /** 编辑某panel前，先备份 */
  const backup = (panel: any) => {
    if (panel && panel.mode === 'edit') {
      panel.backup();
    }
  };

  function onContextChange({ env, starttime, endtime, range }: any) {
    dashboard?.context.update({ env, starttime, endtime, range });
  }

  useEffect(() => {
    if (dashboard && updateFilter) {
      dashboard.panels.forEach((p:any, index: number) => {
        p.updateConfig({ targets: dashboardConfig.panels[index]?.targets });
      });
      dashboard?.refresh();
    }
  }, [dashboardConfig, dashboardConfig.title]);

  useEffect(() => {
    if(dashboard){
        setDashboard(null);
    }
    if (dashboardConfig) {
      initDashboard().then((d) => {
        if (!d) return;
        setDashboard(d);
        d.on(DashboardEvents.RENDER, () => {
          forceUpdate();
        });
        d.on(DashboardEvents.CONFIG_CHANGE, forceUpdate);
        d.on(PanelEvents.BLUR, forceUpdate);
        d.on(PanelEvents.FOCUS, forceUpdate);
        d.on(PanelEvents.FOCUS, backup);
        d.variables.on('change', (vars: any) => {
          syncVars(vars);
        });
      });
      return () => {
        if (!dashboard) return;
        dashboard.off(PanelEvents.FOCUS, backup);
        dashboard.off(PanelEvents.FOCUS, forceUpdate);
        dashboard.off(PanelEvents.BLUR, forceUpdate);
        dashboard.off(DashboardEvents.CONFIG_CHANGE, forceUpdate);
        dashboard.off(DashboardEvents.RENDER, forceUpdate);
        dashboard.variables.off('change', syncVars);
      };
    }
  }, [mode, dashboardConfig, dashboardConfig.title]);
  useEffect(() => {
    if (hasFavirate && hasFavirate.relateId) {
      userFavoriteQueryById(hasFavirate.type, hasFavirate.relateId).then(
        (res: any) => {
          if (res) {
            setFavorite(true);
          }
        },
      );
    }
  }, [hasFavirate?.relateId]);
  useEffect(() => {
    dashboard && dashboard?.variables.update(getVars(getQuery()));
  }, [JSON.stringify(getVars(getQuery())) ]);
  useEffect(()=>{
     if(workspace){
      dashboard?.refresh();
     }
  },[workspace])

  const onCloseDrawer = () => {
    setVisible(false);
  };

  function changeFavorite() {
    if (favorite) {
      userFavoriteDeleteById(hasFavirate.type, hasFavirate.relateId).then(
        () => {
          setFavorite(false);
        },
      );
    } else {
      userFavoriteCreate(hasFavirate).then(() => {
        setFavorite(true);
      });
    }
  }
  const themeFavorite: string =
    window?.holoTheme === 'holoLight' ? '#000' : '#fff';
  function renderFavirateTitle() {
    return (
      <div>
        <div className={styles.titleRender}>
          {getTitle()}
          <StarOutlined
            onClick={changeFavorite}
            style={{ color: favorite ? 'gold' : themeFavorite }}
            className={styles.icon}
          />
        </div>
        <UserInfo />
      </div>
    );
  }
  return (
    <div className="magiRefeshContainerContent">
      <PageContainer
        ghost
        header={
          !allowEdit
            ? {
                title:
                  hasFavirate && getTitle()
                    ? renderFavirateTitle()
                    : getTitle(),
              }
            : {
                title:
                  hasFavirate && getTitle()
                    ? renderFavirateTitle()
                    : getTitle(),
                extra: getActionBtns(),
              }
        }
      >
        {hasTimePicker ? (
          <MagiRefeshContainer
            context={{ range, env: 'prod' }}
            id = {id}
            getNewWorkSpace = {getNewWorkSpace}
            hasWorkSpace = {hasWorkSpace}
            workspace = {workspace}
            onChange={onContextChange}
          />
        ) : null}
        {dashboardConfig && dashboard && (
          <DashboardComponent
            key={mode}
            dashboard={dashboard}
            theme={'dark'}
          />
        )}
        <Drawer
          title={
            <>
              {$i18n.get({
                id: 'holoinsight.pages.dashboardMagi.commonDashboard.EditPlaceholder',
                dm: '编辑占位符',
              })}

              <QuestionCircleOutlined
                style={{ marginLeft: 8 }}
                links-tip-id="links@variables"
              />
            </>
          }
          open={visible}
          width={720}
          onClose={onCloseDrawer}
        >
          <VariablesEditor dashboard={dashboard} />
        </Drawer>
      </PageContainer>
    </div>
  );
};

export default memo(DashboardMagi, (prevProps, nextProps) => {
  if (
    JSON.stringify(prevProps.dashboardConfig) !==
    JSON.stringify(nextProps.dashboardConfig) || prevProps.workspace !== nextProps.workspace || prevProps.mode !== nextProps.mode
  ) {
    return false;
  } else {
    return true;
  }
});
