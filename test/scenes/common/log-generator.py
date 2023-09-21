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


log1 = logwriter.FileWrapper('test/1.log', 100)
atexit.register(log1.close)

# for multiline test
multiline_log = logwriter.FileWrapper('test/multiline.log', 100)
atexit.register(multiline_log.close)

# for loganalysis test
loganalysis_log = logwriter.FileWrapper('test/loganalysis.log', 100)
atexit.register(loganalysis_log.close)


def write_1_log(*args):
    time_str = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    log1.write('%s level=%s biz=[biz1] cost=%d\n' % (time_str, 'INFO', 1))
    log1.write('%s level=%s biz=[biz2] cost=%d\n' % (time_str, 'INFO', 2))
    log1.write('%s level=%s cost=%d\n' % (time_str, 'INFO', 3))
    log1.write('%s level=%s cost=%d\n' % (time_str, 'WARN', 4))
    log1.write('%s level=%s cost=%d\n' % (time_str, 'ERROR', 5))
    log1.write('%s level=%s cost=%d\n' % (time_str, '错误', 5))
    log1.flush()


def write_multiline_log(*args):
    time_str = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    text = '''%s ERROR thread=[thread-%d] i.h.s.a.r.s.CacheUpdateTimer - Cache update failure.
java.lang.RuntimeException: [holoinsight-network_address_mapping] ElasticsearchStatusException[Elasticsearch exception [type=index_not_found_exception, reason=no such index [holoinsight-network_address_mapping]]]
	at io.holoinsight.server.apm.receiver.scheduler.CacheUpdateTimer.lambda$init$0(CacheUpdateTimer.java:39) ~[apm-receiver-1.0.0-SNAPSHOT.jar!/:1.0.0-SNAPSHOT]
	at io.holoinsight.server.apm.receiver.scheduler.RunnableWithExceptionProtection.run(RunnableWithExceptionProtection.java:18) ~[apm-receiver-1.0.0-SNAPSHOT.jar!/:1.0.0-SNAPSHOT]
	at java.lang.Thread.run(Thread.java:750) ~[?:1.8.0_362]
Caused by: org.elasticsearch.ElasticsearchStatusException: Elasticsearch exception [type=index_not_found_exception, reason=no such index [holoinsight-network_address_mapping]]
	at org.elasticsearch.rest.BytesRestResponse.errorFromXContent(BytesRestResponse.java:177) ~[elasticsearch-7.8.0.jar!/:7.8.0]
	at org.elasticsearch.client.RestHighLevelClient.parseEntity(RestHighLevelClient.java:1897) ~[elasticsearch-rest-high-level-client-7.8.0.jar!/:7.8.0]
	at io.holoinsight.server.apm.server.service.impl.NetworkAddressMappingServiceImpl.loadByTime(NetworkAddressMappingServiceImpl.java:25) ~[apm-service-1.0.0-SNAPSHOT.jar!/:1.0.0-SNAPSHOT]
	at io.holoinsight.server.apm.receiver.scheduler.CacheUpdateTimer.updateNetAddressAliasCache(CacheUpdateTimer.java:60) ~[apm-receiver-1.0.0-SNAPSHOT.jar!/:1.0.0-SNAPSHOT]
	at io.holoinsight.server.apm.receiver.scheduler.CacheUpdateTimer.lambda$init$0(CacheUpdateTimer.java:37) ~[apm-receiver-1.0.0-SNAPSHOT.jar!/:1.0.0-SNAPSHOT]
	... 8 more
	Suppressed: org.elasticsearch.client.ResponseException: method [POST], host [http://es:9200], URI [/holoinsight-network_address_mapping/_search?typed_keys=true&max_concurrent_shard_requests=5&ignore_unavailable=false&expand_wildcards=open&allow_no_indices=true&ignore_throttled=true&search_type=query_then_fetch&batched_reduce_size=512&ccs_minimize_roundtrips=true], status line [HTTP/1.1 404 Not Found]
Warnings: [Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.16/security-minimal-setup.html to enable security., [ignore_throttled] parameter is deprecated because frozen indices have been deprecated. Consider cold or frozen tiers in place of frozen indices.]
{"error":{"root_cause":[{"type":"index_not_found_exception","reason":"no such index [holoinsight-network_address_mapping]","resource.type":"index_or_alias","resource.id":"holoinsight-network_address_mapping","index_uuid":"_na_","index":"holoinsight-network_address_mapping"}],"type":"index_not_found_exception","reason":"no such index [holoinsight-network_address_mapping]","resource.type":"index_or_alias","resource.id":"holoinsight-network_address_mapping","index_uuid":"_na_","index":"holoinsight-network_address_mapping"},"status":404}
		at org.elasticsearch.client.RestClient.convertResponse(RestClient.java:283) ~[elasticsearch-rest-client-7.8.0.jar!/:7.8.0]
		at java.lang.Thread.run(Thread.java:750) ~[?:1.8.0_362]
'''
    multiline_log.write(text % (time_str, 0))
    multiline_log.write(text % (time_str, 1))

    text = '''%s ERROR thread=[thread-%d] i.h.s.a.r.s.CacheUpdateTimer - Other exception.
java.lang.RuntimeException: [holoinsight-network_address_mapping] ElasticsearchStatusException[Elasticsearch exception [type=other ... ]
	at io.holoinsight.server.apm.receiver.scheduler.CacheUpdateTimer.lambda$init$0(CacheUpdateTimer.java:39) ~[apm-receiver-1.0.0-SNAPSHOT.jar!/:1.0.0-SNAPSHOT]
	at io.holoinsight.server.apm.receiver.scheduler.RunnableWithExceptionProtection.run(RunnableWithExceptionProtection.java:18) ~[apm-receiver-1.0.0-SNAPSHOT.jar!/:1.0.0-SNAPSHOT]
	at java.lang.Thread.run(Thread.java:750) ~[?:1.8.0_362]
'''
    multiline_log.write(text % (time_str, 1))
    multiline_log.flush()


def write_loganalysis_1(*args):
    time_str = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    # style 1 log
    loganalysis_log.write('%s [ERROR] [DefaultCluster-Ping-1] c.a.g.s.c.c.v.i.FooCluster - fail to ping 1.1.1.1:8080\n' % (time_str,))
    loganalysis_log.write('%s [ERROR] [DefaultCluster-Ping-1] c.a.g.s.c.c.v.i.FooCluster - fail to ping 2.2.2.2:8080\n' % (time_str,))

    # style 2 log
    loganalysis_log.write('%s agent=[3.3.3.3] add=[1] del=[2] template=[2] result=[true]\n' % (time_str,))
    loganalysis_log.write('%s agent=[4.4.4.4] add=[1] del=[0] template=[1] result=[true]\n' % (time_str,))

    # style 3 log
    loganalysis_log.write('%s INFO [TemplateMaintainer-1] foo.TemplateMaintainer - template [1] cancel maintenance\n' % (time_str,))
    loganalysis_log.write('%s INFO [TemplateMaintainer-1] foo.TemplateMaintainer - template [2] cancel maintenance\n' % (time_str,))

    loganalysis_log.flush()


schedule_with_fixed_rate(0, 1, write_1_log, ())
schedule_with_fixed_rate(1, 5, write_multiline_log, ())
schedule_with_fixed_rate(0, 1, write_loganalysis_1, ())

s.run()
