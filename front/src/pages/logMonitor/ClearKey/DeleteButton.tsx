import React from 'react'
import { Col, Row, Button, Popconfirm } from 'antd';
import { deleteTagsApi } from '@/services/alarm/api';

export default function DeleteButton({ selectArr, datasources, updateFun, fastSelect }) {

    const deleteSelect = async (type: string) => {
        let newSelectArr = JSON.parse(JSON.stringify(selectArr))
        newSelectArr.forEach(item => {
            if (Object.keys(item).indexOf("key") != -1) {
                delete item['key']
            }
        });
        if (type != 'all') {
            const data = await deleteTagsApi(datasources, false, newSelectArr)
            updateFun()
        } else {
            const data = await deleteTagsApi(datasources, true)
            updateFun()
        }
    }

    return (
        <>
            <Row justify={'end'} gutter={10}>
                <Col >
                    <Button onClick={() => fastSelect('all')}>
                        全选
                    </Button>
                </Col>
                <Col >
                    <Button onClick={() => fastSelect('allNo')}>
                        全不选
                    </Button>
                </Col>
                <Col  >
                    <Button onClick={() => fastSelect('reverse')}>
                        反选
                    </Button>
                </Col>
                <Popconfirm
                    placement="top"
                    title={`确认删除一共${selectArr.length}条数据？`}
                    onConfirm={() => deleteSelect('any')}
                    okText="是"
                    cancelText="否"
                >
                    <Button type="primary" danger >
                        删除所选key
                    </Button>
                </Popconfirm>
                <Col >
                </Col>
                <Col >
                    <Popconfirm
                        placement="top"
                        title={`确认删除所有数据？`}
                        onConfirm={() => deleteSelect('all')}
                        okText="是"
                        cancelText="否"
                    >
                        <Button type="primary" danger >
                            删除所有key
                        </Button>
                    </Popconfirm>
                </Col>
            </Row></>
    )
}
