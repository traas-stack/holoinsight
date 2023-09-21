import SecondPlugin from './SecondPlugin';
import MultiSecondPlugin from './MultiSecondPlugin';
import SingleMinutePlugin from './SingleMinutePlugin';
import MultiMinutePlugin from './MultiMinutePlugin';
import SpmPlugin from './SpmPlugin';
import MetricsPlugin from './MetricsPlugin';
import StatisTopnPlugin from './StatisTopnPlugin';
import TopnPlugin from './TopnPlugin';
import PatternMatchPlugin from './PatternMatchPlugin';
import MultiGroupbyMultiValueMinutePlugin from './MultiGroupbyMultiValueMinutePlugin';
import MultiGroupbyMultiValueSecondPlugin from './MultiGroupbyMultiValueSecondPlugin';
import DatasetPlugin from './DatasetPlugin';
import PurgeGroupbyPlugin from './PurgeGroupbyPlugin';
import RrdPlugin from './RrdPlugin';
import LegoPlugin from './LegoPlugin';

import StackPlugin from './StackPlugin';
import BizPlugin from './BizPlugin';

import CcmPlugin from './CrossAlarm';
import MarketPlugin from './MarketPlugin';

const PLUGINS = {
  second: SecondPlugin,
  multiSecond: MultiSecondPlugin,
  singleMinute: SingleMinutePlugin,
  multiMinute: MultiMinutePlugin,
  spm: SpmPlugin,
  metrics: MetricsPlugin,
  statisTopn: StatisTopnPlugin,
  topn: TopnPlugin,
  patternMatch: PatternMatchPlugin,
  purgeGroupby: PurgeGroupbyPlugin,
  multiGroupbyMultiValueMinute: MultiGroupbyMultiValueMinutePlugin,
  multiGroupbyMultiValueSecond: MultiGroupbyMultiValueSecondPlugin,
  rrd: RrdPlugin,
  lego: LegoPlugin,
  dataset: DatasetPlugin,

  stack: StackPlugin,
  biz: BizPlugin,
  crossCloudMarket: CcmPlugin,
  marketdata: MarketPlugin,
};

export default PLUGINS;
