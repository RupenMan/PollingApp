package edu.mum.polls.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.mum.polls.model.ChoiceVoteCount;
import edu.mum.polls.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{
	
	@Query("SELECT NEW edu.mum.polls.model.ChoiceVoteCount(v.choice.id, count(v.id)) FROM "
			+ "Vote v WHERE v.poll.id in :pollIds GROUP BY v.choice.id")
	public List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds") List<Long> pollIds);
	
	@Query("SELECT NEW edu.mum.polls.model.ChoiceVoteCount(v.choice.id, count(v.id)) FROM"
			+ "Vote v WHERE v.poll.id =:pollId GROUP BY v.choice.id")
	public List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollId") Long pollId);
	
	@Query("SELECT v FROM vote v WHERE v.user.id =:userId AND v.poll.id in :pollIds")
	public List<Vote> findByUserIdAndPollIdIn(@Param ("userId") Long userId, @Param("pollIds") List<Long> pollIds);
	
	@Query("SELECT v FROM vote v WHERE v.user.id=:userId AND v.poll.id =:pollId")
	public Vote findByUserIdAndPollIdIn(@Param ("userId") Long userId, @Param("pollId") Long pollId);
	
	@Query("SELECT COUNT(v.id) FROM vote v WHERE v.user.id=:userId")
	public long countByUserId(@Param("userId") Long userId);
	
	@Query("SELECT v.poll.id FROM vote v where WHERE v.user.id =:userId")
	public Page<Long> findVotedPollIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}
