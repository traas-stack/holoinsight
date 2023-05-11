import React, { useState, memo, useRef, useEffect } from 'react';
import MagiChart from '../dashboardMagi/commonDashboard';
import { queryPluginById, queryProductById, updatePlugin } from '@/services/intergration/api';

import { message } from 'antd';
import CommonBreadcrumb from '@/components/CommonBreadcrumb/index';
import $i18n from '../../i18n';
import _ from 'lodash';
import qs from 'query-string';

const Dashboard: React.FC = (props: any) => {
  const id = props.match.params.type;
  const { product, installed } = qs.parse(location.search);
  const [item, setItem] = useState({});
  const [template, setTemplate] = useState<any>({});
  const urlList = [
    {
      name: $i18n.get({
        id: 'holoinsight.pages.integration.productDashboard.IntegratedComponents',
        dm: '集成组件',
      }),
      url: '/integration/agentComp',
    },

    {
      name: product,
      url: `/integration/dashboard/${name}?product=${product}`,
    },
  ];

  useEffect(() => {
    getTemplate();
  }, []);

  function reload() {
    getTemplate();
  }

  async function getTemplate() {
    if (installed == 'true') {
      const plugins: any = await queryPluginById(id);
      if (plugins && plugins.template) {
        setItem(plugins);
        let template = plugins.template;
        setTemplate(template);
      }
    }else {
      const res: any = await queryProductById(id);
      if (res && res.template) {
        setItem(res);
        let template = res.template;
        setTemplate(template);
      }
    }
  }

  return (
    <div>
      <CommonBreadcrumb urlList={urlList} to="chart" />
      <DashboardRender reload={reload} item={item} id={id} installed={installed} product={product} template={template} />
    </div>
  );
};

export default Dashboard;
const DashboardRender = (props: any) => {
  const { item, product, template, reload, installed, id } = props;
  const [mode, setMode] = useState('preview');
  const onChangeMode = (newMode: string) => {
    setMode(newMode);
  }
  const getChangeDashboardConfig = (newDashboardConfig: any) => {
    // 做更新操作
    item.template = newDashboardConfig;
    updatePlugin(item).then((r) => {
      if (r) {
        message.success('保存成功！')
        setMode('preview')
      }
    });
  }

  return (
    <div>
      {
        template ? <MagiChart
          hasTimePicker={true}  //是否有时间选择器
          allowEdit={installed == 'true' ? true : false}  //是否允许编辑
          mode={mode}   //大盘模式
          onChangeMode={onChangeMode}  //切换模式
          dashboardConfig={template}  //大盘配置
          getTitle={() => <div>{product}</div>} //大盘标题
          getChangeDashboardConfig={getChangeDashboardConfig} //拿到大盘的数据
          hasFavirate={{
            url: `/integration/dashboard/${id}?product=${product}&installed=${installed}`,   //收藏的url
            type: "integration",  //收藏的类型
            relateId: product, //id
            name: product
          }}
          reload={reload}
        /> : null
      }

    </div>
  )


}