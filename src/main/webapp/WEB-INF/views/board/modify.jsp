<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%> 
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@include file="../includes/header.jsp"%>
	<div class="row">
		<div class="col-lg-12">
		<h1 class="page-header">게시물 수정</h1> </div>
		<!-- /.col-lg-12 --> </div>
		<!-- /.row -->
		<div class="row">
			<div class="col-lg-12">
				<div class="panel panel-default">
					<div class="panel-heading">게시물 수정</div> 
					<!-- /.panel-heading -->
						<div class="panel-body">
						
						<form role="form" name="form1" action="/board/modify" method="post">
							<input type="hidden" name="${ _csrf.parameterName }" value="${ _csrf.token}"/>
							<input type='hidden' name='pageNum' value='<c:out value="${cri.pageNum }"/>'>
							<input type='hidden' name='amount' value='<c:out value="${cri.amount }"/>'>
							<input type='hidden' name='keyword' value='<c:out value="${cri.keyword}"/>'>
							<input type='hidden' name='type' value='<c:out value="${cri.type}"/>'>
							
							<div class="form-group">
							<label>글번호</label> <input class="form-control" id="bno" name='bno'
							value='<c:out value="${board.bno }"/>'readonly="readonly" > 
							</div>
							
							<div class="form-group">
							<label>제목</label> <input id="title" class="form-control" name='title'
							value='<c:out value="${board.title }"/>' > 
							</div>
							
							<div class="form-group">
							<label>내용</label>
							<textarea class="form-control" id="content" rows="3" name='content'
							><c:out value="${board.content}"/></textarea>						
							</div>
							
							<div class="form-group">
							<label>작성자</label> 
							<input class="form-control" id="writer" name='writer'
							value='<c:out value="${board.writer }"/>' readonly="readonly">
							</div>
							
							<div class="form-group"> 
							<label>RegDate</label><fmt:setTimeZone value="GMT+18" />
							<input class="form-control" id="regDate" name='regDate'
							value='<fmt:formatDate pattern = "yyyy/MM/dd KK:mm:ss" value = "${board.regdate}" />' readonly="readonly">
							</div>
							
							<div class="form-group">
							<label>Update Date</label><fmt:setTimeZone value="GMT+18" />
							<input class="form-control" id="updateDate" name='updateDate'
							value='<fmt:formatDate pattern = "yyyy/MM/dd KK:mm:ss" value = "${board.updateDate}" />' readonly="readonly">
							</div>
							

							<sec:authentication property="principal" var="pinfo"/>
							
							<sec:authorize access="isAuthenticated()">
							
								<c:if test="${pinfo.username eq board.writer }">
									<button data-oper='modify' class="btn btn-default" type="submit">수정</button>
									<button data-oper='remove' class="btn btn-danger" type="submit">삭제</button>
									
								</c:if>
							</sec:authorize>
									<button data-oper='list' class="btn btn-info" type="submit">리스트로</button>
						</form>
						
						</div>
						<!-- end panel-body -->
				</div>
				<!-- end panel-body --> 
			</div>
			<!-- end panel --> 
		</div>
		<!-- /.row -->
		
		<div class='bigPictureWrapper'> 
	<div class='bigPicture'> 
	</div>
</div>

<style> .uploadResult {
width:100%;
background-color: gray; 
}
.uploadResult ul{ 
display:flex;
flex-flow: row; 
justify-content: center; 
align-items: center;
}
.uploadResult ul li {
list-style: none; 
padding: 10px; 
align-content: center; 
text-align: center;
}
.uploadResult ul li img{
   width: 100px;
 }
.uploadResult ul li span {
   color:white;
 }
.bigPictureWrapper { 
position: absolute; 
display: none; 
justify-content: center; 
align-items: center; 
top:0%;
width:100%;
height:100%;
background-color: gray;
z-index: 100; 
background:rgba(255,255,255,0.5);
}
.bigPicture {
position: relative; 
display:flex; 
justify-content: center; 
align-items: center;
}
.bigPicture img {
  width:600px;
}
</style>

