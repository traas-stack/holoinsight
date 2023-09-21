/**
 * 聚合日志数据
 * 1.根据tags中的实例先做一次分类
 * 2.根据开头是日期的数据做一次聚合
 * @param logs
 */
export function polyLogs(logs: any[]) {
  const polyedResultByHostName: any = {}; // 根据实例分类，key是实例名
  const polyedResult: any[] = []; // 最终聚合结果

  const dateReg =
    /^ *([1-9]\d{3}-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-9]))) )?([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]/;

  logs.forEach((item) => {
    const hostnameKey =
      Object.keys(item.tags || {}).find((keyName) =>
        keyName.includes('hostname'),
      ) || '';
    const hostname = item.tags?.[hostnameKey];
    if (polyedResultByHostName[hostname]) {
      polyedResultByHostName[hostname].push(item);
    } else {
      polyedResultByHostName[hostname] = [item];
    }
  });
  // 根据日期聚合
  for (const key in polyedResultByHostName) {
    if (key) {
      const instanceItemLogs = polyedResultByHostName[key]; // 单实例数据
      if (Array.isArray(instanceItemLogs)) {
        let lastPolyedContent = '';
        let lastLogItem: any = {};
        instanceItemLogs.forEach((logItem) => {
          if (logItem) {
            const { content } = logItem;
            if (dateReg.test(content)) {
              // 新开头的日志

              // 把之前聚合好的日志push进数组
              polyedResult.push({ ...lastLogItem, content: lastPolyedContent });
              // 重置
              lastLogItem = logItem;
              lastPolyedContent = content;
            } else {
              // 续写的日志
              if (
                lastLogItem.content === undefined ||
                lastLogItem.content === null
              ) {
                // 如果第一条日志数据没有日期的话，在这里赋值日志对象
                lastLogItem = logItem;
              }
              lastPolyedContent += content;
            }
          }
        });
        if (lastPolyedContent) {
          // 最后一条
          polyedResult.push({ ...lastLogItem, content: lastPolyedContent });
        }
      }
    }
  }

  // 第一条数据可能是 {content: ''}
  if (polyedResult.length > 0) {
    if (!polyedResult[0].content && Object.keys(polyedResult[0]).length === 1) {
      polyedResult.shift();
    }
  }

  // 按照时间排序
  const sortedPolyedResult = polyedResult.sort(
    (a: any, b: any) => b.time - a.time,
  );

  return sortedPolyedResult;
}
