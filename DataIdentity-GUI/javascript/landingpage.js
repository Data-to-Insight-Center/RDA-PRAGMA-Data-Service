$(document).ready(
function() {
    var repoID = getParameter('ID');
    $.ajax({
            type:'GET',
            url: identityURL+'PID/resolveRepoID?repoID='+repoID,
            crossDomain:true,
            dataType:'json',
            success: function(data){
                    if(data.success)
                    {
                        var message=JSON.parse(data.message)
                        var content = "<table class='table table-condensed' style='width: 70%'>";
                        content += "<tr><td style='width: 20%'><b>PID</b></td><td>"+message.pid+"</td>"
                        content += "<tr><td style='width: 20%'><b>Creation Date</b></td><td>"+message["Creation date"]+"</td>";
                        if(message["Checksum"] != null)
                            content += "<tr><td style='width: 20%'><b>Checksum</b></td><td>"+message["Checksum"]+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Checksum</b></td><td></td>";
                        if(message["Predecessor identifier"]!=null)
                            content += "<tr><td style='width: 20%'><b>Predecessor Identifier</b></td><td>"+message["Predecessor identifier"]+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Predecessor Identifier</b></td><td></td>";
                        if(message["Successor identifier"]!=null)
                            content += "<tr><td style='width: 20%'><b>Successor Identifier</b></td><td>"+message["Successor identifier"]+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Successor Identifier</b></td><td></td>";
           
                        content +="</tr></table>";
                        $('#pidmetadata').html(content);
           
                        getDOinfo(repoID,dtrURL);
           
                        var prov_nodes = [];
                        var prov_edges = [];
                        prov_nodes.push(
                            {
                                        id:1,
                                        label:message.pid
                            }
                        );
                        var predecessors = message["Predeccessor identifier"];
                        if(predecessors!=null & predecessors!="")
                        {
                            var predecessorList = predecessors.split(",");

                            for(var i=0; i<predecessorList.length;i++)
                            {
                                prov_nodes.push({
                                        id:i+2,
                                        label:predecessorList[i]
                                });
                                prov_edges.push({
                                            from:1,
                                            to:i+2,
                                            label:""
                                })
                            };
                        }
                        visProvenance("network", prov_nodes,prov_edges);
                    }
                    else
                    {
                        $('#metadata').html('<h1 style="color:red">Sorry! Your Data Object does not exist.</h1>');
                        $('#DOpage').html('');
                    }
                },
                error:function(data)
                {
                    alert(data.message);
                }
    });
}
);

function toggler() {
    $("#domainmetadata").toggle();
}

function getDTRinfo(datatype)
{
    $.ajax({
           type:'GET',
           url: dtrJSONURL+datatype,
           crossDomain:true,
           dataType:'json',
           success: function(data){
            var content = "<table class='table table-condensed' style='width: 70%'>";
            content += "<tr><td style='width: 20%'><b>Expected Use</b></td><td>"+data.expectedUses+"</td>"
            content +="</tr></table>";
            $('#usage').html(content);
           },
           error:function(data)
           {
                alert(data);
           }
    });
}

function getDOinfo(repoID,dtrURL) {
    $.ajax({
           type:'GET',
           url: repoURL+'repo/find/metadata?ID='+repoID,
           crossDomain:true,
           dataType:'json',
           success: function(data){
            if(data.success)
            {
                var domain_metadata = JSON.parse(data.message);
                var content = "<table class='table table-condensed' style='width: 70%'>";
                content += "<tr><td style='width: 20%'><b>DO Name</b></td><td>"+domain_metadata.DOname+"</td>";
                content += "<tr><td style='width: 20%'><b>Data Type</b></td><td><a href='"+dtrURL+domain_metadata.DataType+"' target='_blank'>"+domain_metadata.DataType+"</a></td>";
                content +="</tr></table>";
                getDTRinfo(domain_metadata.DataType);
                $('#generalmetadata').html(content);
                var pretty_json = JSON.stringify(domain_metadata.metadata, undefined, 4);
                //$('#domainmetadata').html(pretty_json);
                output(syntaxHighlight(pretty_json));

		var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(domain_metadata.metadata));
var dlAnchorElem = document.getElementById('downloadAnchorElem');
dlAnchorElem.setAttribute("href",     dataStr     );
dlAnchorElem.setAttribute("download", "workflow.json");

            }
            else
            {
                $('#generalmetadata').html("");
            }
           },
           error:function(data)
           {
                alert(data.message);
           }
    });
    var DOpage = "<a href='"+repoURL+"repo/find/data?ID="+repoID+"' class='btn btn-primary'>Download Data Object</a>";
    $('#DOURL').html(DOpage);

};

function output(inp) {
    document.getElementById("domainmetadata").appendChild(document.createElement('pre')).innerHTML = inp;
}

function syntaxHighlight(json) {
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
                        var cls = 'number';
                        if (/^"/.test(match)) {
                            if (/:$/.test(match)) {
                            cls = 'key';
                            } else {
                            cls = 'string';
                            }
                            } else if (/true|false/.test(match)) {
                            cls = 'boolean';
                            } else if (/null/.test(match)) {
                            cls = 'null';
                            }
                            return '<span class="' + cls + '">' + match + '</span>';
                            });
}

function getParameter(theParameter) {
    var params = window.location.search.substr(1).split('&');
    
    for (var i = 0; i < params.length; i++) {
        var p=params[i].split('=');
        if (p[0] == theParameter) {
            return decodeURIComponent(p[1]);
        }
    }
    return false;
};

function visProvenance(element, nodes, edges){
    var data = {
    nodes: nodes,
    edges: edges
    };
    // create a network
    var container = document.getElementById(element);
    
    var options = {
    nodes: {
    shape: 'dot',
    size: 20,
    font: {
    size: 8,
    color: 'black'
    }
    },
    interaction: {dragNodes :true},
    physics: {
    enabled: false
    },
};
    var network = new vis.Network(container, data, options);
}
