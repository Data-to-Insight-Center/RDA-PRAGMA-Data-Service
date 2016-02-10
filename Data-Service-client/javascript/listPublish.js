$(document).ready(
function() {
    $.ajax({
            type:'GET',
            url: restURL+'/publish/list',
            crossDomain:true,
                dataType:'json',
                success: function(data){
                    if(data.success)
                    {
                        if(data.publishList.length!=0)
                        {
                            var content = "";
                            for(var i=0;i<data.publishList.length;i++)
                            {
                                content+="<div class='item'><div class='well'>";
                                content+="<p>"+data.publishList[i].title+"</p>";
                                content+="<a href='"+host+"landingpage.html?ID="+data.publishList[i].objectID+"&revid="+data.publishList[i].objectRevID+"' target='_blank'>"+data.publishList[i].pid+"</a>";
                                content+="</div></div>";
                            }
                            $('#objectlist').html(content);
                        }
                        else
                        {
                            $('#objectlist').html("No Data Object is found.");
                        }
                    }
                    else
                    {
                        $('#objectlist').html("Unexpected error. Please refresh and try again.");
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