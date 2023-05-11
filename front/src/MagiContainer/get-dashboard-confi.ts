import { getConfig, getRelatePlugin } from '@/pages/dashboardMagi/services';
import { parseJSONStr } from '@/util';
import { Modal, notification } from 'antd';
import plugins from '@/MagiContainer/DataSource/plugins';
import $i18n from '../i18n';

export async function getDashboardConfig(pluginId: string | number) {
  const data = await getConfig(pluginId);
  if (!data) {
    Modal.info({
      title: $i18n.get({
        id: 'holoinsight.src.MagiContainer.get-dashboard-confi.TheDashboardConfigurationIsEmpty',
        dm: '大盘配置为空，请检查是否已被删除！',
      }),
    });
    return { pluginConfig: {}, config: undefined };
  }
  const { config, pluginType, bizDomainId } = data;
  if (pluginType === 'dashboard' || pluginType === 'ClientGrayBoard') {
    if (config.version === undefined) {
      notification.info({
        top: 100,
        message: $i18n.get({
          id: 'holoinsight.src.MagiContainer.get-dashboard-confi.UpgradeTipsForEarlierVersions',
          dm: '旧版升级提示',
        }),
        description: $i18n.get({
          id: 'holoinsight.src.MagiContainer.get-dashboard-confi.TheOldVersionOfThe',
          dm: '你使用的旧版大盘，需要升级，你可以联系 @璆鸣 进行配合升级',
        }),
      });
      return { pluginConfig: data, config: { panels: [] } };
    }
    if (config.panels) {
      config.panels.forEach((item: any) => {
        if (!item.transformations) {
          item.transformations = [];
        }
        if (typeof item.datasource === 'object' && item.datasource !== null) {
          item.targets = item.datasource;
          item.datasource = null;
        }
      });
    }
    if (config.variables) {
      config.variables.forEach((item: any) => {
        const { width, values } = item;
        if (item.type === 'select' && !item.options) {
          item.options = { width, values };
          delete item.width;
          delete item.values;
        }
      });
    }
    return { config: { ...config, id: pluginId }, pluginConfig: data };
  }
  if (pluginType === 'rrd') {
    const calConf = parseJSONStr(data.calConf);
    const { relatePluginId, relatePluginType, customPluginId, fileId } =
      calConf;
    const relatePlugin = await getRelatePlugin({
      fileId,
      pluginId: relatePluginId,
      pluginType: relatePluginType,
      customPluginId,
      bizDomainId,
    });
    data.relatePlugin = relatePlugin;
  }
  const P = plugins[pluginType];
  const defaultConfig = new P(data).getViewConfig();

  return { pluginConfig: data, config: { ...defaultConfig, id: pluginId } };
}
