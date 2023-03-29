# coding=utf-8
import atexit
import datetime
import sched
import time

s = sched.scheduler(time.time, time.sleep)


def schedule_with_fixed_rate(init_delay, interval, action, args):
    def wrapper(*args2):
        action()
        now = datetime.datetime.now()
        next_align_time = datetime.datetime.now().replace(microsecond=0) + datetime.timedelta(seconds=interval)
        delta = next_align_time - now
        s.enter(delta.total_seconds(), 1, wrapper, args2)

    s.enter(init_delay, 1, wrapper, args)


log1 = open('1.log', mode='a')
atexit.register(log1.close)


def task1(*args):
    time_str = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    log1.write('%s level=%s biz=[biz1] cost=%d\n' % (time_str, 'INFO', 1))
    log1.write('%s level=%s biz=[biz2] cost=%d\n' % (time_str, 'INFO', 2))
    log1.write('%s level=%s cost=%d\n' % (time_str, 'INFO', 3))
    log1.write('%s level=%s cost=%d\n' % (time_str, 'WARN', 4))
    log1.write('%s level=%s cost=%d\n' % (time_str, 'ERROR', 5))
    log1.write('%s level=%s cost=%d\n' % (time_str, '错误', 5))
    log1.flush()


schedule_with_fixed_rate(0, 1, task1, ())

s.run()
