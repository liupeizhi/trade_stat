import { PageLoading } from '@ant-design/pro-layout';
import { history } from 'umi';
const isDev = process.env.NODE_ENV === 'development';
/** 获取用户信息比较慢的时候会展示一个 loading */

export const initialStateConfig = {
  loading: <PageLoading />,
};

/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */

export async function getInitialState() {
  return {
    settings: {},
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout = ({ initialState }) => {
  return {
    rightContentRender: () => null,
    disableContentMargin: false,
    onPageChange: () => {
      // 无需登录检查
    },
    menuHeaderRender: undefined,
    ...initialState?.settings,
  };
};
