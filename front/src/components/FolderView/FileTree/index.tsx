import React, { useState, useEffect, forwardRef, useImperativeHandle } from 'react';
import { message, Tree } from 'antd';
import _ from 'lodash';
import { FolderOpenOutlined, ProfileOutlined, StarFilled } from '@ant-design/icons';
import type { DataNode } from 'antd/lib/tree';
import { queryByParentFolderId, getQueryPath } from '@/services/customplugin/folderApi';
import { queryByParentFolderId as queryByParentLogId } from '@/services/customplugin/api';
import { getFolderSeach } from '@/services/customplugin/folderApi';
import { history } from 'umi';
import './index.less';

import $i18n from '../../../i18n';
import {userFavoriteQueryAll } from '@/services/tenant/favoriteApi'

var multiGroupbyPlugins = ['multiGroupbyMultiValueSecond', 'multiGroupbyMultiValueMinute', 'dataset'];
const FileTree: React.FC<any> = forwardRef((props: any, ref) => {
  const { from, handleGetCheck, parentId, check, handleGetSelect, keyWord, handleClose, refss } = props;
  const [treeData, setTreeData] = useState([]);
  const [expandedKeys, setExpandedKeys] = useState([]);
  const [checkedKeys, setCheckedKeys] = useState([]);
  const [checkedNodes, setCheckedNodes] = useState([]);
  const [selectedKeys, setSelectedKeys] = useState([])
  const [loadKey, setLoadKey] = useState([]);
  const [favoriteData, setFavoriteData] = useState([]);

  useEffect(() => {
    initData()
  }, []);

  async function initData() {
    const initData = await queryAllNodes(-1, 'init');
    setTreeData(initData);
  }

  useEffect(() => {
    if (from === 'search' && keyWord) {
      getFolderSeach({ keyword: keyWord }).then((res: any) => {
        if (!(res?.[0]?.datas || []).length && !(res?.[1]?.datas || []).length) {
          message.info(
            $i18n.get({
              id: 'holoinsight.FolderView.FileTree.FilesOrFoldersWithThe',
              dm: '未搜索到当前关键字的文件或文件夹！',
            }),
          );
          return;
        }
        const result = _.concat(res?.[0]?.datas, res?.[1]?.datas); // results[0].length > 0 ? results[0] : results[1];
        if (result && result.length > 0) {
          backSearch(result)
        }
      });
    }
  }, [keyWord]);
  async function backSearch(result: any) {
    findAllParents(result).then(({ parentIds, pathesList }) => {
      queryAllChildren(parentIds, pathesList).then(res => {
        setTreeData(res.treeData);
        setExpandedKeys(res.expandedKeys);

      });
    });
  }
  function queryAllChildren(parentIds: any, pathesList: any) {
    const reqs: any = [];
    _.each(parentIds, async (parent) => {

      reqs.push(queryAllNodes(parent));
    });
    const dataMap: any = {};
    return Promise.all(reqs).then(results => {
      _.each(results, (res, idx) => {
        if (!dataMap?.[parentIds?.[idx]]) {
          dataMap[parentIds[idx]] = res;
        }
      });
      createTreeData(dataMap, treeData, pathesList);
      const expandedKeys: any = [];
      getExpandedKeys(treeData, expandedKeys);
      return {
        treeData,
        expandedKeys,
      };
    });
  }
  function createTreeData(map: any, treeData: any, pathesList: any) {
    _.each(pathesList, pathes => {
      if (_.isUndefined(pathes) || pathes === null || pathes.paths === null) return;
      addChildren(0, (pathes.paths || []).reverse(), treeData, map);
    });
  }
  function addChildren(index: number, pathes: any[], treeData: any, map: any) {
    for (let i = 0; i < treeData.length; i++) {
      const item = treeData[i];
      const path = pathes[index];
      if (path && item.id === path.id) {
        treeData[i].children = map[path.id];
        treeData[i].open = true;
        if (index + 1 < pathes.length) {
          addChildren(index + 1, pathes, treeData[i].children, map);
          break;
        }
      }
    }
  }
  function getExpandedKeys(treeData: any, expandedKeys: any) {
    _.each(treeData, node => {
      if (node.children && needOpen(node.children) && _.indexOf(expandedKeys, node.id) < 0) {
        expandedKeys.push(node.id);
        getExpandedKeys(node.children, expandedKeys);
      }
      if (multiGroupbyPlugins.indexOf(node.pluginType) > -1) {
        expandedKeys.push(node.id);
      }
    });
  }

  function needOpen(data: any): any {

    for (const item of data) {
      // 这里的item.name数据类型 string==>reactNode 取值需要往下取值
      if (Array.isArray(item.name.props.children)) {
        if (item.children && item.name.props.children[1].props.children.indexOf(keyWord) < 0) {
          return needOpen(item.children);
        }
        if (item.name.props.children[1].props.children.indexOf(keyWord) > -1) {
          return true;
        }
      } else {
        if (item.children && item.nameprops.children.indexOf(keyWord) < 0) {
          return needOpen(item.children);
        }
        if (item.name.props.children.indexOf(keyWord) > -1) {
          return true;
        }
      }
    }
    return false;
  }
  async function findAllParents(data: any) {
    const cmds: any = [];
    if (data) {
      _.each(data, v => {
        if (v.pluginType) {
          cmds.push({ customPluginId: v.id });
        } else {
          cmds.push({ folderId: v.id })
        }
      })
      return getQueryPath({
        requests: cmds
      }).then((pathesList: any) => {
        const parentIds: any = [];
        _.each(pathesList, (pathes, k) => {
          if (_.isUndefined(pathes) || pathes === null || pathes.paths === null) return;
          _.each(pathes.paths, (v, k) => {
            if (parentIds.indexOf(v.id) == -1) {
              parentIds.push(v.id);
            }
          });
        })
        return {
          parentIds,
          pathesList
        }
      })
    } else {
      return {
        parentIds: [],
        pathesList: []
      }
    }
  }
  async function queryAllNodes(id: number | string, type?: string, expandKeys?: string[]) {
    userFavoriteQueryAll().then((r:any) => {
      const favoriteDatas = (Array.isArray(r) ? r : []).map((ri: any) => {
        return ri.relateId - 0
      })
      setFavoriteData(favoriteDatas)
    })
    const res: any = await Promise.all([queryByParentFolderId(id), queryByParentLogId(id)])
    const [arr1, arr2] = res;
    
    const arrlenth = arr1.length;
    const arrAll = (Array.isArray(arr1) ? arr1 : [] ).concat(Array.isArray(arr2) ? arr2 : []).map((item: any, index: number) => {
      const strTitle = item.name;
      const key = keyWord ? strTitle.indexOf(keyWord) : -1;
      const beforeStr = strTitle.substring(0, key);
      const afterStr = strTitle.slice(key + (keyWord || '').length);
      const title =
        key > -1 ? (
          <span>
            {beforeStr}

            <span style={{ color: 'orange' }}>{keyWord}</span>

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
      item.key = item.id;
      item.title = title;
      item.isLeaf = isLeaf;
      item.name = title
      return item;
    });
    return arrAll;
  }
  // async function onLoadData(hasChild) {
  //   if (hasChild.children) {
  //     return;
  //   }
  //   const res = await Promise.all([
  //     queryByParentFolderId(hasChild.id),
  //     queryByParentLogId(hasChild.id),
  //   ]);
  //   const [arr1, arr2] = res;
  //   const arrlenth = arr1.length;
  //   const arrAll = arr1.concat(arr2).map((item, index) => {
  //     const type = index < arrlenth ? 'folder' : 'file';
  //     const icon = index < arrlenth ? <FolderOpenOutlined /> : <ProfileOutlined />;
  //     const isLeaf = index < arrlenth ? false : true;
  //     item.type = type;
  //     item.icon = icon;
  //     // item.key = `${hasChild.key}-${index}`;
  //     item.key = item.id;
  //     item.title = item.name;
  //     item.isLeaf = isLeaf;
  //     return item;
  //   });
  //   const newArr = updateTreeData(treeData, hasChild.key, arrAll);
  //   setTreeData(newArr);
  // }

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
    const backArr = getTreeItemNode(treeData, checkedKeysValue.checked);
    setCheckedKeys(checkedKeysValue)
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


      if (backArr.type === 'folder') {
        expandClose(e.node.key)
        history.push(`/log/${backArr.id}`);
        handleClose();
      } else {
        history.push(`/log/metric/${backArr.id}?parentId=${backArr.parentFolderId}`);
      }
    } else {
      handleGetSelect && handleGetSelect(backArr);
    }
  }
  const onExpand = async (e: any, n: any) => {
    const arr = await queryAllNodes(n.node.id);
    const newArr: any = updateTreeData(treeData, n.node.id, arr);
    setTreeData(newArr);
    expandClose(n.node.key);
  }
  const onLoad = (e: any, n: any) => {
    setLoadKey(e)
  }
  const remake = async () => {
    setLoadKey([])
    setExpandedKeys([])
    setCheckedKeys([])
    const initData = await queryAllNodes(-1, 'init');
    setTreeData(initData);
    // queryAllNodes(-1, 'init')
  }
  useImperativeHandle(refss, () => ({
    remakes: () => {
      remake()
    }
  }))


  return (
    <Tree
      showIcon
      // loadData={onLoadData}
      loadedKeys={loadKey}
      onLoad={onLoad}
      expandedKeys={expandedKeys}
      treeData={treeData}
      checkable={check}
      fieldNames={{
        key: 'id'
      }}
      onCheck={onCheck}
      checkStrictly={true}
      onSelect={onSelect}
      height={500}
      onExpand={onExpand}
      checkedKeys={checkedKeys}
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
