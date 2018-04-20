$(document).ready(
function() {
    //var repoDataURL = "http://149.161.154.205:8080/pragma-data-repo/";
    var DataTypePID=getParameter("DataTypePID");
    var DataTypeName = getParameter("DataTypeName");
    var workflow=getParameter("workflow");
    var creator =getParameter("creator");
    var timerange = getParameter("timerange");
    $('.DataType').html("DataType - <a href='"+dtrJSONURL+DataTypePID+"' target='_blank'>"+DataTypeName+"</a>");
    var ajaxTime= new Date().getTime();
    var ajaxTime= new Date().getTime();
    var searchPID = getParameter("searchpid");
    $.ajax({
            type:'GET',
            url: identityURL+'pidrepo/list/DObyDTR?DataType='+DataTypePID,
            crossDomain:true,
                dataType:'json',
                success: function(data){
                    if(data.success)
                    {
                        var totalTime = new Date().getTime()-ajaxTime;
                        if(data.records.length!=0)
                        {
                            var filtered_results = filterResults(data.records,workflow,creator,searchPID);
                            $('.performance').html("<span style='color:#4ECAF0'>"+filtered_results.length+"</span> Data Object found in <span style='color:#4ECAF0'>"+totalTime+"</span> milliseconds");
                            var content="";
                            for(var i=0;i<filtered_results.length;i++)
                            {
                                var doname_token=filtered_results[i].doname.split("_");
                                var repoID=filtered_results[i].repoID;
                                content+= "<div class='col-lg-12' style='border:#000000 1px solid'></div>";
                                content+= "<div class='col-lg-8'>";
                                content+="<h4 style='font-size:16px;'>"+filtered_results[i].doname+"</h4>";
           
                                content+="<a href='"+handleURL+filtered_results[i].pid+"' target='_blank'>"+"<h4 style='font-size:16px;'>"+filtered_results[i].pid+"</h4></a>";
                                content+="<h4 style='font-size:16px;'>DO Name: <span style='font-size:14px;font-weight:500'>"+filtered_results[i].doname+"</span></h4>";
                                content+="<h4 style='font-size:16px;'>Creator: <span style='font-size:14px;font-weight:500'>"+doname_token[0]+"</span></h4>";
                                content+="<h4 style='font-size:16px;'>Timestamp: <span style='font-size:14px;font-weight:500'>"+doname_token[2]+"</span></h4></div>";
                                content+="<div class='col-lg-4' style='padding-top:60px'>";
                                content+="<h5><a href='"+repoDataURL+"repo/find/data?ID="+repoID+"'>Download Data Object</a></h5>";
                                content+="<h5><a href='"+repoURL+"repo/find/metadata?ID="+repoID+"'>Download Metadata Object</a></h5>";
                                content+="</div>";
                            }
                            $('.searchResult').html(content);
                        }
                        else
                        {
                            $('.searchResult').html("No Data Object is found.");
                        }
                    }
                    else
                    {
                        $('#searchResult').html("Unexpected error. Please refresh and try again.");
                    }
                },
                error:function(data)
                {
                    alert("Unexpected error. Please refresh and try again.");
                }
    });
});

function filterResults(results,workflow,creator,searchPID) {
    var filtered_results=[];
    for(var i=0;i<results.length;i++)
    {
        var doname_token=results[i].doname.split("_");
        if(workflow!="")
            if(doname_token[1]!=workflow)
                continue;
        if(creator!="")
            if(doname_token[0]!=creator)
                continue;
        if(searchPID!="")
	    if(results[i].pid!=searchPID)
		continue;
        filtered_results.push(results[i]);
    }
    return filtered_results;
}

$(document).ready(
        function(){
        $('#filter').submit(function(e)
        {
                e.preventDefault();
                var workflow = document.getElementById("workflow");
                var workflow_val=null;
                if(workflow!="")
                            workflow_val=workflow.value;
                            
                var timerange= document.getElementById("daterange");
                var timerange_val=null;
                if(timerange!="")
                            timerange_val=timerange.value;
                            
                var creator= document.getElementById("creator");
                var creator_val=null;
                if(creator!="")
                            creator_val=creator.value;
                        
                var location_token=window.location.href.split("&");
                window.location.href = location_token[0]+"&"+location_token[1]+ "&workflow="+workflow_val+"&timerange="+timerange_val+"&creator="+creator_val;
        })
    }
);

$(document).ready(
        function(){
        $('#search').submit(function(e)
        {
                e.preventDefault();
                var searchpid = document.getElementById("searchpid");
                var searchpid_val=null;
                if(searchpid!="")
                            searchpid_val=searchpid.value;
                var location_token=window.location.href.split("&");
                window.location.href = location_token[0]+"&"+location_token[1]+ "&searchpid="+searchpid_val;
        })
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
