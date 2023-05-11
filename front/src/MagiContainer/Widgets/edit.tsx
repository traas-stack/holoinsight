import React from 'react';
import { injectable } from 'inversify'
import { EditOutlined } from '@ant-design/icons';
import { Tooltip } from 'antd';
import $i18n from '../../i18n';
interface PanelWidget {
  panel: any;
  test?(): boolean;
  name?: string;
  type: any;
  render(): React.ReactNode;
}

@injectable()
export default class EditWidget implements PanelWidget {
  test() {
    return (
      this.panel.dashboard.meta.editable === true &&
      this.panel.mode === 'default'
    );
  }

  name = 'edit';

  type: any = 'action';

  panel!: any;

  render() {
    const { name } = this;
    return (
      <Tooltip
        title={$i18n.get({
          id: 'holoinsight.MagiContainer.Widgets.edit.Edit',
          dm: '编辑',
        })}
      >
        <EditOutlined
          key={name}
          onClick={() => {
            this.panel.focus('edit');
          }}
        />
      </Tooltip>
    );
  }
}
