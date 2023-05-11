import MiniCard from '../MiniCard';
import styles from './index.less';

type TabContainerTProps = {
  data: any;
  getMoreMenu: any;
};
const TabContainer = (props: TabContainerTProps) => {
  const { data, getMoreMenu } = props;
  return (
    <div className={styles.body}>
      <div className={styles.favList}>
        {Array.isArray(data)
          ? data.map((item: any) => {
              return (
                <MiniCard
                  data={item}
                  getMoreMenu={getMoreMenu}
                  key={item.name}
                ></MiniCard>
              );
            })
          : null}
      </div>
    </div>
  );
};

export default TabContainer;
