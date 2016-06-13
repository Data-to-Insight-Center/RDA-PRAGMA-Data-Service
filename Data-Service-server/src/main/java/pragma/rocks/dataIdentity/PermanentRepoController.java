package pragma.rocks.dataIdentity;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.util.JSON;

import pragma.rocks.dataIdentity.mongo.PermanentRepository;
import pragma.rocks.dataIdentity.response.MessageListResponse;
import pragma.rocks.dataIdentity.response.MessageResponse;

@RestController
public class PermanentRepoController {
	@Autowired
	private PermanentRepository permanent_repository;

	// DOs in Permanent Repo can only be read and listed;
	// Do not support update and delete

	@RequestMapping("/repo/find/metadata")
	@ResponseBody
	public MessageResponse DOfindMedata(@RequestParam(value = "ID", required = true) String ID) {
		// Connect to MongoDB and return DO metadata as response
		// return
		try {
			GridFSDBFile doc = permanent_repository.findDOByID(ID);
			DBObject doc_metadata = doc.getMetaData();
			// Convert DBObject to message response type
			MessageResponse response = new MessageResponse(true, JSON.serialize(doc_metadata));
			return response;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}

	}

	@RequestMapping(value = "/repo/find/data", method = RequestMethod.GET)
	@ResponseBody
	public void DOfindData(@RequestParam(value = "ID", required = true) String ID, HttpServletResponse response) {
		// Connect to MongoDB and return DO data files as response
		// return
		try {
			GridFSDBFile doc = permanent_repository.findDOByID(ID);
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

	@RequestMapping("/repo/list")
	@ResponseBody
	public MessageListResponse DOlist() {
		// Connect to mongoDB and list all DOs in staging DB
		// return list of DO ids
		try {
			List<GridFSDBFile> DO_list = permanent_repository.listAll();
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

}
