$(document).ready(
function() {
    var DOid = getParameter("ID");
    var revid = getParameter("revID");
    $("#object").html("<iframe src='"+couchDB_dataobject+DOid+"@"+revid+"' width=800px height=500px></iframe>");
    $('#publish').submit(function(e) {
                  e.preventDefault();
                  var title = $("#title").val();
                  var creator = $("#creator").val();
                  var landingpageAddr = landingpageURI+"?ID="+DOid+"&revid="+revid;
                  var publicationDate = getDate();
                  var creationDate = $("#creationdate").val();
                  var checksum = $("#checksum").val();
                  var dataIdentifier = $("#dataID").val();
                  var parentID = $("#parentID").val();
                  var childID = $("#childID").val();
                  var predecessorID = $("#predecessorID").val();
                  var successorID = $("#successorID").val();
                  var license = $("#license").val();
                  
                  var informationtype = {"dbID":DOid,"revID":revid,"title":title,"creator":creator,"landingpageAddr":landingpageAddr,"publicationDate":publicationDate,"creationDate":creationDate,"checksum":checksum,"dataIdentifier":dataIdentifier,"parentID":parentID,"childID":childID,"predecessorID":predecessorID,"successorID":successorID,"license":license};
                  
                  $.ajax({
                         type: 'POST',
                         url: 'http://localhost:9001/DO/publish',
                         crossDomain:true,
                         // The key needs to match your method's input parameter (case-sensitive).
                         data: JSON.stringify(informationtype),
                         contentType: 'application/json; charset=utf-8',
                         dataType: 'json',
                         success:
                            function(data)
                            {
                                $("#result").html("<h3 style='color:green'> Data Object published with PID: "
                                                  +data.message+"</h3>");
                            },
                         error:
                            function(data)
                            {
                                alert("Unexpected error. Please refresh and try again.");
                            }
                         });
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

function getDate()
{
    var currentdate = new Date();
    var datetime = currentdate.getFullYear() + "/"
    + (currentdate.getMonth()+1)  + "/"
    + currentdate.getDate() + "T"
    + currentdate.getHours() + ":"
    + currentdate.getMinutes() + ":"
    + currentdate.getSeconds();
    
    return datetime;
}

