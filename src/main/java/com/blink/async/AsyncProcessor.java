package com.blink.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AsyncProcessor<T> {
	
	private List<CompletableFuture<?>> futures = new ArrayList<>();
	private Consumer<T> callback;
	
	public AsyncProcessor() {
		
	}

	public AsyncProcessor(Consumer<T> callback) {
		setCallback(callback);
	}

	private T execute(Function<T, T> method, T param){
		T result = method.apply(param);
		callback.accept(result);
		return result;
		
	}

	public CompletableFuture<T> executeAsync(Function<T, T> method, T param){
		CompletableFuture<T> future = CompletableFuture.supplyAsync(
				() -> execute(method, param));
		
		futures.add(future);
		return future;
	}
  
	
	public CompletableFuture<Collection<T>> executeAsync(Function<T, T> method, Collection<T> params ){
		CompletableFuture<Collection<T>> future = CompletableFuture.supplyAsync(() -> {
			Collection<T> results = new ArrayList<>();
			params.forEach(param -> results.add(execute(method, param)));
			return results;
		});
		
		futures.add(future);
		return future;
	}

	public void syncAll() {
		getFutures().forEach(future -> future.join());
		getFutures().clear();
	}
	
	public List<CompletableFuture<?>> getFutures(){
		return futures;
	}
	
	public AsyncProcessor<T> setCallback(Consumer<T> callback) {
		this.callback = callback;
		return this;
	}
}
