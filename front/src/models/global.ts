
import { useState } from 'react';



const globalChangeCollapse  = () => {
  const [collapse, setCollapse] = useState<Boolean>(false);
  return {
    collapse,
     setCollapse
  };
};

export default globalChangeCollapse;
