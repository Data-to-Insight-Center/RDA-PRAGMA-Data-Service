$(document).ready(
function() {
    $.ajax({
            type:'GET',
            url: restURL+'/projection/list',
            crossDomain:true,
                dataType:'json',
                success: function(data){
                    if(data.length != 0)
                    {
                        var content = "<table class='table table-bordered'>";
                        content+= "<thead><tr><th>Name</th><th>PID</th></tr></thead><tboday>"
                        for(var i=0;i<data.length;i++)
                        {
                            content+="<tr><td>"+data[i].displayName+"</td>";
                            content+="<td><a href='http://hdl.handle.net/"+data[i].pid+"'>"+data[i].pid+"</a></td></tr>";
                        }
                        content+="</tbody></table>";
                        $('#listObject').html(content);
                    }
                    else
                    {
                        $('#listObject').html('<h1 style="color:red">No Data Objects are currently available.</h1>');
                    }
                },
                error:function(data)
                {
                    alert("Unexpected error. Please refresh and try again.");
                }
    });
}
);

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