package org.zerock.service;

import java.util.List;

import org.zerock.criteria.Criteria;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;

public interface BoardService {
	
		
	   public void register(BoardVO board);
	   public BoardVO get(Long bno);
	   public boolean modify(BoardVO board);
	   public boolean remove(Long bno);
	   //public List<BoardVO> getList();
	   public List<BoardVO> getList(Criteria cri);
	   
	   public int getTotal(Criteria cri);
	   
	   public List<BoardAttachVO> getAttachList(Long bno);
	   
}
