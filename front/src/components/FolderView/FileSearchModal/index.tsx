import React, { useState } from 'react';
import { Modal, Form, Input } from 'antd';
import { TNewFolderProp } from '@/components/FolderView/index.d';
import FileTree from '../FileTree';
import $i18n from '../../../i18n';

const FileSearchModal: React.FC<any> = (props: TNewFolderProp) => {
  const { visible, handleClose, parentId } = props;
  const [searchValue, setSearchValue] = useState('');
  function handleSearch(value: string) {
    setSearchValue(value);
  }
  return (
    <Modal
      width={700}
      title={$i18n.get({ id: 'holoinsight.FolderView.FileSearchModal.FileSearch', dm: '文件搜索' })}
      open={visible}
      onOk={() => handleClose(false)}
      onCancel={() => handleClose(false)}
      okText={$i18n.get({ id: 'holoinsight.FolderView.FileSearchModal.Confirm', dm: '确认' })}
      cancelText={$i18n.get({ id: 'holoinsight.FolderView.FileSearchModal.Cancel', dm: '取消' })}
    >
      <Input.Search
        placeholder={$i18n.get({
          id: 'holoinsight.FolderView.FileSearchModal.EnterAKeyword',
          dm: '请输入关键字',
        })}
        style={{ marginBottom: 20 }}
        onSearch={handleSearch}
      />

      <FileTree
        from="search"
        parentId={parentId}
        check={false}
        keyWord={searchValue}
        handleClose={handleClose}
      />
    </Modal>
  );
};

export default FileSearchModal;
