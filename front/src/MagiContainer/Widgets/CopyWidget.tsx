import { CopyOutlined } from '@ant-design/icons';
import { Tooltip } from 'antd';
import { injectable } from 'inversify';
import { cloneDeep } from 'lodash';
import * as React from 'react';
import $i18n from '../../i18n';

interface PanelWidget {
  panel: any;
  /**
   * 是不是要展示
   */
  test?(): boolean;
  name?: string;
  type: any;
  render(): React.ReactNode;
}

@injectable()
export default class CopyWidget implements PanelWidget {
  test() {
    return (
      this.panel.dashboard.meta.editable === true &&
      this.panel.mode === 'default'
    );
  }

  name = 'copy';

  type: any = 'action';

  panel!: any;

  onCopy() {
    const { panel } = this;
    const model = cloneDeep(panel.model);
    delete model.id;
    model.title = $i18n.get(
      {
        id: 'holoinsight.MagiContainer.Widgets.CopyWidget.ModeltitleCopy',
        dm: '{modelTitle} 复制',
      },
      { modelTitle: model.title },
    );
    panel.dashboard.addPanel(model);
  }

  render() {
    const { name } = this;
    return (
      <Tooltip
        title={$i18n.get({
          id: 'holoinsight.MagiContainer.Widgets.CopyWidget.Copy',
          dm: '复制',
        })}
      >
        <CopyOutlined key={name} onClick={this.onCopy.bind(this)} />
      </Tooltip>
    );
  }
}
