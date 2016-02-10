$(document).ready(
function() {
    var DOid = getParameter('ID');
    var DOname = getParameter('DOname');
    $("#DOname").html(DOname);
    $("#object").html("<iframe src='"+couchDB_dataobject+DOid+"' width=800px height=500px></iframe>");
    $.ajax({
                         type:'GET',
                         url: restURL+'/DO/find/versionpublish?ID='+DOid,
                         crossDomain:true,
                         dataType:'json',
                         success: function(data){
                           if(data.success)
                           {
                                if(data.publishbooleanlist.length!=0)
                                {
                                    var content = "<table class='table table-bordered'>";
                                    content+= "<thead><tr><th>RevisionID</th><th>PID</th><th>Published</th></tr></thead><tboday>";
                                    for (var i=0;i<data.publishbooleanlist.length;i++)
                                    {
                                        if(data.publishbooleanlist[i].success)
                                        {
                                            content+="<tr><td>"+data.publishbooleanlist[i].rev+"</td>";
                                            content+="<td><a href='http://hdl.handle.net/"+data.publishbooleanlist[i].pid+"' target='_blank'>"+data.publishbooleanlist[i].pid+"</a></td>";
                                            content+="<td>Yes</td></tr>"
                                        }
                                        else
                                        {
                                            content+="<tr><td>"+data.publishbooleanlist[i].rev+"</td>";
                                            content+="<td>Not Published</td>";
                                            content+="<td><a href='"+host+"publish.html?ID="+DOid+"&revID="+data.publishbooleanlist[i].rev+"' target='_blank'>Go Publish!</a></td></tr>"
                                        }
                                    }
                                    content+="</tbody></table>";
                                    $('#revision').html(content);
                                }
                                else
                                {
                                    $('#revision').html("No revision history is available.");
                                }
                           }
                           else
                           {
                              $('#revision').html("Unexpected error. Please refresh and try again.");
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