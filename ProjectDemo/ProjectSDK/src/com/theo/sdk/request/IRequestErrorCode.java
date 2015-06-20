package com.theo.sdk.request;

/**
 * 客户端内部错误标示
 * @author Theo
 *
 */
public interface IRequestErrorCode {    

    /** 未知错误 */
    int ERROR_CODE_UNKNOW = -1;
    
    /** 没有请求的Url地址 */
    int ERROR_CODE_NO_URL = ERROR_CODE_UNKNOW - 1;
    
    /** 网络访问错误 */
    int ERROR_CODE_NET_FAILED = ERROR_CODE_NO_URL - 1;
    
    /** 获取到的String不是Json格式 */
    int ERROR_CODE_RESULT_IS_NOT_JSON_STYLE = ERROR_CODE_NET_FAILED - 1;
    
    /** 数据解析错误 */
    int ERROR_CODE_PARSE_DATA_ERROR = ERROR_CODE_RESULT_IS_NOT_JSON_STYLE - 1;
    /** 服务器下发了空数据*/
    int ERROR_CODE_DATA_VACANCY = ERROR_CODE_PARSE_DATA_ERROR - 1;
    
}
