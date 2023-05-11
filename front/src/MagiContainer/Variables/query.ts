import { injectable } from 'inversify';
import {  Select } from 'antd';

type VariablePlugin = any
@injectable()
export default class QueryVariable implements VariablePlugin {
  config!: any;
  context!: any;

  isArray = false;

  init(config: any) {
    this.config = config;
    if (this.config.options && this.config.options.multiple)
      this.isArray = true;
  }

  viewProps?: any;

  private resolved = false;

  /**
   * 获取默认值
   */
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

  getAllValues() {
    if (!this.viewProps) return [];
    const { options } = this.viewProps;
    return options.map((item: any) => item.value);
  }

  async resolve(force = false) {
    const {
      width = 'auto',
      placeholder,
      frequency,
      metricTags,
      multiple,
    } = this.config;
    if (!force && frequency === 1 && this.resolved) return;
    const option: any = {
      options: metricTags,
      style: { width, minWidth: 100 },
      showSearch: true,
      maxTagCount: 1,
      allowClear: true,
      mode: multiple ? 'multiple' : undefined,
      placeholder,
      dropdownMatchSelectWidth: false,
    };
    this.viewProps = option;
    this.resolved = true;
  }

  view = Select;
}
