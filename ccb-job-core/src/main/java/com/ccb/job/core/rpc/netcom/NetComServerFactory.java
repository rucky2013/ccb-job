package com.ccb.job.core.rpc.netcom;

import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.rpc.codec.RpcRequest;
import com.ccb.job.core.rpc.codec.RpcResponse;
import com.ccb.job.core.rpc.netcom.jetty.server.JettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * netcom init
 * @author xuxueli 2015-10-31 22:54:27
 */
public class NetComServerFactory  {
	private static  final Logger logger = LoggerFactory.getLogger(NetComServerFactory.class);

	// ---------------------- server start ----------------------
	JettyServer server = new JettyServer();
	public void start(int port, String ip, String appName) throws Exception {
		server.start(port, ip, appName);
	}

	// ---------------------- server destroy ----------------------
	public void destroy(){
		server.destroy();
	}

	// ---------------------- server init ----------------------
	/**
	 * init local rpc service map
	 */
	private static  Map<String, Object> serviceMap = new HashMap<String, Object>();
	public static  void putService(Class<?> iface, Object serviceBean){
		serviceMap.put(iface.getName(), serviceBean);
	}
	public static  RpcResponse invokeService(RpcRequest request, Object serviceBean) {
		if (serviceBean==null) {
			serviceBean = serviceMap.get(request.getClassName());
		}
		if (serviceBean == null) {
			// TODO
		}

		RpcResponse response = new RpcResponse();
		
		//如果当前系统时间 减去 请求时间  超过180s 则认为请求超时
		if (System.currentTimeMillis() - request.getCreateMillisTime() > 180000) {
			logger.info("system current time :{} request create time:{}  resut:{}", System.currentTimeMillis() , request.getCreateMillisTime()
					,(System.currentTimeMillis() - request.getCreateMillisTime()));
			response.setResult(new ReturnT<String>(ReturnT.FAIL_CODE, "Timestamp Timeout."));
			return response;
		}

		try {
			Class<?> serviceClass = serviceBean.getClass();
			String methodName = request.getMethodName();
			Class<?>[] parameterTypes = request.getParameterTypes();
			Object[] parameters = request.getParameters();

			FastClass serviceFastClass = FastClass.create(serviceClass);
			FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);

			Object result = serviceFastMethod.invoke(serviceBean, parameters);

			response.setResult(result);
		} catch (Throwable t) {
			t.printStackTrace();
			response.setError(t.getMessage());
		}

		return response;
	}

}
