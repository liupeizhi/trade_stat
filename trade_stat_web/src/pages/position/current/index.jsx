import {Button, DatePicker, Drawer, Form, InputNumber, message, Select, Space} from 'antd';
import ProTable from '@ant-design/pro-table';
import umiRequest from 'umi-request';
import React, {useRef, useState, useEffect} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import { API_BASE_URL } from '@/utils/apiConfig';
import moment from 'moment';
import qs from 'qs';

// 添加全局样式以确保表格不换行
const TableStyle = () => {
  useEffect(() => {
    // 创建样式标签
    const style = document.createElement('style');
    style.textContent = `
      .ant-table-thead > tr > th {
        white-space: nowrap !important;
        overflow: hidden !important;
        text-overflow: ellipsis !important;
      }
      .ant-table-tbody > tr > td {
        white-space: nowrap !important;
        overflow: hidden !important;
        text-overflow: ellipsis !important;
      }
    `;
    // 添加到文档头部
    document.head.appendChild(style);
    // 组件卸载时移除样式
    return () => {
      document.head.removeChild(style);
    };
  }, []);
  return null;
};

const {Option} = Select;

// 转换排序方向
const convertDsc = value => {
  if (value === 'ascend') return 'asc'
  if (value === 'descend') return 'desc'
};

// 转换排序字段
const convertfiled = value => {
  if (value === 'tradeTime') return 'trade_time'
  if (value === 'transFee') return 'trans_fee'
  return value;
};

// 构建查询参数
const getQueryParams = (params) => {
  const resp = {
    results: params.pageSize,
    page: params.pageNo,
    pageSize: params.pageSize,
    pageNo: params.pageNo,
    order: convertDsc(params.sortOrder),
    field: convertfiled(params.sortField),
  };

  if (params.code) {
    resp.code = params.code;
  }

  return resp;
};


