import Magi from '@/Magi'
import DataSource from './DataSource';

Magi.bind('DataSourcePlugin')
  .to(DataSource)
  .inSingletonScope()
  .whenTargetNamed('default');

export default Magi;
