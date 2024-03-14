/// *
// * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
// */
//
// package io.holoinsight.server.home.task.eventengine.event;
//
// import com.google.common.collect.HashMultimap;
// import com.google.common.collect.Multimap;
// import io.holoinsight.server.common.AddressUtil;
// import io.holoinsight.server.home.biz.service.TimedEventService;
// import io.holoinsight.server.home.common.util.MonitorException;
// import io.holoinsight.server.home.dal.model.TimedEvent;
// import io.holoinsight.server.home.task.eventengine.EventMetricsMonitorDaemon;
// import io.holoinsight.server.home.task.eventengine.broker.EventBrokerGroupKeeper;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.InitializingBean;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.TransactionStatus;
// import org.springframework.transaction.support.TransactionCallback;
// import org.springframework.transaction.support.TransactionTemplate;
//
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;
// import java.util.Timer;
// import java.util.concurrent.TimeUnit;
//
/// **
// * @author jsy1001de
// * @version 1.0: DelayEventServiceImpl.java, Date: 2024-03-14 Time: 14:16
// */
// @Slf4j
// @Service
// public class DelayEventServiceImpl implements DelayEventService, InitializingBean {
//
// /**
// * 当前event broker所对应的本地地址
// */
// private String localhostAddress = AddressUtil.getHostAddress();
//
// /**
// * topic与订阅者的对应关系
// */
// private final Multimap<String/* topic */, EventSubscriber> subscriberMap = HashMultimap
// .create();
//
// /**
// * 业务定时器，用于处理各类定时事件
// * <br><br/>
// * 注：每个单例的event service分配一个timer，其相关的daemon，服务等共享这一个event timer，不能随意创建
// */
// private final EventTimer eventTimer = new EventTimerProxyImpl();
//
// /**
// * broker自身使用的timer，用于orphan events scan, metrics scan等功能，不参与业务定时功能
// */
// private Timer timer;
//
// /**
// * broker集群管理对象，用于自我注册，节点感知，节点选择等功能
// */
// private EventBrokerGroupKeeper eventBrokerGroupKeeper;
//
// /**
// * 事件运行时监控线程
// */
// private EventMetricsMonitorDaemon eventMetricsMonitorDaemon;
//
// /**
// * 持久化定时事件的数据库访问事务模版
// */
// private TransactionTemplate transactionTemplate;
//
// @Autowired
// private TimedEventService timedEventService;
//
//
// @Override
// public void subscribe(String eventTopic, EventSubscriber subscriber) {
// if (null != eventTopic && null != subscriber) {
// subscriberMap.put(eventTopic, subscriber);
// } else {
// String exceptionMsg = "Invalid subscription: event topic:" + eventTopic
// + ", subscriber:" + subscriber;
// log.warn(exceptionMsg);
//
// throw new MonitorException(exceptionMsg);
// }
// }
//
// @Override
// public void unsubscribe(String eventTopic, EventSubscriber subscriber) {
// if (null != eventTopic && null != subscriber) {
// subscriberMap.remove(eventTopic, subscriber);
// } else {
// String exceptionMsg = "Invalid unsubscription: event topic:" + eventTopic
// + ", subscriber:" + subscriber;
// log.warn(exceptionMsg);
//
// throw new MonitorException(exceptionMsg);
// }
// }
//
// @Override
// public void publish(EventData eventData) {
// throw new UnsupportedOperationException();
// }
//
// @Override
// public void schedule(TimedEventData event) {
// schedule(event, true);
// }
//
// @Override
// public void cleanUpScheduler(TimeUnit timeUnit) {
// eventTimer.cleanEvents(timeUnit);
// }
//
// @Override
// public void afterPropertiesSet() throws Exception {
// log.info("Loading ward events...");
// loadWardEvents();
//
// log.info("Register event service...");
// eventBrokerGroupKeeper.register();
//
// log.info("Start orphan events scan thread...");
// orphanEventsScanDaemon = new OrphanEventsScanDaemon(localhostAddress, timer,
// eventBrokerGroupKeeper, eventGroupObservable, this, cloudProvisionTimedEventDAO);
// orphanEventsScanDaemon.start();
//
// eventMetricsMonitorDaemon = new EventMetricsMonitorDaemon(timer, eventTimer);
// eventMetricsMonitorDaemon.start();
// }
//
//
// private void loadWardEvents() {
// List<TimedEvent> wardEventDos = timedEventService.selectPendingWardEvents(localhostAddress);
// List<TimedEventData> wardEvents = TimedEventConverter.Dos2Bos(wardEventDos);
//
// for (TimedEventData timedEventData : wardEvents) {
// try {
// if (timedEventData.isExpired()) {
// // 重新分配超时时间
// timedEventData.extendTimeout(true);
// }
// schedule(timedEventData, true);
// } catch (Exception e) {
// log.warn("Unable to schedule event " + timedEventData);
// }
// }
// }
//
// private void schedule(final TimedEventData event, final boolean assignToLocalBroker) {
// if (null != event && event.isValid()) {
//
// boolean assignToLocal = assignToLocalBroker;
//
// if (event.shouldPersist()) {
// // 持久化定时事件
// assignToLocal = Boolean.TRUE.equals(transactionTemplate.execute(new
/// TransactionCallback<Boolean>() {
// @Override
// public Boolean doInTransaction(TransactionStatus status) {
//
// if (null == event.getId()) {
// // 持久化定时事件
// event.setStatus(EventStatusEnum.NEW);
// event.setCreatedAt(new Date());
// event.setModifiedAt(new Date());
// TimedEvent timedEvent = TimedEventConverter
// .Dto2Do(event);
// timedEventService.save(timedEvent);
// } else {
// timedEventService.seletForUpdate(event.getId());
// }
//
// String guardianServer = assignToLocalBroker ? localhostAddress
// : eventBrokerGroupKeeper.choseProperMember(String.valueOf(event.getId()));
// boolean assignToLocal = (guardianServer.equals(localhostAddress));
//
// event.setGuardianServer(guardianServer);
// TimedEvent timedEvent = TimedEventConverter
// .Dto2Do(event);
// timedEventService.updateById(timedEvent);
//
// return assignToLocal;
// }
// }));
// }
//
// if (assignToLocal) {
// log.info("Attach new event:" + event);
// // 如果定时事件分配当前broker，加入定时器
// eventTimer.attach(event, event.getTimeoutMills(), new TimedEventCallback() {
// @Override
// public void callback(TimedEventData timedEventData) {
// log.info("Fire event:" + event);
// fireEvent(event);
// }
// });
// }
//
// } else {
// String exceptionMsg = "Invalid timed event: event topic:" + event;
// log.warn(exceptionMsg);
//
// throw new MonitorException(exceptionMsg);
// }
// }
//
// private void fireEvent(final TimedEventData timedEventData) {
//
// final List<EventSubscriber> subscribers = new
/// ArrayList<>(subscriberMap.get(timedEventData.getTopic()));
//
// Boolean shouldReschedule = (Boolean) transactionTemplate
// .execute(new TransactionCallback<Object>() {
// @Override
// public Object doInTransaction(TransactionStatus transactionStatus) {
// Boolean shouldReschedule = false;
//
// // 对于持久化定时事件，判断事件是否已被再分配。即当前broker出现了长时间未响应或网络故障的情况而离开broker分组
// if (null != timedEventData.getId()) {
// TimedEvent timedEvent = timedEventService.seletForUpdate(timedEventData.getId());
//
// // 如果事件已被重新分配，则放弃事件触发
// if (!timedEventData.getGuardianServer().equals(timedEvent.getGuardianServer())) {
// log.warn(
// "Event has been reassigned to another event service instance. "
// + "Cancel event notify. Event details: " + timedEventData);
//
// return shouldReschedule;
// }
// }
//
// try {
// // 通知订阅者
// for (EventSubscriber subscriber : subscribers) {
// subscriber.inform(timedEventData);
// }
//
// timedEventData.setStatus(EventStatusEnum.SUCCESS);
// } catch (Exception e) {
// log.error("Failed to dispatch event:{" + timedEventData + "}, with exception: {" + e.getCause() +
/// "}", e);
//
// // 如果通知订阅者失败，进行重试；超过重试次数则失败
// if (timedEventData.shouldRetry()) {
// timedEventData.setRetryTimes(timedEventData.getRetryTimes() + 1);
// shouldReschedule = true;
// log.info("Try to reschedule event due to notify exception. "
// + "Event details: " + timedEventData);
// } else {
// timedEventData.setStatus(EventStatusEnum.FAILED);
// }
// }
//
// if (null != timedEventData.getId()) {
// TimedEvent timedEvent = TimedEventConverter.Dto2Do(timedEventData);
// timedEventService.updateById(timedEvent);
// }
//
// return shouldReschedule;
// }
// });
//
// // 如果需要重新触发，则重新分配定时器
// if (Boolean.TRUE.equals(shouldReschedule)) {
// log.info("Reschedule event " + timedEventData);
//
// // 重新分配超时时间
// timedEventData.extendTimeout(true);
// schedule(timedEventData);
// }
// }
// }
