package rdapit.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.*;

import jersey.repackaged.com.google.common.collect.PeekingIterator;
import rdapit.pitservice.EntityClass;
import rdapit.pitservice.InconsistentRecordsException;
import rdapit.pitservice.PIDInformation;
import rdapit.pitservice.TypingService;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;
import rdapit.typeregistry.ProfileDefinition;

/**
 * This is the main class for REST web service interaction. The class offers
 * basic methods to create, read and delete PID records. Advanced methods
 * include to read individual properties, read property and type definition
 * information or check conformance to types.
 * 
 * All methods return JSON-encoded responses if not explicitly stated otherwise.
 * 
 * <h3>Example calls</h3>
 * 
 * The following calls assume that the REST service is deployed at the base URI
 * <u>http://localhost/pitapi</u>. <br/>
 * 
 * A simple test to check whether the REST service is running can be performed
 * by calling the {@link #simplePing ping} method:
 * 
 * <pre>
 * $ curl http://localhost/pitapi/ping
 * Hello World
 * </pre>
 * 
 * <h4>Query properties of a pid</h4>
 * 
 * To query all properties, simply call {@link #resolvePID /pid}. If the
 * identifier contains a slash, this may be percent-encoded, though the
 * prototype also tolerates path elements in the style of <i>prefix/suffix</i>.<br/>
 * An unsuccessful request will return a 404:
 * 
 * <pre>
 * $ curl http://localhost/pitapi/pid/1234%2F5678
 * ...
 * &lt; HTTP/1.1 404 Not Found
 * ...
 * Identifier not registered
 * </pre>
 * 
 * A successful request may look like this:
 * 
 * <pre>
 * $ curl http://localhost/pitapi/pid/11043.4%2FPITAPI_TEST1
 * {
 *   "values": {
 *       "11314.2/2f305c8320611911a9926bb58dfad8c9": {
 *           "name": "",
 *           "value": "CC-BY"
 *       }
 *   }
 * }
 * </pre>
 * 
 * Note how the result does not provide the property's name. To resolve the
 * name, the parameter <i>include_property_name</i> must be set which makes
 * answering the request however more expensive.<br/>
 * 
 * Special care must be taken in case a PID record should contian more than one
 * value per key. The current implementation will only return the first of these
 * entries; the long-term recommendation is to use JSON-encoded lists in case of
 * multiple values per key.<br/>
 * 
 * The {@link #resolvePID /pid} method also supports HEAD requests for quick
 * checks whether an identifier is registered.
 * 
 * <h4>Using filters</h4>
 * 
 * The pid query method also supports filters. This can be either a filter for a
 * single property or one or several filters for types. Note that it is not
 * possible to combine both property and type filters in a single request.<br/>
 * 
 * Querying a single property works by providing its identifier. If you do not
 * know the identifier but e.g. only the property name (which is not unique),
 * you will have to look at exemplary records or use the search facilities of
 * the type registry to determine the identifier. This may for instance be done
 * as part of a service startup procedure which determines the necessary
 * property and type identifiers and caches them for later use.<br/>
 * The following request queries a "license" property by its identifier:
 * 
 * <pre>
 * $ curl http://localhost/pitapi/pid/11043.4/PITAPI_TEST1?filter_by_property=11314.2%2F2f305c8320611911a9926bb58dfad8c9
 * </pre>
 * 
 * Type filtering is similar, but there can also be several type filters in the
 * same request. This emulates a <i>profile</i> functionality where a profile is
 * understood as a combination of several types: all properties are returned
 * that are specified in any of the types (the selection is thus additive).<br/>
 * Also note that filtering by type doubles as a <b>conformance check</b>, also
 * for several given types. Note that the method will not fail upon conformance
 * failure: If a type lists a mandatory property that is missing from the PID
 * record, the method does not fail but provides simply as many property values
 * as it can.<br/>
 * An artificial example:
 * 
 * <pre>
 * $ curl http://localhost/pitapi/pid/1234/5678?filter_by_type=my%2Ftype_1&filter_by_type=my%2Ftype_2
 * {
 *   "values": {
 *       "11314.2/2f305c8320611911a9926bb58dfad8c9": {
 *           "name": "",
 *           "value": "CC-BY"
 *       }
 *   }
 *   "conformance": {
 *     "my/type1": true,
 *     "my/type2": false
 *   }
 * }
 * </pre>
 * 
 * Overview of all supported parameters for the {@link #resolvePID /pid} method:
 * 
 * <p>
 * <table border="1px">
 * <tbody>
 * <tr>
 * <th>Parameter</th>
 * <th>Cardinality</th>
 * <th>Value type</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>filter_by_property</td>
 * <td>0..1</td>
 * <td>Identifier</td>
 * <td>Filter by given property.</td>
 * </tr>
 * <tr>
 * <td>filter_by_type</td>
 * <td>0..n</td>
 * <td>Identifier</td>
 * <td>Filter by given type(s). Also includes conformance information.</td>
 * </tr>
 * <tr>
 * <td>include_property_names
 * <td>0..1</td>
 * <td>Boolean</td>
 * <td>If true, the method also provies property names along identifiers and
 * values. This is mostly useful for interfaces targeting human end-users. Note
 * that this call comes at additional costs because the property definitions
 * must be retrieved from the type registry.</td>
 * </tr>
 * </tbody>
 * </table>
 * </p>
 * 
 * <h4>Querying property and type definitions</h4>
 * 
 * Since properties and types are registered in the type registry, their
 * metadata can be retrieved. This is also called property and type
 * <i>definitions</i>.<br/>
 * To query a property definition, use the {@link #resolveProperty /property}
 * method:
 * 
 * <pre>
 * $ curl http://localhost/pitapi/property/11314.2/2f305c8320611911a9926bb58dfad8c9
 * {
 *     "identifier": "11314.2/2f305c8320611911a9926bb58dfad8c9",
 *     "name": "License",
 *     "range": "STRING",
 *     "namespace": "RDA",
 *     "description": "License information for a digital object."
 * }
 * </pre>
 * 
 * A similar call exists with the {@link #resolveType /type} method for type
 * definitions.
 * 
 * <h4>Dealing with identifiers where the entity identified is not known</h4>
 * 
 * A particular problem arises if either a machine agent or human user does not
 * know a priori whether an identifier points to a simple object or a property
 * or type definition. This class offers two approaches for solving the problem.<br/>
 * 
 * One option is to determine the entity class first via the
 * {@link #peekIdentifier(String) /peek} method and then use one of the more
 * specific methods ({@link #resolvePID /pid}, {@link #resolveProperty
 * /property}, {@link #resolveType /type}). The alternative is to use the
 * {@link #resolveGenericPID(String) /generic} method.
 * 
 * <h3>Registering new properties and types in the type registry</h3>
 * 
 * Currently, there are no methods provided for registration of new properties
 * and types since the type registry model is quite complex and requires some
 * intelligence of the user that makes it highly doubtful whether this can be
 * done by non-human agents.
 * 
 * When registering new types and properties, the following things must be taken
 * care of:
 * 
 * <p>
 * <ul>
 * <li>
 * The <i>human description</i> field in the type registry is used as the name
 * of the property or type.</li>
 * <li>
 * The <i>explanation of use</i> field in the type registry is used as the
 * description for the property or type.</li>
 * <li>
 * The <i>key value</i> entries hold the core information through which the
 * prototype works. See below for schemas.</li>
 * </ul>
 * </p>
 * 
 * There are two schemas for <i>key value</i> entries, one for property
 * definitions and one for type definitions. Though the keys are not case
 * sensitive, upper case is recommended.<br/>
 * 
 * Property definition schema:
 * <p>
 * <table border="1px">
 * <tbody>
 * <tr>
 * <th>Key</th>
 * <th>Cardinality</th>
 * <th>Value</th>
 * </tr>
 * <tr>
 * <td>PIT_CONSTRUCT</td>
 * <td>1</td>
 * <td>PROPERTY_DEFINITION</td>
 * </tr>
 * <tr>
 * <td>RANGE</td>
 * <td>1</td>
 * <td>The range (value type) of the property. Example: STRING</td>
 * </tr>
 * <tr>
 * <td>NAMESPACE</td>
 * <td>1</td>
 * <td>The namespace of the property. This should be used to distinguish
 * different usage scenarios from each other, e.g. different community
 * understandings.</td>
 * </tr>
 * </tbody>
 * </table>
 * </p>
 * 
 * Type definition schema:
 * <p>
 * <table border="1px">
 * <tbody>
 * <tr>
 * <th>Key</th>
 * <th>Cardinality</th>
 * <th>Value</th>
 * </tr>
 * <tr>
 * <td>PIT_CONSTRUCT</td>
 * <td>1</td>
 * <td>TYPE_DEFINITION</td>
 * </tr>
 * <tr>
 * <td>PROPERTY</td>
 * <td>0..n</td>
 * <td>The properties which make up the type. The value must be a JSON snippet
 * with the registered PID of a property and a mandatory flag.<br/>
 * Example: {"id": "11314.2/2f305c8320611911a9926bb58dfad8c9", "mandatory":true}
 * </td>
 * </tr>
 * </tbody>
 * </table>
 * </p>
 * 
 * 
 */