<!-- 첨부파일  -->
<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
				<div class="panel-heading">Files</div> 
				<!-- /.panel-heading -->
				<div class="panel-body">
				
					<div class="form-group uploadDiv">
						<input type="file" name='uploadFile' multiple="multiple">
					</div>
				
					<div class='uploadResult'> 
						<ul>
					
						</ul> 
					</div>
				</div>
				<!-- end panel-body --> 
		</div>
		<!-- end panel-body --> 
	</div>
	<!-- end panel --> 
</div>
<!-- /.row -->
		
<script type="text/javascript"> 

	$(document).ready(function() {
		var formObj = $("form"); 

		$('button').on("click", function(e){
				e.preventDefault();/*  폼의 기본 액션 결로를 막는 역할 */
				
				var operation = $(this).data("oper"); /* data-oper  속성의 값을 얻어온다. */
				
				console.log(operation);
				
				
				
				if(operation === 'remove'){ 
					
					
					if(confirm("정말로 지우시겠습니까?")){
						
						console.log("삭제 오케이");
						formObj.attr("action", "/board/remove");
						formObj.submit();
					}else{
						console.log("삭제 취소");
						return false;
					}
					return true;
				}else if(operation === 'list'){ //move to list

					formObj.attr("action", "/board/list").attr("method","get");
					
					var pageNumTag = $("input[name='pageNum']").clone(); 
					var amountTag = $("input[name='amount']").clone(); 
					var keywordTag = $("input[name='keyword']").clone(); 
					var typeTag = $("input[name='type']").clone();
					
					formObj.empty();
					
					formObj.append(pageNumTag); 
					formObj.append(amountTag); 
					formObj.append(keywordTag); 
					formObj.append(typeTag);
				
				}else if( operation === 'modify'){
					
					if(confirm("정말로 수정하시겠습니까?")){

						var theForm = document.form1;
						
						if(theForm.title.value == "" || theForm.content.value == ""){
							console.log("값이 비어있습니다.");
							alert("필수입력란이 비어있습니다.");
							return false;
						}
						
						console.log("submit clicked");
						var str = "";

						$(".uploadResult ul li").each(function(i, obj){
							var jobj = $(obj);
							console.log(jobj);
							console.dir(jobj);
							str += "<input type='hidden' name='attachList["+i+"].fileName' value='"+jobj.data("filename")+"'>";
							str += "<input type='hidden' name='attachList["+i+"].uuid' value='"+jobj.data("uuid")+"'>";
							str += "<input type='hidden' name='attachList["+i+"].uploadPath' value='"+jobj.data("path")+"'>";
							str += "<input type='hidden' name='attachList["+i+"].fileType' value='"+ jobj.data("type")+"'>";
						});
						formObj.append(str).submit();
					}else{
						return false;
					}
				} 

				
				formObj.submit();
			}); 
		});
</script>
<script>
$(document).ready(function(){
	(function(){
		var bno = '<c:out value="${board.bno}"/>';
			console.log(bno);
			$.getJSON("/board/getAttachList", {bno: bno}, function(arr){
			console.log(arr);

			var str = "";

				$(arr).each(function(i, attach){

					if(attach.fileType){
						var fileCallPath = encodeURIComponent( attach.uploadPath+ "/s_"+attach.uuid +"_"+attach.fileName);

						str += "<li data-path='"+attach.uploadPath+"' data-uuid='"
							+attach.uuid+"' data-filename='"+attach.fileName+"' data-type='"+attach.fileType+"' ><div>";
						str += "<span> "+ attach.fileName+"</span>";
						str += "<button type='button' class='btn btn-warning btn-circle' data-file='"+fileCallPath +"' data-type='image'>"
							+"<i class='fa fa-times'></i></button><br>";
						str += "<img src='/display?fileName="+fileCallPath+"'>"; 
						str += "</div></li>";
					}else{
						str += "<li data-path='"+attach.uploadPath+"' data-uuid='"
							+ attach.uuid+"' data-filename='"+attach.fileName+"' data-type='"+attach.fileType+"' ><div>";
						str += "<img src='/resources/img/attach.png' style=\"width: 20px;\">"; 
						str += "<span> "+ attach.fileName+"</span>"; 
						str += "<button type='button' data-file=\'"+fileCallPath+"\' data-type='file' ";	
						str += " class='btn btn-warning btn-circle'><i class='fa fa- times'></i>x</button>";
						str += "</div>";
						str += "</li>";
					}
				});
				$(".uploadResult ul").html(str);
			});//end getjson
	})();
});

