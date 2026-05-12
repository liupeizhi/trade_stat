import React, { Suspense } from 'react';
import { GridContent } from '@ant-design/pro-layout';
import IntroduceRow from './components/IntroduceRow';
import { useRequest } from 'umi';
import { fakeChartData } from './service';
import ProfitCard from './components/ProfitCard';
import DistCard from "./components/DistCard";
import { API_BASE_URL } from '@/utils/apiConfig';

// 初始化股票代码映射表
if (!sessionStorage.getItem("codeMap")) {
  fetch(`${API_BASE_URL}stockInfo`)
    .then(res => res.json())
    .then(data => {
      const codeMap = {};
      data.data.forEach(d => {
        codeMap[d['code']] = d;
      });
      sessionStorage.setItem("codeMap", JSON.stringify(codeMap));
    })
    .catch(err => {
      console.error('Failed to load stock info:', err);
    });
}


const Analysis = () => {
  const { loading, data } = useRequest(fakeChartData);

  return (
    <GridContent>
      <>
        <Suspense fallback={null}>
          <IntroduceRow loading={loading} visitData={data ? data : {}} />
        </Suspense>
        <Suspense fallback={null}>
          <ProfitCard
            isActive={() => { }}
            loading={loading}
            width={"100%"}
            height={"300px"}

          />
        </Suspense>

        <Suspense fallback={null}>
          <DistCard

            // handleRangePickerChange={}
            loading={loading}
            width={"100%"}
            height={"500px"}

          />
        </Suspense>


      </>
    </GridContent>
  );
};

export default Analysis;
