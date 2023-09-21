import DataSource from '../MagiContainer/DataSource';
import EditWidget from '../MagiContainer/Widgets/edit';
import CopyWidget from '../MagiContainer/Widgets/CopyWidget';
import ElseWidget from '../MagiContainer/Widgets/ElseWidget';
import QueryVariable from '../MagiContainer/Variables/query';
import DSQueryVariable from '../MagiContainer/Variables/ds-query';
import SelectVariable from '../MagiContainer/Variables/SelectVariable';
import RadioVariable from '../MagiContainer/Variables/RadioVariable';
import RadioQueryVariable from '../MagiContainer/Variables/RadioQueryVariable';

const { HMagi } = window;
HMagi.Magi.bind('DataSourcePlugin')
  .to(DataSource)
  .inSingletonScope()
  .whenTargetNamed('default');
HMagi.Magi.bind('PanelWidget').to(EditWidget);
HMagi.Magi.bind('PanelWidget').to(CopyWidget);
HMagi.Magi.bind('PanelWidget').to(ElseWidget);
HMagi.Magi.bind('VariablePlugin').to(SelectVariable).whenTargetNamed('select');
HMagi.Magi.bind('VariablePlugin').to(RadioVariable).whenTargetNamed('radio');
HMagi.Magi.bind('VariablePlugin')
  .to(RadioQueryVariable)
  .whenTargetNamed('radioQuery');
HMagi.Magi.bind('VariablePlugin').to(QueryVariable).whenTargetNamed('query');
HMagi.Magi.bind('VariablePlugin')
  .to(DSQueryVariable)
  .whenTargetNamed('ds-query');

export const createDashboard = HMagi.createDashboard;
export const create = HMagi.create;
export const EditorEvents = HMagi?.MagiEditor?.EditorEvents;
export const DSMetricEditor = HMagi?.MagiEditor?.DSMetricEditor;
export const DashboardComponent = HMagi.DashboardComponent;
export const DASHBOARD_VERISON = HMagi.DASHBOARD_VERISON;
export const DashboardEvents = HMagi.DashboardEvents;
export const Dashboard = HMagi.Dashboard;
export const DashboardConfig = HMagi.DashboardConfig;
export const DashboardModel = HMagi.DashboardModel;
export const PanelEvents = HMagi.PanelEvents;
export const PanelModel = HMagi.PanelModel;
export const TimePicker = HMagi.holoComponent.TimePicker;
export const ProLayout = HMagi.holoComponent.ProLayout;
export const injectable = HMagi.injectable
export const context =  HMagi.holoComponentWay.context
export const request =  HMagi.holoComponentWay.request
export const timeFloor =  HMagi.holoComponentWay.timeFloor
export const universal =  HMagi.holoComponentWay.universal

 
export default HMagi.Magi;
