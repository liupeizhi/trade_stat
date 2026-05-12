import {Card, Col, DatePicker, Row, Table, Tabs, Tooltip} from 'antd';
import {Column, TinyArea} from '@ant-design/charts';
import numeral from 'numeral';
import styles from '../style.less';

const {RangePicker} = DatePicker;
const {TabPane} = Tabs;
const rankingListData = [];
import * as echarts from 'echarts';
import 'zrender/lib/svg/svg';
import React, {Component} from "react";
import moment from "moment";
import {InfoCircleOutlined} from "@ant-design/icons";
import { API_BASE_URL } from '@/utils/apiConfig';


class DistCard extends Component {

  constructor(props) {
    super(props);
    this.state = {
      code: '',
      visible: false,
      data: [],
      details: [],
    };
    this.flowChart = null;
    this.profitChart = null;
    this.timesChart = null;
    this.holdChart = null;


  }

  flowOptions = {}
  profitOptions = {}
  timesOptions = {}
  holdOptions = {}

  initFlowOption = (data) => {
    this.flowOptions = {
      title: {
        text: '资金流水分布',
        subtext: '各股票资金流水',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        type: 'scroll',
        orient: 'vertical',
        right: 10,
        top: 20,
        bottom: 20,
        data: data.legendData
      },
      series: [
        {
          name: '资金流水',
          type: 'pie',
          radius: '55%',
          center: ['40%', '50%'],
          data: data.seriesData,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
  };

  initProfitOption = (data, order) => {
    this.profitOptions = {
      title: {
        text: '利润排名',
        left: 'center'
      },
      dataset: [
        {
          dimensions: ['name', 'profit'],
          source: data
        },
        {
          transform: {
            type: 'sort',
            config: {dimension: 'profit', order: order}
          }
        }
      ],
      toolbox: {
        feature: {
          dataZoom: {
            yAxisIndex: false
          },
          saveAsImage: {
            pixelRatio: 2
          }
        }
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        axisLabel: {interval: 0, rotate: 30}
      },
      yAxis: {
      },
      series: {
        type: 'bar',
        encode: {x: 'name', y: 'profit'},
        datasetIndex: 1
      }
    };
  };

  initHoldOption = (data) => {
    this.holdOptions = {
      title: {
        text: '持股时长排名'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {},
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        boundaryGap: [0, 0.01]
      },
      yAxis: {
        type: 'category',
        data: data.legendData
      },
      series: [
        {
          name: '',
          type: 'bar',
          data: data.seriesData
        }
      ]
    };
  };

  initTimesOption = (data) => {
    this.timesOptions= {
      title: {
        text: '交易次数分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        top: '5%',
        left: 'center'
      },
      series: [
        {
          name: '次数分布',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '40',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: data
        }
      ]
    };
  };


  async componentDidMount() {

    this.fetchData(this.props);

  }
  fetchData =  (params) => {

    // let start = moment(params.start, "YYYY-MM-DD").add('days',-20).format('YYYY-MM-DD');
    // let end = moment(params.end, "YYYY-MM-DD").add('days',20).format('YYYY-MM-DD');

    // console.log("拉取数据：" + params.code + "," + start + "," + end);

    let url = `${API_BASE_URL}dashboard`;
    fetch(url)
      .then(res => res.json())
      .then(async data => {

        this.setState({
          width: this.props.width,
          height: this.props.height,
          // data: data.data,
        });

        var codeMap = JSON.parse(sessionStorage.getItem('codeMap'));
        data = data.data;

        console.log(data)
        console.log("XXXXXX")

        // this.capitalFlows = props.data.capitalFlows;
        // this.profitRanks = data.profitRanks;
        // this.tradeCounts = props.data.tradeCounts;
        // this.holdTimes = props.data.holdTimes;

        let capitalFlow = {}
        capitalFlow['legendData']=[]
        capitalFlow['seriesData']=[]

        for (let code in data.capitalFlows) {
          let name = code;
          if (codeMap[code]) {
            name = codeMap[code]['name']
          }
          capitalFlow['legendData'].push(name);
          capitalFlow['seriesData'].push({'name':name,'value':data.capitalFlows[code]});
        }

        let profits = []
        for (let code in data.profitRanks) {
          let name = code;
          if (codeMap[code]) {
            name = codeMap[code]['name']
          }

          profits.push({'name':name,'profit':data.profitRanks[code]});

        }


        let holdDays = {}
        holdDays['legendData']=[]
        holdDays['seriesData']=[]

        for (let code in data.holdTimes) {
          let name = code;
          if (codeMap[code]) {
            name = codeMap[code]['name']
          }
          holdDays['legendData'].push(name);
          holdDays['seriesData'].push({'name':name,'value':data.holdTimes[code]});
        }



        // this.initHoldOption(this.genData(20));
        //

        let times = []
        for (let code in data.tradeCounts) {
          let name = code;
          if (codeMap[code]) {
            name = codeMap[code]['name']
          }
          times.push({'name':name,'value':data.tradeCounts[code]});

        }
        console.log(times)



        this.initFlowOption(capitalFlow);

        this.initProfitOption(profits, 'desc');
        //

        this.initTimesOption(times);
        //

        this.initHoldOption(holdDays);

        // 初始化图表
        await this.initChart();

        this.setOption(this.flowChart, this.flowOptions);
        this.setOption(this.profitChart, this.profitOptions);
        this.setOption(this.timesChart, this.timesOptions);
        this.setOption(this.holdChart, this.holdOptions);




      })
  };



  render = () => {
    const {width, height, visible, loading} = this.state;

    return (
      <>
        <Card
          loading={loading}
          bordered={false}
          title="数据分布"
          style={{
            height: '100%',
          }}
        >
          <Row gutter={68}>
            <Col
              sm={12}
              xs={24}
              style={{
                marginBottom: 24,
              }}
            >
              <div
                ref={el => (this.flowEl = el)}
                style={{width, height}}
              />

            </Col>
            <Col
              sm={12}
              xs={24}
              style={{
                marginBottom: 24,
              }}
            >
              <div
                ref={el => (this.profitEl = el)}
                style={{width, height}}
              />
            </Col>
          </Row>
          <Row gutter={68}>
            <Col
              sm={12}
              xs={24}
              style={{
                marginBottom: 24,
              }}
            >
              <div
                ref={el => (this.timesEl = el)}
                style={{width, height}}
              />
            </Col>
            <Col
              sm={12}
              xs={24}
              style={{
                marginBottom: 24,
              }}
            >
              <div
                ref={el => (this.holdEl = el)}
                style={{width, height}}
              />
            </Col>
          </Row>
        </Card>
      </>
    );
  };


  initChart = () => {
    console.log(this.flowEl)
    // renderer 用于配置渲染方式 可以是 svg 或者 canvas
    const renderer = this.props.renderer || 'canvas';

    return new Promise(resolve => {
      setTimeout(() => {
        this.flowChart = echarts.init(this.flowEl, null, {
          renderer,
        });

        this.profitChart = echarts.init(this.profitEl, null, {
          renderer,
        });
        this.timesChart = echarts.init(this.timesEl, null, {
          renderer,
        });
        this.holdChart = echarts.init(this.holdEl, null, {
          renderer,
        });
        console.log("初始化图形：" + this.flowEl)
        resolve();
      }, 0);
    });
  };

  setOption = (chart, option) => {
    console.log("this.flowChart"+chart)
    if (!chart) {
      return;
    }

    const notMerge = this.props.notMerge;
    const lazyUpdate = this.props.lazyUpdate;
    console.log("加载图形", option)
    chart.setOption(option, notMerge, lazyUpdate);
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


export default DistCard;
