package pragma.rocks.dataIdentity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.util.JSON;
import pragma.rocks.dataIdentity.container.PIDRecord;
import pragma.rocks.dataIdentity.mongo.PIDRepository;
import pragma.rocks.dataIdentity.mongo.PermanentRepository;
import pragma.rocks.dataIdentity.mongo.StagingDBRepository;
import pragma.rocks.dataIdentity.response.MessageListResponse;
import pragma.rocks.dataIdentity.response.MessageResponse;
import pragma.rocks.dataIdentity.utils.PITUtils;

/**
 * Handles requests for the occurrence set upload and query
 * 
 */
@RestController
public class DataObjectController {
	@Autowired
	private StagingDBRepository Staging_repository;

	@Autowired
	private PermanentRepository permanent_repository;

	@Autowired
	private PIDRepository pid_repository;

	@Value("${dtr.uri}")
	private String dtr_uri;

	@Value("${pit.uri}")
	private String pit_uri;

	@Value("${gui.addr}")
	private String gui_addr;

	@Value("${gui.port}")
	private String gui_port;

	@Value("${server.addr}")
	private String server_addr;

	@Value("${server.port.num}")
	private String server_port;

	@Value("${Timezone}")
	private String timezone;

	@Value("${pit.record.creationDate}")
	private String pit_creationdate;

	@Value("${pit.record.metadataURL}")
	private String pit_metadataURL;

	@Value("${pit.record.checksum}")
	private String pit_checksum;

	@Value("${pit.record.predecessorID}")
	private String pit_predecessorID;

	@Value("${pit.record.successorID}")
	private String pit_successorID;

	/*
	 * @Value("${pit.record.license}") private String pit_license;
	 */

