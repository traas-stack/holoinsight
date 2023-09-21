import { DAY, HOUR, MINUTE, SECOND } from './constants';

export function getVars(source: any) {
  const vars: Record<string, any> = {};
  const keys = Object.keys(source);
  keys.forEach((item) => {
    if (item.startsWith('var-')) {
      vars[item.slice(4)] = source[item];
    }
  });
  return vars;
}

export function isRelativeTime(timeStr: string = '') {
  return (
    /^(\d+(hour|minute|day|month)),(now|\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})(,\d+(hour|minute|day|month))?$/.test(
      timeStr,
    ) ||
    /^midnight,(now|\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})$/.test(timeStr) ||
    /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},now$/.test(timeStr)
  );
}

export function getGap(source: string): number {
  const G_MAP = {
    m: MINUTE,
    h: HOUR,
    d: DAY,
    s: SECOND,
  };
  const [num, type] = source.split(/(?=(?:m|h|d|s))/);
  return Number(num) * G_MAP[type as keyof typeof G_MAP];
}
