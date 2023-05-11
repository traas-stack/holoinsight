import {
  createDashboard,
  DASHBOARD_VERISON,
  DashboardComponent,
  DashboardEvents,
  PanelEvents,
} from '@/Magi';

import {
  addDashboard,
  queryDashboardById,
  updateDashboard,
  
} from '@/services/dashbard';

import { history, Link, useParams } from '@umijs/max';
import { PageContainer } from '@ant-design/pro-components';
import { Button, Drawer, message, Space, Form, Input, Modal } from 'antd';
import VariablesEditor from '@/MagiContainer/variables';
import { QuestionCircleOutlined } from '@ant-design/icons';
import Editable from '@/MagiContainer/components/Editable';
import MagiRefeshContainer from '@/MagiContainer/MagiRefeshContainer';
import React, { useEffect, useReducer, useState } from 'react';
import qs from 'query-string';
import './index.less';
import $i18n from '../../i18n'

function getQuery() {
  return qs.parse(window.location.search);
}
function getQueryString(name) {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
  var r = window.location.search.substr(1).match(reg);
  if (r != null) return decodeURI(r[2]);
  return null;
}
const initConfig: any = {
  title: $i18n.get({
    id: 'holoinsight.pages.dashboardMagi.NewLargePlate',
    dm: '新大盘',
  }),
  version: DASHBOARD_VERISON,
  variables: [],
  groupType: 'collapse',
  panels: [],
};

