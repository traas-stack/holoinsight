import React from 'react'
import { Breadcrumb } from 'antd';
import { breadProps, urlList } from './index.d'
import styles from './index.less'
import { history } from 'umi';

const CommonBreadcrumb: React.FC<any> = (props: breadProps) => {
    const { urlList, to } = props;
    const handleChangePage = (item: urlList, index: number) => {
        index !== urlList.length - 1 && history.push(item.url);
    }
    return (
        <div className={styles.commonBread}>
            <Breadcrumb>
                {
                    urlList.map((item, index) => {
                        return <Breadcrumb.Item key={index}>
                            <a onClick={() => { handleChangePage(item, index) }} key={index}>{item.name}</a>
                        </Breadcrumb.Item>
                    })
                }
            </Breadcrumb >
        </div >
    );
}

export default CommonBreadcrumb;