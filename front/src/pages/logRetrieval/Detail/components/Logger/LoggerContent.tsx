import { Typography } from 'antd';
import React from 'react';
import { useBoolean } from 'ahooks';

type TLoggerContent = {
  content: string;
};

const LoggerContent: React.FC<TLoggerContent> = (props) => {
  const { content } = props;

  const [expanded, { toggle }] = useBoolean(false);

  const handleClickText = (e: any) => {
    e.stopPropagation();
    toggle();
  };

  return (
    <div
      style={{ width: '100%', wordBreak: 'break-all', whiteSpace: 'pre-wrap' }}
    >
      <Typography.Paragraph
        code
        key={`${expanded}`}
        ellipsis={{
          rows: expanded ? 1000 : 5,
          expandable: true,
          symbol: '展开',
          onExpand: handleClickText,
        }}
        style={{ marginBottom: 0 }}
      >
        {content}
        {expanded && <a onClick={handleClickText}>收起</a>}
      </Typography.Paragraph>
    </div>
  );
};

export default LoggerContent;