$(".uploadResult").on("click", "button", function(e){ 

	console.log("delete file");

	if(confirm("Remove this file? ")){
		
		var targetLi = $(this).closest("li");
		targetLi.remove(); 
	}
});

var regex = new RegExp("(.*?)\.(exe|sh|zip|alz|png)$"); 
var maxSize = 5242880; //5MB

function checkExtension(fileName, fileSize){
	
	if(fileSize >= maxSize){ 
		alert("파일 사이즈 초과");
	     return false;
     }
	   
	if(regex.test(fileName)){

		alert("해당 종류의 파일은 업로드할 수 없습니다.");
	     return false;
	 }
   return true;
 }

	var csrfHeaderName ="${_csrf.headerName}";
	var csrfTokenValue="${_csrf.token}";

 $("input[type='file']").change(function(e){

		var formData = new FormData();

		var inputFile = $("input[type='file']");
		console.log(inputFile);
		console.log(inputFile[0]);
		var files = inputFile[0].files;

		for(var i = 0; i < files.length; i++){

			if(!checkExtension(files[i].name, files[i].size) ){
			      return false;
			}
			console.log(files[i])
			formData.append("uploadFiles", files[i]); 
		}
		
		$.ajax({
			url : "/uploadAjaxAction",
			processData : false,
			contentType : false,
			data : formData,
			type : "post",
			beforeSend: function(xhr) {
				xhr.setRequestHeader(csrfHeaderName, csrfTokenValue); 
			},
			dataType : "json",
			success : function(result){
					console.log(result);

					showUploadResult(result);

				}
		});

 });

 function showUploadResult(uploadResultArr){
	 
	 if(!uploadResultArr || uploadResultArr.length == 0){ return; } 

	 var uploadUL = $(".uploadResult ul");
	 var str ="";

	 $(uploadResultArr).each(function(i, obj){

		console.log(obj.fileName);
		
		//image type 
		if(obj.image){

			var fileCallPath = encodeURIComponent( obj.uploadPath+ "/s_"+obj.uuid +"_"+obj.fileName);
			str += "<li data-path='"+obj.uploadPath+"' data-uuid='"
				+obj.uuid+"' data-filename='"+obj.fileName+"' data-type='"+obj.image+"'><div>";
			str += "<span> "+ obj.fileName+"</span>";
			str += "<button type='button' class='btn btn-warning btn-circle' data-file='"+fileCallPath +"' data-type='image'>"
				+"<i class='fa fa-times'></i></button><br>";
			str += "<img src='/display?fileName="+fileCallPath+"'>"; 
			str += "</div></li>";

		}else{
			var fileCallPath = encodeURIComponent( obj.uploadPath+"/"+obj.uuid +"_"+obj.fileName);
			console.log(fileCallPath);
			console.log(obj.uploadPath);
			console.log(obj.uuid);
			/* var fileLink = fileCallPath.replace(new RegExp(/\\/g),"/"); */
			str += "<li data-path='"+obj.uploadPath+"' data-uuid='"
				+obj.uuid+"' data-filename='"+obj.fileName+"' data-type='"+obj.image+"'><div>";
			str += "<img src='/resources/img/attach.png' style=\"width: 20px;\">";
			str += "<span> "+ obj.fileName+"</span>";
			str += "<button type='button' class='btn btn-warning btn-circle' "
				+"data-file='"+fileCallPath +"' data-type='file'>"
				+"<i class='fa fa-times'></i></button>";
			str += "</div></li>";

		}
		
	});

	uploadUL.append(str);
 }
</script>
<%@include file="../includes/footer.jsp"%>
