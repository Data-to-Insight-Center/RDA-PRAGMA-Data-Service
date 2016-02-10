$(document).ready(
function() {
    var DOid = getParameter('ID');
    var revid = getParameter('revid');
    $.ajax({
            type:'GET',
            url: restURL+'/publish/find?objectID='+ DOid+'&objectRevID='+ revid,
            crossDomain:true,
            dataType:'json',
            success: function(data){
                    if(data.success)
                    {
                        var content = "<table class='table table-condensed' style='width: 70%'>";
                        content += "<tr><td style='width: 20%'><b>PID</b></td><td>"+data.pid+"</td>"
                        content += "<tr><td style='width: 20%'><b>Title</b></td><td>"+data.informationtype.title+"</td>";
                        content += "<tr><td style='width: 20%'><b>Creator</b></td><td>"+data.informationtype.creator+"</td>";
                        content += "<tr><td style='width: 20%'><b>Publicatoin Date</b></td><td>"+data.informationtype.publicationDate+"</td>";
                        content += "<tr><td style='width: 20%'><b>Creation Date</b></td><td>"+data.informationtype.creationDate+"</td>";
                        content += "<tr><td style='width: 20%'><b>Checksum</b></td><td>"+data.informationtype.checksum+"</td>";
                        content += "<tr><td style='width: 20%'><b>Data Identifier</b></td><td>"+data.informationtype.dataIdentifier+"</td>";
                        if(data.informationtype.parentID!=null)
                            content += "<tr><td style='width: 20%'><b>Parent Identifier</b></td><td>"+data.informationtype.parentID+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Parent Identifier</b></td><td></td>";
                        if(data.informationtype.childID!=null)
                            content += "<tr><td style='width: 20%'><b>Child Identifier</b></td><td>"+data.informationtype.childID+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Child Identifier</b></td><td></td>";
                        if(data.informationtype.predecessorID!=null)
                            content += "<tr><td style='width: 20%'><b>Predecessor Identifier</b></td><td>"+data.informationtype.predecessorID+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Predecessor Identifier</b></td><td></td>";
                        if(data.informationtype.successorID!=null)
                            content += "<tr><td style='width: 20%'><b>Successor Identifier</b></td><td>"+data.informationtype.successorID+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>Successor Identifier</b></td><td></td>";
                        if(data.informationtype.license!=null)
                            content += "<tr><td style='width: 20%'><b>License</b></td><td>"+data.informationtype.license+"</td>";
                        else
                            content += "<tr><td style='width: 20%'><b>License</b></td><td></td>";
                        content +="</tr></table>";
                        $('#metadata').html(content);
                        $('#name').html(data.informationtype.title);
           
           
                        var DOpage = "<a href='"+couchDB_dataobject+DOid+"@"+revid+"' class='btn btn-primary'>Go to Data Object</a>";
                        $('#DOpage').html(DOpage);
           
                        var predecessors = data.informationtype.predecessorID;
                        var predecessorList = predecessors.split(",");
                        var prov_nodes = [];
                        var prov_edges = [];
                        prov_nodes.push(
                            {
                                        id:1,
                                        label:data.pid,
                                        group:'mints'
                            }
                        );
                        for(var i=0; i<predecessorList.length;i++)
                        {
                            prov_nodes.push({
                                        id:i+2,
                                        label:predecessorList[i],
                                        group:'diamonds'
                            });
                            prov_edges.push({
                                            from:1,
                                            to:i+2,
                                            label:"wasDerivedFrom"
                            })
                        };
           
                        visProvenance(prov_nodes,prov_edges);
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

function visProvenance(prov_nodes, prov_edges){
    var color = 'gray';
    var len = undefined;
    
    var nodes = prov_nodes;
    var edges = prov_edges;
    
    // create a network
    var container = document.getElementById('mynetwork');
    var data = {
    nodes: nodes,
    edges: edges
    };
    var options = {
    nodes: {
    shape: 'dot',
    size: 20,
    font: {
    size: 8,
    color: '#ffffff'
    },
    borderWidth: 2
    },
    edges: {
    font:{
        size:8
    },
    width: 2
    },
    groups: {
    diamonds: {
    color: {background:'red',border:'white'},
    shape: 'diamond'
    },
    mints: {color:'rgb(0,255,140)'},
    icons: {
    shape: 'icon',
    icon: {
    face: 'FontAwesome',
    code: '\uf0c0',
    size: 50,
    color: 'orange'
    }
    },
    source: {
    color:{border:'white'}
    }
    }
    };
    network = new vis.Network(container, data, options);
}