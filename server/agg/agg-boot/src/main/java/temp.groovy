import com.alibaba.fastjson.JSON
import io.holoinsight.server.common.dao.entity.AggTaskV1DO
import io.holoinsight.server.common.dao.entity.AggTaskV1DOExample
import io.holoinsight.server.common.dao.mapper.AggTaskV1DOMapper

// sh /home/admin/logs/api/groovy/execute

def mapper = ctx.getBean(AggTaskV1DOMapper.class)
def json = '''
{
  "aggId": "k8s_pod_system_5s",
  "version": 1,
  "partitionKeys": [],
  "select": {
    "items": [
      {
        "agg": {
          "type": "COUNT"
        },
        "elect": "k8s_pod_cpu_util_5s.value",
        "as": "count"
      },
      {
        "agg": {
          "type": "AVG"
        },
        "elect": "k8s_pod_cpu_util_5s.value",
        "as": "cpu_util"
      }
    ]
  },
  "from": {
    "type": "metrics",
    "metrics": {
      "metrics": [
        "k8s_pod_cpu_util_5s"
      ]
    },
    "completeness": {
      "mode": "DATA",
      "dimTable": "%s_server",
      "groupBy": {
        "items": [
          {
            "tag": "app",
            "as": "app"
          },
          {
            "tag": "_cluster",
            "as": "cluster"
          }
        ]
      },
      "keepTargetKeys": [
        "namespace",
        "name"
      ]
    }
  },
  "where": {
  },
  "groupBy": {
    "keyLimit": 1000,
    "items": [
      {
        "tag": "tenant"
      },
      {
        "tag": "workspace"
      },
      {
        "tag": "app"
      }
    ]
  },
  "window": {
    "interval": 5000
  },
  "output": {
    "items": [
      {
        "type": "TSDB",
        "name": "k8s_pod_%s_5s_agg",
        "fields": [
          {
            "name": "cpu_util",
            "expression": "cpu_util"
          },
          {
            "name": "count",
            "expression": "count"
          }
        ]
      }
    ]
  }
}
'''

def aggId = JSON.parseObject(json).getString("aggId")

def example = AggTaskV1DOExample.newAndCreateCriteria()
        .andAggIdEqualTo(aggId) //
        .andDeletedEqualTo(0) //
        .example()

def d = mapper.selectOneByExampleWithBLOBs(example)
if (d == null) {
    d = new AggTaskV1DO()
    d.gmtCreate = new Date()
    d.gmtModified = new Date()
    d.aggId = aggId
    d.json = json
    d.version = 0
    d.deleted = 0
    mapper.insert(d)
} else {
    d.gmtModified = new Date()
    d.json = json
    d.version++
    mapper.updateByPrimaryKeyWithBLOBs(d)
}

return d.version
