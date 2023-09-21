import {
  create,
  deleteById as deleteByFileId,
  queryById,
  queryByParentFolderId as queryByParentLogId,
} from '@/services/customplugin/api';
import {
  deleteById,
  queryByParentFolderId,
} from '@/services/customplugin/folderApi';
import {
  userFavoriteCreate,
  userFavoriteDeleteById,
} from '@/services/tenant/favoriteApi';
import clipboard from '@/utils/clipboard';
import {
  Col,
  Dropdown,
  message,
  Modal,
  Popover,
  Row,
  Tooltip,
  Typography,
} from 'antd';
import _ from 'lodash';
import moment from 'moment';
import React, { useEffect, useState } from 'react';
import { history } from 'umi';

import {
  BellOutlined,
  CloudSyncOutlined,
  DeleteOutlined,
  EditOutlined,
  FolderOpenOutlined,
  LockOutlined,
  ProfileOutlined,
  PushpinOutlined,
  ScissorOutlined,
  StarFilled,
  StarOutlined,
} from '@ant-design/icons';
import $i18n from '../../../i18n';
import './index.less';

const { confirm } = Modal;
const { Paragraph } = Typography;

const XfItem: React.FC<any> = (props) => {
  const {
    file,
    popType,
    parentId,
    refesh,
    pasteFileIds,
    handleChangePopType,
    handleCut,
  } = props;
  const [favorited, setFavorited] = useState(file.favorited);
  const [cutDisabled, setCutDisabled] = useState(
    pasteFileIds.indexOf(file.id) > -1,
  );
  // const iconClass = getIconClass(file);
  useEffect(() => {
    setFavorited(file.favorited);
  }, [file?.favorited]);
  useEffect(() => {
    setCutDisabled(pasteFileIds.indexOf(file.id) > -1);
  }, [pasteFileIds.indexOf(file.id)]);
  const iconClass = (file) => {
    if (file.type === 'folder') {
      return 'icon-31wodeyewuxiang';
    } else {
      return 'icon-xiangmushezhi';
    }
  };
  let className = 'xf-item';
  const now = new Date().getTime();
  const alarmCount =
    !_.isUndefined(file.recentAlarm) &&
    file.recentAlarm > 0 &&
    file.recentAlarm &&
    now - file.alarmRrdTime < 10 * 60000
      ? file.recentAlarm
      : 0;
  function getNocStatus(file) {
    const nocStatus = file.noc;
    const ret = {
      display:
        nocStatus == 'PRE' || nocStatus == 'DONE' || nocStatus == 'REJECT',
      color: '',
      title: '',
    };

    if (nocStatus === 'PRE') {
      ret.color = 'grey';
      ret.title = $i18n.get({
        id: 'holoinsight.FolderView.XfItem.PushAlertsToMonitoringCenter',
        dm: '推送预警到监控中心-审核中',
      });
    } else if (nocStatus === 'DONE') {
      ret.color = 'orange';
      ret.title = $i18n.get({
        id: 'holoinsight.FolderView.XfItem.TheAlertHasBeenPushed',
        dm: '预警已被推送到监控中心.点击取消',
      });
    } else if (nocStatus === 'REJECT') {
      ret.color = 'red';
      ret.title = $i18n.get({
        id: 'holoinsight.FolderView.XfItem.PushAlertToMonitoringCenter',
        dm: '推送预警到监控中心-已驳回',
      });
    }
    return ret;
  }
  const alarmConfiged = false;

  const nocStatus = getNocStatus(file);
  function renderOperate() {
    let items = [
      {
        key: 'edit',
        label: (
          <span
            onClick={() => {
              if (file.type === 'folder') {
                handleChangePopType(
                  true,
                  {
                    type: 'addFolder',
                    way: 'edit',
                  },
                  file,
                );
              } else {
                history.push(
                  `/log/metric/edit/${file.parentFolderId}/${file.id}?logMonitor=edit&parentId=${file.parentFolderId}`,
                );
              }
            }}
          >
            <EditOutlined style={{ marginRight: '5px' }} />
            {`${
              file.type === 'folder'
                ? $i18n.get({
                    id: 'holoinsight.FolderView.XfItem.EditFolder',
                    dm: '编辑文件夹',
                  })
                : $i18n.get({
                    id: 'holoinsight.FolderView.XfItem.EditFile',
                    dm: '编辑文件',
                  })
            }`}
          </span>
        ),
      },
      {
        key: 'cut',
        disabled: cutDisabled,
        label: (
          <div
            onClick={() => {
              clipboard.cut(file);
              handleCut(file);
              setCutDisabled(true);
            }}
          >
            <ScissorOutlined style={{ marginRight: '5px' }} />
            {$i18n.get({
              id: 'holoinsight.FolderView.XfItem.Shear',
              dm: '剪切',
            })}
          </div>
        ),
      },
      {
        key: 'delete',
        label: (
          <span
            onClick={() => {
              Modal.confirm({
                title: $i18n.get({
                  id: 'holoinsight.FolderView.XfItem.AreYouSureYouWant',
                  dm: '请确认是否删除？',
                }),
                onOk: () => {
                  userFavoriteDeleteById(
                    file.type === 'folder' ? 'folder' : 'logmonitor',
                    file.id,
                  );
                  if (file.type === 'folder') {
                    Promise.all([
                      queryByParentFolderId(file.id),
                      queryByParentLogId(file.id),
                    ])
                      .then((res) => {
                        const [arr1, arr2] = res;
                        if (arr1.length || arr2.length) {
                          Modal.error({
                            title: $i18n.get({
                              id: 'holoinsight.FolderView.XfItem.TheCurrentFolderIsNot',
                              dm: '当前文件夹下不为空，禁止删除！',
                            }),
                            okText: $i18n.get({
                              id: 'holoinsight.FolderView.XfItem.Ok',
                              dm: '确定',
                            }),
                          });
                        } else {
                          deleteById(file.id).then((res) => {
                            message.success(
                              $i18n.get({
                                id: 'holoinsight.FolderView.XfItem.DeletedSuccessfully',
                                dm: '删除成功！',
                              }),
                            );

                            refesh();
                          });
                        }
                      })
                      .catch((err) => {});
                  } else {
                    deleteByFileId(file.id).then((res) => {
                      message.success(
                        $i18n.get({
                          id: 'holoinsight.FolderView.XfItem.DeletedSuccessfully',
                          dm: '删除成功！',
                        }),
                      );

                      refesh();
                    });
                  }
                },
              });
            }}
          >
            <DeleteOutlined style={{ color: 'red', marginRight: '5px' }} />
            <span style={{ color: 'red' }}>
              {$i18n.get({
                id: 'holoinsight.FolderView.XfItem.Delete',
                dm: '删除',
              })}
            </span>
          </span>
        ),
      },
    ];

    if (file.type != 'folder') {
      let clone = {
        key: 'clone',
        label: (
          <span
            onClick={() => {
              Modal.confirm({
                title: $i18n.get({
                  id: 'holoinsight.FolderView.XfItem.AreYouSureYouWant.1',
                  dm: '请确认是否克隆?',
                }),
                onOk: () => {
                  queryById(file.id).then((res) => {
                    (res.id = null),
                      (res.name =
                        res.name +
                        $i18n.get({
                          id: 'holoinsight.FolderView.XfItem.Clone',
                          dm: '_克隆',
                        }));
                    create(res).then((res) => {
                      message.success(
                        $i18n.get({
                          id: 'holoinsight.FolderView.XfItem.ClonedSuccessfully',
                          dm: '克隆成功',
                        }),
                      );
                      refesh();
                    });
                  });
                },
              });
            }}
          >
            <ScissorOutlined style={{ marginRight: '5px' }} />
            {$i18n.get({
              id: 'holoinsight.FolderView.XfItem.Clone.1',
              dm: '克隆',
            })}
          </span>
        ),
      };
      items.splice(items.length - 1, 0, clone);
    }
    return { items };
  }

  function getClick(file, mode = 'preview', newWindow = false) {
    let url = '';
    if (file.type === 'folder') {
      url = `/log/${file.id}`;
    } else {
      url = `/log/metric/${file.id}?parentId=${parentId}`;
    }
    history.push(url);
    // 跳转到日志详情页
  }

  return (
    // 日志监控第二层，日志选择页面
    <div className={`${className} ${cutDisabled ? 'cutting' : ''}`}>
      <div className="header">
        <Col
          span={20}
          className="content-position"
          onClick={() => getClick(file)}
        >
          {file.top ? (
            <Tooltip
              title={$i18n.get({
                id: 'holoinsight.FolderView.XfItem.TopFrontRowDisplay',
                dm: '已置顶前排显示',
              })}
            >
              <span
                className="xf-item-icon"
                style={{
                  fontSize: '12px',
                  fontStyle: 'italic',
                  position: 'absolute',
                  top: -2,
                  left: -4,
                }}
              >
                <PushpinOutlined />
              </span>
            </Tooltip>
          ) : null}
          <Col span={5} className="icon-position">
            {/* <span><Icon type="lock" /></span> */}
            {file.type === 'folder' ? (
              <FolderOpenOutlined className={`item-icon`} />
            ) : (
              <ProfileOutlined className={`item-icon`} />
            )}
            {/* <i className={`item-icon ${iconClass(file)}`} /> */}
          </Col>

          <Col span={19} className="word-position">
            {/* <div className="item-name"> */}
            <Popover content={file.name} placement="topLeft">
              <Paragraph ellipsis style={{ width: 'calc(100% - 20px)' }}>
                {file.name}
                {/* 日志名称 */}
              </Paragraph>
            </Popover>
            {/* </div> */}

            <div className="description">
              {file.gmtModified
                ? moment(file.gmtModified).format('YYYY-MM-DD HH:mm')
                : $i18n.get({
                    id: 'holoinsight.FolderView.XfItem.NoModificationRecord',
                    dm: '无修改记录',
                  })}
              {/* 日志修改时间 */}
            </div>
          </Col>
        </Col>
        <Col span={4} className="alarm-position">
          {/* <!-- 有预警 --> */}

          {file.recentAlarmed && alarmCount > 0 && !file.alarmClose && (
            <Tooltip
              placement="top"
              title={$i18n.get(
                {
                  id: 'holoinsight.FolderView.XfItem.AlarmcountAlert',
                  dm: '{alarmCount} 预警',
                },
                { alarmCount: alarmCount },
              )}
            >
              <span>
                <BellOutlined
                  type="bell"
                  className="alarm-icon-alarmed"
                  onClick={() => {}}
                />
              </span>
            </Tooltip>
          )}

          {/* <!-- 配置了，但无预警 --> */}

          {alarmConfiged && !alarmCount && !file.alarmClose && (
            <Tooltip
              placement="top"
              title={$i18n.get({
                id: 'holoinsight.FolderView.XfItem.NoWarningInformation',
                dm: '无预警信息',
              })}
            >
              <span>
                <BellOutlined
                  type="bell"
                  className="alarm-icon-alarmed"
                  onClick={() => {}}
                />
              </span>
            </Tooltip>
          )}

          {/* <!-- 设置了访问权限 --> */}

          {
            file.locked && (
              // <PrivateFolderModal useSpan config={this.config} hasAdmin={hasAdmin} reload={this.props.reload}>
              <Tooltip
                placement="top"
                title={$i18n.get({
                  id: 'holoinsight.FolderView.XfItem.ViewFolderAccessPermissions',
                  dm: '查看文件夹访问权限',
                })}
              >
                {/* <Icon type="lock" className="alarm-icon-unalarmed" /> */}

                <LockOutlined className="alarm-icon-unalarmed" />
              </Tooltip>
            )

            // </PrivateFolderModal>
          }

          {/* <!-- 预警关闭 --> */}

          {file.alarmClose && (
            <Tooltip
              title={
                <Row>
                  <Row>
                    {$i18n.get({
                      id: 'holoinsight.FolderView.XfItem.AlarmOff',
                      dm: '告警关闭',
                    })}
                  </Row>

                  <Row>
                    {$i18n.get({
                      id: 'holoinsight.FolderView.XfItem.Operator',
                      dm: '操作人:',
                    })}
                    {file.alarmStatus.creater}
                  </Row>

                  <Row>
                    {$i18n.get({
                      id: 'holoinsight.FolderView.XfItem.WarningShutdownReason',
                      dm: '预警关闭原因:',
                    })}
                    {file.alarmStatus.reason}
                  </Row>

                  <Row>
                    {$i18n.get({
                      id: 'holoinsight.FolderView.XfItem.Start',
                      dm: '启始:',
                    })}

                    {moment(file.alarmStatus.start).format(
                      'YYYY-MM-DD HH:mm:ss',
                    )}
                  </Row>

                  <Row>
                    {$i18n.get({
                      id: 'holoinsight.FolderView.XfItem.End',
                      dm: '结束:',
                    })}
                    {moment(file.alarmStatus.end).format('YYYY-MM-DD HH:mm:ss')}
                  </Row>
                </Row>
              }
              placement="top"
            >
              <span
                className="xf-item-icon icon-bellslasho"
                style={{ fontSize: '12px', paddingLeft: '0px' }}
                fc-tooltipdata-placement="top"
                onClick={() => {}}
              />
            </Tooltip>
          )}

          {file.position && file.position.syncSite && (
            <span className="xf-item-icon" style={{ fontSize: '13px' }}>
              {/* <EditFolder file={file} reload={this.props.reload}> */}

              <Tooltip
                title={$i18n.get({
                  id: 'holoinsight.FolderView.XfItem.TheCollectionConfigurationHasBeen',
                  dm: '采集配置已同步至xxxxx站点',
                })}
              >
                {/* <Icon type="cloud-sync" style={{ color: '#1890FF' }} /> */}

                <CloudSyncOutlined style={{ color: '#1890FF' }} />
              </Tooltip>

              {/* </EditFolder> */}
            </span>
          )}

          {/* <!-- 推送到通知中心 -2是初始状态，表示没有任何设置，所以不显示--> */}

          {nocStatus.display && (
            <Tooltip title={nocStatus.title}>
              <span
                className="xf-item-icon icon-pushnotifysetting"
                style={{ fontSize: 12, color: nocStatus.color }}
                onClick={() => {}}
              />
            </Tooltip>
          )}

          {favorited ? (
            <span className="xf-item-icon" style={{ fontSize: '13px' }}>
              <Tooltip
                title={$i18n.get({
                  id: 'holoinsight.FolderView.XfItem.FavoriteClickCancel',
                  dm: '已收藏，点击取消收藏',
                })}
              >
                <StarFilled
                  style={{ color: 'gold' }}
                  type="star"
                  onClick={() =>
                    userFavoriteDeleteById(
                      file.type === 'folder' ? 'folder' : 'logmonitor',
                      file.id,
                    ).then((r) => {
                      message.success(
                        $i18n.get({
                          id: 'holoinsight.FolderView.XfItem.TheCollectionHasBeenCanceled',
                          dm: '取消收藏成功',
                        }),
                      );

                      refesh();
                    })
                  }
                />
              </Tooltip>
            </span>
          ) : (
            <span className="xf-item-icon" style={{ fontSize: '13px' }}>
              <Tooltip
                title={$i18n.get({
                  id: 'holoinsight.FolderView.XfItem.AddToFavorites',
                  dm: '加入收藏',
                })}
              >
                <StarOutlined
                  style={{ color: 'gold' }}
                  type="star"
                  onClick={() => {
                    userFavoriteCreate({
                      url:
                        file.type === 'folder'
                          ? `/log/${file.id}`
                          : `/log/metric/${file.id}`,
                      type: file.type === 'folder' ? 'folder' : 'logmonitor',
                      relateId: file.id,
                      name: file.name,
                    }).then((r) => {
                      message.success(
                        $i18n.get({
                          id: 'holoinsight.FolderView.XfItem.CollectedSuccessfully',
                          dm: '收藏成功',
                        }),
                      );

                      refesh();
                    });
                  }}
                />
              </Tooltip>
            </span>
          )}

          <span
            style={{
              display: 'inline-block',
              height: '20px',
              lineHeight: '0px',
              marginLeft: '20px',
            }}
          >
            <Dropdown menu={renderOperate()}>
              <a
                className="ant-dropdown-link item-drop item-drop-color"
                style={{ textAlign: 'center' }}
              >
                ...
              </a>
            </Dropdown>
          </span>
        </Col>
      </div>
      {/* <div className="footer moveable">
        </div> */}
    </div>
  );
};

export default XfItem;
