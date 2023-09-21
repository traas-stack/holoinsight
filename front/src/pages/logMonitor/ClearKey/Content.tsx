import React, { useEffect, useState } from 'react';
import { Table, } from 'antd';
import { queryAllApi, queryTagsApi } from '@/services/alarm/api';
import DeleteButton from './DeleteButton';

export const Content = ({ activeTabName, value, checked, setZeroNumFun, updateFun, fresh }) => {
    const [columns, setColumns] = useState([])
    const [dataSourceBeforeFilter, setDataSourceBeforeFilter] = useState([])
    const [dataSource, setDataSource] = useState([])
    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
    const [selectArr, setSelectArr] = useState([])
    const [queryZeroValue, setQueryZeroValue] = useState([])
    let timeStamp = Date.now()
    const datasources = [{
        start: timeStamp - 60 * 60 * 1000,
        end: timeStamp,
        metric: activeTabName,
    }]


    const handleDateFun = (results:any, isTags:any) => {
        if ((Array.isArray(results) ? results : []).length) {
            let columnsArr:any = []
            let dataSourceArr = results.map((item:any, index:any) => {
                Object.keys(item.tags).forEach((item) => {
                    if (columnsArr.indexOf(item) === -1) {
                        columnsArr.push(item)
                    }
                })
                if (isTags) {
                    return { ...item.tags, key: index }
                } else {
                    if (item.values.length === 0) {
                        return { ...item.tags }
                    }
                }
            });
            let columnsDate = columnsArr.map((item:any) => {
                return {
                    title: item,
                    key: item,
                    dataIndex: item
                }
            })
            if (isTags) {
                setColumns(columnsDate)
                setDataSource(dataSourceArr)
                setDataSourceBeforeFilter(dataSourceArr)
            } else {
                let newArr = dataSourceArr.filter(Boolean)
                newArr.forEach((item:any, index:any) => {
                    item['key'] = index
                })
                setQueryZeroValue(newArr)
                setZeroNumFun(newArr.length)
            }
        } else {
            setDataSource([])
        }

    }
    useEffect(() => {
        if (activeTabName) {
            const queryQuest = async () => {
                const data:any = await queryTagsApi({ datasources })
                handleDateFun(data.results, true)
            }
            const queryQuestAll = async () => {
                const data:any = await queryAllApi({ datasources })
                handleDateFun(data.results, false)
            }
            queryQuest()
            queryQuestAll()
            setSelectedRowKeys([])
        }
    }, [activeTabName, fresh])
    const onSelectChange = (newSelectedRowKeys: React.Key[]) => {
        setSelectedRowKeys(newSelectedRowKeys);
        let selectData = newSelectedRowKeys.map((item) => {
            return dataSource[item]
        })
        setSelectArr(selectData)
    };

    const rowSelection = {
        selectedRowKeys,
        onChange: onSelectChange,
    };


    const fastSelect = (type: string) => {
        let newSelectedRowKeys = dataSource.map((item) => item['key'])
        let isZero = false
        switch (type) {
            case 'all':
                setSelectedRowKeys(newSelectedRowKeys);
                setSelectArr(dataSource)
                break;
            case 'allNo':
                setSelectedRowKeys([]);
                setSelectArr([])
                break;
            case 'reverse':
                newSelectedRowKeys = newSelectedRowKeys.filter((item) => {
                    if (selectedRowKeys.indexOf(item) === -1) {
                        if (item != 0) {
                            return true
                        }
                        isZero = true
                        return false
                    }
                })
                isZero ? newSelectedRowKeys.unshift(0) : null
                setSelectedRowKeys(newSelectedRowKeys);
                let selectData = newSelectedRowKeys.map((item) => {
                    return dataSource[item]
                })
                setSelectArr(selectData)
                break;

            default:
                break;
        }
    }

    useEffect(() => {
        if (value) {
            let reg = new RegExp(value)
            let afterFilterArr = dataSourceBeforeFilter.filter((item) => {
                for (let key in item) {
                    if (reg.test(item[key])) {
                        return true
                    }
                }
                return false
            })
            afterFilterArr.forEach((item:any, index) => {
                item['key'] = index
            })
            setDataSource(afterFilterArr)
        } else {
            updateFun()
        }
    }, [value])

    useEffect(() => {
        if (checked) {
            setDataSource(queryZeroValue)
        } else {
            updateFun()
        }
    }, [checked])

    return (
        <>
            <Table rowSelection={rowSelection} columns={columns} dataSource={dataSource} />
            <DeleteButton selectArr={selectArr} datasources={datasources[0]} updateFun={updateFun} fastSelect={fastSelect} />
        </>
    )
}
