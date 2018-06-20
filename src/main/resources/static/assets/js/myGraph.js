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

            // var categories = [];
            // categories[0] = { name: '导演' };
            // categories[1] = { name: '演员' };

            graph.nodes.forEach(function (node) {
                // node.itemStyle = null;
                // node.category = node.attributes.modularity_class;
                if(node.name === entity1 || node.name === entity2) {
                    // node.symbolSize = 40;
                    node.itemStyle = { color: '#283593'};
                }
                // node.value = node.symbolSize;
                // Use random x, y
                // node.x = node.y = null;
                // node.draggable = true;
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
                        return 'node: ' + param.data.name;
                    }
                },
                // legend: [{
                //     // selectedMode: 'single',
                //     data: categories.map(function (a) {
                //         return a.name;
                //     })
                // }],
                // animation: false,
                animationEasingUpdate: "quinticInOut",
                animationDurationUpdate: 100,
                series : [
                    {
                        name: '实体关系图',
                        type: 'graph',
                        layout: 'force',
                        draggable: true,
                        symbolSize: 30,
                        data: graph.nodes,
                        links: graph.edges,
                        // categories: categories,
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
                        // edgeSymbolSize: [4, 10],
                        force: {
                            edgeLength: [100,200],
                            repulsion: 100
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
