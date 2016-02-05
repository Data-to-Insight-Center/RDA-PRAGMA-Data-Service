$(document).ready(
function() {
    var internalID = getParameter('id');
    $.ajax({
            type:'POST',
            url: restURL+'/projection/'+ internalID,
            crossDomain:true,
                dataType:'json',
                success: function(data){
                    if(data.success)
                    {
                        var content = "<table class='table table-condensed' style='width: 70%'>";
                        content += "<tr><td style='width: 20%'><b>ProjectionSet PID</b></td><td>"+data.projset.pid+"</td>"
                        content += "<tr><td style='width: 20%'><b>Display Name</b></td><td>"+data.projset.displayName+"</td>";
                        content += "<tr><td style='width: 20%'><b>Scenario Code</b></td><td>"+data.projset.scenarioCode+"</td>";
                        content += "<tr><td style='width: 20%'><b>Bounding Box</b></td><td>"+data.projset.boundingBox+"</td>";
                        content += "<tr><td style='width: 20%'><b>Resolution</b></td><td>"+data.projset.resolution+"</td>";
                        content += "<tr><td style='width: 20%'><b>Last Modified</b></td><td>"+data.projset.lastModified+"</td>";
                        content += "<tr><td style='width: 20%'><b>OccrrenceSet PID</b></td><td>"+data.projset.occurrenceSetPID+"</td>";
                        content += "<tr><td style='width: 20%'><b>Experiment ID</b></td><td>"+data.projset.experimentID+"</td>";
                        content += "<tr><td style='width: 20%'><b>Lifemapper VM PID</b></td><td>"+data.projset.vmpid+"</td>";
                        content += "<tr><td style='width: 20%'><b>Checksum</b></td><td>"+data.projset.checksum+"</td>"
                        content +="</tr></table>";
                        $('#metadata').html(content);
                        $('#name').html(data.projset.displayName);
           
                        var download = "<form action = '"+data.projset.downloadingURL+"'>";
                        download += "<button type='submit' class='btn btn-primary'>Download</button>";
                        download += "</form>";
                        $('#download').html(download);
           
                        var occurrence = "<form action = 'http://hdl.handle.net/"+data.projset.occurrenceSetPID+"'>";
                        occurrence += "<button type='submit' class='btn btn-primary'>Go to Occurrence Set</button>";
                        occurrence += "</form>";
                        $('#occurrence').html(occurrence);
           
                        var prov_nodes=[{id:1,label:data.projset.occurrenceSetPID,group:'mints'},{id:2,label:"Exp "+data.projset.experimentID,      group:'diamonds'},{id:3,label:data.projset.displayName}];
                        var prov_edges=[{from:1,to:2,label:"used"},{from:2,to:3,label:"wasGeneratedBy"}];
                        visProvenance(prov_nodes,prov_edges);
                    }
                    else
                    {
                        $('#metadata').html('<h1 style="color:red">Sorry! Your Data Object does not exist.</h1>');
                        $('#download').html('');
                        $('#occurrence').html('');
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