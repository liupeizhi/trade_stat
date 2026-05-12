import qs from 'qs';
import React, { Component } from "react";
import { Button, Divider, Modal, Space, Table, Tag, Card, Row, Col, Statistic, Progress, message } from 'antd';

import SearchInput from "@/pages/position/current/components/SearchInput";
import Kline from "@/pages/trade/kline/components/kline";
import { API_BASE_URL } from '@/utils/apiConfig';
import moment from 'moment';

const expandedRowRender = (data, index) => {

  const columns = [
    {
      title: '操作时间', dataIndex: 'tradeTime', key: 'tradeTime', sorter: (a, b) => a.tradeTime - b.tradeTime,
    },
    {
      title: '操作方向', dataIndex: 'opt', key: 'opt',
      render: (text, record) => {
        if (text) {
          return <span>买入</span>
        } else {
          return <span>卖出</span>
        }
      },
      sorter: (a, b) => a.opt - b.opt,
    },
    {
      title: '数量',
      dataIndex: 'vol',
      key: 'vol',
      sorter: (a, b) => a.vol - b.vol,
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      sorter: (a, b) => a.price - b.price,
    },
    {
      title: '佣金', dataIndex: 'commission', key: 'commission', sorter: (a, b) => a.commission - b.commission,
    },
    {
      title: '税费',
      dataIndex: 'tax',
      key: 'tax',
      sorter: (a, b) => a.tax - b.tax,
    },
    {
      title: '操作', dataIndex: 'action', key: 'action',
      render: () => (
        <Space size="middle">
          <a>删除</a>
        </Space>
      ),
    },
  ];

  return <Table columns={columns} rowKey={record => record.tradeTime} dataSource={data.details} pagination={false} />;
};


const getRandomuserParams = params => ({
  pageSize: params.pagination.pageSize,
  pageNo: params.pagination.current,
  order: convertDsc(params.sortOrder),
  field: convertfiled(params.sortField),
  ...params,
});
const convertDsc = value => {
  if (value === 'ascend') return 'asc'
  if (value === 'descend') return 'desc'
}
const convertfiled = value => {
  if (value === 'openTime') return 'open_time'
  if (value === 'closeTime') return 'close_time'
  if (value === 'holdDays') return 'hold_time'
  if (value === 'profitRate') return 'profit_rate'
  if (value === 'profit') return 'profit'
  if (value === 'expend') return 'expend'
  if (value === 'income') return 'income'
  if (value === 'count') return 'trade_count'
}

export default class StockTable extends Component {

  constructor(props) {
    super(props);
    this.onSearch = this.onSearch.bind(this);
    this.selectCode = this.selectCode.bind(this);
    this.myRef = React.createRef();
    this.state = {
      title: '',
      visible: false,
      code: '',
      start: '',
      end: '',
      term: '',
      reqBody: {},
      data: [],
      secData: [],
      pagination: {
        current: 1,
        pageSize: 10,
      },
      loading: false,
      // 评分弹窗状态
      scoreModalVisible: false,
      currentScoreData: null,
      scoreLoading: false,
    };

  }


  columns = [
    {
      title: '股票名称', dataIndex: 'name', key: 'name',
      render: (text, record) => {
        var codeMap = JSON.parse(sessionStorage.getItem('codeMap'));
        if (codeMap[record.code]) {
          record.name = codeMap[record.code]['name'];
          return codeMap[record.code]['name']
        }

      },
    },
    { title: '股票代码', dataIndex: 'code', key: 'code' },
    { title: '开仓时间', dataIndex: 'openTime', key: 'openTime', sorter: true, },
    { title: '清仓时间', dataIndex: 'closeTime', key: 'closeTime', sorter: true, },
    { title: '持仓天数', dataIndex: 'holdDays', key: 'holdDays', sorter: true, },
    {
      title: '总支出', dataIndex: 'expend', key: 'expend', render: (text) => {
        return '¥ ' + text.toFixed(2)
      }, sorter: true,
    },
    {
      title: '总收入', dataIndex: 'income', key: 'income', render: (text) => {
        return '¥ ' + text.toFixed(2)
      }, sorter: true,
    },
    { title: '操作次数', dataIndex: 'count', key: 'count', sorter: true, },
    {
      title: '最终收益', dataIndex: 'profit', key: 'profit', sorter: true,
      render: (text, record) => {
        if (text > 0) {
          return <Tag color='red'>{text}</Tag>
        } else {
          return <Tag color='green'>{text}</Tag>
        }
      }
    },
    {
      title: '最终收益率', dataIndex: 'profitRate', key: 'profitRate', sorter: true,
      render: (text, record) => {
        if (text > 0) {
          return <Tag color='red'>{(text * 100).toFixed(2)}%</Tag>
        } else {
          return <Tag color='green'>{(text * 100).toFixed(2)}%</Tag>
        }
      }
    },
    {
      title: '操作', dataIndex: 'view', key: 'view',
      render: (text, record) => (
        <Space size="middle">
          <Button type="link" block onClick={() => this.selectCode(record)}>
            查看K线图
          </Button>
          <Button type="link" block onClick={() => this.showScoreModal(record)}>
            查看评分
          </Button>
        </Space>
      ),
    },
  ];

