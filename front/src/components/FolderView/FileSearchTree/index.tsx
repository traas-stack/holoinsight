import React, { useState } from 'react';
import { Button, Input } from 'antd';
import { TNewFolderProp } from '@/components/FolderView/index.d';
import FileTree from '../FileTree';
import FileMoveModal from '../FileMoveModal';
import $i18n from '../../../i18n';

const FileSearchModal: React.FC<any> = (props: TNewFolderProp) => {
  const {  handleClose, parentId, setVisible, refss, refesh } =
    props;
  const [searchValue, setSearchValue] = useState('');
  const [checkValue, setCheckValue] = useState(false);
  const [readyMove, setReadymove] = useState([]); //待移动
  const [moveTo, setMoveTo] = useState(false); //移动目标
  function handleSearch(value: string) {
    setSearchValue(value);
  }
  function handleGetCheck(value) {
    setReadymove(value.flat(Infinity));
  }

  return (
    <>
      <div style={{ display: 'flex' }}>
        <Input.Search
          placeholder={$i18n.get({
            id: 'holoinsight.FolderView.FileSearchModal.EnterAKeyword',
            dm: '请输入关键字',
          })}
          style={{ marginBottom: 20 }}
          onSearch={handleSearch}
        />

        {checkValue ? (
          <>
            <Button
              type="link"
              style={{ color: !readyMove.length ? 'gray' : '' }}
              disabled={!readyMove.length}
              onClick={() => {
                setMoveTo(true);
                // setPopType({
                //   type: 'moveModal',
                // });
                // setVisible(true);
                // setCheckValue(true)
              }}
            >
              {$i18n.get({
                id: 'holoinsight.FolderView.FileSearchTree.Move',
                dm: '移动到...',
              })}
            </Button>
            <Button
              type="link"
              style={{ color: '#3b7bd5', padding: '0px 0px' }}
              onClick={() => {
                // setPopType({
                //   type: 'moveModal',
                // });
                // setVisible(true);
                setCheckValue(false);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.FolderView.FileSearchTree.Cancel',
                dm: '取消',
              })}
            </Button>
          </>
        ) : (
          <>
            <Button
              type="link"
              style={{ color: '#3b7bd5' }}
              onClick={() => {
                // setPopType({
                //   type: 'moveModal',
                // });
                // setVisible(true);
                setCheckValue(true);
              }}
            >
              {$i18n.get({
                id: 'holoinsight.components.FolderView.BatchMove',
                dm: '批量移动',
              })}
            </Button>
          </>
        )}
      </div>

      <FileTree
        from="search"
        parentId={parentId}
        check={checkValue}
        keyWord={searchValue}
        handleClose={handleClose}
        refss={refss}
        selectTrigger={readyMove}
        handleGetCheck={handleGetCheck}
      />

      <FileMoveModal
        refesh={refesh}
        readyMove={readyMove}
        setReadymove={setReadymove}
        visible={moveTo}
        handleClose={handleClose}
        setMoveTo={setMoveTo}
        treeRefss={refss}
      />
    </>
  );
};

export default FileSearchModal;
