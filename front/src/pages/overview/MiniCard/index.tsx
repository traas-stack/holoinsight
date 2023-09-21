import {
  AppstoreOutlined,
  DashboardOutlined,
  DotChartOutlined,
  EllipsisOutlined,
  FolderOpenOutlined,
  LineChartOutlined,
  PictureOutlined,
} from '@ant-design/icons';
import { Dropdown, Tooltip } from 'antd';
import classNames from 'classnames';
import { history } from 'umi';
import favItemCardStyles from './index.less';

type MiniCardTProps = {
  data: any;
  getMoreMenu: any;
};
const MiniCard = (props: MiniCardTProps) => {
  const { data, getMoreMenu } = props;
  let url = data.url || '/';

  const initIcon = (type: any) => {
    return (
      <i
        className={classNames(
          `iconfont icon-${type}`,
          favItemCardStyles.iconSize,
        )}
        onClick={() => history.push(`${url}`)}
      />
    );
  };
  const integrationIcon: any = {
    AlibabaCloud: initIcon('alibabacloud'),
    JVM: initIcon('JVM'),
    SpringBoot: initIcon('SpringBoot'),
    Mysql: initIcon('mysql'),
    Tbase: initIcon('tbase'),
    Spanner: initIcon('spanner'),
    OceanBase: initIcon('oceanbase'),
  };
  const iconNode = (icon: any) => {
    return (
      <div
        className={favItemCardStyles.icon}
        onClick={() => history.push(`${url}`)}
      >
        {icon}
      </div>
    );
  };
  const renderIcon = (type: any) => {
    switch (type) {
      case 'dashboard':
        return iconNode(<DashboardOutlined />);
      case 'folder':
        return iconNode(<FolderOpenOutlined />);
      case 'logmonitor':
        return iconNode(<LineChartOutlined />);
      case 'infra':
        return iconNode(<AppstoreOutlined />);
      case 'app':
        return iconNode(<DotChartOutlined />);
      case 'dashbord':
        return (
          <i
            className={classNames(
              `iconfont icon-app`,
              favItemCardStyles.iconSize,
            )}
            onClick={() => history.push(`${url}`)}
          />
        );
      case 'integration':
        return integrationIcon[data.relateId] || iconNode(<PictureOutlined />);
    }
  };
  return (
    <div className={favItemCardStyles.container} key={data.name}>
      <div
        className={classNames(
          favItemCardStyles.left,
        )}
      >
        {renderIcon(data.type)}
        <div className={favItemCardStyles.text}>
          <Tooltip title={data.name}>
            <div
              className={favItemCardStyles.name}
              onClick={() => history.push(`${url}`)}
            >
              {data.name}
            </div>
          </Tooltip>
          {data.type === 'infra' ? null : (
            <div className={favItemCardStyles.description}>
              {data.description}
            </div>
          )}
        </div>
      </div>

      <div>
        <Dropdown menu={getMoreMenu(data)}>
          <EllipsisOutlined />
        </Dropdown>
      </div>
    </div>
  );
};

export default MiniCard;
