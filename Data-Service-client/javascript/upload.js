$(document).ready(
function() {
        $('#upload').submit(function(e) {
            e.preventDefault();
            var DTRPID = $("#DTRPID").val();
            var DOname = $("#DOname").val();
            $.ajax({
                type:'GET',
                url: restURL+'/DO/upload?DataType='+DTRPID+'&DOname='+DOname,
                crossDomain:true,
                dataType:'json',
                success: function(data){
                       if(data.success)
                       {
                           window.location.replace("objectupload.html?id="+data.message);
                       }
                       else
                       {
                           $("#result").html("Sorry! Cannot upload Data Object. Please try again.");
                       }
                },
                error:function(data)
                {
                       alert("Unexpected internal error. Please try again.");
                }
        });
    })
});

function getParameter(theParameter) {
    var params = window.location.search.substr(1).split('&');
    
    for (var i = 0; i < params.length; i++) {
        var p=params[i].split('=');
        if (p[0] == theParameter) {
            return decodeURIComponent(p[1]);
        }
    }
    return false;
}