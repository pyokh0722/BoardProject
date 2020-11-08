package org.zerock.criteria;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDTO {
	
	private int startPage;
	private int endPage;
	private boolean prev, next;
	
	private int total;
	private Criteria cri;
	
	public PageDTO(Criteria cri, int total) {
		this.cri = cri;
		System.out.println("cri.pageNum : " + cri.getPageNum());
		this.total = total; // 전체 게시물 갯수
		
		this.endPage = (int) (Math.ceil(cri.getPageNum() / 10.0)) * 10;
		// 기본 끝페이지를 구해야 넥스트, 이전 형성 가능
		// ex> 현재 5 페이지에서 10개의 페이지만 보여줄 경우 끝페이지 구하기 
		// 5/10.0 = 0.5 -> 올림 1 -> 1 * 10 -> 끝페이지는 10!
		// ex> 현재 13 페이지에서 10개의 페이지만 보여줄 경우 끝페이지 구하기 
		// 13/ 10.0 = 1.3 -> 올림 2 -> 2 * 10 -> 끝페이지는 20!
		
		this.startPage = this.endPage - 9;
		// 끝페이지 10이라면 시작페이지는 10 - 9 = 1(시작페이지)
		// 끝페이지 20이라면 시작페이지는 20 - 9 = 11(시작페이지)
		
		int realEnd = (int) (Math.ceil((total * 1.0) / cri.getAmount()));		
		// 총데이터가 125개 라면
		//125 * 1.0 = 125.0 -> 125.0 / 10(보여질 최대 페이지 개수) = 12.5 -> 올림 13 (리얼 끝페이지)
		
		// 총데이터가 55개 라면
		//55 * 1.0 = 55.0 -> 55.0 / 10(보여질 최대 페이지 개수) = 5.5 -> 올림 6 (리얼 끝페이지)
	
		if(realEnd < this.endPage) {			
			this.endPage = realEnd;			
		}
		this.prev = this.startPage > 1;		
		this.next = this.endPage < realEnd;
	}
}
