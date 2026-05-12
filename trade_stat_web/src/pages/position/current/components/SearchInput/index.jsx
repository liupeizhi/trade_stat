import { Select } from 'antd';
import qs from 'qs';
import React, { useState, useRef } from 'react';
import { API_BASE_URL } from '@/utils/apiConfig';

const { Option } = Select;

let timeout;
let currentValue;




class SearchInput extends React.Component {
  state = {
    data: [],
    value: undefined,
  };

   fetchStocks = (value, callback)=> {
    // if (timeout) {
    //   clearTimeout(timeout);
    //   timeout = null;
    // }

    fetch(`${API_BASE_URL}historyStocks`)
      .then(response => response.json())
      .then(d => {

          const data = [];
          d.data.forEach(r => {
            data.push({
              value: r['code']+"_"+r['spelling']+"_"+r['name'],
              text: r['name'],
            });
          });

          callback(data);

      });
  }

  componentDidMount() {
    this.fetchStocks(null,data => this.setState({ data }));
  }


  handleSearch = value => {

    console.log("handleSearch="+value)
  };

  handleChange = value => {
    this.setState({ value });
    console.log("handleChange="+value)
    this.props.handleChange(value)
  };



  render() {

    const options = this.state.data.map(d => <Option key={d.value}>{d.text}</Option>);
    return (
      <Select
        allowClear
        showSearch
        value={this.state.value}
        placeholder={this.props.placeholder}
        style={{width:160}}
        defaultActiveFirstOption={false}
        showArrow={true}
        filterOption={(input, option) =>
          option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0
        }
        onSearch={this.handleSearch}
        onChange={this.handleChange}
        onClear = {this.props.onClear}
        notFoundContent={null}
      >
        {options}
      </Select>
    );
  }
}
export default SearchInput
