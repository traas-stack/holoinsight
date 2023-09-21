
  import { injectable } from 'inversify';
  import { Radio } from 'antd';
  const Group = Radio.Group;

  type VariablePlugin = any
  @injectable()
  export default class RadioQueryVariable implements VariablePlugin {
    config!: any;
    context!: any;
    isArray?: boolean;
  
    init(config: any) {
      this.config = config;
      if (this.config.options && this.config.options.multiple)
        this.isArray = true;
    }
  
    viewProps?: any;
    /**
     * 获取变量的值
     * @param current - 原始值
     */
    private resolved = false;
    getValue(current?: string | string[]): any {
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
            frequency,
            metricTags,
          } = this.config;
          if (!force && frequency === 1 && this.resolved) return;
      
          const option: any = {
            options: metricTags,
            style: { width, minWidth: 100 },
            optionType: 'button'
          };
          this.viewProps = option;
          this.resolved = true;
    }
  
    view = Group;
  }
  