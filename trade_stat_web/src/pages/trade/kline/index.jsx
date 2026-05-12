import {Upload, Button, message, Space} from 'antd';
import {UploadOutlined} from '@ant-design/icons';
import React from "react";
import {Select} from 'antd';
import Kline from "@/pages/trade/kline/components/kline";
const {Option} = Select;


class KlineSample extends React.Component {

  render() {
    return (
      <Kline width={"100%"}  height={"800px"} title={"测试Kline"} markPoint={{}} code={"601888"} start={"2019-01-01"} end={"2022-04-15"}>

      </Kline>
    )
  }
}
export default KlineSample
