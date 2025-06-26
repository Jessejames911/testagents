package com.agents.builder.common.script.common;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GroovyExecuteHandler extends FunctionExecutor {

	private final FunctionExecutor functionExecutor;

	@Override
	public Object execute(Map<String, Object> params) throws Exception {
		log.info("GroovyExecuteHandler execute ==== params: {}",params);
		return functionExecutor.execute(params);
	}

	@Override
	public void init() throws Exception {
		this.functionExecutor.init();
	}

	@Override
	public void destroy() throws Exception {
		this.functionExecutor.destroy();
	}
}
