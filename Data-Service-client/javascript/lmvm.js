$(document).ready(
function() {
    var internalID = getParameter('id');
    $.ajax({
            type:'POST',
            url: restURL+'/lmvm/'+internalID,
            crossDomain:true,
                dataType:'json',
                success: function(data){
                    if(data.success)
                    {
                        var content = "<table class='table table-condensed' style='width: 70%'>";
                        content += "<tr><td style='width: 20%'><b>VM PID</b></td><td>"+data.vmobject.pid+"</td>"
                        content += "<tr><td style='width: 20%'><b>VM Identifier</b></td><td>"+data.vmobject.identifier+"</td>";
                        content += "<tr><td style='width: 20%'><b>Display Name</b></td><td>"+data.vmobject.name+"</td>";
                        content += "<tr><td style='width: 20%'><b>Description</b></td><td>"+data.vmobject.description+"</td>";
                        content += "<tr><td style='width: 20%'><b>Keywords</b></td><td>";
                        for(var i=0; i<data.vmobject.keywords.length;i++)
                        {
                            content += data.vmobject.keywords[i].keyword+"  ";
                        }
                        content +="</td></tr></table>";
                        $('#metadata').html(content);
                        $('#name').html(data.vmobject.name);
           
                        var button = "<form action = ''>";
                        button += "<button type='submit' class='btn btn-primary'>Download</button>";
                        button += "</form>";
                        $('#download').html(button);
           
                        var button2 = "<form action = '"+restURL+'/lmvm/'+internalID+"'>";
                        button2 += "<button type='submit' class='btn btn-primary'>Metadata</button>";
                        button2 += "</form>";
                        $('#fullurl').html(button2);
                    }
                    else
                    {
                        $('#metadata').html('<h1 style="color:red">Sorry! Your Data Object does not exist.</h1>');
                        $('#download').html('');
                        $('#fullurl').html('');
                    }
                },
                error:function(data)
                {
                    alert(data.message);
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