import qs from 'qs';
import React from 'react';
import { Button, Col, DatePicker, Divider, Form, Row, Select, Space, Table, Tag } from 'antd';
import SearchInput from "@/pages/position/current/components/SearchInput";
import { API_BASE_URL } from '@/utils/apiConfig';

const { RangePicker } = DatePicker;
const columns = [
  {
    title: '股票名称', dataIndex: 'name', key: 'name',
    render: (text, record) => {
      var codeMap = JSON.parse(sessionStorage.getItem('codeMap'));
      if (codeMap[record.code]) {
        return codeMap[record.code]['name']
      }

    },
  },
  { title: '股票代码', dataIndex: 'code', key: 'code' },
  { title: '交易时间', dataIndex: 'tradeTime', key: 'tradeTime', sorter: true, },
  {
    title: '操作', dataIndex: 'opt', key: 'opt', sorter: true,
    render: (text) => {
      return text ? '买入' : '卖出';
    },
  },

  { title: '价格', dataIndex: 'price', key: 'price', sorter: true, },
  {
    title: '交易量(股)', dataIndex: 'vol', key: 'vol', render: (text) => {
      return text + ''
    }, sorter: true,
  },

  {
    title: '佣金', dataIndex: 'commission', key: 'commission', render: (text) => {
      return '¥ ' + text.toFixed(2)
    }, sorter: true,
  },
  {
    title: '交易税', dataIndex: 'tax', key: 'tax', render: (text) => {
      return '¥ ' + text.toFixed(2)
    }, sorter: true,
  },
  {
    title: '过户费', dataIndex: 'transFee', key: 'transFee', render: (text) => {
      return '¥ ' + text.toFixed(2)
    }, sorter: true,
  },

];

const getRandomuserParams = (params) => {
  const resp = {
    results: params.pagination.pageSize,
    page: params.pagination.current,

    params,
    pageSize: params.pagination.pageSize,
    pageNo: params.pagination.current,
    order: convertDsc(params.sortOrder),
    field: convertfiled(params.sortField),

  };

  if (params.code) {
    resp.code = params.code;
  }

  if (params.opt) {
    resp.opt = params.opt;
  }

  if (params.clear) {
    resp.clear = params.clear;
  }

  if (params.startTime) {
    resp.startTime = params.startTime;
  }

  if (params.endTime) {
    resp.endTime = params.endTime;
  }

  return resp;


};

const convertDsc = value => {
  if (value === 'ascend') return 'asc'
  if (value === 'descend') return 'desc'
}
const convertfiled = value => {
  if (value === 'tradeTime') return 'trade_time'
  if (value === 'transFee') return 'trans_fee'

  return value;
}


class TradeDetails extends React.Component {


  state = {
    data: [],
    pagination: {
      current: 1,
      pageSize: 10,
    },
    loading: false,
    condition: {},
  };



  componentDidMount() {
    const { pagination } = this.state;
    // 读取URL参数
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const sortField = urlParams.get('sortField') || 'tradeTime';
    const sortOrder = urlParams.get('sortOrder') || 'descend';
    
    // 设置条件
    if (code) {
      this.state.condition['code'] = code;
    }
    
    // 执行查询，默认按照交易时间降序
    this.fetch({
      pagination,
      sortField,
      sortOrder,
      ...this.state.condition
    });
  }

  handleTableChange = (pagination, filters, sorter) => {
    const params = this.state.condition;
    this.fetch({
      sortField: sorter.field,
      sortOrder: sorter.order,
      pagination,
      ...params,
      ...filters,
    });
  };

  changeCode = (value) => {
    if (value) {
      this.state.condition['code'] = value.substr(0, 6);
    } else {
      this.state.condition['code'] = '';
    }

  };
  changeOpt = (value) => {
    this.state.condition['opt'] = value;
  };
  changeClear = (value) => {
    this.state.condition['clear'] = value;
  };
  changeTime = (date, dateString) => {
    this.state.condition['startTime'] = dateString[0];
    this.state.condition['endTime'] = dateString[1];
  };


  search = () => {
    const { pagination, condition } = this.state;
    this.fetch({
      pagination,
      ...condition,
    });
  };


  fetch = (params = {}) => {
    this.setState({ loading: true });
    fetch(`${API_BASE_URL}trade_details/raw_query?${qs.stringify(getRandomuserParams(params))}`)
      .then(res => res.json())
      .then(data => {
        this.setState({
          loading: false,
          data: data.data,
          pagination: {
            ...params.pagination,
            total: data.total,
            // 200 is mock data, you should read it from server
            // total: data.totalCount,
          },
        });
      });
  };

  onFinish = (values) => {
    // Form submission handler
  };

  render() {

    const { data, pagination, loading } = this.state;
    return (
      <>
        <Divider orientationMargin="2" orientation="left">原始交易记录</Divider>
        <Row>
          <Col span={24}>
            <Table
              columns={columns}
              rowKey={record => record.code + record.vol + record.tradeTime}
              dataSource={data}
              pagination={pagination}
              loading={loading}
              onChange={this.handleTableChange}
            />
          </Col>
        </Row>
      </>
    );
  }
}

export default TradeDetails;
