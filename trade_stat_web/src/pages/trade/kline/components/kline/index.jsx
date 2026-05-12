import React, { Component } from "react";
import ReactECharts from 'echarts-for-react';

import * as echarts from 'echarts';
import 'zrender/lib/svg/svg';
import { Button, List, Modal, Table } from "antd";
import moment from "moment";
import { API_BASE_URL } from '@/utils/apiConfig';

class Kline extends Component {

  constructor(props) {
    super(props);
    this.state = {
      code: '',
      visible: false,
      data: [],
      details: [],
    };
    this.chart = null;
    this.clickGraph = this.clickGraph.bind(this)

  }


  options = {};
  upColor = '#ec0000';
  upBorderColor = '#8A0000';
  downColor = '#00da3c';
  downBorderColor = '#008F28';
  tColor = 'rgba(238, 126, 7, 1)';
  clearColor = 'rgba(136, 136, 136, 1)';
  //分离数据成日期和数据
  splitData = (rawData) => {

    // ['open', 'close', 'lowest', 'highest', 'volumn']

    const values = [];

    for (let i = 0; i < rawData.length; i++) {
      let d = [];
      d.push(rawData[i]['day']);
      d.push(rawData[i]['open'].toFixed(2));
      d.push(rawData[i]['close'].toFixed(2));
      d.push(rawData[i]['low'].toFixed(2));
      d.push(rawData[i]['high'].toFixed(2));
      d.push(rawData[i]['turnover'].toFixed(2));
      d.push(this.getSign(values, i, rawData[i]['open'].toFixed(2), rawData[i]['close'].toFixed(2), 4));

      values.push(d);
    }

    let ma5 = this.calculateMA(values, 5);
    let ma10 = this.calculateMA(values, 10);
    let ma20 = this.calculateMA(values, 20);
    let ma30 = this.calculateMA(values, 30);
    let rate = this.calculateRate(values);
    for (let i = 0; i < values.length; i++) {
      values[i].push(ma5[i] === '-' ? ma5[i] : ma5[i].toFixed(2));
      values[i].push(ma10[i] === '-' ? ma10[i] : ma10[i].toFixed(2));
      values[i].push(ma20[i] === '-' ? ma20[i] : ma20[i].toFixed(2));
      values[i].push(ma30[i] === '-' ? ma30[i] : ma30[i].toFixed(2));
      values[i].push((rate[i] * 100).toFixed(2) + '%');
    }


    return values;


  };


  // Each item: open，close，lowest，highest

  /**
   * 计算均值
   * @param dayCount
   * @param data
   * @returns {[]}
   */
  calculateMA = (data, dayCount) => {
    let result = [];
    for (let i = 0, len = data.length; i < len; i++) {
      if (i < dayCount) {
        result.push('-');
        continue;
      }
      let sum = 0;
      for (let j = 0; j < dayCount; j++) {
        sum += +data[i - j][2];
      }
      result.push(sum / dayCount);
    }
    return result;
  };

  calculateRate = (data) => {
    let result = [];
    for (let i = 0, len = data.length; i < len; i++) {
      let d = data[i];
      if (i === 0) {
        result.push(((d[2] - d[1]) / d[1]).toFixed(4))
      } else {
        let pre = data[i - 1];
        result.push(((d[2] - pre[2]) / pre[2]).toFixed(4))
      }
    }

    return result;
  };

  getSign = (data, dataIndex, openVal, closeVal, closeDimIdx) => {
    let sign;
    if (openVal > closeVal) {
      sign = -1;
    } else if (openVal < closeVal) {
      sign = 1;
    } else {
      sign =
        dataIndex > 0
          ? // If close === open, compare with close of last record
          data[dataIndex - 1][closeDimIdx] <= closeVal
            ? 1
            : -1
          : // No record of previous, set to be positive
          1;
    }
    return sign;
  };


