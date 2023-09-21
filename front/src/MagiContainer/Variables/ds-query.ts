import { injectable } from 'inversify';
import { Select } from 'antd';
import { uniq } from 'lodash';


import {
  getLabelValues,
  getPostLabelValues,
} from '@/MagiContainer/DataSource/fetch';



export interface VariablePlugin<T = any> {
    /**
     * 配置
     */
    config: any;
    /**
     * dashboard的上下文
     */
    context: any;
    /**
     * 获取变量的值，用于初始化变量的值 一起刷新后重新获取值
     * @param current - 当前变量的值
     */
    getValue(current?: any): any;
    /**
     * 准备变量的基础诗句
     * @param force - 强制更新
     */
    resolve(force?: boolean): Promise<any>;
    /**
     *
     * @param cfg
     */
    init(cfg: any): void;
    /**
     * 变量如何渲染
     */
    view: React.ComponentType;
    /**
     * 变量渲染的props
     */
    viewProps?: any;
    /** 数值是否为数组, 主要是为了辅助query转换为变量 */
    isArray?: boolean;
}

export default class DSQueryVariable implements VariablePlugin {
  config!: any;

  context!: any;

  isArray = false;

  inputValues: string[] = [];

  init(config: any) {
    this.config = config;
    if (this.config.multiple) this.isArray = true;
  }

  viewProps?: any = {};

  getValue(current?: string | string[]) {
    if (!this.viewProps) return [];
    if (!this.config.multiple) {
      return current;
    }
    if (current) {
      return Array.isArray(current) ? current : [current];
    }
    return [];

  }

  async resolve() {
    const {
      width = 'auto',
      placeholder,
      combinedDim,
      dim,
      dsType,
      dsId,
      pluginType,
      groupbyType,
      outsideDomainId,
      originalDsId,
      multiple,
    } = this.config;
    let values: string[] = [];
    const groupbyName =
      pluginType === 'dataset' ||
      pluginType === 'multiGroupbyMultiValueSecond' ||
      pluginType === 'multiGroupbyMultiValueMinute'
        ? combinedDim
        : undefined;
    const option = {
      options: [] as { label: string; value: string }[],
      mode: multiple ? 'tags' : undefined,
      tokenSeparators: [','],
      style: { width, minWidth: 100 },
      placeholder,
      showSearch: true,
      maxTagCount: 1,
      allowClear: true,
      onDeselect: (value: string) => {
        this.inputValues = this.inputValues.filter((item) => item !== value);
      },
      onSelect: (value: string) => {
        if (values.indexOf(value) < 0) {
          this.inputValues = this.inputValues.concat(value);
        }
      },
      onClear: () => {
        this.inputValues = [];
      },

      dropdownMatchSelectWidth: false,
    };
    this.viewProps = option;
    if (pluginType === 'dataset' && groupbyType === 'post') {
      values = await getPostLabelValues({ cubeId: originalDsId, dim });
    } else {
      values = await getLabelValues(
        {
          dsId,
          outsideDomainId,
          pluginType: pluginType || dsType,
          label: dim,
        },
        groupbyName,
      );
    }
    option.options = uniq(values)
      .concat(this.inputValues)
      .map((item: string) => ({ label: item, value: item }));
    this.viewProps = option;
  }

  view = Select;
}