@Path("pitapi")
@Api(value = "/pitapi", description = "PID Information Types API" )
@Produces({"application/json"})
public class TypingRESTResource {

	protected TypingService typingService;

	public TypingRESTResource() {
		super();
		this.typingService = ApplicationContext.getInstance().getTypingService();
	}

	/**
	 * Simple ping method for testing (check whether the API is running etc.).
	 * Not part of the official interface description.
	 * 
	 * @return responds with 200 OK and a "Hello World" message in the body.
	 */
	@GET
	@Path("/ping")
	@ApiOperation(value = "Ping service", notes = "Determine if service is running", response = String.class)
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Service available"),
	})
	public Response simplePing() {
		return Response.status(200).entity("Hello World").build();
	}

	/**
	 * Generic resolution method to read PID records, property or type
	 * definitions. Optionally implemented method. May be slower than the
	 * specialized methods due to an increased number of back-end requests.
	 * 
	 * @param identifier
	 *            an identifier string
	 * @return depending on the nature of the identified entity, the result can
	 *         be a PID record, a property or a type definition.
	 * @throws IOException
	 */
	@GET
	@Path("/generic/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Lookup by identifier", notes = "More notes about this method", response = Object.class)
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found") 
	})	
	public Response resolveGenericPID(
			@ApiParam(value = "ID of entity") @PathParam("identifier") String identifier) 
			throws IOException {
		Object obj = typingService.genericResolve(identifier);
		if (obj == null)
			return Response.status(404).build();
		return Response.status(200).entity(obj).build();
	}

	/**
	 * Similar to {@link #resolveGenericPID(String)} but supports native slashes
	 * in the identifier path.
	 * 
	 * @see #resolveGenericPID(String)
	 */
	@GET
	@Path("/generic/{prefix}/{suffix}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Lookup by identifier", notes = "More notes about this method", response = Object.class)
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolveGenericPID(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws IOException {
		return resolveGenericPID(prefix + "/" + suffix);
	}

	/**
	 * Simple HEAD method to check whether a particular pid is registered.
	 * 
	 * @param identifier
	 *            an identifier string
	 * @return either 200 or 404, indicating whether the PID is registered or
	 *         not registered
	 * @throws IOException
	 */
	@HEAD
	@Path("/pid/{identifier}")
	@ApiOperation(value = "Identifier registered?", notes = "Check to see if identifier is registered")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response isPidRegistered(@PathParam("identifier") String identifier) throws IOException {
		boolean b = typingService.isIdentifierRegistered(identifier);
		if (b)
			return Response.status(200).build();
		else
			return Response.status(404).build();
	}

	/**
	 * Similar to {@link #isPidRegistered(String)} but supports native slashes
	 * in the identifier path.
	 * 
	 * @see #isPidRegistered(String)
	 */
	@HEAD
	@Path("/pid/{prefix}/{suffix}")
	@ApiOperation(value = "Identifier registered?", notes = "Check to see if identifier is registered")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response isPidRegistered(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws IOException {
		return isPidRegistered(prefix + "/" + suffix);
	}

	/**
	 * Queries what kind of entity an identifier will point to (generic object,
	 * property, type, ...). See {@link EntityClass} for possible return values.
	 * 
	 * @param identifier
	 *            full identifier name
	 * @return a simple JSON object with the kind of entity the identifier
	 *         points to. See {@link EntityClass} for details.
	 * @throws IOException
	 * @see rdapit.pitservice.EntityClass
	 */
	@GET
	@Path("/peek/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Class of identifier's entity?", notes = "Get the class of the identified entity")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found")
	})
	public Response peekIdentifier(@PathParam("identifier") String identifier) throws IOException {
		EntityClass result = typingService.determineEntityClass(identifier);
		return Response.status(200).entity(result).build();
	}

	/**
	 * Similar to {@link #peekIdentifier(String)} but supports native slashes in
	 * the identifier path.
	 * 
	 * @see #peekIdentifier(String)
	 */
	@GET
	@Path("/peek/{prefix}/{suffix}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Class of identifier's entity?", notes = "Get the class of the identified entity")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found")
	})
	public Response peekIdentifier(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws IOException {
		EntityClass result = typingService.determineEntityClass(prefix + "/" + suffix);
		return Response.status(200).entity(result).build();
	}

	/**
	 * Sophisticated GET method to return all or some properties of an
	 * identifier.
	 * 
	 * @param identifier
	 *            full identifier name
	 * @param propertyIdentifier
	 *            Optional. Cannot be used in combination with the type
	 *            parameter. If given, the method returns only the value of the
	 *            single property. The identifier must be registered for a
	 *            property in the type registry. The method will return 404 if
	 *            the PID exists but does not carry the given property.
	 * @param typeIdentifiers
	 *            Optional. Cannot be used in combination with the property
	 *            parameter. If given, the method will return all properties
	 *            (mandatory and optional) that are specified in the given
	 *            type(s) and listed in the identifier's record. The type
	 *            parameter must be a list of type identifiers available from
	 *            the registry. If an identifier is not known in the registry,
	 *            the method will return 404. The result will also include a
	 *            boolean value <i>typeConformance</i> that is only true if all
	 *            mandatory properties of the type are present in the PID
	 *            record.
	 * @param includePropertyNames
	 *            Optional. If set to true, the method will also provide
	 *            property names in addition to identifiers. Note that this is
	 *            more expensive due to extra requests sent to the type
	 *            registry.
	 * @return if the request is processed properly, the method will return 200
	 *         OK and a JSON object that contains a map of property identifiers
	 *         to property names (which may be empty) and values. It may also
	 *         contain optional meta information, e.g. conformance indications.
	 *         The method will return 404 if the identifier is not known.
	 * @throws IOException
	 *             on communication errors with identifier system or type
	 *             registry
	 * @throws InconsistentRecordsException
	 *             if records in the identifier system and/or type registry are
	 *             inconsistent, e.g. use property or type identifiers that are
	 *             not registered
	 */
	@GET
	@Path("/pid/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get associated attributes", notes = "Get attributes associated with given identifier")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 400, message = "Bad request"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolvePID(@PathParam("identifier") String identifier, @QueryParam("filter_by_property") @DefaultValue("") String propertyIdentifier,
			@QueryParam("filter_by_type") List<String> typeIdentifiers,
			@QueryParam("include_property_names") @DefaultValue("false") boolean includePropertyNames) throws IOException, InconsistentRecordsException {
		if (!typeIdentifiers.isEmpty()) {
			// Filter by type ID
			if (!propertyIdentifier.isEmpty())
				return Response.status(400).entity("Filtering by both type and property is not supported!").build();
			PIDInformation result;
			if (typeIdentifiers.size() == 1)
				result = typingService.queryByTypeWithConformance(identifier, typeIdentifiers.get(0), includePropertyNames);
			else
				result = typingService.queryByTypeWithConformance(identifier, typeIdentifiers, includePropertyNames);
			if (result == null)
				return Response.status(404).entity("Type not registered in the registry").build();
			return Response.status(200).entity(result).build();
		} else if (propertyIdentifier.isEmpty()) {
			// No filtering - return all properties
			PIDInformation result = typingService.queryAllProperties(identifier, includePropertyNames);
			if (result == null)
				return Response.status(404).entity("Identifier not registered").build();
			return Response.status(200).entity(result).build();
		} else {
			// Filter by property ID
			PIDInformation result = typingService.queryProperty(identifier, propertyIdentifier);
			if (result == null)
				return Response.status(404).entity("Property not present in identifier record").build();
			return Response.status(200).entity(result).build();
		}
	}

	/**
	 * Similar to {@link #resolvePID(String, String, List, boolean)} but
	 * supports native slashes in the identifier path.
	 * 
	 * @see #resolvePID(String, String, List, boolean)
	 */
	@GET
	@Path("/pid/{prefix}/{suffix}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get associated attributes", notes = "Get attributes associated with given identifier")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 400, message = "Bad request"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolvePID(@PathParam("prefix") String identifierPrefix, @PathParam("suffix") String identifierSuffix,
			@QueryParam("filter_by_property") @DefaultValue("") String propertyIdentifier, @QueryParam("filter_by_type") List<String> typeIdentifiers,
			@QueryParam("include_property_names") @DefaultValue("false") boolean includePropertyNames) throws IOException, InconsistentRecordsException {
		return resolvePID(identifierPrefix + "/" + identifierSuffix, propertyIdentifier, typeIdentifiers, includePropertyNames);
	}

	/**
	 * GET method to read the definition of a property from the type registry.
	 * 
	 * @param identifier
	 *            the property identifier
	 * @return a property definition record or 404 if the property is unknown.
	 * @throws IOException
	 */
	@GET
	@Path("/property/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get property definition", notes = "Get definition of specified property")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolveProperty(@PathParam("identifier") String identifier) throws IOException {
		PropertyDefinition propDef = typingService.describeProperty(identifier);
		if (propDef == null)
			return Response.status(404).build();
		return Response.status(200).entity(propDef).build();
	}

	/**
	 * Similar to {@link #resolveProperty(String)} but supports native slashes
	 * in the identifier path.
	 * 
	 * @see #resolveProperty(String)
	 */
	@GET
	@Path("/property/{prefix}/{suffix}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get property definition", notes = "Get definition of specified property")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolveProperty(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws IOException {
		return resolveProperty(prefix + "/" + suffix);
	}

	/**
	 * GET method to read the definition of a type from the type registry.
	 * 
	 * @param identifier
	 *            the type identifier
	 * @return a type definition record or 404 if the type is unknown.
	 * @throws IOException
	 */
	@GET
	@Path("/type/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get type definition", notes = "Get definition of specified type")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolveType(@PathParam("identifier") String identifier) throws IOException {
		TypeDefinition typeDef = typingService.describeType(identifier);
		if (typeDef == null)
			return Response.status(404).build();
		return Response.status(200).entity(typeDef).build();
	}

	/**
	 * Similar to {@link #resolveType(String)} but supports native slashes in
	 * the identifier path.
	 * 
	 * @see #resolveType(String)
	 */
	@GET
	@Path("/type/{prefix}/{suffix}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get type definition", notes = "Get definition of specified type")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolveType(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws IOException {
		return resolveType(prefix + "/" + suffix);
	}
	
	/**
	 * GET method to read the definition of a profile from the type registry.
	 * 
	 * @param identifier
	 *            the profile identifier
	 * @return a profile definition record or 404 if the profile is unknown.
	 * @throws IOException
	 * 
	 * Added by Quan (Gabriel) Zhou @ Indiana University Bloomington
	 */
	@GET
	@Path("/profile/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get profile definition", notes = "Get definition of specified profile")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolveProfile(@PathParam("identifier") String identifier) throws IOException {
		ProfileDefinition profileDef = typingService.describeProfile(identifier);
		if (profileDef == null)
			return Response.status(404).build();
		return Response.status(200).entity(profileDef).build();
	}

	/**
	 * Similar to {@link #resolveProfile(String)} but supports native slashes in
	 * the identifier path.
	 * 
	 * @see #resolveProfile(String)
	 */
	@GET
	@Path("/profile/{prefix}/{suffix}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get profile definition", notes = "Get definition of specified profile")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 404, message = "Not found")
	})
	public Response resolveProfile(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) throws IOException {
		return resolveProfile(prefix + "/" + suffix);
	}

	/**
	 * Generic POST method to create new identifiers. The method determines an
	 * identifier name automatically, based on a purely random (version 4) UUID.
	 * 
	 * @param properties
	 *            a map from string to string, mapping property identifiers to
	 *            values.
	 * @return a simple string with the newly created PID name.
	 */
	@POST
	@Path("/pid")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get type definition", notes = "Get definition of specified type")
	@ApiResponses(value = {
	  @ApiResponse(code = 200, message = "Found"),
	  @ApiResponse(code = 500, message = "Server error")
	})
	public Response registerPID(Map<String, String> properties) {
		try {
			String pid = typingService.registerPID(properties);
			return Response.status(201).entity(pid).build();
		} catch (IOException exc) {
			return Response.status(500).entity("Communication failure to identifier system: " + exc.getMessage()).build();
		}
	}

	/**
	 * DELETE method to delete identifiers. Testing purposes only! Not part of
	 * the official specification.
	 * 
	 * @param identifier
	 *            full identifier name
	 * @return 200 or 404
	 */
	@DELETE
	@Path("/pid/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePID(@PathParam("identifier") String identifier) {
		boolean b = typingService.deletePID(identifier);
		if (b) {
			// This is not strictly necessary, but we just do it as a courtesy
			// (additional information to the user)
			Map<String, String> result = new HashMap<>();
			result.put(identifier, "deleted");
			return Response.status(200).entity(result).build();
		} else
			return Response.status(404).build();
	}

	/**
	 * Similar to {@link #deletePID(String)} but supports native slashes in the
	 * identifier path.
	 * 
	 * @see #deletePID(String)
	 */
	@DELETE
	@Path("/pid/{prefix}/{suffix}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePID(@PathParam("prefix") String prefix, @PathParam("suffix") String suffix) {
		return deletePID(prefix + "/" + suffix);
	}

}
