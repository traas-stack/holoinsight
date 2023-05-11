import React, { useState } from 'react';
import { Modal, Card, Steps, Row, Col, Alert, message } from 'antd';
import { TNewFolderProp } from '@/components/FolderView/index.d';
import { updateParentFolderId } from '@/services/customplugin/folderApi';
import { updateParentFolderId as updateFile } from '@/services/customplugin/api';
import FileTrees from '../FileTrees';

// id?: any;
// name: string;
// tenant?: string;
// parentFolderId: any;
// extInfo?: string;
import $i18n from '../../../i18n';
const { Step } = Steps;
const FileMoveModal: React.FC<any> = (props: TNewFolderProp) => {
  const { visible, handleClose, refesh, setMoveTo, readyMove, setReadymove,treeRefss } =
    props;
  const [current, setCurrent] = useState(0);
  // const [readyMove, setReadymove] = useState([]); //待移动
  const [moveTod, setMoveTod] = useState([]); //移动目标

  // function handleChangeStep(value: number) {
  //   setCurrent(value);
  // }
  function handleSubmit() {
    if (moveTod && readyMove.length) {
      const newId = moveTod.id;
      const requestArr = [];
      readyMove.forEach((item) => {
        if (item.type === 'folder') {
          requestArr.push(
            updateParentFolderId({
              id: item.id,
              parentFolderId: newId,
            }),
          );
        } else {
          requestArr.push(
            updateFile({
              id: item.id,
              parentFolderId: newId,
            }),
          );
        }
      });
      Promise.all(requestArr).then((res) => {
        message.success(
          $i18n.get({
            id: 'holoinsight.FolderView.FileMoveModal.MovedSuccessfully',
            dm: '移动成功',
          }),
        );

        handleClose();
        setMoveTo(false);
        setReadymove([]);
        treeRefss?.current?.remakes()
        refesh();
      });
    } else {
      message.error(
        $i18n.get({
          id: 'holoinsight.FolderView.FileMoveModal.FailedToMove',
          dm: '移动失败！',
        }),
      );
    }
  }
  function handleBack(e) {
    setMoveTo(false);
  }
  // function handleGetCheck(value) {
  //   setReadymove(value.flat(Infinity));
  // }
  function handleGetSelect(value) {
    setMoveTod(value);
  }
  // function commonInfo(info) {
  //   return info.map((item, index) => {
  //     return <p key={index}>{`名称：${item.name} 类型：${item.type}`}</p>
  //   });
  // }
  return (
    <Modal
      width={700}
      title={$i18n.get({
        id: 'holoinsight.FolderView.FileMoveModal.FileMovement',
        dm: '文件移动',
      })}
      open={visible}
      onOk={() => handleSubmit()}
      onCancel={(e) => handleBack(e)}
      okText={$i18n.get({
        id: 'holoinsight.FolderView.FileMoveModal.Confirm',
        dm: '确认',
      })}
      cancelText={$i18n.get({
        id: 'holoinsight.FolderView.FileMoveModal.Cancel',
        dm: '取消',
      })}
      destroyOnClose={true}
    >
      <Row style={{ display: current === 0 ? 'block' : 'none' }}>
        {/* <FileTree handleGetCheck={handleGetCheck} check={true} />
         */}
        <div style={{ display: 'flex' }}>
          <Card
            title={$i18n.get({
              id: 'holoinsight.FolderView.FileMoveModal.FolderToBeMoved',
              dm: '待移动文件夹',
            })}
            style={{ width: '300px' }}
            // style={{ marginBottom: 20 }}
          >
            <FileTrees
              readyMove={readyMove}
              setReadymove={setReadymove}
              check={false}
            />
          </Card>
          <Card
            title={$i18n.get({
              id: 'holoinsight.FolderView.FileMoveModal.DestinationFolder',
              dm: '目标文件夹',
            })}
            style={{ width: '300px' }}
          >
            <FileTrees
              readyMove={readyMove}
              TreeType={'moveSlectTree'}
              handleGetSelect={handleGetSelect}
              check={false}
            />
          </Card>
        </div>
      </Row>

      {/* <Row style={{ display: current === 1 ? 'block' : 'none' }}>
         <FileTrees handleGetSelect={handleGetSelect} check={false} />
        </Row> */}

      <Row style={{ display: current === 2 ? 'block' : 'none' }}>
        {/* <Card
           title={$i18n.get({
             id: 'holoinsight.FolderView.FileMoveModal.FolderToBeMoved',
             dm: '待移动文件夹',
           })}
           style={{ marginBottom: 20 }}
          >
           {readyMove.length ? (
             commonInfo(readyMove)
           ) : (
             <Alert
               message={$i18n.get({
                 id: 'holoinsight.FolderView.FileMoveModal.NoFilesOrFoldersTo',
                 dm: '未选择需要移动的文件或者文件夹',
               })}
               type="error"
             />
           )}
          </Card>
          <Card
           title={$i18n.get({
             id: 'holoinsight.FolderView.FileMoveModal.DestinationFolder',
             dm: '目标文件夹',
           })}
          >
           {moveTo.length ? (
             commonInfo(moveTo)
           ) : (
             <Alert
               message={$i18n.get({
                 id: 'holoinsight.FolderView.FileMoveModal.DestinationFolderNotSelectedTo',
                 dm: '未选择移动到的目标文件夹',
               })}
               type="error"
             />
           )}
          </Card> */}
      </Row>
    </Modal>
  );
};

export default FileMoveModal;
