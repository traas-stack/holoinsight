import React from 'react';
import { Modal, Tree, Row, Col, Form, Input, Button, Icon, Tag, Alert, Card } from 'antd';
import { FileOutlined, FolderAddOutlined } from '@ant-design/icons';
import { listFiles } from '@/services/agent/api';
import './common.less';
import $i18n from '../../../i18n';

const { TreeNode } = Tree;
const FormItem = Form.Item;
const formItemLayout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 },
  colon: false,
  style: {
    marginBottom: 16,
  },
};

const treeIconStyle = {
  style: {
    marginRight: 4,
    marginTop: 6,
  },
};

const generateList = (data, dataList) => {
  data.forEach((node) => {
    const { key } = node;
    dataList.push({ key, title: key });
    if (node.children) {
      generateList(node.children, dataList);
    }
  });
};
const getParentKey = (key, data) => {
  let parentKey = '';
  data.forEach((node) => {
    if (node.children) {
      if (node.children.some((item) => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  });
  return parentKey;
};

class LogFileModal extends React.PureComponent {
  constructor(props) {
    super(props);
  }

  static defaultProps = {
    btnText: '',
    preApp: '',
    preHost: '',
    preLabel: {},
    logpath: '',
    disabledSelect: true,
  };

  state = {
    visible: this.props.visible,
    root: '/home/admin/logs',
    app: this.props.preApp || '',
    label: this.props.preLabel || '',
    server: this.props.preHost || '',
    selectedLog: this.props.logpath || '',
    hideTip: false,
    scanData: [],
    searchValue: '',
    expandedKeys: [this.props.logpath || '/home/admin/logs'],
    autoExpandParent: true,
  };

  componentWillReceiveProps(nextProps) {
    const { preHost, preApp, visible, logpath, preLabel } = this.props;
    const {
      preHost: nextHost,
      preApp: nextApp,
      visible: nextVsible,
      logpath: nextLogPath,
      preLabel: nextLabel,
    } = nextProps;
    preApp !== nextApp &&
      this.setState({
        app: nextApp,
      });

    preHost !== nextHost &&
      this.setState({
        server: nextHost,
      });

    visible !== nextVsible &&
      this.setState({
        visible: nextVsible,
      });

    preLabel !== nextLabel &&
      this.setState({
        lebel: nextLabel,
      });

    nextLogPath !== logpath &&
      this.setState({
        selectedLog: nextLogPath,
      });

    if (
      (preHost !== nextHost || preApp !== nextApp || nextVsible || preLabel !== nextLabel) &&
      (nextHost !== '' || nextApp !== '' || nextLabel !== '')
    ) {
      this.searchLogs(nextHost, nextApp, nextLabel);
    }
  }

  searchLogsByHandler = (preHost) => {
    const { root, server, app } = this.state;
    // if (!server && !preHost) {
    //   message.warn(
    //     $i18n.get({
    //       id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.PleaseSpecifyTheIpAddress',
    //       dm: '请指定机器ip进行选择',
    //     }),
    //   );
    //   return;
    // }
    this.searchLogs(preHost, null);
  };

  searchLogs = (preHost, preApp, preLabel) => {
    const { root, server, app, label } = this.state;
    let labelObj={}
    let labelArr=this.props.preLabel
    if((labelArr||[]).length>0){
      labelArr.forEach((item)=>{
        if(item){
          Object.assign(labelObj,{[item.key]:item.value[0]})
        }
    })}
    let { hideTip } = this.state;
    let params = {};
    if (server) {
      params = {
        ip: server || preHost,
        app: app || "",
        label: labelObj || {},
        logpath: root?.trim(),
      }
    } else {
      params = {
        ip: (server || preHost)||"",
        app: preApp || app,
        label: preLabel
          ? {
            [preLabel[0].key]: preLabel[0].value[0],
          }
          : {},
        logpath: root?.trim(),
      }
    }
    listFiles(params).then((data) => {
      if (root.length || server.length) {
        hideTip = true;
      } else {
        hideTip = false;
      }
      if (data && data.dirTrees) {
        const scanData = this.convertToTree(data.dirTrees);
        this.setState({
          hideTip,
          scanData,
        });
      }
    });
  };

  convertToTree = (data) => {
    const ret = [];
    data.forEach((item) => {
      if (item) {
        const type = item.dir ? 'folder' : 'file';
        const node = {
          title: item.name,
          key: item.fullPath,
          type,
          path: item.fullPath,
        };

        if (item.subs && item.subs.length) {
          node.children = this.convertToTree(item.subs);
        }
        ret.push(node);
      }
    });
    ret.sort((a, b) => a.title - b.title);
    return ret;
  };

  changeRoot = (e) => {
    this.setState({
      root: e.target.value?.trim(),
    });
  };

  changeServer = (e) => {
    this.setState({
      server: e.target.value?.trim(),
    });
  };

  loop = (data) => {
    const { searchValue } = this.state;
    return data.map((item) => {
      const index = item.title.indexOf(searchValue);
      const beforeStr = item.title.substr(0, index);
      const afterStr = item.title.substr(index + searchValue.length);
      const title =
        index > -1 ? (
          <span>
            {beforeStr}

            <span style={{ color: '#f50' }}>{searchValue}</span>

            {afterStr}
          </span>
        ) : (
          <span>{item.title}</span>
        );

      if (item.children) {
        return (
          <TreeNode
            key={item.key}
            title={
              <Row>
                {item.type === 'folder' ? (
                  <FolderAddOutlined {...treeIconStyle} />
                ) : (
                  <FileOutlined {...treeIconStyle} />
                )}

                {title}
              </Row>
            }
            dataRef={item}
            selectable={false}
          >
            {this.loop(item.children)}
          </TreeNode>
        );
      }
      return (
        <TreeNode
          key={item.key}
          title={
            <Row>
              {item.type === 'folder' ? (
                <FolderAddOutlined {...treeIconStyle} />
              ) : (
                <FileOutlined {...treeIconStyle} />
              )}

              {title}
            </Row>
          }
          dataRef={item}
        />
      );
    });
  };

  onExpand = (expandedKeys) => {
    this.setState({
      expandedKeys,
      autoExpandParent: false,
    });
  };

  onSelect = (selectedKeys, { selectedNodes }) => {
    const logPath = selectedNodes[0] ? selectedNodes[0]?.dataRef?.path : '';
    this.setState({
      selectedLog: logPath,
    });
  };

  handleSearchChange = (e) => {
    const { value } = e.target;
    const { scanData } = this.state;
    const dataList = [];
    generateList(scanData, dataList);
    const expandedKeys = dataList
      .map((item) => {
        if (item.title.indexOf(value) > -1) {
          return getParentKey(item.key, scanData);
        }
        return null;
      })
      .filter((item, index, self) => item && self.indexOf(item) === index);
    this.setState({
      expandedKeys,
      searchValue: value,
      autoExpandParent: true,
    });
  };

  openModal = () => {
    this.setState({
      visible: true,
    });

    // this.props.handleTypeChange();
  };

  handleCancel = () => {
    this.setState({
      visible: false,
      server: "",
      app: "",
      label: {},
      selectedLog: ""
    });

    this.props.handleClose(false);
  };

  handleConfirm = () => {
    this.props.handleConfirm(this.state.selectedLog);
    this.handleCancel();
    this.props.handleClose(false);

  };

  render() {
    const { visible, selectedLog, hideTip, scanData, expandedKeys, autoExpandParent } = this.state;
    return (
      <Modal
        destroyOnClose={true}
        title={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.SelectACollectionFile',
          dm: '选取采集文件',
        })}
        open={visible}
        width={700}
        cancelText={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.Cancel',
          dm: '取消',
        })}
        okText={$i18n.get({
          id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.Ok',
          dm: '确定',
        })}
        onCancel={this.handleCancel}
        onOk={this.handleConfirm}
      >
        <Row gutter={8}>
          <Col span={9}>
            <FormItem
              label={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.SpecifyTheRootDirectory',
                dm: '指定根目录',
              })}
              {...formItemLayout}
            >
              <Input placeholder="/home/admin/logs/" onChange={this.changeRoot} />
            </FormItem>
          </Col>

          <Col span={12}>
            <FormItem
              label={$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.SpecifyTheServerIpAddress',
                dm: '指定服务器 IP',
              })}
              {...formItemLayout}
            >
              <Input
                onChange={this.changeServer}
                placeholder={$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.IfEmptyRandomlySelectFrom',
                  dm: '空则从应用服务器列表中随机挑选',
                })}
              />
            </FormItem>
          </Col>

          <Col span={3}>
            <FormItem {...formItemLayout}>
              <Button onClick={() => this.searchLogsByHandler()}>
                {$i18n.get({
                  id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.Scan',
                  dm: '扫描',
                })}
              </Button>
            </FormItem>
          </Col>
        </Row>

        <Card>
          <Input
            placeholder={$i18n.get({
              id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.IfEmptyAllFilesAre',
              dm: '空则显示所有文件',
            })}
            onChange={this.handleSearchChange}
          />

          {scanData.length ? (
          <div className="tree-max">
            <Tree
              onSelect={this.onSelect}
              onExpand={this.onExpand}
              expandedKeys={expandedKeys}
              autoExpandParent={autoExpandParent}
              defaultExpandParent={true}
            >
              {this.loop(scanData)}
            </Tree>
           </div>
          ) : (
            <Tag color="#108ee9" style={{ marginTop: 16 }}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.NoContent',
                dm: '无内容',
              })}
            </Tag>
          )}
        </Card>

        {selectedLog.length ? (
          <Row className="info-block">
            <span style={{ fontWeight: 'bold' }}>
              {$i18n.get({
                id: 'holoinsight.logMonitor.LogMonitorEdit.LogFileModal.SelectedFile',
                dm: '选取的文件',
              })}
            </span>

            {selectedLog}
          </Row>
        ) : (
          false
        )}
      </Modal>
    );
  }
}

export default LogFileModal;
