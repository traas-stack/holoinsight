import LogComp from '@/pages/logRetrieval';
import { getQueryString } from '@/utils/help';
function LogRetrieval() {
  const app = getQueryString('app') || '';
  return <LogComp serverName={app} inServer/>;
}
export default LogRetrieval;
