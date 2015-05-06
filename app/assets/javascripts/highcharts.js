requirejs.config({
    shim: {
        'highcharts': {
        }
    },
    paths: {
        'highcharts': '/assets/lib/highcharts/highcharts'
    }
});

require(["jquery", "highcharts"], function( __tes__ ) {
    $(document).ready(function() {
        var time;
        $(".realtime_highcharts").each(function () {
            var title = $(this).attr("data-title");
            var url = $(this).attr("data-url");
            var interval = parseInt($(this).attr("data-interval"));
            $(this).highcharts({
                chart: {
                    type: 'spline',
                    animation: true,
                    events: {
                        load: function () {
                            // set up the updating of the chart each second
                            var series = this.series[0];
                            time = new Date().getTime();
                            $.post(url, {startTime: (time - 600000), endTime: time}, function (data) {
                                for (var i=0;i<data.length;++i) {
                                    var curData = data[i];
                                    var x = curData.time, // current time
                                        y = curData.value;
                                    var shift = (series.data.length >= 10);
                                    series.addPoint([x, y], true, shift);
                                    if (x > time) {
                                        time = x;
                                    }
                                }
                            }, "json");
                            setInterval(function () {
                                var newTime = new Date().getTime();
                                $.post(url, {startTime: time, endTime: newTime}, function (data) {
                                    for (var i=0;i<data.length;++i) {
                                        var curData = data[i];
                                        var x = curData.time, // current time
                                            y = curData.value;
                                        var shift = (series.data.length >= 10);
                                        series.addPoint([x, y], true, shift);
                                        if (x > time) {
                                            time = x;
                                        }
                                    }
                                }, "json");
                            }, interval);
                        }
                    }
                },
                title: {
                    text: title
                },
                xAxis: {
                    type: 'datetime'
                },
                yAxis: {
                    title: {
                        text: 'Percent'
                    },
                    min: 0,
                    plotLines: [{
                        value: 0,
                        width: 1,
                        color: '#808080'
                    }]
                },
                tooltip: {
                    formatter: function () {
                        return Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                            Highcharts.numberFormat(this.y, 2);
                    }
                },
                legend: {
                    enabled: false
                },
                exporting: {
                    enabled: false
                },
                series: [{
                    name: title,
                    data: []
                }]
            });
        });
    });
});