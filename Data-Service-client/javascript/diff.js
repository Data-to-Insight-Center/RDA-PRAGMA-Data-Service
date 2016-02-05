$(document).ready(
function() {
$('#validate').submit(function(e) {
    e.preventDefault();
    var projPID = $("#projectionPID").val();
    var vmPID = $("#VMPID").val();
    $.ajax({
            type:'GET',
            url: restURL+'/validation?projPID='+projPID+'&vmPID='+vmPID,
            crossDomain:true,
            dataType:'json',
            success: function(data){
                    if(data.success)
                    {
                        $("#result").html(data.message);
                    }
                    else
                    {
                        $("#result").html(data.message);
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