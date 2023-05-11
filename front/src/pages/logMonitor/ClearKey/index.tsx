import { Checkbox, Card, Col, Row, Input, Form } from 'antd';
import React, { useState } from 'react';
import styles from './index.less';
import { Content } from './Content';

const { Search } = Input;
export const ClearKey = ({ tagsNameArr }) => {
    const [activeTabKey, setActiveTabKey] = useState(0);
    const [searchValue, setSearchValue] = useState('');
    const [value, setValue] = useState('');
    const [checked, setChecked] = useState(false);
    const [zeroNum, setZeroNum] = useState(0);
    const [fresh, setFresh] = useState(false)
    const tabList = tagsNameArr.map((item: string, index: number) => {
        return {
            key: index,
            tab: item,
        }
    })

    const onTabChange = (key: string) => {
        setActiveTabKey(key);
        updateFun()
    };
    const checkboxChange = (e) => {
        setChecked(e.target.checked)
    }
    const onSearchFun = (value) => {
        setValue(value)
    }
    const searchChangeFun = (e) => {
        setSearchValue(e.target.value)
    }
    const setZeroNumFun = (num) => {
        setZeroNum(num)
    }
    const updateFun = () => {
        setSearchValue('')
        setValue('')
        setChecked(false)
        setFresh(!fresh)
    }

    const p = {
        activeTabName: tagsNameArr[activeTabKey],
        value: value,
        checked: checked,
        setZeroNumFun: setZeroNumFun,
        updateFun: updateFun,
        fresh: fresh
    }
    return (
        <>
            <Card tabList={tabList} onTabChange={onTabChange}>

                <Row>
                    <div>key删除建议：</div>
                    <Col span={6}>  <Checkbox checked={checked} onChange={checkboxChange}>近60分钟数据都为0{`(${zeroNum}条)`}</Checkbox></Col>
                </Row>

                <div className={styles.search} >
                    <span>搜索Key(支持正则)：</span>
                    <Search onChange={searchChangeFun} value={searchValue} onSearch={onSearchFun}
                        style={{ width: 200 }}
                    />
                </div>
                <Content {...p} />
            </Card>
        </>
    )
}
