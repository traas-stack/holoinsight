# coding=utf-8
import atexit
import datetime
import sched
import time

import logwriter

s = sched.scheduler(time.time, time.sleep)


def schedule_with_fixed_rate(init_delay, interval, action, args):
    def wrapper(*args2):
        action()
        now = datetime.datetime.now()
        next_align_time = datetime.datetime.now().replace(microsecond=0) + datetime.timedelta(seconds=interval)
        delta = next_align_time - now
        s.enter(delta.total_seconds(), 1, wrapper, args2)

    s.enter(init_delay, 1, wrapper, args)


increase_value_log = logwriter.FileWrapper('test/fine-tune-sample.log')
atexit.register(increase_value_log.close)

start = int(time.time())


def task1(*args):
    time_str = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    increase_value_log.write('%s level=%s news=[体育新闻] entrance=[推荐] uv=100\n' % (time_str, 'INFO'))
    increase_value_log.write('%s level=%s news=[体育新闻] entrance=[搜索] uv=10\n' % (time_str, 'INFO'))
    increase_value_log.write('%s level=%s news=[财经] entrance=[推荐] uv=10\n' % (time_str, 'INFO'))
    increase_value_log.write('%s level=%s news=[财经] entrance=[搜索] uv=100\n' % (time_str, 'INFO'))
    increase_value_log.flush()


schedule_with_fixed_rate(0, 1, task1, ())

s.run()