  //图形参数
  initOption = (data) => (this.options = {

    dataset: {
      source: data
    },
    legend: {
      data: ['日K', 'MA5', 'MA10', 'MA20', 'MA30']
    },
    title: {
      text: this.props.title,
      left: 0
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: [
      {
        left: '10%',
        right: '10%',
        bottom: 200
      },
      {
        left: '10%',
        right: '10%',
        height: 80,
        bottom: 80
      }
    ],
    xAxis: [{
      type: 'category',
      boundaryGap: false,
      // inverse: true,
      axisLine: { onZero: false },
      splitLine: { show: false },
      min: 'dataMin',
      max: 'dataMax'
    },
    {
      type: 'category',
      gridIndex: 1,
      boundaryGap: false,
      axisLine: { onZero: false },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false },
      min: 'dataMin',
      max: 'dataMax'
    }
    ],
    yAxis: [
      {
        scale: true,
        splitArea: {
          show: true
        }
      },
      {
        scale: true,
        gridIndex: 1,
        splitNumber: 2,
        axisLabel: { show: false },
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '日K',
        type: 'candlestick',
        itemStyle: {
          color: this.upColor,
          color0: this.downColor,
          borderColor: this.upBorderColor,
          borderColor0: this.downBorderColor
        },
        encode: {
          x: 0,
          y: [1, 2, 3, 4]
        },
        markPoint: {

          data: this.props.markPoint,

        },
        markLine: {
          symbol: ['none', 'none'],
          data: [
            [
              {
                name: 'from lowest to highest',
                type: 'min',
                valueDim: 'lowest',
                symbol: 'circle',
                symbolSize: 10,
                label: {
                  show: false
                },
                emphasis: {
                  label: {
                    show: false
                  }
                }
              },
              {
                type: 'max',
                valueDim: 'highest',
                symbol: 'circle',
                symbolSize: 10,
                label: {
                  show: false
                },
                emphasis: {
                  label: {
                    show: false
                  }
                }
              }
            ],
            {
              name: 'min line on low',
              type: 'min',
              valueDim: 'lowest'
            },
            {
              name: 'max line on close',
              type: 'max',
              valueDim: 'highest'
            }
          ]
        }
      },
      {
        name: 'MA5',
        type: 'line',
        encode: {
          x: 0,
          y: 7
        },
        smooth: true,
        lineStyle: {
          opacity: 0.5
        }
      },
      {
        name: 'MA10',
        type: 'line',
        encode: {
          x: 0,
          y: 8
        },
        smooth: true,
        lineStyle: {
          opacity: 0.5
        }
      },
      {
        name: 'MA20',
        type: 'line',
        encode: {
          x: 0,
          y: 9
        },
        smooth: true,
        lineStyle: {
          opacity: 0.5
        }
      },
      {
        name: 'MA30',
        type: 'line',
        encode: {
          x: 0,
          y: 10
        },
        smooth: true,
        lineStyle: {
          opacity: 0.5
        }
      },
      {
        name: '涨跌幅',
        type: 'line',
        encode: {
          x: 0,
          y: 11
        },
        smooth: true,
        lineStyle: {
          opacity: 0.5
        }
      },
      {
        name: 'Volumn',
        type: 'bar',
        xAxisIndex: 1,
        yAxisIndex: 1,
        itemStyle: {
          color: '#7fbe9e'
        },
        large: true,
        encode: {
          x: 0,
          y: 5
        }
      }
    ]
    ,
    dataZoom: [
      {
        type: 'inside',
        xAxisIndex: [0, 1],
        start: 10,
        end: 100
      },
      {
        show: true,
        xAxisIndex: [0, 1],
        type: 'slider',
        bottom: 10,
        start: 10,
        end: 100
      }
    ],
    visualMap: {
      show: false,
      seriesIndex: 1,
      dimension: 6,
      pieces: [
        {
          value: 1,
          color: this.upColor
        },
        {
          value: -1,
          color: this.downColor
        }
      ]
    },

  });


  /*
        注意：
            虽然在 componentDidMount 中组件已经被装配，
            但是如果设置容器宽高为百分比的值，那么容器的 clientWidth 和 clientHeight 有可能还处于计算中
            这个时候如果在容器中实例化 echarts，echarts 获得的 clientWidth 和 clientHeight 不一定是我们预期的，
            因此这里使用了定时器延迟实例化，也可以提前计算出像素之后 赋值给 width、height，这样不是百分比就没有问题
    */
  async componentDidMount() {

    this.fetchData(this.props);

  }

  fetchData = (params) => {

    let start = moment(params.start, "YYYY-MM-DD").add('days', -20).format('YYYY-MM-DD');
    let end = moment(params.end, "YYYY-MM-DD").add('days', 20).format('YYYY-MM-DD');

    console.log("拉取数据：" + params.code + "," + start + "," + end);

    let url = "quota/kline/day?code=" + params.code + "&start=" + start + "&end=" + end;
    fetch(`${API_BASE_URL}${url}`)
      .then(res => res.json())
      .then(async data => {

        this.setState({
          width: this.props.width,
          height: this.props.height,
          data: data.data,
        });
        console.log(data.data)
        // 初始化图表
        await this.initChart(this.el);
        this.chart.on('click', function (params) {
          // 控制台打印数据的名称
          console.log(params);
        });
        // 将传入的配置(包含数据)注入
        this.setOption(this.initOption(this.splitData(data.data)));

        // 监听屏幕缩放，重新绘制 echart 图表
        // window.addEventListener('resize', throttle(this.resize, 100));

        this.fetchPoints();


      })
  };


