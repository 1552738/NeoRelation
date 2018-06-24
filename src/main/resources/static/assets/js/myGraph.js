function search() {
    var entity1 = $("#entity1").val();
    var entity2 = $("#entity2").val();
    var jump = $("#jump").val();
    $.ajax({
        type: "get",
        url: '/search?entity1=' + entity1 + '&entity2=' + entity2 + '&jump=' + jump,
        data: {},
        success: function (data) {
            var dom = document.getElementById("container");
            var myChart = echarts.init(dom);
            var option = null;

            var graph = JSON.parse(data);

            var categories = [];
            categories[0] = { name: 'found' };
            categories[1] = { name: 'prefix' };
            categories[2] = { name: 'suffix' };

            graph.nodes.forEach(function (node) {
                if(Math.abs(node.level) < 5) {
                    node.symbolSize = 60-10*(Math.abs(node.level)-1);
                }
                if(node.level > 0) {
                    node.category = 1;
                }
                else if(node.level < 0) {
                    node.category = 2;
                }
                else {
                    node.category = 0;
                    // node.itemStyle = { color: '#c62828'};
                }
                // node.value = node.symbolSize;
            });

            option = {
                title: {
                    text: '实体关系图',
                    top: '3%',
                    left: '3%'
                },
                tooltip: {
                    formatter: function(param) {
                        if (param.dataType === 'edge') {
                            return param.data.source + ' > ' + param.data.target + ': ' + param.data.type;
                        }
                        return param.data.name + '  ' + param.data.level;
                    }
                },
                toolbox: {
                    show : true,
                    feature : {
                        dataView: {show: true, readOnly: false},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                legend: [{
                    // selectedMode: 'single',
                    data: categories.map(function (a) {
                        return a.name;
                    })
                }],
                animationEasingUpdate: "quinticInOut",
                animationDurationUpdate: 100,
                series : [
                    {
                        name: '实体关系图',
                        type: 'graph',
                        layout: 'force',
                        draggable: true,
                        symbolSize: 20,
                        data: graph.nodes,
                        links: graph.edges,
                        categories: categories,
                        roam: true,
                        focusNodeAdjacency: true,
                        lineStyle: {
                            normal: {
                                opacity: 0.8
                            }
                        },
                        label: {
                            normal: {
                                show: true,
                                position: 'inside',
                                formatter: function(param) {
                                    return param.data.name;
                                }
                            }
                        },
                        edgeLabel: {
                            normal: {
                                show: true,
                                textStyle: {
                                    fontSize: 14
                                },
                                capacity: 0.8,
                                formatter: function(param) {
                                    return param.data.type;
                                }
                            }
                        },
                        edgeSymbol: ['none', 'arrow'],
                        force: {
                            edgeLength: [200, 300],
                            repulsion: 200
                        }
                    }
                ]
            };

            myChart.setOption(option);
            if (option && typeof option === "object") {
                myChart.setOption(option, true);
            }
        }
    });
}
