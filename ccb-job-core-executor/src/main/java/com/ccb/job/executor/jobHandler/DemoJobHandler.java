package com.ccb.job.executor.jobHandler;


import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.handler.IJobHandler;
import com.ccb.job.core.handler.annotation.JobHander;
import com.ccb.job.core.log.CcbJobLogger;
import com.ccb.job.executor.model.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * 任务Handler的一个Demo（Bean模式）
 *
 * 开发步骤：
 * 1、继承 “IJobHandler” ；
 * 2、装配到Spring，例如加 “@Service” 注解；
 * 3、加 “@JobHander” 注解，注解value值为新增任务生成的JobKey的值;多个JobKey用逗号分割;
 * 4、执行日志：需要通过 "CcbJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHander(value="springbootJobHandler")
@Service
public class DemoJobHandler extends IJobHandler {

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public ReturnT<String> execute(String... params)  {
		CcbJobLogger.log("CCB-JOB, Hello World.");
		String msg  =  "";
		if(params == null){
			msg = "没有转发请求的URL，无法转发请求！";
			CcbJobLogger.log(msg);
			return  new ReturnT<>(ReturnT.FAIL_CODE,msg);
		}
		CcbJobLogger.log("正在转发请求。。。请求地址："+params[0]);
		//http://server-sms/ccbJob
		try{
			RestResponse restResponse = restTemplate.getForObject(params[0],RestResponse.class);
			if (null  == restResponse){
				msg = "请求异常";
				CcbJobLogger.log(msg);
			}else{
				if ("200".equals(restResponse.getRespCode() )){
					CcbJobLogger.log("请求成功");
					return ReturnT.SUCCESS;
				}else{
					msg = "请求异常。。。响应信息："+restResponse.getRespMsg();
					CcbJobLogger.log(msg);
				}
			}
		}catch (Exception e){
			msg = "请求异常:"+e.getMessage();
			CcbJobLogger.log(msg);
		}
		return  new ReturnT<>(ReturnT.FAIL_CODE,msg);
	}

}
