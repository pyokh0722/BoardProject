package org.zerock.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.criteria.Criteria;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.mapper.BoardAttachMapper;
import org.zerock.mapper.BoardMapper;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class BoardServiceImpl implements BoardService{
	

	@Setter(onMethod_ = @Autowired)
	BoardMapper mapper;
	
	@Setter(onMethod_ = @Autowired)
	BoardAttachMapper attachMapper;
	
	@Transactional
	@Override
	public void register(BoardVO board) {

		System.out.println("register : " + board);

		mapper.insertSelectKey(board);
		
		if(board.getAttachList() == null || board.getAttachList().size() <= 0) {
			System.out.println("작성된 글번호 : " + board.getBno());
			System.out.println("작성된 내용 : " + board.getContent());
			System.out.println("작성된 작성자 : " + board.getWriter());
			return; 
		}		
		board.getAttachList().forEach(attach -> {
			attach.setBno(board.getBno());
			attachMapper.insert(attach);
		});
	}

	@Override
	public BoardVO get(Long bno) {
		log.info("get........");
		return mapper.read(bno);
	}

	@Override
	@Transactional
	public boolean modify(BoardVO board) {

		System.out.println("modify.............."+ board);
		attachMapper.deleteAll(board.getBno());
		
		boolean modifyResult = mapper.update(board) == 1;
		System.out.println("수정 결과 " + modifyResult);
		if (modifyResult && board.getAttachList() != null && board.getAttachList().size() > 0) {
			
			board.getAttachList().forEach(attach -> {
				
				attach.setBno(board.getBno());
				attachMapper.insert(attach);
			});
		}
		 return modifyResult;
	}

	@Override
	@Transactional
	public boolean remove(Long bno) {
		log.info("remove..........."+bno);
		
		attachMapper.deleteAll(bno);
		return mapper.delete(bno) == 1;
	}

	@Override
	public List<BoardVO> getList(Criteria cri) {
		System.out.println("배열사이즈 " + cri.getTypeArr().length);
		System.out.println("get list with paging : "+ cri);
		return mapper.getListWithPaging(cri);

	}

	@Override
	public int getTotal(Criteria cri) {
		log.info("get total count");
		
		return mapper.getTotalCount(cri);
	}

	@Override
	public List<BoardAttachVO> getAttachList(Long bno) {
		log.info("get Attach list by bno" + bno);
		return attachMapper.findByBno(bno);
	}

//	@Override
//	public List<BoardVO> getList() {
//		log.info("getList............");
//		
//		return mapper.getList();
//	}

}
