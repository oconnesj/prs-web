package com.prs.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.JsonResponse;
import com.prs.business.Request;
import com.prs.db.RequestRepository;

@RestController
@RequestMapping("/requests")

public class RequestController {
	@Autowired
	private RequestRepository requestRepo;

	@GetMapping("/")
	public JsonResponse list() {
		JsonResponse jr = null;
		List<Request> requests = requestRepo.findAll();
		if (requests.size() > 0) {
			jr = JsonResponse.getInstance(requests);

		} else {
			jr = JsonResponse.getErrorInstance("No requests found.");

		}

		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		// Expected responses?
		// 1 - a single request
		// 2 - bad id - no request found
		// 3 - Exception?? - hold off for now, implement
		// exception handling as needed
		JsonResponse jr = null;
		Optional<Request> request = requestRepo.findById(id);
		if (request.isPresent()) {
			jr = JsonResponse.getInstance(request.get());

		} else {
			jr = JsonResponse.getErrorInstance("No request found for id: " + id);
		}
		return jr;
	}

	// create method
	@PostMapping("/")
	public JsonResponse createRequest(@RequestBody Request r) {
		JsonResponse jr = null;

		try {
			r = requestRepo.save(r);
			jr = JsonResponse.getInstance(r);
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getErrorInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		}

		return jr;

	}
	// update method

	@PutMapping("/")
	public JsonResponse updateRequest(@RequestBody Request r) {
		JsonResponse jr = null;

		try {
			r = requestRepo.save(r);
			jr = JsonResponse.getInstance(r);
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error updating request: " + e.getMessage());
			e.printStackTrace();

		}
		return jr;

	}

	// Get - request review
	@GetMapping("/list-review/{id}")
	public JsonResponse reviewRequest(@PathVariable  Integer id) {
		JsonResponse jr = null;

		try {
			jr = JsonResponse.getInstance(requestRepo.findByStatusAndUserIdNot("Review", id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	// put - submit for review
	@PutMapping("/submit-review")
	public JsonResponse submitForReview(@RequestBody Request r) {
		JsonResponse jr = null;

		if (r.getTotal() <= 50) {
			r.setStatus("Approved");
		} else {
			r.setStatus("Review");
		}

		r.setSubmittedDate(LocalDate.now());
		try {
			jr = JsonResponse.getInstance(requestRepo.save(r));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
		}
		return jr;
	}

	@PutMapping("/approve")
	public JsonResponse approveRequest(@RequestBody Request r) {
		r.setStatus("Approved");

		JsonResponse jr = null;
		jr = updateRequest(r);
		return jr;
	}

	@PutMapping("/reject")
	public JsonResponse rejectRequest(@RequestBody Request r) {
		r.setStatus("Reject");

		JsonResponse jr = null;
		jr = updateRequest(r);
		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse deleteRequest(@PathVariable int id) {
		JsonResponse jr = null;

		try {
			requestRepo.deleteById(id);
			jr = JsonResponse.getInstance("Request id:  " + id + " deleted successfully.");
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error deleting request: " + e.getMessage());
			e.printStackTrace();

		}
		return jr;

	}

}
