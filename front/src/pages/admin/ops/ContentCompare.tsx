import { Col, Modal, Row } from 'antd';
import { type Props, pasreOpContent } from './const';
import $i18n from '@/i18n';
import React from 'react';
import ReactJson from 'react-json-view';

export class ContentCompare extends React.PureComponent<Props, any> {
  constructor(props: Props) {
    super(props);
    this.state = {
      visible: false,
      creator: null,
      entity: null,
      opType: null,
    };
  }

  showModelHandler = (e: React.MouseEvent<HTMLSpanElement, MouseEvent>) => {
    if (e) e.stopPropagation();
    this.setState({
      visible: true,
    });
  };

  hideModelHandler = () => {
    this.setState({
      visible: false,
    });
  };

  render() {
    const { children, record } = this.props;
    return (
      <span>
        <span onClick={this.showModelHandler}>{children}</span>

        <Modal
          title={$i18n.get({
            id: 'holoinsight.admin.ops.OpsHistory.ComparisonOfOperationContents',
            dm: '操作内容对比',
          })}
          open={this.state.visible}
          onCancel={this.hideModelHandler}
          footer={null}
          width={1200}
        >
          <Row gutter={16}>
            <Col span={12} style={{ wordBreak: 'break-all' }}>
              <h4>
                {$i18n.get({
                  id: 'holoinsight.admin.ops.OpsHistory.BeforeOperation',
                  dm: '操作前',
                })}
              </h4>

              <ReactJson
                src={pasreOpContent(record.opBeforeContext)}
                displayDataTypes={false}
                displayObjectSize={false}
                theme="monokai"
                name={false}
              />
            </Col>

            <Col span={12} style={{ wordBreak: 'break-all' }}>
              <h4>
                {$i18n.get({
                  id: 'holoinsight.admin.ops.OpsHistory.AfterOperation',
                  dm: '操作后',
                })}
              </h4>

              <ReactJson
                src={pasreOpContent(record.opAfterContext)}
                displayDataTypes={false}
                displayObjectSize={false}
                theme="monokai"
                name={false}
              />
            </Col>
          </Row>
        </Modal>
      </span>
    );
  }
}
