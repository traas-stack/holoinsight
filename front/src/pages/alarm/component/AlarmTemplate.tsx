import React, { useState, useEffect } from 'react';
import { Radio, Form, Col, Button, Select, Row, Card, Input, Tooltip } from 'antd';
import $i18n from '@/i18n';
import { queryAlarmKey } from '@/services/alarm/api';

const AlarmTemplate: React.FC<any> = (props)=>{
        const { templateShow } = props;
        const [show, setShow] = useState<boolean>(templateShow);
        const [selectData, setSelectData] = useState<any>([]);
        useEffect(()=>{
            queryAlarmKey().then((res)=>{
                 if(Array.isArray(res)){
                    setSelectData(res);
                 }
    
            })
        },[])
        useEffect(()=>{
            if(templateShow){
                setShow(templateShow)
            }
        },[templateShow])

        return (
            <>
                <Form.Item label="是否需要自定义钉钉告警模版">
                    <Radio.Group value = {show} onChange = {(e)=>{setShow(e.target.value)}}>
                        <Radio value={true}>
                            {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.Yes',
                                dm: '是',
                            })}
                        </Radio>
    
                        <Radio value={false}>
                            {$i18n.get({
                                id: 'holoinsight.pages.alarm.Detail.No',
                                dm: '否',
                            })}
                        </Radio>
                    </Radio.Group>
                </Form.Item>
                {
                    show ?
                        <Form.List name="dingtalkTemplate" initialValue={[{}]}>
                            {(fields, { add: addItem, remove: removeItem }) => {
                                return (
                                    <Card title = "钉钉模版配置">
                                        {
                                            fields.map((fieldAll: any, fieldIndex: any) => {
                                                return (
                                                    <Row key={fieldAll.name}>
                                                        <Col span={3}>
                                                            <Form.Item
                                                                labelCol={{ span: 2, offset: 1 }}
                                                                name={[fieldAll.name, 'key']}
                                                                rules={[
                                                                    {
                                                                        required: true,
                                                                        message: $i18n.get({
                                                                            id: 'holoinsight.pages.alarm.Detail.Required',
                                                                            dm: '必填项',
                                                                        }),
                                                                    },
                                                                ]}
                                                            >
                                                                <Input
                                                                    placeholder= "请输入模版字段名称"
                                                                />
                                                            </Form.Item>
                                                        </Col>
    
                                                        <Col span={3}>
                                                            <Form.Item
                                                                labelCol={{ span: 2, offset: 1 }}
                                                                name={[fieldAll.name, 'value']}
                                                                rules={[
                                                                    {
                                                                        required: true,
                                                                        message: $i18n.get({
                                                                            id: 'holoinsight.pages.alarm.Detail.Required',
                                                                            dm: '必填项',
                                                                        }),
                                                                    },
                                                                ]}
    
                                                            >
                                                                <Select
                                                                    placeholder="请选择告警模版key"
                                                                    allowClear
                                                                >
                                                                    {
                                                                        selectData.map((v:any) => (
                                                                            <Select.Option value={`${v.fieldName}`} key={v.fieldName}>
                                                                              <Tooltip placement="left" title={v.describe} >
                                                                                    {
                                                                                        v.fieldName
                                                                                    }
                                                                              </Tooltip>
                                                                            </Select.Option>
                                                                        ))
                                                                    }
                                                                </Select>
                                                            </Form.Item>
                                                        </Col>
                                                        <Col>
                                                        <Button
                                                            type="dashed"
                                                            block
                                                            onClick={() => {
                                                                addItem();
                                                            }}
                                                        >
                                                            新增
                                                        </Button>
                                                        </Col>
                                                        <Col>
                                                        {
                                                            fieldIndex > 0 ? <Button
                                                            type="dashed"
                                                            block
                                                            onClick={() => {
                                                                removeItem(fieldAll.name)
                                                            }}
                                                        >
                                                            删除
                                                        </Button> : null
                                                        }
                                                        </Col>
                                                       
                                                    </Row>
                                                );
                                            })
                                        }
    
                                    </Card>
                                )
                            }}
                        </Form.List>
                        : null
                }
            </>
        )
    
    }
    




export default AlarmTemplate;

