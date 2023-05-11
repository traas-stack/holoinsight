import React, { useState, useEffect } from 'react';
import { Input, message } from 'antd';
import { EditOutlined } from '@ant-design/icons';
import { useParams } from '@umijs/max';
import { StarOutlined } from '@ant-design/icons';
import $i18n from '../../i18n';
import {
  userFavoriteCreate,
  userFavoriteQueryById,
  userFavoriteDeleteById,
} from '@/services/tenant/favoriteApi';

export default function Editable(props: {
  value?: string;
  size?: string;
  editable?: boolean;
  onChange: (value?: string) => void;
}) {
  const { editable = true, value, onChange, ...rest } = props;
  const params: any = useParams();
  const [isEditting, set] = React.useState(false);
  const [values, setValues] = useState<any>(value);
  const [isFavorite, setIsFavorite] = useState<any>(null);

  useEffect(() => {
    if (params && params.id) {
      getQueryStar();
    }
  }, []);

  async function getQueryStar() {
    const star = await userFavoriteQueryById('dashboard', params.id);
    setIsFavorite(star);
  }
  if (isEditting) {
    return (
      <Input
        style={{ width: 200 }}
        {...rest}
        autoFocus
        value={values}
        onChange={(e) => {
          setValues(e.target.value);
          onChange(e.target.value);
        }}
        onBlur={() => set(false)}
      />
    );
  }

  return (
    <>
      <span style={{ marginRight: 4 }}>{value}</span>
      {params.mode && params.mode !== 'create' ? (
        <StarOutlined
          style={{ color: isFavorite ? 'gold' : '#fff' }}
          type="star"
          onClick={() => {
            if (!isFavorite) {
              userFavoriteCreate({
                url: `/m/dashboard/preview/${params.id}`,
                type: 'dashboard',
                relateId: params.id,
                name: value,
              }).then((r) => {
                message.success(
                  $i18n.get({
                    id: 'holoinsight.FolderView.XfItem.CollectedSuccessfully',
                    dm: '收藏成功',
                  }),
                );
                setIsFavorite(true);
              });
            } else {
              userFavoriteDeleteById('dashboard', params.id).then((res) => {
                message.success(
                  $i18n.get({
                    id: 'holoinsight.pages.dashboard.Dashboard.TheCollectionHasBeenCanceled',
                    dm: '取消收藏成功',
                  }),
                );
                setIsFavorite(null);
              });
            }
          }}
        />
      ) : null}
      {editable && params.mode !== 'preview' ? (
        <>
          <EditOutlined
            style={{
              marginLeft: 8,
              fontSize: 12,
              fontWeight: 'normal',
              cursor: 'pointer',
              color:'#fff',
            }}
            onClick={() => set(true)}
          />
        </>
      ) : null}
    </>
  );
}
