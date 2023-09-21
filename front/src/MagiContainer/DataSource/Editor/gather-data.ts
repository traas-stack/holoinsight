import plugins from '../plugins';

export function getDS(source: any, type?: string) {
  if (!source) return undefined;

  if (type === 'PQL') {
    const { id, name } = source;
    return { id, name };
  }
  if (type === 'INDICATORS_AGGREGATE') {
    const PluginDs = plugins.marketdata;
    return new PluginDs(source).format();
  }
  if (type === 'STACK' || type === 'SOFA_STACK') {
    const PluginDs = plugins.stack;
    return new PluginDs(source).format();
  }
  if (type === 'BIZ') {
    const PluginDs = plugins.biz;
    return new PluginDs(source).format();
  }
  if (type === 'LEGO') {
    const PluginDs = plugins.lego;
    return new PluginDs(source).format();
  }
  const { pluginType } = source;
  const PluginDs = plugins[pluginType];
  return new PluginDs(source).format();
}
