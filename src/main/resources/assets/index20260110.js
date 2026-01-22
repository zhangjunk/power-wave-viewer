
// 1. 配置axios默认项（可选）
//axios.defaults.baseURL = 'http://localhost:8099/front/fileDown'; // 后端接口基础地址
axios.defaults.baseURL = '/front/fileDown';
axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8";
let stationName ='';
// 分页核心参数
let paginationParams = {
    pageNum: 1,
    pageSize: 20,
    totalCount: 0,
    totalPage: 0
};
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
    // ① 自动获取URL中的所有参数
    const urlParams = getUrlParams();
    console.log('页面加载获取到的URL参数：', urlParams);

    // ② 示例1：获取指定参数（比如stationName和chart）
    stationName = getUrlParams('stationName'); // 获取变电站参数
    //const chartType = getUrlParams('chart'); // 获取图表类型参数

    // 2. 获取单个指定参数
    console.log(stationName); // 输出：'xiaocai'

    //根据参数调用后端接口获取设备列表
    const queryParams = {
        station: stationName,
        pageNum: 1,
        pageSize: 20
    };
    renderDeviceList(queryParams);

    bindPageEvents();
})
// 加载数据（查询+分页+span赋值）
async function loadData() {
    let fileTypeName = document.getElementById('fileType').value;
    if (stationName=='龙河'){
        fileTypeName=''
    }
    const queryParams = {
        station:stationName,
        fileName: document.getElementById('fileName').value,
        fileType: fileTypeName,
        beginDate: document.getElementById('beginDate').value,
        endDate: document.getElementById('endDate').value,
        pageNum: paginationParams.pageNum,
        pageSize: paginationParams.pageSize
    };
    renderDeviceList(queryParams);
}

//获取后端数据,渲染列表和分页
function renderDeviceList(queryParams){
    // 调用函数
    getDataByGet(queryParams).then(list => {
        // 核心：先统一兜底并判断 rows 是否为有效非空数组
        // 1. 兜底处理：list不存在/rows非数组 → 空数组
        const rows = Array.isArray(list?.rows) ? list.rows : [];
        // 2. 核心判断：rows是否有有效数据
        const hasData = rows.length > 0;

        // 3. 获取列表容器（非空判断）
        const listElement = document.getElementById('list-container');

        // ====== 只有容器存在时，才处理列表渲染 ======
        if (listElement) {
            if (hasData) {
                // rows 不为空：渲染列表项
                listElement.innerHTML = rows.map(obj => {
                    const fileName = obj?.fileName || '未知文件名';
                    return `<li data-file-name="${fileName}" onclick="getGraphData('${fileName}')" ${fileName === currentSelectedFileName ? 'class="active"' : ''}>${fileName}</li>`;
                }).join('');
            } else {
                // rows 为空：显示空数据提示
                listElement.innerHTML = '<li class="empty-tip">暂无数据</li>';
            }
        }

        // ====== 只有 rows 不为空时，才执行分页逻辑 ======
        if (hasData) {
            // 渲染分页组件（仅当有数据时执行）
            initPaginationParams(queryParams, list);
            // 更新分页UI
            updatePaginationUI();
        } else {
            // 可选：空数据时重置分页（避免分页残留旧数据）
            resetPaginationForEmptyData(queryParams);
            updatePaginationUI();
        }
    });
}
// 补充空数据分页重置函数（如果需要）
function resetPaginationForEmptyData(queryParams) {
    window.paginationParams = {
        pageNum: 1,
        pageSize: 20,
        totalCount: 0,
        totalPage: 0
    };
}
function initPaginationParams(params,list) {
    // 更新分页参数
    paginationParams.totalCount = list.total;
    paginationParams.totalPage = Math.ceil(list.total / params.pageSize);
    paginationParams.pageNum = params.pageNum;
    paginationParams.pageSize = params.pageSize;
}
// 分页组件的绑定事件
function bindPageEvents() {
    /**document.querySelector('.search-btn').addEventListener('click', () => {
        paginationParams.pageNum = 1;
        loadData();
    });**/

    document.getElementById('pageSizeSelect').addEventListener('change', (e) => {
        paginationParams.pageSize = parseInt(e.target.value);
        paginationParams.pageNum = 1;
        loadData();
    });

    document.getElementById('prevPageBtn').addEventListener('click', () => {
        if (paginationParams.pageNum > 1) {
            paginationParams.pageNum--;
            loadData();
        }
    });

    document.getElementById('nextPageBtn').addEventListener('click', () => {
        if (paginationParams.pageNum < paginationParams.totalPage) {
            paginationParams.pageNum++;
            loadData();
        }
    });

    // 绑定下载按钮点击事件
    document.getElementById('downloadChart').addEventListener('click', function() {
        // 这里写下载逻辑（比如下载图表为图片）
        if (currentSelectedFileName==''){
            alert('请先选择要下载的文件！');
        }else{
            //下载源dat文件
            const encodedStation = encodeURIComponent(stationName);
            const encodedFileName = encodeURIComponent(currentSelectedFileName);
            const downloadUrl = axios.defaults.baseURL+`/file/download?station=${encodedStation}&fileName=${encodedFileName}`;
            window.location.href = downloadUrl;
        }
    });

    // 绑定查询按钮点击事件
    document.getElementById('searchFileBtn').addEventListener('click', function() {
        loadData();
    });
}

