import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Form, Input, Select, DatePicker, Button, Statistic, Tabs, Table, message } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Scatter } from 'recharts';
import request from 'umi-request';
import { API_BASE_URL } from '@/utils/apiConfig';

const { Option } = Select;
const { TabPane } = Tabs;
const { RangePicker } = DatePicker;

const Backtest = () => {
  const [form] = Form.useForm();
  const [strategies, setStrategies] = useState([]);
  const [selectedStrategy, setSelectedStrategy] = useState('');
  const [strategyParams, setStrategyParams] = useState({});
  const [backtestResult, setBacktestResult] = useState(null);
  const [loading, setLoading] = useState(false);

  // 获取策略列表
  useEffect(() => {
    const fetchStrategies = async () => {
      try {
        const response = await request(`${API_BASE_URL}backtest/strategies`);
        if (response.code === 0) {
          setStrategies(response.data);
        }
      } catch (error) {
        console.error('Failed to load strategies:', error);
        message.error('获取策略列表失败');
      }
    };

    fetchStrategies();
  }, []);

  // 策略变化时重置参数
  useEffect(() => {
    if (selectedStrategy) {
      const strategy = strategies.find(s => s.name === selectedStrategy);
      if (strategy) {
        const defaultParams = {};
        strategy.params.forEach(param => {
          defaultParams[param.name] = param.defaultValue;
        });
        setStrategyParams(defaultParams);
      }
    }
  }, [selectedStrategy, strategies]);

  // 处理策略参数变化
  const handleParamChange = (paramName, value) => {
    setStrategyParams(prev => ({
      ...prev,
      [paramName]: value
    }));
  };

  // 执行回测
  const handleBacktest = async (values) => {
    setLoading(true);
    try {
      const { code, strategy, dateRange } = values;
      const startDate = dateRange[0].format('YYYY-MM-DD');
      const endDate = dateRange[1].format('YYYY-MM-DD');

      // 构建参数字符串
      let params = '';
      Object.entries(strategyParams).forEach(([key, value]) => {
        params += `${key}=${value}&`;
      });
      params = params.slice(0, -1);

      const response = await request(`${API_BASE_URL}backtest/run?code=${code}&strategy=${strategy}&startDate=${startDate}&endDate=${endDate}&${params}`);
      if (response.code === 0) {
        setBacktestResult(response.data);
        message.success('回测成功');
      } else {
        message.error('回测失败：' + response.message);
      }
    } catch (error) {
      console.error('Backtest failed:', error);
      message.error('回测失败');
    } finally {
      setLoading(false);
    }
  };

  // 渲染策略参数表单
  const renderStrategyParams = () => {
    const strategy = strategies.find(s => s.name === selectedStrategy);
    if (!strategy || !strategy.params) return null;

    return (
      <Card title="策略参数" style={{ marginTop: 16 }}>
        <Row gutter={[16, 16]}>
          {strategy.params.map((param, index) => (
            <Col key={index} span={8}>
              <Form.Item label={param.description}>
                <Input
                  value={strategyParams[param.name] || ''}
                  onChange={(e) => handleParamChange(param.name, e.target.value)}
                  placeholder={param.defaultValue}
                />
              </Form.Item>
            </Col>
          ))}
        </Row>
      </Card>
    );
  };

  // 渲染回测结果
  const renderBacktestResult = () => {
    if (!backtestResult) return null;

    return (
      <Card title="回测结果" style={{ marginTop: 16 }}>
        <Tabs defaultActiveKey="overview">
          <TabPane tab="概览" key="overview">
            <Row gutter={[16, 16]}>
              <Col span={6}>
                <Statistic title="总收益率" value={backtestResult.totalReturn.multiply(100).toFixed(2)} suffix="%" />
              </Col>
              <Col span={6}>
                <Statistic title="年化收益率" value={backtestResult.annualizedReturn.multiply(100).toFixed(2)} suffix="%" />
              </Col>
              <Col span={6}>
                <Statistic title="最大回撤" value={backtestResult.maxDrawdown.multiply(100).toFixed(2)} suffix="%" />
              </Col>
              <Col span={6}>
                <Statistic title="夏普比率" value={backtestResult.sharpeRatio.toFixed(2)} />
              </Col>
              <Col span={6}>
                <Statistic title="胜率" value={backtestResult.winRate.multiply(100).toFixed(2)} suffix="%" />
              </Col>
              <Col span={6}>
                <Statistic title="交易次数" value={backtestResult.tradeCount} />
              </Col>
              <Col span={6}>
                <Statistic title="平均持仓天数" value={backtestResult.avgHoldDays.toFixed(1)} />
              </Col>
            </Row>
          </TabPane>

          <TabPane tab="资金曲线" key="equity">
            <div style={{ height: 400 }}>
              <ResponsiveContainer width="100%" height="100%">
                <LineChart
                  data={backtestResult.equityCurve}
                  margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="value" stroke="#1890ff" strokeWidth={2} name="资金曲线" />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </TabPane>

          <TabPane tab="交易信号" key="signals">
            <Table
              dataSource={backtestResult.tradeSignals}
              rowKey="date"
              columns={[
                {
                  title: '日期',
                  dataIndex: 'date',
                  key: 'date',
                },
                {
                  title: '信号类型',
                  dataIndex: 'type',
                  key: 'type',
                  render: (type) => (
                    <span style={{ color: type === 'buy' ? '#52c41a' : '#ff4d4f' }}>
                      {type === 'buy' ? '买入' : '卖出'}
                    </span>
                  ),
                },
                {
                  title: '价格',
                  dataIndex: 'price',
                  key: 'price',
                  render: (price) => `¥ ${price}`
                },
                {
                  title: '数量',
                  dataIndex: 'volume',
                  key: 'volume',
                },
                {
                  title: '持仓',
                  dataIndex: 'position',
                  key: 'position',
                },
              ]}
            />
          </TabPane>
        </Tabs>
      </Card>
    );
  };

  return (
    <PageContainer title="交易回测">
      <Card>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleBacktest}
        >
          <Row gutter={[16, 16]}>
            <Col span={8}>
              <Form.Item
                name="code"
                label="股票代码"
                rules={[{ required: true, message: '请输入股票代码' }]}
              >
                <Input placeholder="请输入股票代码" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="strategy"
                label="策略"
                rules={[{ required: true, message: '请选择策略' }]}
              >
                <Select
                  placeholder="请选择策略"
                  onChange={setSelectedStrategy}
                >
                  {strategies.map(strategy => (
                    <Option key={strategy.name} value={strategy.name}>
                      {strategy.description}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="dateRange"
                label="时间范围"
                rules={[{ required: true, message: '请选择时间范围' }]}
              >
                <RangePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          {renderStrategyParams()}

          <Form.Item style={{ textAlign: 'center' }}>
            <Button type="primary" htmlType="submit" loading={loading}>
              开始回测
            </Button>
          </Form.Item>
        </Form>
      </Card>

      {renderBacktestResult()}
    </PageContainer>
  );
};

export default Backtest;
