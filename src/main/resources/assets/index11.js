// 记录当前选中的文件名（用于高亮）
let currentSelectedFileName = '';
/**
 * 获取URL中携带的参数
 * @param {string} paramName 可选，指定要获取的参数名；不传则返回所有参数的对象
 * @param {string} url 可选，指定要解析的URL；不传则默认解析当前页面地址
 * @returns {string|object|null} 若传paramName则返回对应值（无则返回null），不传则返回所有参数对象
 */
function getUrlParams(paramName, url) {
    // 优先使用传入的url，否则取当前页面地址
    const targetUrl = url || window.location.href;
    // 匹配URL中?后的参数部分（处理hash锚点#的情况）
    const searchStr = targetUrl.split('?')[1]?.split('#')[0];
    if (!searchStr) return paramName ? null : {};

    // 解析参数为键值对对象
    const paramsObj = {};
    searchStr.split('&').forEach(item => {
        const [key, value] = item.split('=');
        if (key) {
            // 解码URL编码的字符（比如空格、中文）
            const decodeKey = decodeURIComponent(key);
            const decodeValue = value ? decodeURIComponent(value) : '';
            paramsObj[decodeKey] = decodeValue;
        }
    });

    // 根据是否传paramName返回对应结果
    return paramName ? (paramsObj[paramName] || null) : paramsObj;
}
// 2. 页面加载完成后执行的核心逻辑
window.addEventListener('DOMContentLoaded', function() {

    // ② 示例1：获取指定参数（比如station和chart）
    const station = getUrlParams('station'); // 获取变电站参数
    // 2. 获取单个指定参数
    console.log(station); // 输出：'xiaocai'

    //根据参数调用后端接口获取设备列表
    const params = {
        station: station,
        pageNum: 1,
        pageSize: 20
    };
})