// 更新分页UI
function updatePaginationUI() {
    document.getElementById('currentPage').textContent = paginationParams.pageNum;
    document.getElementById('totalPage').textContent = paginationParams.totalPage;
    document.getElementById('prevPageBtn').disabled = paginationParams.pageNum <= 1;
    document.getElementById('nextPageBtn').disabled = paginationParams.pageNum >= paginationParams.totalPage;
    document.getElementById('pageSizeSelect').value = paginationParams.pageSize;
}


/**
 * 发送GET请求获取数据
 * @param {String} station 站点名称
 * @param {Number} pageNum 页码（从1开始）
 * @param {Number} pageSize 每页条数
 * @returns {Promise} 返回后端的列表数据
 */
async function getDataByGet(params) {
    try {
        // 3. 发送GET请求，参数通过params传递
        const response = await axios.get('/file/station/list', {
            // params：GET请求的参数，会自动拼接到URL
            params: params,
            // 可选：请求超时时间
            timeout: 5000
        });

        // 4. 处理后端返回的结果（假设后端返回统一格式：{code:200, data: [...], message:"成功"}）
        const result = response.data;
        if (result.code === 200) {
            console.log('请求成功，列表数据：', result.data);
            return result.data; // 返回后端的List数据
        } else {
            console.error('接口返回错误：', result.msg);
            alert(`获取数据失败：${result.msg}`);
            return [];
        }
    } catch (error) {
        // 5. 异常处理（网络错误/接口报错）
        if (error.response) {
            // 后端返回错误状态码（如400/500）
            console.error(`接口错误：${error.response.status}，信息：${error.response.data.message}`);
            alert(`接口错误：${error.response.data.message}`);
        } else if (error.request) {
            // 网络错误（后端服务未启动/跨域）
            console.error('网络错误：无法连接到后端服务器');
            alert('网络异常，请检查后端服务是否启动或跨域配置是否正确');
        } else {
            // 其他错误
            console.error('请求错误：', error.message);
            alert('请求失败，请稍后重试');
        }
        return [];
    }
}

