<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@include file="../includes/header.jsp" %>
      
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">게시판<button onClick="location.href='/board/list';" type="button" class="btn btn-md btn-default pull-right" style="margin-top: 5px; margin-right: 10px;">첫 페이지로</button></h1>                    
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            게시판 리스트 페이지
                            <button id='regBtn' type="button" class="btn btn-xs btn-primary pull-right">새글 등록하기</button>
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <table width="100%" class="table table-striped table-bordered table-hover">
                                <thead>
                                    <tr>
                                        <th>#번호</th>
                                        <th>제목</th>
                                        <th>작성자</th>
                                        <th>작성일</th>
                                        <th>수정일</th>
                                    </tr>
                                </thead>  
                                <c:forEach items="${list}" var="board">
                                	<tr>
                                		<td><c:out value="${board.bno}"/></td>
                                		<td><a class="move" href='<c:out value="${board.bno}"/>' >
                                		<c:out value="${board.title}"/></a></td>
                                		<td><c:out value="${board.writer}"/></td>
                                		<td><fmt:timeZone value="GMT+18"><fmt:formatDate pattern="yyyy/MM/dd KK:mm:ss" 
                                		value="${board.regdate }"/></fmt:timeZone></td>
                                		<td><fmt:timeZone value="GMT+18"><fmt:formatDate pattern="yyyy/MM/dd KK:mm:ss"  
                                		value="${board.updateDate }"/></fmt:timeZone></td>
                                	</tr>
                                </c:forEach>                         
                            </table>
                            
                         <div class='row'>
							<div class="col-lg-12">
								<form id='searchForm' action="/board/list" method='get'> 
									<select name='type'>
										<option value=""
										<c:out value="${pageMaker.cri.type == null?'selected':''}"/>>검색 조건</option>
										<option value="T"
										<c:out value="${pageMaker.cri.type eq 'T'?'selected':''}"/>>제목</option>
										<option value="C"
										<c:out value="${pageMaker.cri.type eq 'C'?'selected':''}"/>>내용</option>
										<option value="W"
										<c:out value="${pageMaker.cri.type eq 'W'?'selected':''}"/>>작성자</option>
										<option value="TC"
										<c:out value="${pageMaker.cri.type eq 'TC'?'selected':''}"/>>제목 or 내용</option>
										<option value="TW"
										<c:out value="${pageMaker.cri.type eq 'TW'?'selected':''}"/>>제목 or 작성자</option>
										<option value="TWC"
										<c:out value="${pageMaker.cri.type eq 'TWC'?'selected':''}"/>>제목 or 내용 or 작성자</option>
									</select>
									
									<input type='text' name='keyword' value='<c:out value="${pageMaker.cri.keyword}"/>' />
									<input type='hidden' name='pageNum' value='<c:out value="${pageMaker.cri.pageNum}"/>' /> 
									<input type='hidden' name='amount' value='<c:out value="${pageMaker.cri.amount}"/>' />
								 	
								<button class='btn btn-default'>검색</button>								
								</form>
								
							</div>
   						</div>
                            
						<!-- 페이징 처리 -->
						<form id='actionForm' action="/board/list" method='get'>
							<input type='hidden' name='pageNum' value = '${pageMaker.cri.pageNum}'> 
							<input type='hidden' name='amount' value = '${pageMaker.cri.amount}'>
							<input type='hidden' name='type' value='<c:out value="${ pageMaker.cri.type }"/>'>
							<input type='hidden' name='keyword' value='<c:out value="${ pageMaker.cri.keyword }"/>'>
						</form>
						
						
						<div class="pull-right">
							<ul class="pagination">
								<c:if test="${pageMaker.prev }">
									<li class="paginate_button previous">
										<a href="${pageMaker.startPage - 1}">Previous</a> 
									</li>
								</c:if>
								
								<c:forEach var="num" begin="${pageMaker.startPage}" end="${pageMaker.endPage}">
									<li class="paginate_button ${pageMaker.cri.pageNum == num ? "active" : ""}">
										<a href="${num}">${num}</a>								
									</li>
								</c:forEach>
								
								<c:if test="${pageMaker.next}">
									<li class="paginate_button next">
										<a href="${pageMaker.endPage + 1}">Next</a>
									</li>
								</c:if>
          			 		</ul>
						</div>
								
					<!-- Modal 추가 -->
					<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
					aria-labelledby="myModalLabel" aria-hidden="true"> 
						<div class="modal-dialog">
							<div class="modal-content"> 
								<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">확인창</h4> 
								</div>
								
								<div class="modal-body">처리가 완료되었습니다.</div> 
								<div class="modal-footer">
									<button type="button" class="btn btn-primary" data-dismiss="modal">확인</button>
						       </div>
						     </div>
						<!-- /.modal-content --> 
						</div>
					<!-- /.modal-dialog --> 
					</div>
					<!-- /.modal -->
								
								
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript"> 
	$(document).ready(function(){
	var result = '<c:out value="${result}"/>';
	console.log(result);
	checkModal(result);
	
	/* 모달창 다시 안뜨게 */
	 history.replaceState({},null,null); 
	
	function checkModal(result) {
		
		if (result === '' || history.state) {
				return; 
		}
	    if (parseInt(result) > 0) {
	   		$(".modal-body").html("게시글 " + parseInt(result) + " 번이 등록되었습니다.");   	   
	    }
	   		$("#myModal").modal("show"); 
	    }
    
		$("#regBtn").on("click", function(){ 
			console.log("글작성 폼으로 감");
			

			if(confirm("새글 작성하시겠습니까?")){
				self.location ="/board/register";
			}else{
				console.log("작성취소");
				return false;
			}	
		});		

		var actionForm = $("#actionForm");
		 
		// 이전, 넥스트 버튼 누를 때 동작
		
		$(".paginate_button a").on("click", function(e) {
			e.preventDefault();
			console.log('click');
			actionForm.find("input[name='pageNum']").val($(this).attr("href"));
			 console.log($(this).attr("href")); /* 페이지 넘버 전달 히든 태그에  */

			actionForm.submit();
		});	

		/*  */       
		$(".move").on("click", function(e){
				e.preventDefault();
				actionForm.append("<input type='hidden' name='bno' value='"+ $(this).attr("href")+"'>");
				actionForm.attr("action","/board/get"); actionForm.submit();
			});

		var searchForm = $("#searchForm");

		$("#searchForm button").on("click", function(e){
			
			if(!searchForm.find("option:selected").val()){
				alert("검색 조건을 선택하세요.");
				return false;
			}
			if(!searchForm.find("input[name='keyword']").val()){ 
				alert("키워드를 입력하세요.");
		     	return false;
		    }

			searchForm.find("input[name='pageNum']").val("1"); 
			e.preventDefault();
			searchForm.submit();
		    
		});				
	 });

	
	 </script>
           
           <%@include file="../includes/footer.jsp" %>
