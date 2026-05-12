import { InfoCircleOutlined } from '@ant-design/icons';
import { Col, Row, Tooltip } from 'antd';
import numeral from 'numeral';
import { ChartCard, Field } from './Charts';
import Yuan from '../utils/Yuan';
import styles from '../style.less';
import Space from "antd/es/space";
import React from "react";
const topColResponsiveProps = {
  xs: 24,
  sm: 12,
  md: 12,
  lg: 12,
  xl: 6,
  style: {
    marginBottom: 24,
  },
};

const IntroduceRow = ({ loading, visitData }) => (

  <Row gutter={24}>
    <Col {...topColResponsiveProps}>
      <ChartCard
        bordered={false}
        title="当前市值"
        action={
          <Tooltip title="当前持仓的总市值">
            <InfoCircleOutlined />
          </Tooltip>
        }
        loading={loading}
        total={() => <Yuan>{visitData.marketValue}</Yuan>}
        contentHeight={46}

      >
        <Space>
          持仓收益:
          <span className={styles.trendText}>￥{numeral(visitData.currentProfit).format('0,0.00')}</span>

        </Space>
      </ChartCard>
    </Col>

    <Col {...topColResponsiveProps}>
      <ChartCard
        bordered={false}
        loading={loading}
        title="总收益"
        action={
          <Tooltip title="所有交易的总收益">
            <InfoCircleOutlined />
          </Tooltip>
        }
        total={() => <Yuan>{visitData.totalProfit}</Yuan>}
        contentHeight={46}
      >
        <Space>
          本周:
          <span className={styles.trendText}>￥{numeral(visitData.weekProfit).format('0,0.00')}</span>

          本月:
          <span className={styles.trendText}>￥{numeral(visitData.monthProfit).format('0,0.00')}</span>
        </Space>
      </ChartCard>
    </Col>
    <Col {...topColResponsiveProps}>
      <ChartCard
        bordered={false}
        loading={loading}
        title="总流水"
        action={
          <Tooltip title="所有交易流水之和">
            <InfoCircleOutlined />
          </Tooltip>
        }
        total={() => <Yuan>{visitData.capitalFlow}</Yuan>}
        contentHeight={46}
      >
        <Space>
        本周:
        <span className={styles.trendText}>￥{numeral(visitData.weekCapitalFlow).format('0,0.00')}</span>
        本月:
        <span className={styles.trendText}>￥{numeral(visitData.monthCapitalFlow).format('0,0.00')}</span>
        </Space>

      </ChartCard>
    </Col>
    <Col {...topColResponsiveProps}>
      <ChartCard
        loading={loading}
        bordered={false}
        title="总次数"
        action={
          <Tooltip title="总交易次数">
            <InfoCircleOutlined />
          </Tooltip>
        }
        total={visitData.tradeCount}
        contentHeight={46}
      >
        <Space>
        本周次数:
        <span className={styles.trendText}>{visitData.weekTradeCount}</span>
        本月次数:
        <span className={styles.trendText}>{visitData.monthTradeCount}</span>
        </Space>

      </ChartCard>
    </Col>
  </Row>
);

export default IntroduceRow;