// 2. 点击li触发：获取图谱数据（核心新功能）
function getGraphData(fileName) {
    // 清空之前的错误/加载状态，高亮选中的li
    highlightSelectedLi(fileName);

    //获取PRPD图谱数据：相位分辨局部放电图谱
    getPrpdData(fileName);
    //获取PRPS图谱数据：相位分辨脉冲序列图谱
    getPrpsData(fileName);
    //获取SYBX图谱数据：时域波形
    getSybxData(fileName);


}
async function getPrpsData(fileName){
    const handlerKey = 'prps';
    try {
        // 调用后端/file/wave/data接口，传递fileName和handlerKey两个参数
        const response = await axios.get('/file/wave/data', {
            params: {
                station: stationName,
                fileName: fileName,   // 必传：选中的文件名
                handlerKey: handlerKey // 必传：处理器key
            }
        });

        // 后端返回的是AjaxResult，数据在response.data（根据实际结构调整）
        const ajaxResult = response.data;
        // 假设AjaxResult结构：{code:200, msg:"成功", data:{...}}
        if (ajaxResult.code !== 200) {
            throw new Error(ajaxResult.msg || '接口返回失败');
        }
        initPrpsChart(ajaxResult);
    } catch (error) {
        //graphContainer.innerHTML = `<div class="error">获取【${fileName}】的图谱数据失败：${error.message}</div>`;
        console.error('请求/file/wave/data失败：', error);
        console.log('请求参数：', { fileName, handlerKey });
    }
}

