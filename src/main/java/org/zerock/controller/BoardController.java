package org.zerock.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.criteria.Criteria;
import org.zerock.criteria.PageDTO;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.service.BoardService;
import org.zerock.service.ReplyService;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/board/*")
public class BoardController {
	
	@Inject
	private BoardService service;
	
	@Inject
	private ReplyService replyService;
	
//	@GetMapping("/list")
//	public void list(Model model) {
//		log.info("list.......");
//		
//		model.addAttribute("list", service.getList());
//		
//	}
	
	@GetMapping("/list")
	public void list(Model model, Criteria cri) {
		log.info("list.......: "+ cri);
		
		int total = service.getTotal(cri);
		System.out.println("총 게시글 수 : " + total );
		model.addAttribute("list", service.getList(cri));
		model.addAttribute("pageMaker", new PageDTO(cri, total));
		
	}	
	@GetMapping("/register")
	@PreAuthorize("isAuthenticated()")
	public void register() {
		log.info("register form......");
	}
	
	@PostMapping("/register")
	@PreAuthorize("isAuthenticated()")
	public String register(BoardVO board, RedirectAttributes rttr ) {
		
		log.info("==============================");
		log.info("register : "+ board);
		
		if (board.getAttachList() != null) {
			
			board.getAttachList().forEach(attach -> log.info(attach)); 
		}

		
		service.register(board);
		
		rttr.addFlashAttribute("result", board.getBno());
	
		return "redirect:/board/list";
	}
	
	@GetMapping({"/get","/modify"})
	public void get(@RequestParam("bno") Long bno, Model model, @ModelAttribute("cri") Criteria cri) {
		log.info("bno : " + bno);

		log.info("cri : " + cri);
		log.info("toString : " + cri.toString());

		log.info("/get or /modify");
		model.addAttribute("board", service.get(bno));
	}
	
	@PreAuthorize("principal.username == #board.writer")
	@PostMapping("/modify")
	public String modify(BoardVO board, RedirectAttributes rttr, @ModelAttribute("cri") Criteria cri) {
		
		log.info("modify : "+ board);
		System.out.println("뭐지? "+board);
		if(service.modify(board)) {
			rttr.addFlashAttribute("result", "success");
		}

		return "redirect:/board/list" + cri.getListLink();
	}
	
	@PreAuthorize("principal.username == #writer")
	@PostMapping("/remove")
	public String remove(@RequestParam("bno") Long bno, RedirectAttributes rttr, Criteria cri, String writer) {
		
		log.info("remove......"+bno);
		
		List<BoardAttachVO> attachList = service.getAttachList(bno);
		int delCount = replyService.deleteAll(bno);
		log.info("지워진 댓글 수 : " + delCount);
		if(service.remove(bno)) {
			
			deleteFiles(attachList);

			rttr.addFlashAttribute("result", "success");
		}

		return "redirect:/board/list"+cri.getListLink();
	}
	
	@GetMapping("/getAttachList")
	@ResponseBody
	public ResponseEntity<List<BoardAttachVO>> getAttachList(Long bno){
		log.info("getAttachList one : " + bno);
		
		return new ResponseEntity<List<BoardAttachVO>>(service.getAttachList(bno), HttpStatus.OK);
	}
	
	private void deleteFiles(List<BoardAttachVO> attachList) {
		if(attachList == null || attachList.size() == 0) {
			return;
		}
		
		log.info("delete attach files.............");
		log.info(attachList);
		System.out.println("들어오기전");
		attachList.forEach(attach -> {
			try {
				System.out.println("들어왔음");
				Path file = Paths.get("/Users/pyomomo/Desktop/upload/"+attach.getUploadPath()
										+ "/" + attach.getUuid() + "_" + attach.getFileName());
				System.out.println("원본파일 삭제 전");
				Files.deleteIfExists(file);
				System.out.println("원본파일삭제 후");
				System.out.println("File : " + file);
				MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
				
				String mimeType = mimeTypesMap.getContentType(file.toFile());
				System.out.println(mimeType);
				System.out.println("if 들어가기 전");
				if(mimeType.contains("image")) {
					Path thumNail = Paths.get("/Users/pyomomo/Desktop/upload/"+attach.getUploadPath()
					+ "/s_" + attach.getUuid() + "_" + attach.getFileName());
				
					Files.delete(thumNail);
				}

			}catch (Exception e) {
				log.error("delete file error " + e.getMessage());
			} // end catch
		});
	}
}
