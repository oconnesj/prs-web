package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.JsonResponse;
import com.prs.business.LineItem;
import com.prs.business.Request;
import com.prs.db.LineItemRepository;
import com.prs.db.RequestRepository;

@RestController
@RequestMapping("/line-items")

public class LineItemController {
	@Autowired
	private LineItemRepository lineItemRepo;
	@Autowired
	private RequestRepository requestRepo;

	@GetMapping("/")
	public JsonResponse listLineItems() {
		JsonResponse jr = null;
		List<LineItem> lineitems = lineItemRepo.findAll();
		if (lineitems.size() > 0) {
			jr = JsonResponse.getInstance(lineitems);

		} else {
			jr = JsonResponse.getErrorInstance("No lineitems found.");

		}

		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		// Expected responses?
		// 1 - a single lineitem
		// 2 - bad id - no lineitem found
		// 3 - Exception?? - hold off for now, implement
		// exception handling as needed
		JsonResponse jr = null;
		Optional<LineItem> lineitem = lineItemRepo.findById(id);
		if (lineitem.isPresent()) {
			jr = JsonResponse.getInstance(lineitem.get());

		} else {
			jr = JsonResponse.getErrorInstance("No lineitem found for id: " + id);
		}
		return jr;
	}

	@GetMapping("/ines-for-pr/{id}")
	public JsonResponse getLineItemsProductRequestID(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findByRequestId(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
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
			recalculateTotal(li.getRequest());
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();

		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;

	}
	// update method

	@PutMapping("/")
	public JsonResponse updateLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;

		try {
			jr = JsonResponse.getInstance(lineItemRepo.save(li));
			recalculateTotal(li.getRequest());

		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();

		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse deleteLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			LineItem li = lineItemRepo.findById(id).get(); 
			lineItemRepo.deleteById(id);
			jr = JsonResponse.getInstance("Line Item id: " + id + " deleted successfully.");
			Request r = li.getRequest();
			recalculateTotal(r);
			} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error deleting Line Item: " + e.getMessage());
			e.printStackTrace();
		}
		return jr;
	}

	public void recalculateTotal(Request r) {

		List<LineItem> lines = lineItemRepo.findAllByRequestId(r.getId());
		// loop thru list to sum a total
		double total = 0.0;
		for (LineItem line : lines) {
			total += line.getQuanity() * line.getProduct().getPrice();
		}
		// save that total in the User instance
		r.setTotal(total);
		try {
			requestRepo.save(r);
		} catch (Exception e) {
			throw e;
		}

	}

}