  onSearch = (body) => {
    this.setState({
      data: [],
    })

    this.fetch({
      sortField: sorter.field,
      sortOrder: sorter.order,
      pagination,
      ...filters,
    }, body);
  }


  componentDidMount() {
    const { pagination } = this.state;

    this.fetch({
      field: 'close_time',
      order: 'desc',
      pagination
    }, this.state.reqBody);
  }

  handleTableChange = (pagination, filters, sorter) => {

    this.setState({
      data: [],
    })


    this.fetch({
      sortField: sorter.field,
      sortOrder: sorter.order,
      pagination,
      ...filters,
    }, this.state.reqBody);
  };

  fetch = (params = {}, body = {}) => {
    this.setState({ loading: true });
    fetch(`${API_BASE_URL}position/stock_history_positions?${qs.stringify(getRandomuserParams(params))}`, {
      method: 'post',
      body: JSON.stringify(body),
      headers: {
        'Content-Type': 'application/json; charset=utf-8'
      }
    })
      .then(res => res.json())
      .then(data => {
        console.log(data.data);
        this.setState({
          loading: false,
          data: data.data,
          pagination: {
            ...params.pagination,
            total: data.total,
            //   // 200 is mock data, you should read it from server
            //   // total: data.totalCount,
          },
        });
      });
  };
  changeCode = (value) => {
    this.setState({ reqBody: { 'code': value } });
  };

  selectCode = async (value) => {

    this.setState({ visible: true, code: value.code, term: value.openTime.substr(0, 10), start: value.openTime.substr(0, 10), end: value.closeTime.substr(0, 10), title: '【' + value.name + '】日K图及买卖操作' });
    console.log(value.openTime.substr(0, 10));
    console.log(this.myRef.current);
    if (this.myRef.current) {
      this.myRef.current.fetchData({ code: value.code, term: value.openTime.substr(0, 10), start: value.openTime.substr(0, 10), end: value.closeTime.substr(0, 10) });

      //
    }
  };