	@RequestMapping(value = "/DO/upload", method = RequestMethod.POST)
	@ResponseBody
	public MessageResponse DOupload(@RequestParam(value = "DataType", required = true) String datatype,
			@RequestParam(value = "DOname", required = true) String DOname,
			@RequestParam(value = "downloadingURL", required = true) String downloadingURL,
			@RequestParam(value = "data", required = true) MultipartFile file, @RequestBody String metadata) {
		try {
			// Create metadata DBObject from input
			DBObject metadataObject = (DBObject) JSON.parse(metadata);
			metadataObject.put("DataType", datatype);
			metadataObject.put("DOname", DOname);
			metadataObject.put("downloadingURL", downloadingURL);

			// Ingest multipart file into inputstream
			byte[] byteArr = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);
			String file_name = file.getOriginalFilename();
			String content_type = file.getContentType();
			// Connect to MongoDB and use GridFS to store metadata and data
			// Return created DO internal id in stagingDB
			String id = Staging_repository.addDO(inputStream, file_name, content_type, metadataObject);
			MessageResponse response = new MessageResponse(true, id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/DO/find/metadata")
	@ResponseBody
	public MessageResponse DOfindMedata(@RequestParam(value = "ID", required = true) String ID) {
		// Connect to MongoDB and return DO metadata as response
		// return
		try {
			GridFSDBFile doc = Staging_repository.findDOByID(ID);
			DBObject doc_metadata = doc.getMetaData();
			// Convert Json Node to message response type
			MessageResponse response = new MessageResponse(true, JSON.serialize(doc_metadata));
			return response;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}

	}

	@RequestMapping(value = "/DO/find/data", method = RequestMethod.GET)
	@ResponseBody
	public void DOfindData(@RequestParam(value = "ID", required = true) String ID, HttpServletResponse response) {
		// Connect to MongoDB and return DO data files as response
		// return
		try {
			GridFSDBFile doc = Staging_repository.findDOByID(ID);
			response.setContentType(doc.getContentType());
			response.setContentLengthLong(doc.getLength());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getFilename() + "\"");
			OutputStream out = response.getOutputStream();
			doc.writeTo(out);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/DO/list")
	@ResponseBody
	public MessageListResponse DOlist() {
		// Connect to mongoDB and list all DOs in staging DB
		// return list of DO ids
		try {
			List<GridFSDBFile> DO_list = Staging_repository.listAll();
			List<String> DO_id_list = new ArrayList<String>();
			for (GridFSDBFile DO : DO_list) {
				String id = DO.getId().toString();
				DO_id_list.add(id);
			}

			MessageListResponse response = new MessageListResponse(true, DO_id_list);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageListResponse response = new MessageListResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/DO/update/metadata")
	@ResponseBody
	public MessageResponse setMetadata(@RequestParam(value = "ID", required = true) String id,
			@RequestBody String metadata) {
		// Connect to MongoDB and set metadata as curation purposes
		// Return a new document ID

		try {
			// Get original Grid FS file
			GridFSDBFile doc = Staging_repository.findDOByID(id);

			// Form updated metadata object
			DBObject metadataObject = (DBObject) JSON.parse(metadata);
			metadataObject.put("DataType", doc.getMetaData().get("DataType"));
			metadataObject.put("DOname", doc.getMetaData().get("DOname"));
			metadataObject.put("downloadingURL", doc.getMetaData().get("downloadingURL"));

			// Push back updated DO to MongoDB and get a new doc ID
			String updated_id = Staging_repository.addDO(doc.getInputStream(), doc.getFilename(), doc.getContentType(),
					metadataObject);
			MessageResponse response = new MessageResponse(true, updated_id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/DO/update/data")
	@ResponseBody
	public MessageResponse setData(@RequestParam(value = "ID", required = true) String id,
			@RequestParam(value = "data", required = true) MultipartFile file) {
		// Connect to MongoDB and set metadata as curation purposes
		// Return a new document ID

		try {
			// Get original Grid FS file
			GridFSDBFile doc = Staging_repository.findDOByID(id);

			// Ingest multipart file into inputstream
			byte[] byteArr = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);
			String file_name = file.getOriginalFilename();
			String content_type = file.getContentType();

			// Push back updated DO to MongoDB and get a new doc ID
			String updated_id = Staging_repository.addDO(inputStream, file_name, content_type, doc.getMetaData());
			MessageResponse response = new MessageResponse(true, updated_id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/DO/delete")
	@ResponseBody
	public MessageResponse DOdelete(@RequestParam(value = "ID", required = true) String ID) {
		// Connect to MongoDB and delete DO
		// Return true or false
		try {
			boolean status = Staging_repository.deleteDOByID(ID);

			// Convert Json Node to message response type
			MessageResponse response = new MessageResponse(status, null);
			return response;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	// After DO upload, edit staging step, user finally register DO with PID
	// (ADD operation)
	// During ADD operation, DO will be copied to permanent repository database
	// and registerd with PID/metadata profile
	@RequestMapping(value = "/DO/add", method = RequestMethod.POST)
	@ResponseBody
	public MessageResponse DOregister(@RequestParam(value = "ID", required = true) String id,
			@RequestParam(value = "predecessorPID") String predPID,
			@RequestParam(value = "successorPID") String succPID) {

		// Connect to MongoDB and get DO information

		try {
			if (Staging_repository.existDOByID(id)) {
				GridFSDBFile doc = Staging_repository.findDOByID(id);

				// Transfer DO from staging database to permanent repository
				// DO in repo can create and read, but update and delete
				// operation is disallowed.
				String repo_id = permanent_repository.addDO(doc.getInputStream(), doc.getFilename(),
						doc.getContentType(), doc.getMetaData());

				// Construct minimum metadata associated with PID for DO
				// The PIT model is using PID as key instead of plain text
				// name(e.g., checksum)
				JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
				ObjectNode PIT_metadata = new ObjectNode(nodeFactory);

				// Put core required types
				// Put landingpage Address
				String landingpageAddr = gui_addr + ":" + gui_port + "/landingpage.html?ID=" + repo_id;
				PIT_metadata.put("URL", landingpageAddr);

				// Put Creation date
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" + timezone);
				PIT_metadata.put(this.pit_creationdate, df.format(doc.getUploadDate()));

				// Put metadata URL
				String metadataURL = this.server_addr + ":" + this.server_port + "/repo/find/metadata?ID=" + repo_id;
				PIT_metadata.put(this.pit_metadataURL, metadataURL);

				// Put Checksum
				PIT_metadata.put(this.pit_checksum, doc.getMD5());

				// Put Predecessor and successor if exists
				// Note: if multiple, separate using comma ","
				PIT_metadata.put(this.pit_predecessorID, predPID);
				PIT_metadata.put(this.pit_successorID, succPID);

				// Register DO with minimum metadata to PIT
				// Return: PID
				// String pid =
				// PITUtils.registerPID("http://pragma8.cs.indiana.edu:8008/rdapit-0.1/pitapi/pid",
				// PIT_metadata);
				String pid = PITUtils.registerPID(pit_uri.trim(), PIT_metadata);

				// Store registered PID record with repoID/DOname/DataType into
				// backend MongoDB
				// collection
				PIDRecord pid_record = new PIDRecord(pid, doc.getMetaData().get("DOname").toString(),
						doc.getMetaData().get("DataType").toString(), repo_id);
				pid_repository.addRecord(pid_record);

				// Return message response with registered PID record
				MessageResponse response = new MessageResponse(true, pid);
				return response;
			} else {
				MessageResponse response = new MessageResponse(false, null);
				return response;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}
}
