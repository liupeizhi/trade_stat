import {Upload, Button, message, Space} from 'antd';
import {UploadOutlined} from '@ant-design/icons';
import React from "react";
import {Select} from 'antd';
import { API_BASE_URL } from '@/utils/apiConfig';

const {Option} = Select;


class TradeUpload extends React.Component {
  state = {
    broker:'',
    fileList: [],
    uploading: false,
  };
  handleChange = (value)=>{
    this.setState({broker:value});
  };

  handleUpload = () => {
    const {fileList} = this.state;
    const formData = new FormData();
    fileList.forEach(file => {
      formData.append('recordFile', file);
    });
    this.setState({
      uploading: true,
    });
    // You can use any AJAX library you like
    fetch(`${API_BASE_URL}trade_details/upload_records?broker=${this.state.broker}`, {
      method: 'POST',
      body: formData,
    })
      .then(res => res.json())
      .then((res) => {
        console.log(res.code)
        if (res.code === 0) {
          message.success('上传成功');
        } else {

          message.error('上传失败:' + res.message);
        }
        this.setState({
          fileList: [],
        });

      })
      .catch(() => {
        message.error('上传失败');
      })
      .finally(() => {
        this.setState({
          uploading: false,
        });
      });
  };

  render() {
    const {uploading, fileList,broker} = this.state;
    const props = {
      onRemove: file => {
        this.setState(state => {
          const index = state.fileList.indexOf(file);
          const newFileList = state.fileList.slice();
          newFileList.splice(index, 1);
          return {
            fileList: newFileList,
          };
        });
      },
      beforeUpload: file => {
        this.setState(state => ({
          fileList: [...state.fileList, file],
        }));
        return false;
      },
      fileList,
    };

    return (

      <>
        <Space direction="vertical" size="5" style={{display: 'flex'}}>
          <Space>
            <span>选择券商：</span>
            <Select
              style={{width: 138}}
              placeholder="选择券商"
              optionFilterProp="children"
              onChange={this.handleChange}
              value={broker}
            >
              <Option value="haitong">海通证券</Option>
              <Option value="zhaoshang">招商证券</Option>
              <Option value="guoxin">国信证券</Option>

            </Select>

          </Space>
          <br/>
          <Space>
            <span>选择文件：</span>
            <Upload {...props} >
              <Button icon={<UploadOutlined/>}>交易记录文件</Button>
            </Upload>
          </Space>
          <br/>
          <Space>

            <Button
              type="primary"
              onClick={this.handleUpload}
              disabled={fileList.length === 0 || broker.length===0}
              loading={uploading}
              style={{marginTop: 16}}
            >
              {uploading ? '上传中' : '上传'}
            </Button>
          </Space>
        </Space>
      </>
    );
  }
}

export default TradeUpload
