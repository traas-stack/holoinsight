import React, { useState, useEffect, forwardRef, useImperativeHandle } from 'react';
import { message,  Tree } from 'antd';
import _ from 'lodash';
import { FolderOpenOutlined, ProfileOutlined, StarFilled } from '@ant-design/icons';
import type { DataNode } from 'antd/lib/tree';
import { queryByParentFolderId, getQueryPath } from '@/services/customplugin/folderApi';
import { queryByParentFolderId as queryByParentLogId } from '@/services/customplugin/api';
import { getFolderSeach } from '@/services/customplugin/folderApi';
import { history } from 'umi';
import './index.less';
import $i18n from '../../../i18n';
import { userFavoriteDeleteById, userFavoriteQueryAll } from '@/services/tenant/favoriteApi'
const FileTree: React.FC<any> = forwardRef((props: any) => {
  const { from, handleGetCheck, parentId, check, handleGetSelect, keyWord, handleClose, refss, readyMove, setReadymove, TreeType } = props;
  const [treeData, setTreeData] = useState([]);
  const [expandedKeys, setExpandedKeys] = useState([]);
  const [checkedKeys, setCheckedKeys] = useState([]);
  const [checkedNodes, setCheckedNodes] = useState([]);
  const [loadKey, setLoadKey] = useState([]);
  const [favoriteData, setFavoriteData] = useState([]);

  useEffect(() => {
    queryAllNodes(-1, 'init');
  }, [keyWord]);
  useEffect(() => {
    if (from === 'search' && keyWord) {
      getFolderSeach({ keyword: keyWord }).then((res) => {
        if (!(res?.[0]?.datas || []).length && !(res?.[1]?.datas || []).length) {
          message.info(
            $i18n.get({
              id: 'holoinsight.FolderView.FileTree.FilesOrFoldersWithThe',
              dm: '未搜索到当前关键字的文件或文件夹！',
            }),
          );
          return;
        }
      });
    }
  }, [keyWord]);
  function queryAllNodes(id: number | string, type: string, expandKeys?: string[]) {
    userFavoriteQueryAll().then((r) => {
      const favoriteDatas = r.map((ri) => {
        return ri.relateId - 0
      })
      setFavoriteData(favoriteDatas)
    })
    Promise.all([queryByParentFolderId(id), queryByParentLogId(id)]).then((res) => {
      const [arr1, arr2] = res;
      const arrlenth = arr1.length;
      const arrAll = arr1.concat(arr2).map((item, index) => {
        const strTitle = item.name;
        const key = keyWord ? strTitle.indexOf(keyWord) : -1;
        const beforeStr = strTitle.substring(0, key);
        const afterStr = strTitle.slice(key + (keyWord || '').length);
        const title =
          key > -1 ? (
            <span>
              {beforeStr}

              <span style={{ color: '#f50' }}>{keyWord}</span>

              {afterStr}
            </span>
          ) : (
            <span>{item.name}</span>
          );

        const type = index < arrlenth ? 'folder' : 'file';
        const icon = index < arrlenth ? <FolderOpenOutlined /> : <ProfileOutlined />;
        const isLeaf = index < arrlenth ? false : true;
        item.type = type;
        item.icon = icon;
        item.key = index;
        item.title = title;
        item.isLeaf = isLeaf;
        if (TreeType === 'moveSlectTree') {
          readyMove.forEach((v) => {
            if (item.id === v.id) {
              item.disabled = true
            }
          })
          if (item.type !== "folder") {
            item.disabled = true
          }
        }
        return item;
      });
      setTreeData(arrAll);

      // if (type === 'init') {
      //     setTreeData([...arrFolder, ...arrFile]);
      // } else {
      //     let newArr = JSON.parse(JSON.stringify(treeData));
      //     newArr[0].children = [...arrFolder, ...arrFile];
      //     setTreeData(newArr);
      // }
    });
  }
  async function onLoadData(hasChild) {
    if (hasChild.children) {
      return;
    }
    const res = await Promise.all([
      queryByParentFolderId(hasChild.id),
      queryByParentLogId(hasChild.id),
    ]);
    const [arr1, arr2] = res;
    const arrlenth = arr1.length;
    const arrAll = arr1.concat(arr2).map((item, index) => {
      const type = index < arrlenth ? 'folder' : 'file';
      const icon = index < arrlenth ? <FolderOpenOutlined /> : <ProfileOutlined />;
      const isLeaf = index < arrlenth ? false : true;
      item.type = type;
      item.icon = icon;
      item.key = `${hasChild.key}-${index}`;
      item.title = item.name;
      item.isLeaf = isLeaf;
      if (TreeType === 'moveSlectTree') {
        readyMove.forEach((v) => {
          if (item.id === v.id) {
            item.disabled = true
          }
        })
        if (item.type !== "folder") {
          item.disabled = true
        }
      }
      return item;
    });
    const newArr = updateTreeData(treeData, hasChild.key, arrAll);
    setTreeData(newArr);
    if (setReadymove) {
      setReadymove(updateTreeData(readyMove, hasChild.key, arrAll))
    }
  }

  const updateTreeData = (list: DataNode[], key: React.Key, children: DataNode[]): DataNode[] =>
    list.map((node) => {
      if (node.key === key) {
        return {
          ...node,
          children,
        };
      }
      if (node.children) {
        return {
          ...node,
          children: updateTreeData(node.children, key, children),
        };
      }
      return node;
    });

  function onCheck(checkedKeysValue) {
    const backArr = getTreeItemNode(treeData, checkedKeysValue);
    handleGetCheck(backArr);
  }

  function getTreeItemNode(allData, checkedKeysValue) {
    return allData
      .map((item, index) => {
        if (checkedKeysValue.includes(item.key)) {
          return item;
        } else {
          if (item.children) {
            return getTreeItemNode(item.children, checkedKeysValue);
          } else {
          }
          return null;
        }
      })
      .filter((item) => item != null);
  }
  const expandClose = (v) => {
    if (!expandedKeys.includes(v)) {
      const exArr = [...expandedKeys]
      exArr.push(v)
      setExpandedKeys(exArr)
    } else {
      const exArr = [...expandedKeys].filter((val) => {
        return val !== v
      })
      setExpandedKeys(exArr)
    }
  }
  function onSelect(selectedKeys, e) {
    // const backArr = getTreeItemNode(treeData, selectedKeys);
    const backArr = e.node

    if (from === 'search') {
      // const item = backArr.flat(Infinity)[0];

      if (backArr.type === 'folder') {
        expandClose(e.node.key)
        history.push(`/log/${backArr.id}`);
        handleClose();
      } else {
        history.push(`/log/metric/${backArr.id}?parentId=${parentId}`);
      }
    } else {
      handleGetSelect && handleGetSelect(backArr);
    }
  }
  const onExpand = (e, n) => {
    expandClose(n.node.key)

  }
  const onLoad = (e, n) => {
    // expandClose(n.node.key)
    setLoadKey(e)
  }
  const remake = () => {
    setLoadKey([])
    setExpandedKeys([])
    queryAllNodes(-1, 'init')
  }
  useImperativeHandle(refss, () => ({
    remakes: () => {
      remake()
    }
  }))
  const deleteFavorite = (record: any) => {
    userFavoriteDeleteById(
      record.type === 'folder' ? 'folder' : 'logmonitor',
      record.id,
    ).then((r) => {
      remake()
      message.success(
        $i18n.get({
          id: 'holoinsight.FolderView.XfItem.TheCollectionHasBeenCanceled',
          dm: '取消收藏成功',
        }),
      );
      // actionRef.current?.reload();
    })
  }
  function handleOpenAddFolder() {
  }
  return (
    <Tree
      showIcon
      loadData={onLoadData}
      loadedKeys={loadKey}
      onLoad={onLoad}
      expandedKeys={expandedKeys}
      treeData={readyMove && setReadymove ? readyMove : treeData}
      checkable={check}
      checkStrictly={true}
      checkedKeys={readyMove}
      onCheck={onCheck}
      onSelect={onSelect}
      height={500}
      onExpand={onExpand}
      titleRender={(e: any) => {
        return (
          <>
            {e.name}
            {favoriteData.includes(e.id) ?
              <>
                {/* <Tooltip title='取消收藏'> */}
                {/* <Button type="link" style={{ padding: '0px', margin: '0px', height: '16px' }} onClick={() =>
                    deleteFavorite(e)
                  }> */}
                <StarFilled style={{ color: 'gold', width: '16px', height: '16px' }} />
                {/* </Button> */}
                {/* </Tooltip> */}
              </> :
              <>
              </>}
          </>)
      }}
    ></Tree>
  );
})

export default FileTree