function initPrpsChart(ajaxResult){
    prpsChart = echarts.init(document.getElementById('prps-chart'));
    const option = {
        tooltip: {
            trigger: 'item',
            formatter: params => `角度: ${params.value[0]}<br>采样顺序: ${params.value[1]}<br>电压: ${params.value[2].toFixed(1)}`
        },
        // 标题
        title: {
            text: 'PRPS图谱',
            left: 'left',
            textStyle: { color: '#1E88E5', fontSize: 12 }
        },
        // 3D坐标系（必须配置）
        grid3D: {
            //控制 3D 模型的整体大小（占容器的比例/像素）
            width: '80%',    // 宽度，支持百分比（相对容器）或数字（像素）
            height: '80%',   // 高度
            depth: '80%',    // 深度（3D 维度的厚度）

            // 2. 控制 3D 模型在容器中的位置（偏移量）
            left: '5%',     // 左偏移，支持百分比/像素
            top: '5%',      // 上偏移
            right: '5%',     // 右偏移
            bottom: '5%',    // 下偏移

            //3D 坐标系的上下左右前后裁剪（可选，控制可视范围）
            //boxWidth: 100,   // x 轴方向的箱体大小
            //boxHeight: 100,  // y 轴方向的箱体大小
            //boxDepth: 100,   // z 轴方向的箱体大小
            viewControl: {
                center: [0, 0, 0],  // 3D 模型的中心坐标（调整整体位置偏移）
                //autoRotate: true, autoRotateSpeed: 3, distance: 300,
                // 3D视角控制（可通过鼠标旋转/缩放）
                //projection: 'orthographic' // 正交投影（更适合立体柱状图）
                //distance: 100, // 相机距离（越大视角越远，柱子越小）
                alpha: 30,     // 水平旋转角度
                //beta: 20,      // 垂直旋转角度
            }
        },
        // X轴3D（对应“角度”）
        yAxis3D: {
            name: '角度',
            // 核心：设置name与刻度值的距离（单位：像素）
            nameGap: 30,
            // 可选：name的样式配置（辅助优化显示效果）
            /**nameTextStyle: {
                color: '#ff6600', // 名称字体颜色
                fontSize: 16,     // 名称字体大小
                fontWeight: 'bold'// 名称字体加粗
            },**/
            // 可选：调整name的显示位置（配合nameGap使用）
            nameLocation: 'middle', // 位置：start/end/middle（默认end）
            // 可选：name的旋转角度（避免重叠时使用）
            nameRotate: 0,
            type: 'value',
            min: 0,
            interval:90,
            max: 360,
            //offset:5
            axisLine: {
                lineStyle: {
                    color: '#1E88E5',
                    //width: 1, // 轴线宽度（px）
                    //type: 'solid', // 轴线样式：solid（实线）/dashed（虚线）/dotted（点线）
                    //opacity: 1 // 透明度（0~1）
                }
            },
            // 刻度线颜色
            axisTick: {
                lineStyle: {
                    color: '#1E88E5'
                }
            }
        },
        // Y轴3D（对应“采样顺序”）
        xAxis3D: {
            name: '采样顺序',
            // 核心：设置name与刻度值的距离（单位：像素）
            nameGap: 30,
            // 可选：name的样式配置（辅助优化显示效果）
            /**nameTextStyle: {
                color: '#ff6600', // 名称字体颜色
                fontSize: 16,     // 名称字体大小
                fontWeight: 'bold'// 名称字体加粗
            },**/
            // 可选：调整name的显示位置（配合nameGap使用）
            nameLocation: 'middle', // 位置：start/end/middle（默认end）
            // 可选：name的旋转角度（避免重叠时使用）
            nameRotate: 0,
            type: 'value',
            min: 0,
            max: ajaxResult.data.chartData.length/360,
            //offset:5
            axisLine: {
                lineStyle: {
                    color: '#1E88E5',
                    //width: 1, // 轴线宽度（px）
                    //type: 'solid', // 轴线样式：solid（实线）/dashed（虚线）/dotted（点线）
                    //opacity: 1 // 透明度（0~1）
                }
            },
            // 刻度线颜色
            axisTick: {
                lineStyle: {
                    color: '#1E88E5'
                }
            }
        },
        // Z轴3D（对应“电压”，柱状图的高度）
        zAxis3D: {
            name: '电压',
            // 核心：设置name与刻度值的距离（单位：像素）
            nameGap: 30,
            // 可选：name的样式配置（辅助优化显示效果）
            /**nameTextStyle: {
                color: '#ff6600', // 名称字体颜色
                fontSize: 16,     // 名称字体大小
                fontWeight: 'bold'// 名称字体加粗
            },**/
            // 可选：调整name的显示位置（配合nameGap使用）
            nameLocation: 'middle', // 位置：start/end/middle（默认end）
            // 可选：name的旋转角度（避免重叠时使用）
            nameRotate: 0,
            type: 'value',
            offset:5,
            min: Math.floor(ajaxResult.data.minWave/10)*10,
            max: Math.floor(ajaxResult.data.maxWave/10)*10+10,
            interval:10,
            axisLine: {
                lineStyle: {
                    color: '#1E88E5',
                    //width: 1, // 轴线宽度（px）
                    //type: 'solid', // 轴线样式：solid（实线）/dashed（虚线）/dotted（点线）
                    //opacity: 1 // 透明度（0~1）
                }
            },
            // 刻度线颜色
            axisTick: {
                lineStyle: {
                    color: '#1E88E5'
                }
            }
        },
        // 颜色比例尺（对应强度）
        visualMap: {
            min: Math.floor(ajaxResult.data.minWave/10)*10,
            max: Math.floor(ajaxResult.data.maxWave/10)*10+10,
            dimension: 2,
            orient: 'vertical',
            right: 10,
            top: 'center',
            text: ['HIGH', 'LOW'],
            calculable: true,
            inRange: {
                color: ['#003399', '#99CCFF', '#FFFF99', '#FFCC00', '#FF4D00']
            }
        },
        // 3D柱状图系列（核心）
        series: [
            {
                type: 'bar3D',
                data: ajaxResult.data.chartData, // 数据格式：[x, y, z]
                barSize: [1, 1], // 柱子的宽度和深度
                label: {
                    show: false // 不显示柱子上的数值
                },
                // 调整柱子的位置偏移（关键：让柱子远离坐标轴）
                position: {
                    x: 0.5, // x 轴方向偏移（0-1 范围）
                    y: 0.5  // y 轴方向偏移（0-1 范围）
                },
                // 柱子透明度（可选，若仍有遮挡可降低透明度）
                itemStyle: {
                    opacity: 0.9,
                    label: {
                        show: false
                    }
                },
                emphasis: {
                    label: {
                        show: false
                    }
                }
            }
        ]
    }
    prpsChart.setOption(option);
    // 窗口resize自适应
    window.addEventListener('resize', function() {
        prpsChart.resize();
    });
}
async function getPrpdData(fileName){
    const handlerKey = 'prpd';
    try {
        // 调用后端/file/wave/data接口，传递fileName和handlerKey两个参数
        const response = await axios.get('/file/wave/data', {
            params: {
                station: stationName,
                fileName: fileName,   // 必传：选中的文件名
                handlerKey: handlerKey // 必传：处理器key
            }
        });

        // 后端返回的是AjaxResult，数据在response.data（根据实际结构调整）
        const ajaxResult = response.data;
        // 假设AjaxResult结构：{code:200, msg:"成功", data:{...}}
        if (ajaxResult.code !== 200) {
            throw new Error(ajaxResult.msg || '接口返回失败');
        }
        console.log('请求成功，返回数据：', ajaxResult.data.chartData.length);
        const graphData = ajaxResult.data.chartData;
        const envelopeData = generateEnvelopeFromRealData(graphData, 5);
        //const scatterData = generateScatterFromRealData(graphData);
        //     myChart.setOption({
        //       series: [
        //         { name: '幅值包络线', data: envelopeData },
        //         { name: '放电点', data: scatterData }
        //       ]
        //     });

        const data = graphData;  // 散点数据 [相位, 幅值, 频次]

        // 初始化ECharts实例
        const prpdChart = echarts.init(document.getElementById('prpd-chart'));
        const option = {
            // 背景色
            //backgroundColor: '#000',
            // 标题（可选）
            title: {
                text: 'PRPD图谱',
                left: 'left',
                textStyle: { color: '#1E88E5', fontSize: 12 }
            },
            // 提示框
            tooltip: {
                trigger: 'item',
                formatter: params => {
                    return `
                        相位：${params.data[0].toFixed(1)}°<br>
                        幅值：${params.data[1].toFixed(1)}<br>
                        频次：${params.data[2].toFixed(2)}
                    `;
                },
                //textStyle: { color: '#000' }
            },
            // 网格（控制坐标系位置）
            grid: {
                left: 30,
                right: 80,
                top: 50,
                bottom: 50,
                containLabel: true
            },
            // 颜色比例尺（对应强度）
            visualMap: {
                type: 'continuous',
                min: 0,
                max: 10,
                right: 10,
                top: 'center',
                width: 10,
                dimension: 2, // 对应数据的第三个维度（频次）
                seriesIndex: 1,
                inRange: {
                    // 颜色渐变：黑(低) → 黄 → 红(高)，匹配参考图
                    color: ['#ffff00','#FF7043','#D81B60']
                },
                // 比例尺样式
                text: ['HIGH', 'LOW'],
                textStyle: { color: '#666666' },
                calculable: true,
                //borderColor: '#666',
                //backgroundColor: 'rgba(0,0,0,0.5)'
            },
            // 坐标轴配置
            xAxis: {
                type: 'value',
                name: '相位 (°)',
                nameLocation: 'middle',
                nameGap: 30,
                nameTextStyle: { color: '#666666' },
                min: 0,
                max: 360,
                //interval:90,
                interval:45,
                axisLine: { lineStyle: { color: '#666' } },
                axisTick: { lineStyle: { color: '#666' } },
                axisLabel: { color: '#666666'},
                splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
            },
            yAxis: {
                type: 'value',
                name: '幅值',
                nameLocation: 'middle',
                nameGap: 30,
                nameTextStyle: { color: '#666666' },
                min: Math.floor(ajaxResult.data.minWave/10)*10,
                max: Math.floor(ajaxResult.data.maxWave/10)*10,
                interval:10,
                //interval:ajaxResult.data.intervalY,
                axisLine: { lineStyle: { color: '#666' } },
                axisTick: { lineStyle: { color: '#666' } },
                axisLabel: { color: '#666666' },
                splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
            },
            // 系列配置
            series: [
                // 1. 包络线（绿色曲线）
                {
                    name: '幅值包络线',
                    type: 'line',
                    data: envelopeData,
                    smooth: true, // 平滑曲线
                    symbol: 'none', // 隐藏数据点
                    lineStyle: {
                        color: '#666666', // 浅绿色，匹配参考图
                        width: 2,
                        opacity: 0.8
                    },
                    z: 2 // 层级高于散点
                },
                // 2. PRPD散点（核心）
                {
                    name: '放电点',
                    type: 'scatter',
                    data: data,
                    symbolSize: 2, // 散点大小（可调整）
                    itemStyle: {
                        opacity: 0.8 // 透明度，避免重叠过密
                    },
                    z: 1 // 层级低于包络线
                }
            ],
            // ========== 核心兜底配置：无数据时显示提示文字 ==========
            graphic: data.length === 0 ? [{
                type: 'text',
                left: 'center',
                top: 'center',
                style: {
                    text: '当前无有效局放脉冲',
                    fontSize: 20,
                    fontWeight: 'bold',
                    fill: '#009688',
                    textAlign: 'center'
                }
            }] : []
        };
        // 更新series的data
        prpdChart.setOption(option);
        // 窗口resize自适应
        window.addEventListener('resize', function() {
            prpdChart.resize();
        });
    } catch (error) {
        //graphContainer.innerHTML = `<div class="error">获取【${fileName}】的图谱数据失败：${error.message}</div>`;
        console.error('请求/file/wave/data失败：', error);
        console.log('请求参数：', { fileName, handlerKey });
    }
}


