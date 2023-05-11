import FileMoveModal from '@/components/FolderView/FileMoveModal';
import FileSearchModal from '@/components/FolderView/FileSearchModal';
import { TpopType } from '@/components/FolderView/index.d';
import NewFileModel from '@/components/FolderView/NewFileModel';
import XfItem from '@/components/FolderView/XfItem';
import {
  queryByParentFolderId as queryByParentLogId,
  updateParentFolderId as updateFile,
} from '@/services/customplugin/api';
import {
  getQueryPath,
  queryByParentFolderId,
  updateParentFolderId,
} from '@/services/customplugin/folderApi';
import { userFavoriteQueryAll } from '@/services/tenant/favoriteApi';
import clipboard from '@/utils/clipboard';
import {
  Button,
  Col,
  message,
  Popconfirm,
  Popover,
  Row,
  Spin,
  Table,
} from 'antd';
import _ from 'lodash';
import {
  CloseSquareOutlined,
  ExclamationCircleOutlined,
  FileAddOutlined,
} from '@ant-design/icons';
import Card from 'antd/lib/card/Card';
import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from 'react';
import { history } from 'umi';
import $i18n from '../../i18n';
import CommonBreadcrumb from '../CommonBreadcrumb';
import FileSearchTree from './FileSearchTree';
import './index.less';
import styles from './theme.less';

