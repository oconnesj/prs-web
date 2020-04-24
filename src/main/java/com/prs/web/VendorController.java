package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.JsonResponse;
import com.prs.business.Vendor;
import com.prs.db.VendorRepository;



@RestController
@RequestMapping("/vendors")

public class VendorController {
	@Autowired
	private VendorRepository vendorRepo;

	@GetMapping("/")
	public JsonResponse list(){
		JsonResponse jr = null;
		List<Vendor> vendors = vendorRepo.findAll();
		if (vendors.size()>0) {
			jr = JsonResponse.getInstance(vendors);

		}
		else {
			jr = JsonResponse.getErrorInstance("No vendors found.");

		}
		
		return jr;
	}
	

	
	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		// Expected responses?
		// 1 - a single vendor 
		// 2 - bad id  - no vendor found 
		// 3 - Exception?? - hold off for now, implement 
		//					exception handling as needed 
		JsonResponse jr = null;
		Optional<Vendor> vendor = vendorRepo.findById(id);
		if (vendor.isPresent()) {
			jr = JsonResponse.getInstance(vendor.get());
			
		}
		else {
			jr = JsonResponse.getErrorInstance("No vendor found for id: "+id);
		}
		return jr;
	}
	
	
	// create method 
	@PostMapping("/")
	public JsonResponse createVendor(@RequestBody Vendor v) {
		JsonResponse jr = null;
		
		
		try {
			v = vendorRepo.save(v);
			jr = JsonResponse.getInstance(v);
		} 		catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getErrorInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		}
		
		return jr;
		
	}
	//update method 
	
	@PutMapping("/")
	public JsonResponse updateVendor(@RequestBody Vendor v) {
		JsonResponse jr = null;
		
		
		try {
			v = vendorRepo.save(v);
			jr = JsonResponse.getInstance(v);
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error updating vendor: " + e.getMessage());
			e.printStackTrace();


		}
		return jr;

		
	}
	@DeleteMapping("/{id}")
	public JsonResponse deleteVendor(@PathVariable int id) {
		JsonResponse jr = null;
		
		
		try {
			vendorRepo.deleteById(id);
			jr = JsonResponse.getInstance("Vendor id:  "+id+" deleted successfully.");
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error deleting vendor: " + e.getMessage());
			e.printStackTrace();


		}
		return jr;
		
	}
	
	
}
