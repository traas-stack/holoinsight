import * as React from 'react';
import { QuestionCircleOutlined } from '@ant-design/icons';
interface TipProps extends React.HTMLAttributes<HTMLSpanElement> {
  id: string;
  text?: string;
}
function Tip(props: TipProps) {
  const { id, text, style, ...rest } = props;
  return (
    <>
      {text}
      <QuestionCircleOutlined
        {...rest}
        style={{ marginLeft: 8, ...style }}
      />
    </>
  );
}
export default React.memo(Tip);
