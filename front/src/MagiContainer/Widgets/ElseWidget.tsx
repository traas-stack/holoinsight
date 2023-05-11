import { DeleteOutlined, EllipsisOutlined } from '@ant-design/icons';
import { Dropdown,} from 'antd';
import { injectable } from 'inversify';
import * as React from 'react';
import $i18n from '../../i18n';

interface PanelWidget {
  panel: any;
  test?(): boolean;
  name?: string;
  type:  any;
  render(): React.ReactNode;
}
@injectable()
export default class ElseWidget implements PanelWidget {
  test() {
    return (
      this.panel.dashboard.meta.editable === true &&
      this.panel.mode === 'default'
    );
  }

  name = 'else';
  type: any = 'action';
  panel!: any;

  render() {
    const { name } = this;
    const items = [{
      label: (<span onClick={() => {
        this.panel.remove();
      }}
        style={{ width: 100 }}>
        <DeleteOutlined />
        {$i18n.get({
          id: 'holoinsight.MagiContainer.Widgets.ElseWidget.Delete',
          dm: '删除',
        })}
      </span>),
      key: 'menu',
    },]

    return (
      <>
          <Dropdown key={name} menu={{ items }}>
            <EllipsisOutlined />
          </Dropdown>
      </>
    );
  }
}
