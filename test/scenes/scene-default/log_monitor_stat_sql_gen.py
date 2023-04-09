#!/usr/bin/env python3

r = {
    "logPaths": [
        {
            "type": "path",
            "path": "/home/admin/logs/holoinsight-server/agent/stat.log",
            "agentLimitKB": -1,
            "charset": "utf-8"
        }
    ],
    "blackFilters": [],
    "whiteFilters": [
        {
            "type": "LR",
            "rule": {
                "leftIndex": 0,
                "left": "ptype=[",
                "right": "]",
                "translate": {}
            },
            "values": [
                "log_monitoring_stat"
            ]
        }
    ],
    "logParse": {
        "splitType": "LR",
        "maxKeySize": -1,
        "multiLine": {
            "multi": False
        }
    },
    "collectRanges": {
        "table": "default_server",
        "condition": [
            {
                "app": [
                    "holoinsight-server-example"
                ]
            }
        ]
    },
    "splitCols": [
        {
            "name": "tenant",
            "rule": {
                "nullable": False,
                "leftIndex": 0,
                "left": "tenant=[",
                "right": "]",
                "translate": {}
            },
            "colType": "DIM"
        },
        {
            "name": "c_key",
            "rule": {
                "nullable": False,
                "leftIndex": 0,
                "left": "t_c_key=[",
                "right": "]",
                "translate": {}
            },
            "colType": "DIM"
        },
        {
            "name": "in_bytes",
            "rule": {
                "nullable": False,
                "leftIndex": 0,
                "left": "in_bytes=[",
                "right": "]",
                "translate": {}
            },
            "colType": "VALUE"
        },
    ],
    "collectMetrics": [
        {
            "tableName": "in_bytes",
            "metricType": "select",
            "tags": [
                "tenant",
                "c_key"
            ],
            "metrics": [
                {
                    "name": "in_bytes",
                    "func": "sum"
                }
            ]
        },
    ]
}

metrics = ["in_bytes", "in_lines", "in_groups"]

for metric in metrics:
    #print(metric)
    pass

import json
json.dumps(r)
