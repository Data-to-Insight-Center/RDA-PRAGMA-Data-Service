$(document).ready(
function() {
    var repoID = getParameter('ID');
    $.ajax({
            type:'GET',
            url: restURL+'pidrepo/resolveRepoID?repoID='+repoID,
            crossDomain:true,
            dataType:'json',
            success: function(data){
                    if(data.success)
                    {
                        var content = "<table class='table table-condensed' style='width: 70%'>";
                        content += "<tr><td style='width: 20%'><b>PID</b></td><td>"+data.pidmetadata.pid+"</td>"
                        content += "<tr><td style='width: 20%'><b>Creation Date</b></td><td>"+data.pidmetadata.creationDate+"</td>";
                        if(data.pidmetadata.checksum != null)
                            content += "<tr><td style='width: 20%'><b>Checksum</b></td><td>"+data.pidmetadata.checksum+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Checksum</b></td><td></td>";
                        if(data.pidmetadata.predecessorID!=null)
                            content += "<tr><td style='width: 20%'><b>Predecessor Identifier</b></td><td>"+data.pidmetadata.predecessorID+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Predecessor Identifier</b></td><td></td>";
                        if(data.pidmetadata.successorID!=null)
                            content += "<tr><td style='width: 20%'><b>Successor Identifier</b></td><td>"+data.pidmetadata.successorID+"</td>";
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
                                        label:data.pidmetadata.pid
                            }
                        );
                        var predecessors = data.pidmetadata.predecessorID;
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


function getDOinfo(repoID,dtrURL) {
    $.ajax({
           type:'GET',
           url: restURL+'repo/find/metadata?ID='+repoID,
           crossDomain:true,
           dataType:'json',
           success: function(data){
            if(data.success)
            {
                var domain_metadata = JSON.parse(data.message);
                var content = "<table class='table table-condensed' style='width: 70%'>";
                content += "<tr><td style='width: 20%'><b>DO Name</b></td><td>"+domain_metadata.DOname+"</td>"
                content += "<tr><td style='width: 20%'><b>Data Type</b></td><td><a href='"+dtrURL+domain_metadata.DataType+"' target='_blank'>"+domain_metadata.DataType+"</a></td>";
                content +="</tr></table>";
                $('#generalmetadata').html(content);
                var pretty_json = JSON.stringify(domain_metadata, undefined, 4);
                //$('#domainmetadata').html(pretty_json);
                output(syntaxHighlight(pretty_json));
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
    var DOpage = "<a href='"+restURL+"repo/find/data?ID="+repoID+"' class='btn btn-primary'>Download Data Object</a>";
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