import { Drawer, Tag } from 'antd';
import React from 'react';

const LoggerTagsDrawer: React.FC<{
  visible: boolean;
  onClose: () => void;
  deployingData?: ZSEAGULL_API.DeployDTO;
  tags: Record<string, string>;
}> = (props) => {
  const { visible, onClose, tags = {} } = props;
  const $tags = Object.keys(tags).map((tagKey) => {
    const key = tagKey
      .replace('__tag__:', '')
      .replaceAll('__', '')
      .toLowerCase();
    const value = tags[tagKey];
    return (
      <div key={key} style={{ marginBottom: '16px' }}>
        <Tag>
          {key}: {value}
        </Tag>
      </div>
    );
  });

  return (
    <Drawer
      width={500}
      title="标签"
      placement="right"
      onClose={onClose}
      open={visible}
    >
      {$tags}
    </Drawer>
  );
};

export default LoggerTagsDrawer;