// ========== 3. 初始化时域波形图表 ==========
function initTimeDomainChart(chartId, pdData) {
    const container = document.getElementById(chartId);
    const myChart = echarts.init(container);

    // 解析数据
    const [phaseArr, amplitudeArr] = pdData.chartData;
    const timeDomainData = parsePDData(phaseArr, amplitudeArr);
    const { minWave, maxWave } = pdData;

    const option = {
        /**title: {
            text: '时域波形   '+`幅值极值：最小值 ${minWave.toFixed(2)} mV | 最大值 ${maxWave.toFixed(2)} mV`,
            left: 'left',
            textStyle: { color: '#666666', fontSize: 12}
        },**/
        title: [
            // 第一个标题：左侧“时域波形”
            {
                text: '时域波形',
                left: '10px', // 距离左侧10px，避免贴边
                //top: '5px',
                textStyle: { color: '#1E88E5', fontSize: 12 }
            },
            // 第二个标题：右侧幅值文本
            {
                text: `幅值极值：最小值 ${minWave.toFixed(2)} mV | 最大值 ${maxWave.toFixed(2)} mV`,
                right: '10px', // 距离右侧10px
                //top: '5px',
                textStyle: { color: '#666', fontSize: 12 }
            }
        ],
        grid: {
            left: '8%',
            right: '5%',
            top: '15%',
            bottom: '25%',
            containLabel: true // 防止标签溢出
        },
        // X轴：时间（ms），关联工频相位
        xAxis: {
            type: 'value',
            name: '时间 (ms) | 工频相位 (0°~720°)',
            nameLocation: 'middle',
            nameGap: 30,
            axisLabel: { formatter: val => val.toFixed(3) }, // 保留3位小数
            axisLine: {
                lineStyle: {
                    color: '#666666',
                    //width: 1, // 轴线宽度（px）
                    //type: 'solid', // 轴线样式：solid（实线）/dashed（虚线）/dotted（点线）
                    //opacity: 1 // 透明度（0~1）
                }
            },
            // 刻度线颜色
            axisTick: {
                lineStyle: {
                    color: '#666666'
                }
            },
            // 标记工频周期分割线（20ms处，对应360°）
            splitLine: {
                lineStyle: { color: '#666666' },
                show: true
            }
            /**,
            markLine: {
                silent: true,
                lineStyle: { color: '#d4da35', type: 'dashed' },
                data: [{ xAxis: 20, label: { formatter: '360°（20ms）' } }]
            }**/
        },
        // Y轴：幅值（mV），限定极值范围
        yAxis: {
            type: 'value',
            name: '幅值 (mV)',
            nameLocation: 'middle',
            nameGap: 40,
            axisLabel: { formatter: val => val.toFixed(1) },
            axisLine: { lineStyle: { color: '#666666' } },
            // 标记工频周期分割线（20ms处，对应360°）
            splitLine: {
                lineStyle: { color: '#666666' },
                show: true
            },
            // 基于后端返回的极值设置Y轴范围（留10%余量）
            min: -(Math.floor(maxWave/10)*10+10),
            max: Math.floor(maxWave/10)*10+10,
        },
        tooltip: {
            trigger: 'axis',
            //backgroundColor: 'rgba(255,255,255,0.9)',
            borderColor: '#ddd',
            borderWidth: 1,
            textStyle: { color: '#333' },
            // 格式化tooltip：显示时间、相位、幅值
            formatter: function(params) {
                const time = parseFloat(params[0].axisValue).toFixed(6);
                const amp = parseFloat(params[0].value[1]).toFixed(2);
                // 时间转相位：0ms→0°，40ms→720°
                const phase = (time / 40) * 720;
                return `时间：${time} ms<br/>相位：${phase.toFixed(1)}°<br/>幅值：${amp} mV`;
            }
        },
        series: [
            {
                name: '放电信号',
                type: 'line',
                data: timeDomainData,
                smooth: false, // 保留一正一负的原始脉冲形状
                lineStyle: { width: 1, color: '#1E88E5' },
                symbol: 'none', // 隐藏数据点，优化性能
                sampling: 'lttb', // 大数据量采样优化（36000点）
                large: true, // 开启大数据渲染优化
                largeThreshold: 10000 // 阈值：超过10000点启用优化
            }
        ],
        dataZoom: [
            {
                type: 'slider', // 底部缩放滑块
                xAxisIndex: 0,
                height: 20, // 设置dataZoom滑动条高度（关键属性，单位px）
                bottom: '2%',
                start: 0,
                end: 100,
                handleStyle: { color: '#1E88E5' },
                /**backgroundColor: '#f5f5f5',
                borderColor: '#d9d9d9' **/
            },
            {
                type: 'inside', // 鼠标滚轮缩放
                xAxisIndex: 0,
                zoomOnMouseWheel: true,
                moveOnMouseMove: true
            }
        ]
    };

    myChart.setOption(option);
    // 窗口resize时图表自适应
    window.addEventListener('resize', () => {
        myChart.resize();
    });
    return myChart;
}
async function getSybxData(fileName){
    const handlerKey = 'sybx';
    try {
        // 调用后端/file/wave/data接口，传递fileName和handlerKey两个参数
        const response = await axios.get('/file/wave/data', {
            params: {
                station: stationName,
                fileName: fileName,   // 必传：选中的文件名
                handlerKey: handlerKey // 必传：处理器key
            }
        });

        // 后端返回的是AjaxResult，数据在response.data（根据实际结构调整）
        const ajaxResult = response.data;
        // 假设AjaxResult结构：{code:200, msg:"成功", data:{...}}
        if (ajaxResult.code !== 200) {
            throw new Error(ajaxResult.msg || '接口返回失败');
        }
        console.log('请求成功，返回数据：', ajaxResult.data.chartData[1].length);
        //const graphData = ajaxResult.data.chartData;

        // 传入模拟的后端数据（真实场景替换为接口请求）
        initTimeDomainChart('waveform-chart', ajaxResult.data);
    } catch (error) {
        //graphContainer.innerHTML = `<div class="error">获取【${fileName}】的图谱数据失败：${error.message}</div>`;
        console.error('请求/file/wave/data失败：', error);
        console.log('请求参数：', { fileName, handlerKey });
    }
}

