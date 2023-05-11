export const TOPO_DEEP = [
    {
        lable:"1",
        value:1
    },
    {
        lable:"2",
        value:2
    },
    {
        lable:"3",
        value:3
    },
    {
        lable:"4",
        value:4
    },
    {
        lable:"5",
        value:5
    }
]


export const LINE_OPTIONS =  {
    "xAxis": {
      "label": {
        "formatter": "HH:mm"
      },
      "visible": true
    },
    "yAxis": {
      "label": {
        "formatter": {
          "op": "/",
          "opValue": 1,
          "unit": "default"
        }
      },
      "min": 0,
      "visible": true
    },
    "lineStyle": {
      "size": 2
    },
    "line": {
      "connectNulls": false
    },
    "point": {
      "visible": false,
      "size": 4
    },
    "area": {
      "visible": false,
      "opacity": 0.03
    },
    "tooltip": {
      "visible": true,
      "mode": "share",
      "order": "desc"
    },
    "legend": {
      "visible": true,
      "asTable": true,
      "position": "bottom",
      "values": {
        "min": false,
        "max": true,
        "avg": false,
        "current": true,
        "total": false
      }
    },
    "stack": false
}


export const CLOUMN_OPTIONS = {
    "timeFormatter": "HH:mm:ss",
    "order": {
        "key": "",
        "type": "DESC"
    },
    "hiddenTime": false,
    "headers": {
        "row": [
            
            "app",
            "result",
            "hostname",
            "pod",
            "ip",
            "namespace",
        ],
        "column": [
          "time",
            "__name__"
        ]
    },
    "renameHeaders": {},
    "columnsWidth": {}
}

enum OFFSET_TYPE_ENUM {
  EARLIER = 'EARLIER',
  EARLIER_AND_LATER = 'EARLIER_AND_LATER',
}
const OFFSETS = [
  '5minute',
  '10minute',
  '1hour',
  '3hour',
  '6hour',
  '12hour',
  'midnight',
  '1day',
];

export const OFFSET_OPTIONS = [
  {
    label: '时刻往前',
    value: OFFSET_TYPE_ENUM.EARLIER,
    children: OFFSETS,
  },
  {
    label: '时刻前后',
    value: OFFSET_TYPE_ENUM.EARLIER_AND_LATER,
    children: OFFSETS.filter((v) => v !== 'midnight').map((item) => [item, item].join(',')),
  },
];

