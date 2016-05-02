package pragma.rocks.dataIdentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pragma.rocks.dataIdentity.container.Edge;
import pragma.rocks.dataIdentity.container.Node;
import pragma.rocks.dataIdentity.container.Provenance;
import pragma.rocks.dataIdentity.response.ProvenanceResponse;
import pragma.rocks.dataIdentity.utils.PITUtils;

/**
 * Handles requests for projection set validation
 * 
 */
@RestController
public class DataDiffController {
	@Value("${couchdb.uri}")
	private String couchdb_uri;

	@Value("${dtr.uri}")
	private String dtr_uri;

	@Value("${couchdb.database.object}")
	private String couchdb_db_object;

	@Value("${couchdb.database.publish}")
	private String couchdb_db_publish;

	@Value("${handle.server.uri}")
	private String handle_uri;

	@Value("${pit.uri}")
	private String pit_uri;

	@Value("${pit.record.title}")
	private String pit_title;

	@Value("${pit.record.landingpageAddr}")
	private String pit_landingpageAddr;

	@Value("${pit.record.creationDate}")
	private String pit_creationdate;

	@Value("${pit.record.checksum}")
	private String pit_checksum;

	@Value("${pit.record.predecessorID}")
	private String pit_predecessorID;

	@Value("${pit.record.successorID}")
	private String pit_successorID;

	@Value("${pit.record.license}")
	private String pit_license;

	@RequestMapping("/DO/provenance")
	@ResponseBody
	public ProvenanceResponse DOprovenance(@RequestParam(value = "PID", required = true) String PID) {
		List<Node> nodes = new ArrayList<Node>();
		List<Edge> edges = new ArrayList<Edge>();

		Node root = new Node(1, PID);
		nodes.add(root);

		buildProvenance(PID, 1, nodes, edges);

		Provenance provenance = new Provenance(nodes, edges);

		ProvenanceResponse response = new ProvenanceResponse(true, provenance);

		return response;
	}

	public void buildProvenance(String pid, int nodeid, List<Node> nodes, List<Edge> edges) {
		Map<String, String> metadata = PITUtils.resolvePID(handle_uri, pid);

		if (!metadata.containsKey(pit_predecessorID))
			return;
		else {
			String predecessorsID = metadata.get(pit_predecessorID);

			if (!predecessorsID.equalsIgnoreCase("")) {
				int from = nodeid;
				String[] predecessorslist = predecessorsID.split(",");
				for (String predecessorID : predecessorslist) {
					int node_id = nodes.size() + 1;
					Node predecessor_node = new Node(node_id, predecessorID);
					nodes.add(predecessor_node);
					Edge predecessor_edge = new Edge(from, node_id, "");
					edges.add(predecessor_edge);
					buildProvenance(predecessorID, node_id, nodes, edges);
				}
			} else
				return;
		}
	}

	// public ProvenanceResponse DOcomparison(@RequestParam(value = "PID1",
	// required = true) String PID1,
	// @RequestParam(value = "PID2", required = true) String PID2) {
	// List<Node> nodes1 = new ArrayList<Node>();
	// List<Edge> edges1 = new ArrayList<Edge>();
	//
	// Node root1 = new Node(1, PID1);
	// nodes1.add(root1);
	//
	// buildProvenance(PID1, 1, nodes1, edges1);
	//
	// Provenance provenance = new Provenance(nodes1, edges1);
	//
	// List<Node> nodes2 = new ArrayList<Node>();
	// List<Edge> edges2 = new ArrayList<Edge>();
	//
	// Node root2 = new Node(1, PID2);
	// nodes2.add(root2);
	//
	// buildProvenance(PID2, 1, nodes2, edges2);
	//
	// Provenance provenance2 = new Provenance(nodes2, edges2);
	//
	// return null;
	// }
}
