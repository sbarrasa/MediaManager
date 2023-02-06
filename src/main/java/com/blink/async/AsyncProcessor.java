package com.blink.async;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


public class AsyncProcessor<T extends Enum<T>> {
	
	private Map<String, ProcessResult<T>> allProcessResult = new HashMap<>();
	
	public AsyncProcessor() {
	}

	public CompletableFuture<ProcessResult<T>> executeAsync(String label, Function<ProcessResult<T>, ProcessResult<T>> method){
 		return CompletableFuture.supplyAsync(
				() -> {
				ProcessResult<T> processResult = new ProcessResult<>();
	    		allProcessResult.put(label+"-"+Thread.currentThread().getId(), processResult);

	    		return method.apply(processResult);
		});
	}



	public Map<String, ProcessResult<T>> getAllProcessResult() {
		return allProcessResult;
	}
  
	public Map<String, ProcessResult<T>> cleanComleted(){
		allProcessResult.entrySet().removeIf(e -> e.getValue().getCompleted());
		return allProcessResult;
	}

}
