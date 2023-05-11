import React, { useState, useEffect } from 'react';
import { Space, Table, Button } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import styles from './index.less';

interface DataType {
  key: string;
  name: string;
  age: number;
}

type Tprops = {
    tableData: any 
    handleChangeShow: (status: boolean) => void;
    handleChangeStatus: (status: string) => void;
    handleEditItem: (record:any, key:number) => void;
    handleDeleteItem: (key:number) => void;
}


const LogComp: React.FC <any> = (props:Tprops ) => {
    const { tableData, handleChangeShow, handleChangeStatus, handleEditItem, handleDeleteItem } = props;
    const [data, setData] = useState(tableData || []);

    const columns: ColumnsType<DataType> = [
        {
          title: '日志名称',
          dataIndex: 'name',
          key: 'name'
        },
        {
          title: '操作',
          width: 200,
          key: 'action',
          render: (_, record,key) => {
               
              return (
                <Space size="middle">
                  <a onClick={() => handleEditItem(record,key)}>编辑</a>
                  <a onClick={() => handleDeleteItem(key)}>删除</a>
                </Space>
              )
          },
        },
    ];
    useEffect(()=>{
        if(tableData){
            setData(JSON.parse(JSON.stringify(tableData)))
        }
    },[JSON.stringify(tableData)])
    return (
        <div>
            <div className={styles.mg20}>
                <Button 
                style={{ width: '100%' }}
                onClick = {()=>{ 
                    handleChangeShow && handleChangeShow(true)
                    handleChangeStatus && handleChangeStatus('add');
                    }}>新增</Button>
            </div>
            <Table 
                pagination = {false}
                columns={columns} 
                dataSource={data} 
            />
        </div>
    )
}

export default LogComp;