import { Card, Col, DatePicker, Row, Tabs } from 'antd';
import { Column } from '@ant-design/charts';
import numeral from 'numeral';
import styles from '../style.less';
const { RangePicker } = DatePicker;
const { TabPane } = Tabs;
import * as echarts from 'echarts';
import 'zrender/lib/svg/svg';
import React, {Component} from "react";
import moment from "moment";
import { API_BASE_URL } from '@/utils/apiConfig';

class ProfitCard extends Component {

  constructor(props) {
    super(props);
    this.state = {
      code: '',
      visible: false,
      data: [],
      details: [],
    };
    this.chart = null;

  }
  options={}
  initOption = (data,dateData,type,period)=> {
    console.log(type,period)
    if(type ==='line'){
      console.log("折线图")
      this.options = {
        tooltip: {
          trigger: 'axis',
          position: function (pt) {
            return [pt[0], '10%'];
          }
        },
        title: {
          left: 'center',
          text: '累计收益趋势'
        },
        toolbox: {
          feature: {
            dataZoom: {
              yAxisIndex: 'none'
            },
            restore: {},
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dateData
        },
        yAxis: {
          type: 'value',
          // boundaryGap: [0, '100%']
        },
        dataZoom: [{
          start: 90
        }],
        series: [
          {
            name: '累计收益',
            type: 'line',
            symbol: 'none',
            sampling: 'lttb',
            itemStyle: {
              color: 'rgb(255, 70, 131)'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgb(255, 158, 68)'
                },
                {
                  offset: 1,
                  color: 'rgb(255, 70, 131)'
                }
              ])
            },
            data: data
          }
        ]
      };
    }
    if(type==='bar'){
      console.log("柱状图")
      this.options = {
        title: {
          text: this.getTitle(period),
          left: 10
        },
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
        grid: {
          bottom: 90
        },
        dataZoom: [{
          start: 90
        }],
        xAxis: {
          data: dateData,
          silent: false,
          splitLine: {
            show: true
          },
          splitArea: {
            show: false
          }
        },
        yAxis: {
          splitArea: {
            show: false
          }
        },
        series: [
          {
            type: 'bar',
            data: data,
            // Set `large` for large data amount
            large: false
          }
        ]
      };
    }

  };

  getTitle=(period)=>{
    if('day'===period){
      return "每日收益"
    }
    if('week'===period){
      return "每周收益"
    }
    if('month'===period){
      return "每月收益"
    }
    if('year'===period){
      return "每年收益"
    }
}

  async componentDidMount() {

    this.fetchData(this.props);

  }
  fetchData =  (params) => {

    // let start = moment(params.start, "YYYY-MM-DD").add('days',-20).format('YYYY-MM-DD');
    // let end = moment(params.end, "YYYY-MM-DD").add('days',20).format('YYYY-MM-DD');

    // console.log("拉取数据：" + params.code + "," + start + "," + end);
    let period = 'day';
    if(params.period){
      period = params.period;
    }
    let url = `${API_BASE_URL}profit/stats?period=${period}`;
    fetch(url)
      .then(res => res.json())
      .then(async data => {

        this.setState({
          width: this.props.width,
          height: this.props.height,
          data: data.data,
        });
        console.log(data.data)
        let date = []
        let value = []
        let type = 'line'
        for (let day in data.data) {
          date.push(day);

          if(params.field){
            value.push(data.data[day][params.field]);
            type = 'bar'
          }else{
            value.push(data.data[day]['sumProfit']);
          }
        }

        this.initOption(value,date,type,period);

        // 初始化图表
        await this.initChart(this.el);

        this.setOption(this.options);







      })
  };


  onChange=(key)=>{
    this.fetchData({'period':key,'field':'profit'});
  };

  render = () => {
    const {width, height, visible,loading} = this.state;

    return (
      <>
        <Card
          loading={loading}
          bordered={false}
          bodyStyle={{
            padding: 0,
          }}
        >
          <div className={styles.salesCard}>
            <Tabs

              size="large"
              tabBarStyle={{
                marginBottom: 24,
              }}
              onChange={this.onChange}
            >
              <TabPane tab="累计收益趋势" key="profitTrend">
                <Row>
                  <Col xl={24} lg={12} md={12} sm={24} xs={24}>
                    <div className={styles.salesBar}>
                      <div
                        ref={el => (this.el = el)}
                        style={{width, height}}
                      />
                    </div>
                  </Col>
                </Row>
              </TabPane>
              <TabPane tab="每日收益" key="day">
                <Row>
                  <Col xl={24} lg={12} md={12} sm={24} xs={24}>
                    <div className={styles.salesBar}>
                      <div
                        ref={el => (this.el = el)}
                        style={{width, height}}
                      />
                    </div>
                  </Col>
                </Row>
              </TabPane>
              <TabPane tab="每月收益" key="month">
                <Row>
                  <Col xl={24} lg={12} md={12} sm={24} xs={24}>
                    <div className={styles.salesBar}>
                      <div
                        ref={el => (this.el = el)}
                        style={{width, height}}
                      />
                    </div>
                  </Col>
                </Row>
              </TabPane>
              <TabPane tab="每年收益" key="year">
                <Row>
                  <Col xl={24} lg={12} md={12} sm={24} xs={24}>
                    <div className={styles.salesBar}>
                      <div
                        ref={el => (this.el = el)}
                        style={{width, height}}
                      />
                    </div>
                  </Col>
                </Row>
              </TabPane>
            </Tabs>
          </div>
        </Card>
      </>
    );
  };


  initChart = el => {
    console.log(el)
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


export default ProfitCard;
