import React from 'react';
import styles from './index.less';
import logo from '../../assets/logo.svg'

const Logo: React.FC = () => {
  return (
    <div className={styles.logo}>
    <img src={logo} alt="" />
    </div>
  );
};

export default Logo;
