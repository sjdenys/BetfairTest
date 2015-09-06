package com.example.sjden.betfairtest;

import java.util.Map;

/**
 * JSONRPCRequest: a request class which instances can be sent to the Betfair API
 *
 * @
 * PUBLIC FEATURES:
 * // Constructors
 *    JSONRPCRequest()
 *    JSONRPCRequest(String strJSONRPC, String strMethod, String strID, Map<String, Object> mpParams)
 * // Methods
 *    getId()
 *    getJsonrpc()
 *    getMethod()
 *    getParams()
 *    setId(String id)
 *    setJsonrpc(String jsonrpc)
 *    setMethod(String method))
 *    setParams(Map<String, Object> params)
 *
 * MODIFIED:
 * @version 1.0, 06/09/2015, SD
 * @author Sean Denys
 */
public class JSONRPCRequest {
	private String jsonrpc;
	private String method;
	private String id;
	private Map<String, Object> params;

	public JSONRPCRequest() {
        this.jsonrpc = "2.0";
	}

	public JSONRPCRequest(String strJSONRPC,String strMethod,String strID, Map<String, Object> mpParams) {
		this.jsonrpc = strJSONRPC;
		this.method = strMethod;
		this.id = strID;
		this.params = mpParams;
	}

	public String getJsonrpc() {
        return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
}

