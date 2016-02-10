package pragma.rocks.dataLit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for projection set validation
 * 
 */
@RestController
public class DataAuditController {
	@Autowired
	private VMRepository vm_repository;

	@Autowired
	private ProjectionRepository proj_repository;

	@RequestMapping("/validation")
	@ResponseBody
	public MessageResponse validation(@RequestParam(value = "vmPID", required = true) String vm_pid,
			@RequestParam(value = "projPID", required = true) String proj_pid) {
		if (!vm_repository.existByPID(vm_pid)) {
			MessageResponse response = new MessageResponse(false, "VM with PID " + vm_pid + "does not exist.");
			return response;
		}

		if (!proj_repository.existByPID(proj_pid)) {
			MessageResponse response = new MessageResponse(false,
					"Projection Set with PID " + vm_pid + "does not exist.");
			return response;
		}

		String result = "Retrieving Lifemapper VM information type..." + "\n";

		VMObject vm_validation = vm_repository.findObjectByPID(vm_pid);
		ProjectionSet proj_origin = proj_repository.findSetByPID(proj_pid);

		result += "Retrieving original VM of target projection set:" + proj_origin.getVMpid() + "\n";
		VMObject vm_origin = vm_repository.findObjectByPID(proj_origin.getVMpid());

		result += "Comparing validation and original VM metadata with LM VM information type...\n";
		result += "Original VM manifest metadata:\n";
		result += "rocks Version:" + vm_origin.getManifest().getImageManifest().getRocksVersion() + "\n";
		result += "Species Dataset:" + vm_origin.getManifest().getImageManifest().getSpeciesDataset() + "\n";
		result += "Environment Dataset:" + vm_origin.getManifest().getImageManifest().getEnvironmentalDataset() + "\n";
		result += "Roll Version:" + vm_origin.getManifest().getImageManifest().getRollVersion() + "; LM Version:"
				+ vm_origin.getManifest().getImageManifest().getLmVersion() + "\n\n";

		result += "Validation VM manifest metadata:\n";
		result += "rocks Version:" + vm_validation.getManifest().getImageManifest().getRocksVersion() + "\n";
		result += "Species Dataset:" + vm_validation.getManifest().getImageManifest().getSpeciesDataset() + "\n";
		result += "Environment Dataset:" + vm_validation.getManifest().getImageManifest().getEnvironmentalDataset()
				+ "\n";
		result += "Roll Version:" + vm_validation.getManifest().getImageManifest().getRollVersion() + "; LM Version:"
				+ vm_origin.getManifest().getImageManifest().getLmVersion() + "\n\n";

		if (!vm_origin.getManifest().getImageManifest().getRocksVersion()
				.equalsIgnoreCase(vm_validation.getManifest().getImageManifest().getRocksVersion())) {
			MessageResponse response = new MessageResponse(false,
					result + "VM rocks version does not match. Provided VM cannot be used for validation.");
			return response;
		}
		if (!vm_origin.getManifest().getImageManifest().getSpeciesDataset()
				.equalsIgnoreCase(vm_validation.getManifest().getImageManifest().getSpeciesDataset())) {
			MessageResponse response = new MessageResponse(false,
					result + "VM species dataset does not match. Provided VM cannot be used for validation.");
			return response;
		}
		if (!vm_origin.getManifest().getImageManifest().getEnvironmentalDataset()
				.equalsIgnoreCase(vm_validation.getManifest().getImageManifest().getEnvironmentalDataset())) {
			MessageResponse response = new MessageResponse(false,
					result + "VM environment dataset does not match. Provided VM cannot be used for validation.");
			return response;
		}
		if (!vm_origin.getManifest().getImageManifest().getRollVersion()
				.equalsIgnoreCase(vm_validation.getManifest().getImageManifest().getRollVersion())) {
			MessageResponse response = new MessageResponse(false,
					result + "VM roll version does not match. Provided VM cannot be used for validation.");
			return response;
		}
		if (!vm_origin.getManifest().getImageManifest().getLmVersion()
				.equalsIgnoreCase(vm_validation.getManifest().getImageManifest().getLmVersion())) {
			result += "VM Lifemapper version does not match. Provided VM cannot be used for validation.";
			MessageResponse response = new MessageResponse(false, result);
			return response;
		}

		result += "Comparison of VMs matches. Validating target projection set...\n\n";
		result += "Retrieve internal ID of target projection set from validation VM:" + proj_origin.getInternalID()
				+ "\n";
		result += "Retrieve latest version of projection set from validation VM and calculate MD5 checksum...\n";

		String file_url = "http://" + vm_validation.getManifest().getImageManifest().getHostname()
				+ "/services/projections/" + proj_origin.getInternalID() + "/tiff";
		String md5_validation = MD5Utils.getMD5(file_url);
		if (md5_validation == null) {
			result += "Unable to calculate md5 checksum from validation VM.\n";
			MessageResponse response = new MessageResponse(false, result);
			return response;
		}
		result += "Lastest version of projection set MD5Sum:" + md5_validation + "\n";
		result += "Target projection set MD5Sum:" + proj_origin.getChecksum() + "\n\n";

		if (md5_validation.equalsIgnoreCase(proj_origin.getChecksum())) {
			result += "Projection set MD5Sum matches. Target projection set is valid.";
			System.out.println(result);
			MessageResponse response = new MessageResponse(true, result);
			return response;
		} else {
			result += "Projection set MD5Sum does not match. Target projection set is invalid.\n";
			result += "Lastest projection set can be accessed with url: " + file_url;
			System.out.println(result);
			MessageResponse response = new MessageResponse(false, result);
			return response;
		}
	}
}