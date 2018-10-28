package edu.mum.polls.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.mum.polls.model.Poll;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long>{
	
	public Optional<Poll> findById(Long pollId);
	
	public Page<Poll> findByCreatedBy(Long userId, Pageable pageable);
	
	public long countByCreatedBy(Long userId);
	
	public List<Poll> findByIdIn(List<Long> pollIds);
	
	public List<Poll> findByIdIn(List<Long> pollIds, Sort sort);
	
}
