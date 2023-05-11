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
  
  