  fetchPoints = () => {

    fetch(`${API_BASE_URL}trade_details/trade_points?code=${this.props.code}&start=${this.props.start}&end=${this.props.end}&term=${this.props.term}`)
      .then(res => res.json())
      .then(data => {
        let mdd = [];
        for (let tp in data.data) {
          let md = {}
          md['details'] = data.data[tp]['tradeDetails']
          md['vol'] = data.data[tp]['vol']

          md['name'] = data.data[tp]['name']
          md['value'] = data.data[tp]['name']

          md['coord'] = []
          md['coord'].push(data.data[tp]['day'])
          md['coord'].push(data.data[tp]['price'])
          md['itemStyle'] = {};
          md['label'] = {};
          md['label']['fontWeight'] = "bold";
          if (md['value'] === 'B') {
            md['itemStyle']['color'] = this.upColor
            md['itemStyle']['borderWidth'] = 2
            md['itemStyle']['borderWidth'] = this.upBorderColor
          }
          if (md['value'] === 'S') {
            md['itemStyle']['color'] = this.downColor
            md['itemStyle']['borderWidth'] = 2
            md['itemStyle']['borderWidth'] = this.downBorderColor
          }
          if (md['value'] === 'T') {
            md['itemStyle']['color'] = this.tColor
          }
          if (md['value'] === 'C') {
            md['itemStyle']['color'] = this.clearColor
          }

          mdd.push(md);
        }

        this.options.series[0]['markPoint']['data'] = mdd;
        // 将传入的配置(包含数据)注入

        this.setOption(this.options);


        this.chart.on('click', this.clickGraph);


        // 监听屏幕缩放，重新绘制 echart 图表
        // window.addEventListener('resize', throttle(this.resize, 100));

      });

  };


  clickGraph = (params) => {


    if (params.componentType === 'markPoint') {
      let list = []
      for (let i = 0; i < params.data.details.length; i++) {
        let d = params.data.details[i]
        let option = '【' + d['tradeTime'] + '】以【' + d['price'].toFixed(2) + '】价格【' + (d['opt'] ? '买入' : '卖出') + '】【' + d['vol'] + '】股';
        list.push(option)
      }
      list.push('当前持仓【' + params.data.vol + '】股');


      this.setState({
        visible: true,
        details: list
      })

      // 点击到了 markPoint 上


    } else if (params.componentType === 'series') {
      if (params.seriesType === 'graph') {
        if (params.dataType === 'edge') {
          // 点击到了 graph 的 edge（边）上。
        } else {


          // 点击到了 graph 的 node（节点）上。
        }
      }
    }
  }


  componentDidUpdate() {
    // 每次更新组件都重置
    // this.fetchData();
    console.log(this.props.code)
    // this.setOption(this.options)
  }

  componentWillUnmount() {
    // 组件卸载前卸载图表
    this.dispose();
  }


  render = () => {
    const { width, height, visible } = this.state;

    return (
      <>
        <div
          className="default-chart"
          ref={el => (this.el = el)}
          style={{ width, height }}
        />
        <Modal
          title="交易详情"
          visible={visible}
          closable={false}
          maskClosable={false}
          centered={true}
          footer={[
            <Button type="primary" onClick={() => this.setState({ visible: false })}>
              确定
            </Button>,

          ]}
        >
          <List
            split={true}
            size={"large"}
            bordered
            dataSource={this.state.details}
            renderItem={item => (
              <List.Item>
                {item}
              </List.Item>
            )}
          />
        </Modal>

      </>
    );
  };

  initChart = el => {
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
  setOption = option => {
    if (!this.chart) {
      return;
    }

    const notMerge = this.props.notMerge;
    const lazyUpdate = this.props.lazyUpdate;
    console.log("加载图形", option)
    this.chart.setOption(option, notMerge, lazyUpdate);
  };
  dispose = () => {
    if (!this.chart) {
      return;
    }

    this.chart.dispose();
    this.chart = null;
  };
  resize = () => {
    this.chart && this.chart.resize();
  };
  getInstance = () => {
    return this.chart;
  };

}

export default Kline;
