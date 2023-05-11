import React, { useEffect } from 'react';
import { Modal, Form, Input, message } from 'antd';
import { TNewFolderProp } from '@/components/FolderView/index.d'
import { create, update } from '@/services/customplugin/folderApi';
import $i18n from '../../../i18n';

const CommonBreadcrumb: React.FC<any> = (props: TNewFolderProp) => {
  const [popForm] = Form.useForm();
  const { parentId, file, type, visible, handleClose, refesh } = props;
  useEffect(() => {
    if (type === 'edit') {
      popForm.setFieldsValue({
        name: file?.name,
      });
    }
  }, [visible]);
  function hanldeSubmit() {
    popForm
      .validateFields()
      .then((res) => {
        const requestWay = type === 'edit' ? update : create;
        let params = {
          name: res.name,
          parentFolderId: parentId,
        };

        if (type === 'edit') {
          params.id = file?.id;
        }
        requestWay(params).then((res) => {
          message.success(
            type === 'edit'
              ? $i18n.get({
                id: 'holoinsight.FolderView.NewFileModel.TheFolderHasBeenEdited',
                dm: '编辑文件夹成功！',
              })
              : $i18n.get({
                id: 'holoinsight.FolderView.NewFileModel.TheFolderHasBeenCreated',
                dm: '新建文件夹成功',
              }),
          );
          popForm.resetFields();
          handleClose(false);
          refesh();
        });
      })
      .catch((err) => {

      });
  }
  return (
    <Modal
      title={
        type === 'add'
          ? $i18n.get({ id: 'holoinsight.FolderView.NewFileModel.CreateAFolder', dm: '新建文件夹' })
          : $i18n.get({ id: 'holoinsight.FolderView.NewFileModel.EditFolder', dm: '编辑文件夹' })
      }
      open={visible}
      onOk={() => hanldeSubmit()}
      onCancel={() => {
        popForm.resetFields();
        handleClose(false);
      }}
      okText={$i18n.get({ id: 'holoinsight.FolderView.NewFileModel.Confirm', dm: '确认' })}
      cancelText={$i18n.get({ id: 'holoinsight.FolderView.NewFileModel.Cancel', dm: '取消' })}
    >
      <Form form={popForm}>
        <Form.Item
          name="name"
          label={$i18n.get({
            id: 'holoinsight.FolderView.NewFileModel.FolderName',
            dm: '文件夹名称',
          })}
          rules={[
            {
              required: true,
              message: $i18n.get({
                id: 'holoinsight.FolderView.NewFileModel.TheFolderNameIsRequired',
                dm: '文件夹名称为必填项',
              }),
            },
          ]}
        >
          <Input
            placeholder={$i18n.get({
              id: 'holoinsight.FolderView.NewFileModel.EnterAFolderName',
              dm: '请输入文件夹名称',
            })}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CommonBreadcrumb;