  // 生成字符串哈希值的函数
  hashCode = (str) => {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convert to 32bit integer
    }
    return Math.abs(hash);
  };

  // 显示评分弹窗
  showScoreModal = (record) => {
    this.setState({ scoreLoading: true });
    // 直接使用当前行的真实数据计算评分
    // 构建评分数据对象
    const scoreData = {
      tradeId: this.hashCode(record.code),
      code: record.code,
      tradeTime: record.closeTime || record.openTime,
      opt: 1, // 默认为买入
      profitRate: record.profitRate,
      holdDays: record.holdDays
    };
    
    // 计算评分
    const calculateScore = (data) => {
      const dimensionScores = {};
      
      // 收益评分（40分）
      let profitScore = 20;
      if (data.profitRate) {
        const rate = data.profitRate;
        if (rate >= 0.3) profitScore = 40;
        else if (rate >= 0.2) profitScore = 35;
        else if (rate >= 0.1) profitScore = 30;
        else if (rate >= 0) profitScore = 25;
        else if (rate >= -0.05) profitScore = 20;
        else if (rate >= -0.1) profitScore = 15;
        else if (rate >= -0.2) profitScore = 10;
        else profitScore = 5;
      }
      dimensionScores['收益评分'] = profitScore;
      
      // 持仓时间评分（20分）
      let holdTimeScore = 10;
      if (data.holdDays) {
        const days = data.holdDays;
        if (days >= 7 && days <= 30) holdTimeScore = 20;
        else if (days >= 3 && days <= 6) holdTimeScore = 15;
        else if (days >= 31 && days <= 60) holdTimeScore = 15;
        else if (days == 1 || days == 2) holdTimeScore = 10;
        else if (days > 60) holdTimeScore = 10;
        else holdTimeScore = 5;
      }
      dimensionScores['持仓时间评分'] = holdTimeScore;
      
      // 时机评分（20分）
      let timingScore = 10;
      if (data.opt && data.profitRate) {
        const rate = data.profitRate;
        if (rate > 0) timingScore = 20;
        else if (rate > -0.05) timingScore = 15;
        else if (rate > -0.1) timingScore = 10;
        else timingScore = 5;
      }
      dimensionScores['时机评分'] = timingScore;
      
      // 风险控制评分（20分）
      let riskScore = 10;
      if (data.profitRate) {
        const rate = Math.abs(data.profitRate);
        if (rate <= 0.05) riskScore = 20;
        else if (rate <= 0.1) riskScore = 15;
        else if (rate <= 0.2) riskScore = 10;
        else riskScore = 5;
      }
      dimensionScores['风险控制评分'] = riskScore;
      
      // 计算总评分
      const totalScore = Object.values(dimensionScores).reduce((sum, score) => sum + score, 0);
      
      // 生成评分说明
      let description = '';
      if (totalScore >= 90) {
        description = '优秀的交易操作！';
      } else if (totalScore >= 80) {
        description = '良好的交易操作。';
      } else if (totalScore >= 70) {
        description = '不错的交易操作，有改进空间。';
      } else if (totalScore >= 60) {
        description = '一般的交易操作，需要注意改进。';
      } else {
        description = '较差的交易操作，建议分析原因。';
      }
      
      if (dimensionScores['收益评分'] < 25) {
        description += ' 建议关注收益情况，优化买入时机。';
      }
      if (dimensionScores['持仓时间评分'] < 15) {
        description += ' 建议合理控制持仓时间。';
      }
      if (dimensionScores['时机评分'] < 15) {
        description += ' 建议提高对市场时机的判断能力。';
      }
      if (dimensionScores['风险控制评分'] < 15) {
        description += ' 建议加强风险控制意识。';
      }
      
      return {
        ...data,
        totalScore,
        dimensionScores,
        scoreDescription: description
      };
    };
    
    // 计算评分并显示弹窗
    const result = calculateScore(scoreData);
    this.setState({ 
      currentScoreData: result, 
      scoreModalVisible: true,
      scoreLoading: false 
    });
  };

  clearStock = () => {
    this.setState({ reqBody: {} });
  };

  search = () => {
    const { pagination } = this.state;

    this.fetch({
      field: 'close_time',
      order: 'desc',
      pagination
    }, this.state.reqBody);

  }
  reset = () => {
    this.setState({ reqBody: {} });
    const { pagination } = this.state;
    this.fetch({
      field: 'close_time',
      order: 'desc',
      pagination
    }, {});
  }


  onExpand = (expanded, record) => {
    this.setState({
      secData: record.details,
    });
  }

  render() {
    const { data, pagination, loading, reqBody, code, start, end, visible, title } = this.state;
    return (
      <>
        <Space>
          选择股票：
          <SearchInput handleChange={this.changeCode} onClear={this.clearStock} value={reqBody.code} />

          <Button type="primary" onClick={this.search}>
            查询
          </Button>

        </Space>
        <Divider dashed />
        <Table
          title={() => '历史持仓记录'}
          columns={this.columns}
          rowKey={record => (record.code + record.openTime)}
          dataSource={data}
          pagination={pagination}
          loading={loading}
          onChange={this.handleTableChange}
          expandedRowRender={record => expandedRowRender(record)}
          onExpand={this.onExpand}
        />

        <Modal
          width={"85%"} height={"500px"}
          title="交易详情"
          centered={true}
          visible={visible}
          closable={false}
          maskClosable={false}
          footer={[
            <Button type="primary" onClick={() => this.setState({ visible: false })}>
              确定
            </Button>,

          ]}
        >
          <Kline ref={this.myRef} width={"100%"} height={"500px"} title={title} markPoint={{}} code={code} start={start} end={end} term={start}>
          </Kline>
        </Modal>

        {/* 评分弹窗 */}
        <Modal
          width={800}
          title="交易评分详情"
          centered={true}
          visible={this.state.scoreModalVisible}
          closable={false}
          maskClosable={false}
          footer={[
            <Button type="primary" onClick={() => this.setState({ scoreModalVisible: false })}>
              确定
            </Button>,
          ]}
        >
          {this.state.scoreLoading ? (
            <div style={{ textAlign: 'center', padding: '40px' }}>加载中...</div>
          ) : this.state.currentScoreData ? (
            <Row gutter={16}>
              <Col span={24}>
                <Card title="评分概览">
                  <Statistic 
                    title="总评分" 
                    value={this.state.currentScoreData.totalScore} 
                    suffix="分" 
                    valueStyle={{ color: this.state.currentScoreData.totalScore >= 80 ? '#52c41a' : this.state.currentScoreData.totalScore >= 60 ? '#faad14' : '#ff4d4f' }}
                  />
                </Card>
              </Col>
              <Col span={24}>
                <Card title="评分维度">
                  {Object.entries(this.state.currentScoreData.dimensionScores || {}).map(([key, value], index) => (
                    <Row key={index} gutter={16} style={{ marginBottom: 8 }}>
                      <Col span={8}>{key}</Col>
                      <Col span={16}>
                        <Progress percent={(value / 100) * 100} strokeColor="#1890ff" />
                        <span style={{ marginLeft: 8 }}>{value}分</span>
                      </Col>
                    </Row>
                  ))}
                </Card>
              </Col>
              <Col span={24}>
                <Card title="评分说明">
                  <p>{this.state.currentScoreData.scoreDescription}</p>
                </Card>
              </Col>
              <Col span={24}>
                <Card title="原始数据">
                  <p>股票代码：{this.state.currentScoreData.code}</p>
                  <p>交易时间：{this.state.currentScoreData.tradeTime}</p>
                  <p>操作类型：{this.state.currentScoreData.opt === 1 ? '买入' : '卖出'}</p>
                  <p>收益率：{this.state.currentScoreData.profitRate ? (this.state.currentScoreData.profitRate * 100).toFixed(2) : '0.00'}%</p>
                  <p>持仓天数：{this.state.currentScoreData.holdDays}天</p>
                </Card>
              </Col>
            </Row>
          ) : (
            <div style={{ textAlign: 'center', padding: '40px' }}>暂无评分数据</div>
          )}
        </Modal>
      </>
    );
  }
}

