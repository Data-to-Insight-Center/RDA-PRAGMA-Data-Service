$(document).ready(
function() {
    $.ajax({
            type:'GET',
            url: identityURL+'/pidrepo/list/DataType',
            crossDomain:true,
                dataType:'json',
                success: function(data){
                    if(data.success)
                    {
                        if(data.typeDefinitions.length!=0)
                        {
                            var content = "";
                            for(var i=0;i<data.typeDefinitions.length;i++)
                            {
                                content+="<div class='item'><div class='well'>";
                                var DTR_json = data.typeDefinitions[i];
                                content+="<image src='"+dtrJSONURL+DTR_json.identifier+"?payload=logo&disposition=attachment' alt='Data Type Logo' width=250px height=100px><br>";
                                content+="<a href='"+hostURL+"DOIndex.html?DataTypePID="+DTR_json.identifier+"&DataTypeName="+DTR_json.description+"' target='_blank'>"+DTR_json.description+"</a><br>";
                                content+="<a href='"+dtrURL+DTR_json.identifier+"' target='_blank'>"+DTR_json.identifier+"</a>";
                                content+="</div></div>";
                            }
                            $('#DataTypelist').html(content);
                        }
                        else
                        {
                            $('#DataTypelist').html("No Data Object is found.");
                        }
                    }
                    else
                    {
                        $('#DataTypelist').html("Unexpected error. Please refresh and try again.");
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
