package org.zerock.service;

import java.util.List;

import org.zerock.criteria.Criteria;
import org.zerock.criteria.ReplyPageDTO;
import org.zerock.domain.ReplyVO;

public interface ReplyService {
	
	
	public int register(ReplyVO vo);
	public ReplyVO get(Long rno);
	public int modify(ReplyVO vo);
	public int remove(Long rno);
	public List<ReplyVO> getList(Criteria cri, Long bno);
	
	public ReplyPageDTO getListPage(Criteria cri, Long bno);
	public int deleteAll(Long bno);
}
