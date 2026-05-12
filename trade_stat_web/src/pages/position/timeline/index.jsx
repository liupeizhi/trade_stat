import {Button, Calendar, Col, Divider, List, Modal, Row, Space, Table} from 'antd';
import moment from 'moment';
import React from "react";
import * as echarts from 'echarts';
import { API_BASE_URL } from '@/utils/apiConfig';

class TimeLine extends React.Component {


  constructor(props) {
    super(props)


    this.state = {
      value: moment(),
      start: '2010-01-01',
      end: moment(new Date()).format('YYYY-MM-DD'),
      data: {},
      pieConfig: {},
      visible: true,
      details: [],
      legendData: [],
      pieData: [],
      // 操作记录相关状态
      operationRecords: [],
      operationLoading: false,
      selectedDay: '',
      heldCodes: [],

    };
    this.chart = null;

    this.getListData = this.getListData.bind(this)
    this.onSelect = this.onSelect.bind(this)
  }


  initOption = (ld, dd,day) => ({
    title: {
      text: '持仓分布图('+day+')',
      subtext: '按市值',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
      left: 'center',
      top: 'bottom',
      data: ld,

    },
    toolbox: {
      show: true,
      feature: {
        mark: {show: true},
        dataView: {show: true, readOnly: false},
        restore: {show: true},
        saveAsImage: {show: true}
      }
    },
    series: [
      {
        name: '市值',
        type: 'pie',
        radius: [20, 140],
        roseType: 'radius',
        itemStyle: {
          borderRadius: 5
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true
          }
        },
        data: dd,
      },
    ]
  });

  initChart = el => {
    console.log("初始化图形", el)
    // renderer 用于配置渲染方式 可以是 svg 或者 canvas
    const renderer = this.props.renderer || 'canvas';

    return new Promise(resolve => {
      setTimeout(() => {
        this.chart = echarts.init(el, null, {
          renderer,
        });
        resolve();
      }, 0);
    });
  };

  getListData = (value) => {
    if (this.state.data)
      return this.state.data[value]
    else
      return []
  }


  onSelect = async value => {
    console.log("选择："+value)
    const day = value.format('YYYY-MM-DD');
    const listData = this.getListData(day);
    if (!listData) {
      return;
    }
    let details = [];
    let pieData = [];
    let legendData = [];
    let total = 0;
    let nums = [];
    let heldCodes = [];
    for (let i = 0; i < listData.length; i++) {
      let profit = listData[i].profit;
      let profitRate = listData[i].profitRate;
      let dayProfit = listData[i].dayProfit;
      let dayProfitRate = listData[i].dayProfitRate;
      let code = listData[i].code;
      let vol = listData[i].vol;
      let price = listData[i].price;
      let cost = listData[i].cost;
      let name = code;
      var codeMap = JSON.parse(sessionStorage.getItem('codeMap'));
      if (codeMap[code]) {
        name = codeMap[code]['name']
      }
      let num = vol * price;
      total += num;
      nums.push({name: name, num: num});
      heldCodes.push(code);


      details.push("持有：" + name + " " + vol + "股，平均成本：" + cost.toFixed(2) + ",总盈亏：" + profit.toFixed(2) + "¥,总盈亏率：" + (profitRate * 100).toFixed(2) + "%"+ ",当日盈亏：" + dayProfit.toFixed(2) + "¥,当日盈亏率：" + (dayProfitRate * 100).toFixed(2) + "%");
    }

    for (let n in nums) {
      legendData.push(nums[n].name);
      pieData.push({
        name: nums[n].name,
        value: parseFloat(nums[n].num).toFixed(2),
      });
    }


    this.setState({
      width: '600px',
      height: '500px',
      legendData: legendData,
      pieData: pieData,
      details: details,
      visible: true,
      pieConfig: {
        appendPadding: 10,
        data: pieData,
        angleField: 'value',
        colorField: 'type',
        radius: 0.75,
        label: {
          type: 'spider',
          labelHeight: 28,
          content: '{name}\n{percentage}',
        },
        interactions: [
          {
            type: 'element-selected',
          },
          {
            type: 'element-active',
          },
        ],
      },
      selectedDay: day,
      heldCodes: heldCodes,
    })
    const notMerge = this.props.notMerge;
    const lazyUpdate = this.props.lazyUpdate;
    let option = this.initOption(legendData, pieData,day);
    console.log("加载图形", document.getElementById("chart"));
    await this.initChart(document.getElementById("chart"));
    this.chart.setOption(option, notMerge, lazyUpdate);

    // 获取当日操作记录
    this.fetchDayOperationRecords(day, heldCodes);


  };

  // 获取某日的操作记录
  fetchDayOperationRecords = (day, codes) => {
    if (!codes || codes.length === 0) {
      this.setState({ operationRecords: [], operationLoading: false });
      return;
    }

    this.setState({ operationLoading: true, operationRecords: [] });

    // 构建 Promise 数组，获取每个持仓股票当日的操作记录
    const promises = codes.map(code => {
      return fetch(`${API_BASE_URL}trade_details/raw_query?code=${code}&startTime=${day} 00:00:00&endTime=${day} 23:59:59`)
        .then(res => res.json())
        .then(data => {
          if (data.code === 0 && data.data) {
            return data.data.map(item => ({ ...item, code: code }));
          }
          return [];
        })
        .catch(err => {
          console.error(`获取${code}操作记录失败:`, err);
          return [];
        });
    });

    Promise.all(promises).then(results => {
      // 合并所有操作记录
      const allRecords = results.flat();
      // 按时间排序
      allRecords.sort((a, b) => (a.tradeTime || '').localeCompare(b.tradeTime || ''));
      this.setState({
        operationRecords: allRecords,
        operationLoading: false,
      });
    });
  };
  dateCellRender = (value) => {
    const listData = this.getListData(value.format('YYYY-MM-DD'));
    if (!listData) {
      return;
    }
    let total = 0.0;
    let totalDay = 0.0;
    let profit = 0.0;
    let dayProfit = 0.0;
    for (let i = 0; i < listData.length; i++) {
      if (listData[i].profitRate && listData[i].profit) {
        total = total + Math.abs(listData[i].profit / listData[i].profitRate)
      }

      if (listData[i].dayProfitRate && listData[i].dayProfit) {
        totalDay = totalDay + Math.abs(listData[i].dayProfit / listData[i].dayProfitRate)
      }


      profit = profit + listData[i].profit
      dayProfit = dayProfit + listData[i].dayProfit
    }

    let profitRate = (profit / total) * 100
    let profitRateDay = (dayProfit / totalDay) * 100
    
    // 根据当日收益设置背景颜色
    let bgColor = '#e0e0e0'; // 默认灰色
    if (dayProfit > 0) {
      bgColor = '#ff4d4f'; // 红色背景
    } else if (dayProfit < 0) {
      bgColor = '#52c41a'; // 绿色背景
    }

    return (
      <div style={{width: '100%', height: '100%', backgroundColor: bgColor, padding: '4px'}}>
        <span>当日收益：{dayProfit.toFixed(2)}</span>
        <br/>
        <span>当日收益率：{profitRateDay.toFixed(2) + '%'}</span>
        <br/>
        <span>持仓收益：{profit.toFixed(2)}</span>
        <br/>
        <span>持仓收益率：{profitRate.toFixed(2) + '%'}</span>
      </div>
    );
  };
  getMonthData = (value) => {
    if (value.month() === 8) {
      return 1394;
    }
  }
  monthCellRender = (value) => {
    const num = this.getMonthData(value);
    return num ? (
      <div className="notes-month">
        <section>{num}</section>
        <span>Backlog number</span>
      </div>
    ) : null;
  };

  onPanelChange = value => {
    this.setState({value});
  };

  componentDidMount() {
    this.fetchData()
    this.setState({
      visible: false,
    })


  }
  
  // 禁用周六和周天
  disabledDate = (current) => {
    // 获取当前日期是星期几，0是周日，6是周六
    const day = current.day();
    // 禁用周六和周天
    return day === 0 || day === 6;
  };

  fetchData = () => fetch(`${API_BASE_URL}profit/history?end=${this.state.end}&start=${this.state.start}`)
    .then(res => res.json())
    .then(data => {
      console.log("拉取数据：" + data + "," + this.state.start + "," + this.state.end)

      this.setState({data: data.data})


    });

  render() {
    const {width, height, value, visible, pieConfig, operationRecords, operationLoading, selectedDay} = this.state;

    // 操作记录表格列定义
    const operationColumns = [
      {
        title: '时间',
        dataIndex: 'tradeTime',
        key: 'tradeTime',
        width: 180,
      },
      {
        title: '股票代码',
        dataIndex: 'code',
        key: 'code',
        width: 100,
      },
      {
        title: '方向',
        dataIndex: 'opt',
        key: 'opt',
        width: 60,
        render: (text) => text ? '买入' : '卖出',
      },
      {
        title: '数量',
        dataIndex: 'vol',
        key: 'vol',
        width: 100,
      },
      {
        title: '价格',
        dataIndex: 'price',
        key: 'price',
        width: 100,
        render: (text) => typeof text === 'number' ? text.toFixed(2) : '0.00',
      },
      {
        title: '佣金',
        dataIndex: 'commission',
        key: 'commission',
        width: 80,
        render: (text) => typeof text === 'number' ? text.toFixed(2) : '0.00',
      },
      {
        title: '税费',
        dataIndex: 'tax',
        key: 'tax',
        width: 80,
        render: (text) => typeof text === 'number' ? text.toFixed(2) : '0.00',
      },
    ];

    return (
      <>
        <style>
          {`
            /* 隐藏周六和周日列 */
            .ant-picker-calendar-date-panel table thead tr th:nth-child(7),
            .ant-picker-calendar-date-panel table thead tr th:nth-child(1),
            .ant-picker-calendar-date-panel table tbody tr td:nth-child(7),
            .ant-picker-calendar-date-panel table tbody tr td:nth-child(1) {
              display: none;
            }
            /* 调整剩余列的宽度 */
            .ant-picker-calendar-date-panel table thead tr th,
            .ant-picker-calendar-date-panel table tbody tr td {
              width: 20%; /* 5列，每列20% */
            }
          `}
        </style>
        <Calendar  onSelect={this.onSelect}
                  // onPanelChange={this.onPanelChange}
                  dateCellRender={this.dateCellRender}
                  monthCellRender={this.monthCellRender}
                  disabledDate={this.disabledDate}/>



        <Modal
          title={"持仓详情 (" + selectedDay + ")"}
          visible={visible}
          closable={false}
          maskClosable={false}
          centered={true}
          width={'90%'}
          style={{ top: 20 }}
          footer={[
            <Button type="primary" onClick={() => this.setState({visible: false})}>
              确定
            </Button>,

          ]}
        >
          <Row gutter={16}>
            <Col span={11} >
              <div
                className="default-chart"
                ref={el => (this.el = el)}
                id={"chart"}
                style={{width, height}}
              />
            </Col>
            <Col span={12} >
              <div
                id="scrollableDiv"
                style={{
                  height: 300,
                  overflow: 'auto',
                  padding: '0 16px',
                  // border: '1px solid rgba(140, 140, 140, 0.35)',
                }}
              >
              <List
                split={true}
                size={"large"}
                bordered
                dataSource={this.state.details}
                hight={500}

                renderItem={item => (
                  <List.Item>
                    {item}
                  </List.Item>
                )}
              >

              </List>
              </div>
            </Col>
          </Row>
          <Divider dashed />
          <div>
            <h4>当日操作记录</h4>
            <Table
              columns={operationColumns}
              dataSource={operationRecords}
              rowKey={(record, index) => index}
              loading={operationLoading}
              pagination={operationRecords.length > 10 ? { pageSize: 10 } : false}
              size="small"
              scroll={{ x: 700 }}
            />
          </div>


        </Modal>
      </>
    );
  }
}

export default TimeLine