const Folders: React.FC<any> = forwardRef((props, ref) => {
  const { id, types } = props;
  const childdRef = useRef<any>();
  const { hideHeader, refs } = props;
  const [showSearch, setShowSearch] = useState<boolean>(false); // 文件视图 or 表格视图
  const [loading, setLoading] = useState<boolean>(false); //loading状态
  // const [parentId, setParentId] = useState() //是否有parentId
  const [hasEdit, setHasEdit] = useState<boolean>(true);
  const [viewFiles, setViewFiles] = useState<any[]>([]);
  // const [fileOpenFlag, setFileOpenFlag] = useState<string>('all')
  const [visible, setVisible] = useState<Boolean>(false);
  const [popType, setPopType] = useState<TpopType>({
    type: '',
  });

  const [selectFile, setSelectFile] = useState<object>({});
  const [breadConfig, setBreadConfig] = useState([]);
  const [pasteFileIds, setPasteFileIds] = useState(
    clipboard.listFileIds() || [],
  );

  const [pasteFiles, setPasteFiles] = useState(clipboard.listFiles() || []); // 剪切文件夹arr

  useEffect(() => {
    getQueryPath({
      requests: [
        {
          folderId: Number(id),
          includePluginName: true,
        },
      ],
    }).then((res) => {
      if (res) {
        const bread = res[0]?.paths || [];
        bread.reverse().unshift({
          name: $i18n.get({
            id: 'holoinsight.components.FolderView.RootDirectory',
            dm: '根目录',
          }),
          url: '/log/',
        });

        const newBreadConfig = bread.map((item, index) => {
          let newItem = {
            name: '',
            url: '',
          };

          newItem.name = item.name;
          newItem.url = `/log/${item.id || -1}`;
          return newItem;
        });
        setBreadConfig(newBreadConfig);
      }
    });
    init();
  }, [id]);

  function handleCut(file) {
    if (_.indexOf(pasteFileIds, file.id) < 0) {
      pasteFileIds.push(file.id);
    }
    setPasteFileIds(pasteFileIds);
    const newPasteFiles = [...pasteFiles];
    newPasteFiles.push(file);
    setPasteFiles(newPasteFiles);
  }

  function buildPasteTable(pasteFiles) {
    const columns = [
      {
        title: $i18n.get({
          id: 'holoinsight.components.FolderView.Type',
          dm: '类型',
        }),
        key: 'type',
        dataIndex: 'type',
        // width: 50,
        render(v, record) {
          return record.pluginType || record.type;
        },
      },

      {
        title: 'id',
        key: 'id',
        dataIndex: 'id',
        // width: 50,
      },
      {
        title: $i18n.get({
          id: 'holoinsight.components.FolderView.Name',
          dm: '名称',
        }),
        key: 'name',
        dataIndex: 'name',
      },
    ];

    return (
      <Table
        size="small"
        style={{ width: 400 }}
        pagination={false}
        columns={columns}
        dataSource={pasteFiles}
        rowKey={(record) => record.id}
      />
    );
  }

  function init() {
    if (types && (id === -1 || !id || id === '-1')) {
      userFavoriteQueryAll().then((item) => {
        // 查询用户的操作信息
        const itemd = item.filter((ite) => {
          return ite.type === 'folder' || ite.type === 'logmonitor';
        });
        const items = itemd.map((ite) => {
          return {
            ...ite,
            id: ite.relateId - 0,
            favorited: true,
          };
        });
        setViewFiles(items);
      });
    } else {
      Promise.all([
        queryByParentFolderId(id),
        queryByParentLogId(id),
        userFavoriteQueryAll(),
      ])
        .then((res) => {
          const [arr1, arr2, arr3] = res;
          const arrFolder = arr1.map((item, index) => {
            let favorited = false;
            item.type = 'folder';
            arr3.forEach((ele, key) => {
              if (
                ele.type === 'folder' &&
                Number(ele.relateId) === Number(item.id)
              ) {
                favorited = true;
              }
            });
            item.favorited = favorited;
            return item;
          });
          const arrFile = arr2.map((item, index) => {
            let favorited = false;
            arr3.forEach((ele, key) => {
              if (
                ele.type === 'logmonitor' &&
                Number(ele.relateId) === Number(item.id)
              ) {
                favorited = true;
              }
            });
            item.type = 'file';
            item.favorited = favorited;
            return item;
          });
          setViewFiles([...arrFolder, ...arrFile]);
        })
        .catch((err) => { });
    }
  }

  function refesh() {
    init();
    if (!types) {
      childdRef.current.remakes();
    }
  }

  function goBack() {
    history.push(breadConfig[breadConfig.length - 2].url);
  }
  function handleChangePopType(flag: Boolean, type: TpopType, file: object) {
    setSelectFile(file);
    setVisible(flag);
    setPopType(type);
  }
  function handleClose(value: boolean) {
    setVisible(false);
  }
  function handleOpenAddFolder() {
    setVisible(true);
    popType.type = 'addFolder';
    popType.way = 'add';
    setPopType({ ...popType });
  }
  useImperativeHandle(refs, () => ({
    handleOpenAddFolder: () => {
      handleOpenAddFolder();
    },
    refeshs: () => {
      refesh();
    },
  }));
  return (
    <Row>
      {types ? (
        <></>
      ) : (
        <>
          <Col span={7}>
            <Card style={{ marginRight: '10px' }}>
              <div style={{ height: '550px' }}>
                <FileSearchTree
                  refesh={refesh}
                  parentId={id}
                  refss={childdRef}
                  setVisible={setVisible}
                  setPopType={setPopType}
                  handleClose={handleClose}
                />
              </div>
            </Card>
          </Col>
        </>
      )}

      <Col span={types ? 24 : 17}>
        <div className="folderView">
          <div className="folderViewBread">
            {breadConfig.length > 1 ? (
              <CommonBreadcrumb urlList={breadConfig} />
            ) : null}
          </div>

          <Spin style={{ width: '100%' }} spinning={loading}>
            {!showSearch && (
              <div className="xf-fs">
                {!hideHeader && (
                  <Row className="b-margin-20 padding10">
                    <Col span={16}>
                      {Number(id) !== -1 ? (
                        // <Button onClick={() => goBack()}>
                        //   <ArrowLeftOutlined />
                        //   {$i18n.get({ id: 'holoinsight.components.FolderView.Return', dm: '返回' })}
                        // </Button>
                        <></>
                      ) : null}

                      {hasEdit ? (
                        <>
                          <>
                            {pasteFiles && pasteFiles.length ? (
                              <Popover
                                placement="bottomRight"
                                content={buildPasteTable(pasteFiles)}
                                title={$i18n.get({
                                  id: 'holoinsight.components.FolderView.FilesAndConfigurationsBeingCut',
                                  dm: '正在剪切中的文件和配置（支持多文件同时剪切）',
                                })}
                              >
                                <Popconfirm
                                  okText={$i18n.get({
                                    id: 'holoinsight.components.FolderView.Confirm',
                                    dm: '确认',
                                  })}
                                  cancelText={$i18n.get({
                                    id: 'holoinsight.components.FolderView.Cancel',
                                    dm: '取消',
                                  })}
                                  title={$i18n.get({
                                    id: 'holoinsight.components.FolderView.PleaseConfirmWhetherToMove',
                                    dm: '请确认是否移动？',
                                  })}
                                  onCancel={() => {
                                    message.error(
                                      $i18n.get({
                                        id: 'holoinsight.components.FolderView.FailedToCut',
                                        dm: '剪切失败',
                                      }),
                                    );
                                  }}
                                  onConfirm={() => {
                                    const requestArr: any = [];
                                    pasteFiles.forEach((item, index) => {
                                      if (item.type === 'folder') {
                                        requestArr.push(
                                          updateParentFolderId({
                                            id: item.id,
                                            parentFolderId: id,
                                          }),
                                        );
                                      } else {
                                        requestArr.push(
                                          updateFile({
                                            id: item.id,
                                            parentFolderId: id,
                                          }),
                                        );
                                      }
                                    });
                                    Promise.all(requestArr).then((res) => {
                                      message.success(
                                        $i18n.get({
                                          id: 'holoinsight.components.FolderView.MovedSuccessfully',
                                          dm: '移动成功',
                                        }),
                                      );

                                      clipboard.cancelCutting();
                                      setPasteFiles([]);
                                      setPasteFileIds([]);
                                      refesh();
                                    });
                                    // });
                                  }}
                                >
                                  <Button>
                                    <FileAddOutlined />
                                    {$i18n.get({
                                      id: 'holoinsight.components.FolderView.Paste',
                                      dm: '粘贴',
                                    })}
                                  </Button>
                                </Popconfirm>
                              </Popover>
                            ) : null}

                            {pasteFiles && pasteFiles.length ? (
                              <Popconfirm
                                okText={$i18n.get({
                                  id: 'holoinsight.components.FolderView.Confirm',
                                  dm: '确认',
                                })}
                                cancelText={$i18n.get({
                                  id: 'holoinsight.components.FolderView.Cancel',
                                  dm: '取消',
                                })}
                                title={$i18n.get({
                                  id: 'holoinsight.components.FolderView.CancelShearingAndClearThe',
                                  dm: '取消剪切，清空剪切板？',
                                })}
                                onConfirm={() => {
                                  clipboard.cancelCutting();
                                  setPasteFileIds([]);
                                  setPasteFiles([]);
                                }}
                              >
                                <Button>
                                  <CloseSquareOutlined />
                                  {$i18n.get({
                                    id: 'holoinsight.components.FolderView.CancelClipping',
                                    dm: '取消剪切',
                                  })}
                                </Button>
                              </Popconfirm>
                            ) : null}
                          </>
                        </>
                      ) : (
                        <span style={{ marginLeft: 8, color: 'red' }}>
                          {/* <Icon type="exclamation-circle" /> */}

                          <ExclamationCircleOutlined />
                          {$i18n.get({
                            id: 'holoinsight.components.FolderView.YouOnlyHaveViewPermissions',
                            dm: '您只有查看权限，因此没有”新建配置“等功能，如果需要，请申请权限。',
                          })}

                          <a target="blank">
                            {$i18n.get({
                              id: 'holoinsight.components.FolderView.PermissionLink',
                              dm: '权限链接',
                            })}
                          </a>
                        </span>
                      )}
                    </Col>
                  </Row>
                )}

                {viewFiles && viewFiles.length <= 0 && (
                  <h3 className="no-content-warning text-center">
                    {$i18n.get({
                      id: 'holoinsight.components.FolderView.NoData',
                      dm: '无数据',
                    })}
                  </h3>
                )}

                <div
                  className={styles.fileList}
                  style={{ height: '550px', overflow: 'auto' }}
                >
                  <Row
                    className="padding10"
                    style={{ display: loading ? 'none' : 'flex' }}
                  >
                    {viewFiles &&
                      _.map(viewFiles, (file, key) => (
                        <Col
                          span={types ? 8 : 12}
                          style={{ display: 'grid' }}
                          key={key}
                        >
                          <XfItem
                            file={file} // 传递用户操作信息
                            handleChangePopType={handleChangePopType}
                            parentId={id}
                            popType={popType}
                            refesh={refesh}
                            handleCut={handleCut}
                            pasteFileIds={pasteFileIds}
                          // changeBreadConfig={changeBreadConfig}
                          // reload={this.reload}
                          // pasteFileIds={pasteFileIds}
                          // hasEdit={hasEdit}
                          // hasAdmin={isSuperAdmin}
                          // handleCut={this.handleCut}
                          />
                        </Col>
                      ))}
                  </Row>
                </div>
              </div>
            )}
          </Spin>

          {popType.type === 'addFolder' ? (
            <NewFileModel
              file={selectFile}
              refesh={refesh}
              parentId={id}
              visible={visible}
              type={popType?.way}
              handleClose={handleClose}
            />
          ) : popType.type === 'searchModal' ? (
            <FileSearchModal
              parentId={id}
              visible={visible}
              handleClose={handleClose}
            />
          ) : (
            <FileMoveModal
              refesh={refesh}
              visible={visible}
              handleClose={handleClose}
            />
          )}
        </div>
      </Col>
    </Row>
  );
})

export default Folders
