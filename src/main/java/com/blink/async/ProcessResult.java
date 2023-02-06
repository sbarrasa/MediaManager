package com.blink.async;

import java.util.Map;
import java.util.HashMap;

public class ProcessResult<T extends Enum<T>>{
	private Integer cntTotal = 0;
	private Integer cntProcessed = 0;
	private Boolean completed = false;
	private Map<T, Integer> ProcessStatus = new HashMap<>();
	

	public Integer getTotal() {
		return cntTotal;
	}
	
	public Integer getProcessed() {
		return cntProcessed;
	}
	
	public Map<T, Integer> getProcessStatus(){
		return ProcessStatus;
	}
	
	public ProcessResult<T> setTotal(Integer value) {
		this.cntTotal = value;
		return this;
	}
		 
	
	
	public Integer incProcessed(T key) {
		Integer cnt = getProcessStatus().get(key);
			if(cnt== null)
				cnt= 0;
			
		getProcessStatus().put(key, ++cnt);
		cntProcessed++;
		
		return cnt;
	}
	
	public String toString() {
		return String.format("Total: %d, Processed: %d, Status: %s", 
				getTotal(),
				getProcessed(),
				getProcessStatus());
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
}