// ========== 2. 数据解析：工频相位→时间 + 适配ECharts格式 ==========
/**
 * 解析PD数据（工频相位转时间）
 * @param {Array} phaseArr - 1~720相位数组
 * @param {Array} amplitudeArr - 36000个幅值数组
 * @returns {Array} ECharts所需的[时间(ms), 幅值(mV)]格式数据
 */
function parsePDData(phaseArr, amplitudeArr) {
    const timeDomainData = [];
    const Hz = 50; // 工频频率
    const singleCycleTime = 1000 / Hz; // 单个工频周期时间：20ms
    const totalCycle = 2; // 720°对应2个周期
    const totalTime = singleCycleTime * totalCycle; // 总时间：40ms
    const pointsPerPhase = amplitudeArr.length / phaseArr.length; // 每个相位的采样数：50

    // 遍历每个相位点，分配对应的50个幅值采样点
    phaseArr.forEach((phase, phaseIndex) => {
        // 计算当前相位对应的幅值起始/结束索引
        const startIndex = phaseIndex * pointsPerPhase;
        const endIndex = startIndex + pointsPerPhase;
        const phaseAmplitudes = amplitudeArr.slice(startIndex, endIndex);

        // 相位转基础时间：phase=1 → 0ms，phase=720 → 40ms
        const baseTime = (phase - 1) * (totalTime / (phaseArr.length - 1));

        // 为每个采样点生成精准时间（细化粒度）
        phaseAmplitudes.forEach((amp, sampleIndex) => {
            // 每个采样点的时间步长：40ms / 36000 ≈ 0.00111ms
            const stepTime = totalTime / amplitudeArr.length;
            const exactTime = baseTime + (sampleIndex * stepTime);
            timeDomainData.push([exactTime.toFixed(6), amp]); // 保留精度
        });
    });

    return timeDomainData;
}

