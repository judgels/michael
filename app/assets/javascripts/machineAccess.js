require(["jquery"], function( __tes__ ) {
    $(document).ready(function() {
        $(".access_autocomplete").each(function() {
            var that = $(this);
            var elementId = $(this).attr("machineref");
            var machineJid = $("#"+elementId).val();
            $.post(accessListURL, {machineJid: machineJid}, function(data) {
                that.empty();
                for (var i=0;i<data.length;++i) {
                    var datum = data[i];
                    var option = "<option value=" + datum.id + ">" + datum.name + "</option>";
                    that.append(option);
                }
                that.selectpicker('refresh');
            }, "json");
            $("#"+elementId).change(function() {
                var machineJid = $("#"+elementId).val();
                $.post(accessListURL, {machineJid: machineJid}, function(data) {
                    that.empty();
                    for (var i=0;i<data.length;++i) {
                        var datum = data[i];
                        var option = "<option value=" + datum.id + ">" + datum.name + "</option>";
                        that.append(option);
                    }
                    that.selectpicker('refresh');
                }, "json");
            });
        });
    });
});