const DashboardMagi: React.FC<any> = (props: any) => {
  const allParams = useParams() || {};
  const [popForm] = Form.useForm();
  const { mode = 'create', id } = useParams() || {};
  const [dashboard, setDashboard] = useState<null | any>(null);
  const [_ignored, forceUpdate] = useReducer((x) => x + 1, 0);
  const [reLoad, setReLoad] = useState('');
  const [isAddPanel, setIsAddPanel] = useState(false);
  const range = decodeURIComponent(decodeURI(getQueryString('range') || ''));
  const [visible, setVisible] = useState(false);
  const paramsId = allParams.id;

  const getDashBoardConfig = async (id: string) => {
    const res: any = await queryDashboardById(id);
    return res?.conf;
  };
  const bindEvent = (dashboard: any) => {
    dashboard.on(DashboardEvents.CONFIG_CHANGE, () => {
      setReLoad(dashboard.model.title);
    });
  };


  const initDashboard = async () => {
    const vars = getVars(getQuery());
    
    const config =
      mode === 'create' ? initConfig : await getDashBoardConfig(id!);
    if (!config) {
      message.error(
        $i18n.get(
          {
            id: 'holoinsight.pages.dashboardMagi.NoDashboardWithIdId',
            dm: '查询不到ID为{id}的大盘',
          },
          { id: id },
        ),
      );
      return;
    }
    return createDashboard(
      { ...config, groupType: config.groupType },
      { editable: mode !== 'preview' },
      {},
      vars,
    );
  };
  const domContent = (
    <Form form={popForm}>
      <Form.Item
        name="title"
        rules={[
          {
            required: true,
            message: '新建大盘时标题为必填！',
          },
        ]}
      >
        <Input placeholder="请输入新建大盘标题" />
      </Form.Item>
    </Form>
  );
  const saveDashboard = async (type: string) => {
    if (!dashboard) return;
    const config = dashboard.toJSON();
    const isEditPanel = dashboard.current?.mode === 'edit';
    if (dashboard.current) {
      dashboard.blur();
    }
    let backUrl = '';
    if (mode === 'create') {
      if (config.title === '新大盘') {
        Modal.confirm({
          title: '大盘标题',
          content: domContent,
          onOk: async () => {
            popForm.validateFields().then(async (d) => {
              config.title = d.title;
              const res = await addDashboard(config);
              backUrl =
                type === 'save'
                  ? '/m/dashboard/update/' + res.id
                  : '/dashboard/';
              history.push(backUrl);
            });
          },
        });
      } else {
        const res = await addDashboard(config);
        backUrl =
          type === 'save' ? '/m/dashboard/update/' + res.id : '/dashboard/';
        history.push(backUrl);
      }
    } else {
      await updateDashboard({ ...config, id });
      backUrl = type === 'save' ? '/m/dashboard/preview/' + id : '/dashboard/';
      if (!isEditPanel) {
        history.push(backUrl);
      }
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
  const addPanel = () => {
    if (!dashboard) return;
    const panel = dashboard.addPanel({
      title: $i18n.get({
        id: 'holoinsight.pages.dashboardMagi.Unnamed',
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
        id: 'holoinsight.pages.dashboardMagi.Unnamed',
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
  function copyText(res) {
    navigator.clipboard
      .writeText(res)
      .then(() => {
        message.success('导出成功，JSON配置已复制到剪切板');
      })
      .catch((err) => {
        message.error('导出失败');
      });
  }
  const copyJson = async () => {
    const { conf } = await queryDashboardById(paramsId);
    copyText(JSON.stringify(conf));
  };
  const getActionBtns = () => {
    if (mode === 'preview') {
      return (
        /* 预览大盘界面 */
        <>
          <Link to={'/dashboard'}>
            <Button>
              {$i18n.get({
                id: 'holoinsight.pages.dashboardMagi.ReturnList',
                dm: '返回',
              })}
            </Button>
          </Link>
          <Space size={8}>
            <Link to={`/m/dashboard/update/${id}`}>
              <Button>
                {$i18n.get({
                  id: 'holoinsight.pages.dashboardMagi.EditDashboard',
                  dm: '编辑大盘',
                })}
              </Button>
            </Link>
          </Space>
          <Button onClick={copyJson} type="primary">
            导出配置
          </Button>
        </>
      );
    } else if (mode === 'update' || mode === 'create') {
      return dashboard?.current?.mode === 'edit' ? (
        /* 编辑面板界面 */
        <Space size={8}>
          <Button onClick={cancelEditPanel}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.Cancel',
              dm: '取消',
            })}
          </Button>
          <Button onClick={() => saveDashboard('save')}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.SavePanel',
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
              id: 'holoinsight.pages.dashboardMagi.SwitchGroupStyle',
              dm: '切换分组样式',
            })}
          </Button>
          <Button onClick={onEditVariables}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.EditPlaceholder',
              dm: '编辑占位符',
            })}
          </Button>
          <Button onClick={addPanel}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.CreateAPanel',
              dm: '新建面板',
            })}
          </Button>
          <Button onClick={addCollapse}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.CreateAGroup',
              dm: '新建分组',
            })}
          </Button>
          <Button onClick={() => saveDashboard('save')}>
            {$i18n.get({
              id: 'holoinsight.pages.dashboardMagi.ExitEditAndSave',
              dm: '退出编辑并保存',
            })}
          </Button>
        </Space>
      );
    }
  };

    const getTitle = () => {
      if (!dashboard) return null;

      if (mode === 'update') {
        if (isAddPanel === true)
          return $i18n.get({
            id: 'holoinsight.pages.dashboardMagi.CreateAPanel',
            dm: '新建面板',
          });
        if (dashboard?.current)
          return $i18n.get({
            id: 'holoinsight.pages.dashboardMagi.EditPanel',
            dm: '编辑面板',
          });
      }
      return (
        <>
          <Editable
            value={dashboard.model.title}
            onChange={(e: string | undefined) => {
              dashboard.updateConfig({ title: e });
              bindEvent(dashboard);
            }}
          />
        </>
      );
    };

  const backup = (panel: any) => {
    if (panel && panel.mode === 'edit') {
      panel.backup();
    }
  };

  function onContextChange({ env, starttime, endtime, range }: any) {
    dashboard?.context.update({ env, starttime, endtime, range });
  }
  function getVars(source: any) {
    const vars: any = {};
    const keys = Object.keys(source);
    keys.forEach((item) => {
      if (item.startsWith('var-')) {
        vars[item.slice(4)] = source[item];
      }
    });
    return vars;
  }
  useEffect(() => {
    if (dashboard) {
      setDashboard(null);
    }
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
        syncVars(vars, d);
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
  }, [mode]);
  useEffect(() => {
    dashboard && dashboard?.variables.update(getVars(allParams));
  }, [JSON.stringify(getVars(allParams))]);

  /**
   *
   * @param oldArr 需要去重的数组
   * @param attr 根据此值去重
   * @returns
   */
  function distinctList(oldArr: any, attr: any) {
    let newArr: any = [];
    oldArr.forEach((t: any) => {
      if (
        !newArr.some((e: any) => {
          return e[attr] === t[attr];
        })
      ) {
        newArr.push(t);
      }
    });
    return newArr;
  }

  function syncVars(vars: any, d: any) {
    const query = allParams;
    const newQuery = { ...query };
    for (const [key, value] of Object.entries(vars)) {
      newQuery[`var-${key}`] = value;
    }
    history.push({ search: `${qs.stringify(newQuery)}` });

    const regex = /(?<=\{)(.+?)(?=\})/g;
    const reloadPanel: any = [];

    Array.isArray(d.panels)
      ? d.panels.map((panel: any) => {
          Array.isArray(panel.model.targets)
            ? panel.model.targets.map((target: any) => {
                Array.isArray(target.filters)
                  ? target.filters.map((filter: any) => {
                      if (filter.values?.length) {
                        if (
                          Object.keys(vars).includes(
                            filter.values[0].match(regex)[0],
                          )
                        ) {
                          reloadPanel.push(panel.model);
                          return;
                        }
                      }
                    })
                  : null;
              })
            : null;
        })
      : null;
    const resList = distinctList(reloadPanel, 'id');
    resList.forEach((panel: any) => [d.getPanel(panel.id).query()]);
  }
  const onCloseDrawer = () => {
    setVisible(false);
  };
  return (
    <div className="magiRefeshContainerContent">
        <PageContainer
        ghost
        header={{
          title: getTitle(),
          extra: getActionBtns(),
        }}
      >
        <MagiRefeshContainer
          context={{ range, env: 'prod' }}
          onChange={onContextChange}
        />
      {dashboard && (
        <DashboardComponent
          mode={'update'}
          dashboard={dashboard}
          theme={'dark'}
        />
      )}
      <Drawer
          title={
            <>
              {$i18n.get({
                id: 'holoinsight.pages.dashboardMagi.EditPlaceholder',
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

export default DashboardMagi;