const Monitor = () => {
  // const history = useHistory();
  const [buyForm] = Form.useForm();
  const [sellForm] = Form.useForm();
  
  // 操作记录抽屉状态
  const [operationRecordVisible, setOperationRecordVisible] = useState(false);
  // 当前操作记录的股票代码
  const [currentStockCode, setCurrentStockCode] = useState('');
  // 操作记录数据
  const [operationRecords, setOperationRecords] = useState([]);
  // 操作记录分页状态
  const [operationRecordPagination, setOperationRecordPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0
  });
  // 操作记录排序状态
  const [operationRecordSort, setOperationRecordSort] = useState({
    sortField: 'tradeTime',
    sortOrder: 'descend'
  });
  
  // 总市值和总盈亏状态
  const [totalMarketValue, setTotalMarketValue] = useState(0);
  const [totalProfit, setTotalProfit] = useState(0);

  // 买入状态
  const [buyState, setBuyState] = useState({
    code: "",
    tradeTime: "",
    vol: 10,
    price: 0.00,
    commission: 0.00,
    tax: 0.00,
    opt: true
  });

  // 卖出状态
  const [sellState, setSellState] = useState({
    code: "",
    tradeTime: "",
    vol: 10,
    price: 0.00,
    commission: 0.00,
    tax: 0.00,
    opt: false
  });


  /** 新建窗口的弹窗 */
  // const [createModalVisible, handleModalVisible] = useState(false);

  const onFinish = (values) => {
    if (!buyState.code) {
      message.error("请选择一只股票");
      return;
    }
    if (!buyState.tradeTime) {
      message.error("请选择交易时间");
      return;
    }
    if (buyState.vol <= 0) {
      message.error("交易数量不能小于1");
      return;
    }
    if (buyState.price <= 0) {
      message.error("交易价格不合法");
      return;
    }


    umiRequest
      .post(`${API_BASE_URL}trade_details`, {
        data: buyState
      })
      .then(function (response) {
        console.log(response);
        if (response['code'] == 0) {
          message.success("保存成功");
          setVisible(false);
          // 自动请求后台接口/position/compute
          const today = moment().format('YYYY-MM-DD');
          umiRequest(`${API_BASE_URL}position/compute?start=${today}`, {
            method: 'GET'
          }).then(function (computeResponse) {
            console.log('计算持仓结果:', computeResponse);
            // 重新加载持仓数据
            actionRef.current?.reload();
          }).catch(function (computeError) {
            console.log('计算持仓失败:', computeError);
          });
        } else {
          message.error(response['message']);
        }
      })
      .catch(function (error) {
        console.log(error);
        message.error(error);
      });

  };

  function changeCode(value) {
    console.log('changeCode:', value);
    setBuyState({...buyState, code: value})
  }

  function changeDate(val, text) {
    console.log('changeDate:', text);
    setBuyState({...buyState, tradeTime: text})
  }

  function changeVol(value) {
    console.log('changeVol:', value);
    setBuyState({...buyState, vol: value})
  }

  function changePrice(value) {
    console.log('changePrice:', value);
    setBuyState({...buyState, price: value})
  }

  function changeCommission(value) {
    console.log('changeCommission:', value);
    setBuyState({...buyState, commission: value})
  }

  function changeTax(value) {
    console.log('changeTax:', value);
    setBuyState({...buyState, tax: value})
  }

  // ========== 卖出相关函数 ==========
  function changeSellCode(value) {
    console.log('changeSellCode:', value);
    setSellState({...sellState, code: value})
  }

  function changeSellDate(val, text) {
    console.log('changeSellDate:', text);
    setSellState({...sellState, tradeTime: text})
  }

  function changeSellVol(value) {
    console.log('changeSellVol:', value);
    setSellState({...sellState, vol: value})
  }

  function changeSellPrice(value) {
    console.log('changeSellPrice:', value);
    setSellState({...sellState, price: value})
  }

  function changeSellCommission(value) {
    console.log('changeSellCommission:', value);
    setSellState({...sellState, commission: value})
  }

  function changeSellTax(value) {
    console.log('changeSellTax:', value);
    setSellState({...sellState, tax: value})
  }

  // 卖出表单提交
  const onSellFinish = (values) => {
    if (!sellState.code) {
      message.error("请选择一只股票");
      return;
    }
    if (!sellState.tradeTime) {
      message.error("请选择交易时间");
      return;
    }
    if (sellState.vol <= 0) {
      message.error("交易数量不能小于1");
      return;
    }
    if (sellState.price <= 0) {
      message.error("交易价格不合法");
      return;
    }

    umiRequest
      .post(`${API_BASE_URL}trade_details`, {
        data: sellState
      })
      .then(function (response) {
        console.log(response);
        if (response['code'] == 0) {
          message.success("卖出成功");
          setSellVisible(false);
          // 自动请求后台接口/position/compute
          const today = moment().format('YYYY-MM-DD');
          umiRequest(`${API_BASE_URL}position/compute?start=${today}`, {
            method: 'GET'
          }).then(function (computeResponse) {
            console.log('计算持仓结果:', computeResponse);
            // 重新加载持仓数据
            actionRef.current?.reload();
          }).catch(function (computeError) {
            console.log('计算持仓失败:', computeError);
          });
        } else {
          message.error(response['message']);
        }
      })
      .catch(function (error) {
        console.log(error);
        message.error(error);
      });
  };

  // 选中当前持仓的股票进行卖出
  function selectStockForSell(record) {
    const now = moment();
    const tradeTime = now.format('YYYY-MM-DD HH:mm:ss');
    const newSellState = {
      ...sellState,
      code: record.code,
      tradeTime: tradeTime,
      vol: record.vol,
      price: record.price,
      commission: 0.00,
      tax: 0.00
    };
    setSellState(newSellState);
    // 更新表单字段值
    sellForm.setFieldsValue({
      sellCode: record.code,
      sellTradeTime: now,
      sellVol: record.vol,
      sellPrice: record.price
    });
    setSellVisible(true);
  }

  // 选中当前持仓的股票进行买入
  function selectStockForBuy(record) {
    const now = moment();
    const tradeTime = now.format('YYYY-MM-DD HH:mm:ss');
    const newBuyState = {
      ...buyState,
      code: record.code,
      tradeTime: tradeTime,
      vol: 10,
      price: record.price,
      commission: 0.00,
      tax: 0.00
    };
    setBuyState(newBuyState);
    // 更新表单字段值
    buyForm.setFieldsValue({
      code: record.code,
      tradeTime: now,
      vol: 10,
      price: record.price
    });
    setVisible(true);
  }

  // 重置买入表单
  function resetBuyForm() {
    setBuyState({
      code: "",
      tradeTime: "",
      vol: 10,
      price: 0.00,
      commission: 0.00,
      tax: 0.00,
      opt: true
    });
    setVisible(true);
  }

  // 重置卖出表单
  function resetSellForm() {
    setSellState({
      code: "",
      tradeTime: "",
      vol: 10,
      price: 0.00,
      commission: 0.00,
      tax: 0.00,
      opt: false
    });
    setSellVisible(true);
  }



  const operationColumns = [
    {
      title: '股票名称', dataIndex: 'name', key: 'name',
      render: (text, record) => {
        try {
          var codeMap = {};
          const codeMapStr = sessionStorage.getItem('codeMap');
          if (codeMapStr) {
            codeMap = JSON.parse(codeMapStr);
          }
          if (codeMap[record.code]) {
            return codeMap[record.code]['name']
          }
        } catch (e) {
          console.error('Failed to parse codeMap from sessionStorage:', e);
        }
        return text || '-';
      },
    },
    { title: '股票代码', dataIndex: 'code', key: 'code' },
    { title: '交易时间', dataIndex: 'tradeTime', key: 'tradeTime', sorter: true },
    {
      title: '操作', dataIndex: 'opt', key: 'opt', sorter: true,
      render: (text) => {
        return text ? '买入' : '卖出';
      },
    },
    {
      title: '价格', dataIndex: 'price', key: 'price', sorter: true,
      render: (text) => {
        return '¥ ' + (typeof text === 'number' ? text.toFixed(2) : '0.00')
      },
    },
    {
      title: '交易量(股)', dataIndex: 'vol', key: 'vol', sorter: true,
      render: (text) => {
        return text + ''
      },
    },
  ];

  function showOperationRecord(record) {
    // 设置当前股票代码
    setCurrentStockCode(record.code);
    // 加载操作记录数据
    loadOperationRecords(record.code);
    // 显示操作记录抽屉
    setOperationRecordVisible(true);
  }
  
  // 加载操作记录数据
  function loadOperationRecords(code, pagination = null, sort = null) {
    const currentPagination = pagination || operationRecordPagination;
    const currentSort = sort || operationRecordSort;
    
    const params = {
      code: code,
      pageNo: currentPagination.current,
      pageSize: currentPagination.pageSize,
      sortOrder: currentSort.sortOrder,
      sortField: currentSort.sortField
    };
    
    umiRequest(`${API_BASE_URL}trade_details/raw_query?${qs.stringify(getQueryParams(params))}`, {
      method: 'GET'
    }).then(function (response) {
      if (response.code === 0) {
        setOperationRecords(response.data);
        // 更新分页信息
        setOperationRecordPagination({
          ...currentPagination,
          total: response.total || 0
        });
        // 更新排序信息
        setOperationRecordSort(currentSort);
      } else {
        message.error(response.message);
        setOperationRecords([]);
        setOperationRecordPagination({
          ...currentPagination,
          total: 0
        });
      }
    }).catch(function (error) {
      console.log(error);
      message.error('加载操作记录失败');
      setOperationRecords([]);
      setOperationRecordPagination({
        ...currentPagination,
        total: 0
      });
    });
  }
  
  // 关闭操作记录抽屉
  function onOperationRecordClose() {
    setOperationRecordVisible(false);
  }
  
  // 处理操作记录表格的分页和排序变化
  function handleOperationRecordTableChange(pagination, filters, sorter) {
    const newSort = {
      sortField: sorter.field || operationRecordSort.sortField,
      sortOrder: sorter.order || operationRecordSort.sortOrder
    };
    
    loadOperationRecords(currentStockCode, pagination, newSort);
  }

  const columns = [
    {
      title: '股票名称', dataIndex: 'name', key: 'name',
      render: (text, record) => {
        try {
          var codeMap = {};
          const codeMapStr = sessionStorage.getItem('codeMap');
          if (codeMapStr) {
            codeMap = JSON.parse(codeMapStr);
          }
          if (codeMap[record.code]) {
            return codeMap[record.code]['name']
          }
        } catch (e) {
          console.error('Failed to parse codeMap from sessionStorage:', e);
        }
        return text || '-';
      },
    },
    {title: '股票代码', dataIndex: 'code', key: 'code'},
    {
      title: '当前价格', dataIndex: 'price', key: 'price', render: (text, record) => {
        const price = typeof record.price === 'number' ? record.price : 0;
        return '¥ ' + price.toFixed(2);
      }, sorter: (a, b) => a.price - b.price,
    },
    {title: '数量', dataIndex: 'vol', key: 'vol'},
    {
      title: '市值', dataIndex: 'marketValue', key: 'marketValue',
      render: (text, record) => {
        const price = typeof record.price === 'number' ? record.price : 0;
        const vol = typeof record.vol === 'number' ? record.vol : 0;
        const marketValue = price * vol;
        return '¥ ' + (typeof marketValue === 'number' && !isNaN(marketValue) ? marketValue.toFixed(2) : '0.00')
      }, sorter: (a, b) => {
        const priceA = typeof a.price === 'number' ? a.price : 0;
        const volA = typeof a.vol === 'number' ? a.vol : 0;
        const priceB = typeof b.price === 'number' ? b.price : 0;
        const volB = typeof b.vol === 'number' ? b.vol : 0;
        return (priceA * volA) - (priceB * volB);
      },
    },
    {
      title: '当前成本', dataIndex: 'currentCost', key: 'currentCost', render: (text, record) => {
        const cost = typeof record.currentCost === 'number' && !isNaN(record.currentCost) ? record.currentCost : 0;
        return '¥ ' + cost.toFixed(2);
      }, sorter: (a, b) => {
        const costA = typeof a.currentCost === 'number' ? a.currentCost : 0;
        const costB = typeof b.currentCost === 'number' ? b.currentCost : 0;
        return costA - costB;
      },
    },
    {
      title: '上次清仓时间', dataIndex: 'lastClearTime', key: 'lastClearTime',
      render: (text, record) => {
        const time = record.lastClearTime;
        if (!time) return '首次建仓';
        return moment(time).format('YYYY-MM-DD HH:mm:ss');
      },
    },
    {
      title: '建仓时间', dataIndex: 'buildPositionTime', key: 'buildPositionTime',
      render: (text, record) => {
        const time = record.buildPositionTime;
        if (!time) return '-';
        return moment(time).format('YYYY-MM-DD HH:mm:ss');
      },
    },
    {
      title: '持仓时间', dataIndex: 'buildPositionTime', key: 'holdTime',
      render: (text, record) => {
        const time = record.buildPositionTime;
        if (!time) return '-';
        const days = moment().diff(moment(time), 'days') + 1;
        return days + '天';
      },
    },
    {
      title: '当前盈亏比例', dataIndex: 'profitRate', key: 'profitRate',
      render: (text, record) => {
        const price = typeof record.price === 'number' ? record.price : 0;
        const currentCost = typeof record.currentCost === 'number' ? record.currentCost : (typeof record.cost === 'number' ? record.cost : 0);
        if (currentCost <= 0) return '0.00 %';
        const profitRate = (price - currentCost) * 100 / currentCost;
        return (typeof profitRate === 'number' && !isNaN(profitRate) ? profitRate.toFixed(2) : '0.00') + " %"
      }, sorter: (a, b) => {
        const priceA = typeof a.price === 'number' ? a.price : 0;
        const costA = typeof a.currentCost === 'number' ? a.currentCost : (typeof a.cost === 'number' ? a.cost : 0);
        const priceB = typeof b.price === 'number' ? b.price : 0;
        const costB = typeof b.currentCost === 'number' ? b.currentCost : (typeof b.cost === 'number' ? b.cost : 0);
        const rateA = costA > 0 ? (priceA - costA) * 100 / costA : 0;
        const rateB = costB > 0 ? (priceB - costB) * 100 / costB : 0;
        return rateA - rateB;
      },
    },
    {
      title: '当前盈亏金额', dataIndex: 'profit', key: 'profit',
      render: (text, record) => {
        const price = typeof record.price === 'number' ? record.price : 0;
        const currentCost = typeof record.currentCost === 'number' ? record.currentCost : (typeof record.cost === 'number' ? record.cost : 0);
        const vol = typeof record.vol === 'number' ? record.vol : 0;
        const profit = (price - currentCost) * vol;
        return '¥ ' + (typeof profit === 'number' && !isNaN(profit) ? profit.toFixed(2) : '0.00')
      }, sorter: (a, b) => {
        const priceA = typeof a.price === 'number' ? a.price : 0;
        const costA = typeof a.currentCost === 'number' ? a.currentCost : (typeof a.cost === 'number' ? a.cost : 0);
        const volA = typeof a.vol === 'number' ? a.vol : 0;
        const priceB = typeof b.price === 'number' ? b.price : 0;
        const costB = typeof b.currentCost === 'number' ? b.currentCost : (typeof b.cost === 'number' ? b.cost : 0);
        const volB = typeof b.vol === 'number' ? b.vol : 0;
        return ((priceA - costA) * volA) - ((priceB - costB) * volB);
      },
    },
    {
      title: '操作', dataIndex: 'action', key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => selectStockForBuy(record)}>买入</a>
          <a onClick={() => selectStockForSell(record)}>卖 出</a>
          <a onClick={() => showOperationRecord(record)}>操作记录</a>
        </Space>
      ),
    },
  ];


  const [visible, setVisible] = useState(false);
  const showDrawer = () => {
    setVisible(true);
  };
  const onClose = () => {
    setVisible(false);
  };

  function disabledDateTime() {
    return {
      disabledHours: () => [0, 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 17, 18, 19, 20, 21, 22, 23, 24],
    };
  }

  function handleCancel() {
  }

  const [sellVisible, setSellVisible] = useState(false);
  const showSellDrawer = () => {
    setSellVisible(true);
  };
  const onSellClose = () => {
    setSellVisible(false);
  };

  const tailLayout = {
    wrapperCol: {offset: 8, span: 16},
  };

  const actionRef = useRef();
  const children = [];
  var codeMap = {};
  try {
    const codeMapStr = sessionStorage.getItem('codeMap');
    if (codeMapStr) {
      codeMap = JSON.parse(codeMapStr);
    }
  } catch (e) {
    console.error('Failed to parse codeMap from sessionStorage:', e);
  }
  Object.keys(codeMap).forEach(function (key) {

    children.push(<Option
      key={codeMap[key]['code'] + "_" + codeMap[key]['spelling'] + "_" + codeMap[key]['name']}
      value={codeMap[key]['code']}>{codeMap[key]['name']}</Option>);

  });


  return (
    <>
      <PageContainer>
        <TableStyle />
        <ProTable
          title={() => '当前持仓'}
          columns={columns.map(col => {
            if (col.key === 'name' || col.key === 'code') {
              return { 
                ...col, 
                fixed: 'left',
                ellipsis: true,
                width: 120,
                onHeaderCell: (column) => {
                  return {
                    style: {
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                    },
                  };
                },
                onCell: (record, rowIndex) => {
                  return {
                    style: {
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                    },
                  };
                },
              };
            }
            if (col.key === 'action') {
              return { 
                ...col, 
                fixed: 'right',
                width: 180,
                onHeaderCell: (column) => {
                  return {
                    style: {
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                    },
                  };
                },
                onCell: (record, rowIndex) => {
                  return {
                    style: {
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                    },
                  };
                },
              };
            }
            return {
              ...col,
              ellipsis: true,
              onHeaderCell: (column) => {
                return {
                  style: {
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                  },
                };
              },
              onCell: (record, rowIndex) => {
                return {
                  style: {
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                  },
                };
              },
            };
          })}
          actionRef={actionRef}
          rowKey={record => record.code}
          // dataSource={data}
          request={async (params = {}, sort, filter) => {
            const response = await umiRequest(`${API_BASE_URL}position/current_positions`, {
              method: 'GET',
              params: {...params},
            });
            console.log(response);
            
            // 计算总市值和总盈亏
            if (response.code === 0 && response.data) {
              let marketValue = 0;
              let profit = 0;
              
              response.data.forEach(record => {
                // 计算市值：当前价格 * 数量
                const price = typeof record.price === 'number' ? record.price : 0;
                const vol = typeof record.vol === 'number' ? record.vol : 0;
                const mv = price * vol;
                marketValue += mv;
                
                // 计算盈亏：(当前价格 - 当前成本) * 数量
                const currentCost = typeof record.currentCost === 'number' ? record.currentCost : (typeof record.cost === 'number' ? record.cost : 0);
                const p = (price - currentCost) * vol;
                profit += p;
              });
              
              setTotalMarketValue(marketValue);
              setTotalProfit(profit);
            }
            
            return response;
          }}
          pagination={false}
          search={false}
          scroll={{ x: 1500 }}
          toolbar={{
            actions: [
              <Button key="buy" type="primary" onClick={resetBuyForm}>
                买入
              </Button>,
              <Button key="sell" type="primary" onClick={resetSellForm}>
                卖出
              </Button>,
            ],
          }}
          footer={() => {
            const safeMarketValue = typeof totalMarketValue === 'number' && !isNaN(totalMarketValue) ? totalMarketValue : 0;
            const safeProfit = typeof totalProfit === 'number' && !isNaN(totalProfit) ? totalProfit : 0;
            return (
              <div style={{ padding: '16px', backgroundColor: '#f5f5f5', borderRadius: '8px', marginTop: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '16px', fontWeight: 'bold' }}>
                  <div>
                    总市值: <span style={{ color: '#1890ff' }}>¥ {safeMarketValue.toFixed(2)}</span>
                  </div>
                  <div>
                    总盈亏: <span style={{ color: safeProfit >= 0 ? '#52c41a' : '#ff4d4f' }}>
                      {safeProfit >= 0 ? '+' : ''}¥ {safeProfit.toFixed(2)}
                    </span>
                  </div>
                </div>
              </div>
            );
          }}
        />


      </PageContainer>

      <Drawer
        title="卖出股票"
        width={720}
        onClose={onSellClose}
        maskClosable={false}
        visible={sellVisible}
        bodyStyle={{paddingBottom: 80}}
      >
        <Form layout="horizontal" hideRequiredMark labelAlign={"right"} onFinish={onSellFinish} name={"sellForm"} form={sellForm}>
          <Form.Item
            name="sellCode"
            label="股票名称"
            rules={[{required: true, message: '请选择股票'}]}
          >
            <Select
              showSearch
              style={{width: '60%'}}
              placeholder="输入字母、代码或者中文"
              notFoundContent={null}
              onChange={changeSellCode}>
              {children}
            </Select>
          </Form.Item>

          <Form.Item
            name="sellTradeTime"
            label="卖出时间"
            rules={[{required: true, message: '请选择卖出时间'}]}
          >
            <DatePicker disabledTime={disabledDateTime}
                        onChange={changeSellDate}
                        format="YYYY-MM-DD HH:mm:ss"
                        showTime
                        style={{width: '60%'}}
            />
          </Form.Item>

          <Form.Item
            name="sellVol"
            label="数量"
            rules={[{required: true, message: '请输入数量'}]}
          >
            <InputNumber min={1} max={1000000000} step={1}
                         addonBefore="+" addonAfter="股" onChange={changeSellVol} style={{width: '60%'}}/>
          </Form.Item>

          <Form.Item
            name="sellPrice"
            label="价格"
            rules={[{required: true, message: '请输入价格'}]}
          >
            <InputNumber min={0.01} max={1000000000} step={0.01}
                         addonBefore="+" addonAfter="¥" onChange={changeSellPrice} style={{width: '60%'}}/>
          </Form.Item>

          <Form.Item
            name="sellCommission"
            label="手续费"
          >
            <InputNumber min={0.00} max={1000000000} step={0.01}
                         addonBefore="+" addonAfter="¥" onChange={changeSellCommission} style={{width: '60%'}}/>
          </Form.Item>

          <Form.Item
            name="sellTax"
            label="税费"
          >
            <InputNumber min={0.00} max={1000000000} step={0.01}
                         addonBefore="+" addonAfter="¥" onChange={changeSellTax} style={{width: '60%'}}/>
          </Form.Item>

          <Form.Item {...tailLayout}>
            <Space>
              <Button type="primary" htmlType="submit">
                卖出
              </Button>
              <Button htmlType="button" onClick={onSellClose}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Drawer>

      <Drawer
        title="买入股票"
        width={720}
        onClose={onClose}
        maskClosable={false}
        visible={visible}
        bodyStyle={{paddingBottom: 80}}
      >
        <Form layout="horizontal" hideRequiredMark labelAlign={"right"} onFinish={onFinish} name={"form"} form={buyForm}>

          <Form.Item
            name="code"
            label="股票名称"
            offset={0}
            span={8}
            rules={[{required: true, message: '请选择股票'}]}
          >
            <Select
              showSearch
              style={{width: '40%'}}
              placeholder="输入字母、代码或者中文"
              notFoundContent={null}
              onChange={changeCode}>
              {children}
            </Select>

          </Form.Item>

          <Form.Item
            name="tradeTime"
            label="买入时间"
            rules={[{required: true, message: '请选择买入时间'}]}
          >
            <DatePicker disabledTime={disabledDateTime}
                        onChange={changeDate}
                        format="YYYY-MM-DD HH:mm:ss"
                        showTime
            />
          </Form.Item>

          <Form.Item
            name="vol"
            label="数量"
            rules={[{required: true, message: '请输入数量'}]}
          >
            <InputNumber min={1} max={1000000000} step={1}
                         addonBefore="+" addonAfter="股" onChange={changeVol}/>
          </Form.Item>

          <Form.Item
            name="price"
            label="价格"
            rules={[{required: true, message: '请输入价格'}]}
          >
            <InputNumber min={0.01} max={1000000000} step={0.01}
                         addonBefore="+" addonAfter="¥" onChange={changePrice}/>

          </Form.Item>
          <Form.Item
            name="commission"
            label="手续费"
          >
            <InputNumber min={0.00} max={1000000000} step={0.01}
                         addonBefore="+" addonAfter="¥" onChange={changeCommission}/>

          </Form.Item>

          <Form.Item
            name="tax"
            label="税费"
          >
            <InputNumber min={0.00} max={1000000000} step={0.01}
                         addonBefore="+" addonAfter="¥" onChange={changeTax}/>

          </Form.Item>
          <Form.Item {...tailLayout}>
            <Space>
              <Button type="primary" htmlType="submit">
                买入
              </Button>
              <Button htmlType="button" onClick={onClose}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Drawer>

      {/* 操作记录抽屉 */}
      <Drawer
        title="操作记录"
        width={720}
        onClose={onOperationRecordClose}
        maskClosable={false}
        visible={operationRecordVisible}
        bodyStyle={{paddingBottom: 80}}
      >
        <ProTable
          columns={operationColumns}
          dataSource={operationRecords}
          rowKey={record => record.id || `${record.code}_${record.tradeTime}`}
          pagination={{
            current: operationRecordPagination.current,
            pageSize: operationRecordPagination.pageSize,
            total: operationRecordPagination.total,
            onChange: (page, pageSize) => {
              const newPagination = {
                ...operationRecordPagination,
                current: page,
                pageSize: pageSize
              };
              loadOperationRecords(currentStockCode, newPagination);
            }
          }}
          search={false}
          showHeader={true}
          onChange={handleOperationRecordTableChange}
          sort={{
            field: operationRecordSort.sortField,
            order: operationRecordSort.sortOrder
          }}
        />
      </Drawer>


    </>

  );
};

export default Monitor;
