import {Col, Row} from 'antd';
import {GridContent, PageContainer} from '@ant-design/pro-layout';
import StockTable from './components/StockTable';
import React, {Component} from "react";


class Stock extends Component {


  constructor(props) {
    super(props);

    this.state = {
      reqBody: {'code': '000786'}
    };
  }


  changeCode = (value) => {
    console.log('changeCode:', this.state.reqBody);
    this.setState({reqBody: {'code': value}});
  }

  search = (body) => {
    console.log(body)
  }


  render() {
    const {reqBody} = this.state;

    return (
      <>
        <GridContent>

            <Row gutter={24}>
            </Row>
            <Row gutter={24}>
              <Col
                xl={24}
                lg={24}
                md={24}
                sm={24}
                xs={24}
                style={{
                  marginBottom: 24,
                }}
              >
                <StockTable onSearch={this.search} reqBody={reqBody}/>
              </Col>
            </Row>

        </GridContent>

      </>
    )


  }
}

export default Stock;

