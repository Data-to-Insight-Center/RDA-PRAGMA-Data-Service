$(document).ready(
function() {
    var DataTypePID=getParameter("DataTypePID");
    var DataTypeName = getParameter("DataTypeName");
    $('#DataType').html("DataType - <a href='"+dtrURL+DataTypePID+"' target='_blank'>"+DataTypeName+"</a>");
    $.ajax({
            type:'GET',
            url: restURL+'pidrepo/list/DObyDTR?DataType='+DataTypePID,
            crossDomain:true,
                dataType:'json',
                success: function(data){
                    if(data.success)
                    {
                        if(data.records.length!=0)
                        {
                            var content = "";
                            for(var i=0;i<data.records.length;i++)
                            {
                                content+="<div class='item'><div class='well'>";
                                content+="<p>"+data.records[i].doname+"</p>";
                                content+="<a href='"+handleURL+data.records[i].pid+"' target='_blank'>"+data.records[i].pid+"</a>";
                                content+="</div></div>";
                            }
                            $('#objectIndex').html(content);
                        }
                        else
                        {
                            $('#objectIndex').html("No Data Object is found.");
                        }
                    }
                    else
                    {
                        $('#objectIndex').html("Unexpected error. Please refresh and try again.");
                    }
                },
                error:function(data)
                {
                    alert("Unexpected error. Please refresh and try again.");
                }
    });
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