// 辅助函数：高亮选中的li
function highlightSelectedLi(fileName) {
    currentSelectedFileName = fileName;
    const allLi = document.querySelectorAll('#list-container li');
    allLi.forEach(li => {
        if (li.dataset.fileName === fileName) {
            li.classList.add('active');
        } else {
            li.classList.remove('active');
        }
    });
}

// ---------------------- 2. 核心：基于真实数据生成包络线 ----------------------
/**
 * 从真实放电数据生成包络线（按相位区间统计最大幅值）
 * @param {Array} rawData 后端返回的原始数据 [{phase, amplitude}, ...]
 * @param {Number} intervalWidth 相位区间宽度（推荐5°，越小越精准）
 * @returns {Array} 包络线数据 [[phase, amplitude], ...]
 */
function generateEnvelopeFromRealData(rawData, intervalWidth = 5) {
    // 步骤1：初始化相位区间的最大幅值存储（key: 区间起始相位, value: 最大幅值）
    const phaseMaxAmplitude = {};
    // 先初始化所有0~360°的区间（避免无数据区间缺失）
    for (let phase = 0; phase <= 360; phase += intervalWidth) {
        phaseMaxAmplitude[phase] = 0; // 无数据时幅值为0
    }

    // 步骤2：遍历原始数据，统计每个区间的最大幅值
    rawData.forEach(item => {
        // 过滤异常值（幅值为负/非数字/超出合理范围）
        //if (typeof item[1] !== 'number' || item[1] < 0 || item[1] > 100) return;
        if (typeof item[1] !== 'number') return;

        // 计算该相位所属的区间起始值（如92° → 90°区间）[92相位的区间起始值是90]
        const intervalStart = Math.floor(item[0] / intervalWidth) * intervalWidth;
        // 确保区间在0~360范围内
        const validInterval = Math.min(Math.max(intervalStart, 0), 360);

        // 更新该区间的最大幅值
        if (item[1] > phaseMaxAmplitude[validInterval]) {
            phaseMaxAmplitude[validInterval] = item[1];
        }
    });

    // 步骤3：转换为ECharts可用的包络线数据（按相位升序排序）
    const envelopeData = Object.keys(phaseMaxAmplitude)
        .map(phase => [Number(phase), phaseMaxAmplitude[phase]])
        .sort((a, b) => a[0] - b[0]);

    return envelopeData;
}

// ---------------------- 3. 生成散点数据（基于真实数据，可选） ----------------------
/**function generateScatterFromRealData(rawData) {
    return rawData.map(item => {
        // 补充强度值（用于颜色映射，可根据幅值/频次计算）
        const intensity = item.amplitude / 100; // 幅值归一化到0-1
        return [item.phase, item.amplitude, intensity];
    });
}**/