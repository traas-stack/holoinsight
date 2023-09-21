export const SECOND = 1e3;
export const MINUTE = 60 * SECOND;
export const HOUR = 60 * MINUTE;
export const DAY = 24 * HOUR;
export const WEEK = 7 * DAY;

export enum TYPES {
  log = 'LOG_MONITOR',
  prometheus = 'PROMETHEUS',
  cloud = 'CLOUD_MONITOR',
}
