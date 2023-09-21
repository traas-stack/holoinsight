import React, { useState, memo } from 'react';
import { Button } from 'antd';
import { DoubleLeftOutlined } from '@ant-design/icons';
import clsx from 'clsx';
import style from './index.less';
export interface Props {
  sider: JSX.Element;
  content: JSX.Element|null;
  className?: string;
  expand?: boolean;
  onExpand?: (expand: boolean) => boolean;
}
const ExpandCard: React.FC<Props> = ({
  sider,
  content,
  className,
  onExpand,
  expand,
}) => {
  const [expandStatus, setExpandStatus] = useState(true);
  const currentExpand = typeof expand === 'boolean' ? expand : expandStatus;

  function handleExpand() {
    if (onExpand) {
      onExpand(!currentExpand);
    } else {
      setExpandStatus(!currentExpand);
    }
  }
  return (
    <div className={clsx(style.expandCard, className)}>
      <div
        className={clsx(style.sider, {
          [style.expand]: currentExpand,
        })}
      >
        {sider}
      </div>
      <div className={style.content}>
        <Button
          className={clsx(style.expandBtn, {
            [style.expand]: currentExpand,
          })}
          shape="circle"
          onClick={handleExpand}
          icon={<DoubleLeftOutlined />}
        />
        {content}
      </div>
    </div>
  );
};
export default memo(ExpandCard);
