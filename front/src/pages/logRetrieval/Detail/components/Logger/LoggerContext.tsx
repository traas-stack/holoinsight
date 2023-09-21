import { Drawer, List, Spin, Typography } from 'antd';
import React, {
  forwardRef,
  useImperativeHandle,
  useRef,
} from 'react';

import styles from './Logger.less';

interface ILoggerContext {
  open: boolean;
  onClose: () => void;
  prevLoading: boolean;
  nextLoading: boolean;
  hasNext: boolean;
  contextLoading: boolean;
  activePackIndex: number;
  contextData: CLOUDMONITOR_API.SlsLog[];
  loadForwardData: () => Promise<void>;
  handleScroll: (e: React.UIEvent<HTMLDivElement>) => void;
}

function LoggerContext(
  {
    open,
    onClose,
    handleScroll,
    prevLoading,
    nextLoading,
    contextData,
    contextLoading,
    activePackIndex,
  }: ILoggerContext,
  ref: React.Ref<{
    activePackIntoView: () => void;
    setScrollTop: (_: number) => void;
  }>,
) {
  const scrollRef = useRef<HTMLDivElement>(null);
  const activePackRef = useRef<HTMLDivElement>(null);

  useImperativeHandle(ref, () => ({
    activePackIntoView: () => {
      activePackRef.current?.scrollIntoView?.();
    },
    setScrollTop: (logLength: number) => {
      if (scrollRef.current) {
        scrollRef.current.scrollTop = logLength * 39;
      }
    },
  }));
  return (
    <Drawer
      title="上下文内容"
      placement="right"
      width={1000}
      destroyOnClose
      open={open}
      onClose={onClose}
    >
      <div
        id="scrollableDiv"
        ref={scrollRef}
        style={{
          height: '100%',
          overflow: 'auto',
        }}
        onScroll={handleScroll}
      >
        {prevLoading && (
          <Spin style={{ marginLeft: '50%', transform: 'translateX(-50%)' }} />
        )}
        <List
          className={styles.loggerList}
          dataSource={contextData}
          loading={!prevLoading && contextLoading}
          renderItem={(item, index) => {
            return (
              <List.Item
                style={
                  activePackIndex === index
                    ? { backgroundColor: '#e6f7ff' }
                    : {}
                }
              >
                <List.Item.Meta
                  title={
                    <div
                      ref={activePackIndex === index ? activePackRef : null}
                      style={{ textAlign: 'right' }}
                    >
                      <Typography.Paragraph code style={{ fontWeight: 'bold' }}>
                        {index - activePackIndex}
                      </Typography.Paragraph>
                    </div>
                  }
                  description={
                    <div
                      style={{
                        width: '100%',
                        wordBreak: 'break-all',
                      }}
                    >
                      {item.content && (
                        <Typography.Paragraph
                          code
                          ellipsis={{
                            rows: 5,
                            expandable: true,
                            symbol: '展开',
                          }}
                          style={{ marginBottom: 0 }}
                        >
                          {item.content}
                        </Typography.Paragraph>
                      )}
                    </div>
                  }
                />
              </List.Item>
            );
          }}
        />
        {nextLoading && (
          <Spin style={{ marginLeft: '50%', transform: 'translateX(-50%)' }} />
        )}
      </div>
    </Drawer>
  );
}

export default forwardRef(LoggerContext);
