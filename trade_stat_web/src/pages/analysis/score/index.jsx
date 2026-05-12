import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Progress } from 'antd';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { PageContainer } from '@ant-design/pro-layout';
import request from 'umi-request';
import { API_BASE_URL } from '@/utils/apiConfig';

const TradeScore = () => {
  const [scoreStats, setScoreStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 获取评分统计数据
    const fetchScoreStats = async () => {
      try {
        const response = await request(`${API_BASE_URL}trade_score/get_stats`);
        if (response.code === 0) {
          setScoreStats(response.data);
        }
      } catch (error) {
        console.error('Failed to load score stats:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchScoreStats();
  }, []);

  // 评分等级颜色
  const scoreColors = {
    '90-100': '#52c41a',
    '80-89': '#73d13d',
    '70-79': '#a0d911',
    '60-69': '#faad14',
    '0-59': '#ff4d4f'
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!scoreStats) {
    return <div>Failed to load score stats</div>;
  }

  // 准备饼图数据
  const pieData = Object.entries(scoreStats.scoreDistribution || {}).map(([key, value]) => ({
    name: key,
    value,
    color: scoreColors[key]
  }));

  // 准备柱状图数据
  const barData = Object.entries(scoreStats.scoreDistribution || {}).map(([key, value]) => ({
    name: key,
    数量: value
  }));

  return (
    <PageContainer title="交易评分统计">
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Card title="评分概览" bordered={false}>
            <Row gutter={[16, 16]}>
              <Col span={6}>
                <Statistic title="平均评分" value={scoreStats.avgScore} suffix="分" />
              </Col>
              <Col span={6}>
                <Statistic title="最高评分" value={scoreStats.highScore} suffix="分" />
              </Col>
              <Col span={6}>
                <Statistic title="最低评分" value={scoreStats.lowScore} suffix="分" />
              </Col>
              <Col span={6}>
                <Statistic title="评分次数" value={scoreStats.scoreCount} />
              </Col>
            </Row>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="评分分布" bordered={false}>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="评分数量" bordered={false}>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart
                data={barData}
                margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="数量" fill="#1890ff" />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        <Col span={24}>
          <Card title="评分说明" bordered={false}>
            <Row gutter={[16, 16]}>
              <Col span={6}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <div style={{ width: 16, height: 16, backgroundColor: scoreColors['90-100'], marginRight: 8 }}></div>
                  <span>优秀 (90-100分)</span>
                </div>
              </Col>
              <Col span={6}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <div style={{ width: 16, height: 16, backgroundColor: scoreColors['80-89'], marginRight: 8 }}></div>
                  <span>良好 (80-89分)</span>
                </div>
              </Col>
              <Col span={6}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <div style={{ width: 16, height: 16, backgroundColor: scoreColors['70-79'], marginRight: 8 }}></div>
                  <span>一般 (70-79分)</span>
                </div>
              </Col>
              <Col span={6}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <div style={{ width: 16, height: 16, backgroundColor: scoreColors['60-69'], marginRight: 8 }}></div>
                  <span>及格 (60-69分)</span>
                </div>
              </Col>
              <Col span={6}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <div style={{ width: 16, height: 16, backgroundColor: scoreColors['0-59'], marginRight: 8 }}></div>
                  <span>较差 (0-59分)</span>
                </div>
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </PageContainer>
  );
};

export default TradeScore;
