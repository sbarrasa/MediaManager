package com.blink.async;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;


public class AsyncProcessor<P, S extends Enum<S>> {
	
	private Map<String, ProcessResult<S>> allProcessResult = new HashMap<>();
	
	public AsyncProcessor() {
	}

	public CompletableFuture<ProcessResult<S>> executeAsync(String label, Function<ProcessResult<S>, ProcessResult<S>> method){
 		return CompletableFuture.supplyAsync(
				() -> {
				ProcessResult<S> processResult = newProcessResult(label);	
	    		return method.apply(processResult);
		});
	}

	public CompletableFuture<ProcessResult<S>> executeAsync(String label, 
												BiFunction<P, ProcessResult<S>, ProcessResult<S>> method, 
												P value){
 		return CompletableFuture.supplyAsync(
				() -> {
				ProcessResult<S> processResult = newProcessResult(label);	
	    		return method.apply(value, processResult);
		});
	}


	public Map<String, ProcessResult<S>> getAllProcessResult() {
		return allProcessResult;
	}
  
	public Map<String, ProcessResult<S>> cleanComleted(){
		allProcessResult.entrySet().removeIf(e -> e.getValue().getCompleted());
		return allProcessResult;
	}
	
	
	public ProcessResult<S> newProcessResult(String label) {
		ProcessResult<S> processResult = new ProcessResult<>();
		allProcessResult.put(label+"-"+Thread.currentThread().getId(), processResult);
		return processResult;
	}


}
