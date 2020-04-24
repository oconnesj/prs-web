package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.JsonResponse;
import com.prs.business.LineItem;
import com.prs.db.LineItemRepository;




@RestController
@RequestMapping("/line-items")

public class LineItemController {
	@Autowired
	private LineItemRepository lineItemRepo;


	
	
	@GetMapping("/")
	public JsonResponse listLineItems(){
		JsonResponse jr = null;
		List<LineItem> lineitems = lineItemRepo.findAll();
		if (lineitems.size()>0) {
			jr = JsonResponse.getInstance(lineitems);

		}
		else {
			jr = JsonResponse.getErrorInstance("No lineitems found.");

		}
		
		return jr;
	}
	

	
	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		// Expected responses?
		// 1 - a single lineitem 
		// 2 - bad id  - no lineitem found 
		// 3 - Exception?? - hold off for now, implement 
		//					exception handling as needed 
		JsonResponse jr = null;
		Optional<LineItem> lineitem = lineItemRepo.findById(id);
		if (lineitem.isPresent()) {
			jr = JsonResponse.getInstance(lineitem.get());
			
		}
		else {
			jr = JsonResponse.getErrorInstance("No lineitem found for id: "+id);
		}
		return jr;
	}
	
	
	// create method 
	@PostMapping("/")
	public JsonResponse createLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		
		
		try {
			li = lineItemRepo.save(li);
			jr = JsonResponse.getInstance(li);
		} 		catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getErrorInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		}
		
		return jr;
		
	}
	//update method 
	
	@PutMapping("/")
	public JsonResponse updateLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		
		
		try {
			li = lineItemRepo.save(li);
			jr = JsonResponse.getInstance(li);
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error updating lineitem: " + e.getMessage());
			e.printStackTrace();


		}
		return jr;

		
	}
	@DeleteMapping("/{id}")
	public JsonResponse deleteLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		
		
		try {
			lineItemRepo.deleteById(id);
			jr = JsonResponse.getInstance("LineItem id:  "+id+" deleted successfully.");
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error deleting lineitem: " + e.getMessage());
			e.printStackTrace();


		}
		return jr;
		
	}
	
	
}
