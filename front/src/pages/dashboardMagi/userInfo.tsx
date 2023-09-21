import { useEffect, useState } from 'react'
import style from './index.less';
import {
  queryByParentFolderId as queryByParentLogId,
} from '@/services/customplugin/index';
import { useLocation, useParams, useSearchParams } from '@umijs/max';

export default function UserInfo() {
  const params = useParams()
  const location = useLocation()
  const isShow = location.pathname.includes("parentId") || location.pathname.includes("log/metric")
  const [searchParams] = useSearchParams();
  const [userInfo, setUserInfo] = useState<any>({})
  useEffect(() => {
    const getData = async () => {
      if (isShow) {
        let data = await queryByParentLogId(searchParams.get('parentId'));
        (Array.isArray(data) ? data : []).forEach((item) => {
          if (item.id === Number(params.id) && item.parentFolderId === Number(searchParams.get('parentId'))) {
            setUserInfo(item)
          }
        })
      }
    }
    getData()
  }, []);
  return (
    isShow &&
    (<div className={style['user-info']}>
      <span>创建人： {userInfo.creator}</span>
      <span>创建时间： {userInfo.gmtCreate}</span>
      <span>最后修改人： {userInfo.modifier}</span>
      <span>最后修改时间： {userInfo.gmtModified}</span>
    </div>)
  )
}
