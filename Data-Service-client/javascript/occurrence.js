$(document).ready(
function() {
    var internalID = getParameter('id');
    $.ajax({
            type:'POST',
            url: restURL+'/occurrence/'+internalID,
            crossDomain:true,
                dataType:'json',
                success: function(data){
                    if(data.success)
                    {
                        var content = "<table class='table table-condensed' style='width: 70%'>";
                        content += "<tr><td style='width: 20%'><b>OccurrenceSet PID</b></td><td>"+data.occurset.pid+"</td>"
                        content += "<tr><td style='width: 20%'><b>Display Name</b></td><td>"+data.occurset.displayName+"</td>";
                        content += "<tr><td style='width: 20%'><b>Count</b></td><td>"+data.occurset.count+"</td>";
                        content += "<tr><td style='width: 20%'><b>Last Modified</b></td><td>"+data.occurset.lastModified+"</td>";
                        content += "<tr><td style='width: 20%'><b>VM PID</b></td><td>"+data.occurset.vmpid+"</td>";
                        content += "<tr><td style='width: 20%'><b>Checksum</b></td><td>"+data.occurset.checksum+"</td>"
                        content +="</tr></table>";
                        $('#metadata').html(content);
                        $('#name').html(data.occurset.displayName);
           
                        var button = "<form action = '"+data.occurset.downloadingURL+"'>";
                        button += "<button type='submit' class='btn btn-primary'>Download</button>";
                        button += "</form>";
                        $('#download').html(button);
                    }
                    else
                    {
                        $('#metadata').html('<h1 style="color:red">Sorry! Your Data Object does not exist.</h1>');
                        $('#download').html('');
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