<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style>
/*댓글*/
.replyWrite>table {	margin-top: 100px; }
.rWriter {
	display: inline-block;
	vertical-align: top;
	font-size : 1.2em;
	font-weight: bold;
}
.rPass, .rDate { display: inline-block; }
.rContent, .replyBtnArea {
	height: 100%;
	width: 100%;
}
.replyBtnArea { text-align: right; }
.replyUpdateWriter{
	width: 16%;
}
.replyUpdatePassword{
	width: 20%;
}
.replyUpdateContent {
	resize: none;
	width: 100%;
}
.reply-row{
	border-top : 1px solid #ccc;
	padding : 15px 0;
}
#replyIdArea, #replyPwArea{
	float:left;
}
</style>

<div id="reply-area ">
	<!-- 댓글 작성 부분 -->
	<div class="replyWrite">
		<table align="center">
			<tr>
				<td id="replyIdArea">
					<label for="replyId" class="input-group-addon mr-3 insert-label">작성자</label>
					<input type="text" class="replyId" id="replyId" name="replyId" size="20"/><span style="color:#aaa;" id="RICounter"  >(0 / 최대 10자)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
				</td>
				
				<td id="replyPwArea">
					<label for="replyPw" class="input-group-addon mr-3 insert-label">비밀번호</label>
					<input type="password" class="replyPw" id="replyPw" name="replyPw" size="20"/><span style="color:#aaa;" id="RPCounter"  >(0 / 최대 10자)</span>
				</td>
			</tr>
			<tr>
				<td id="replyContentArea">
					<textArea rows="3" id="replyContent"></textArea>
					<span style="color:#aaa;" id="RCCounter"  >(0 / 최대 100자)</span>
				</td>
				<td id="replyBtnArea">
					<button class="btn btn-primary" id="addReply" onclick="addReply();">
						댓글<br>등록
					</button>
				</td>
			</tr>
		</table>
	</div>


	<!-- 댓글 출력 부분 -->
	<div class="replyList mt-5 pt-2">
		<ul id="replyListArea">
			<c:forEach items="${rList}" var="reply">
				<li class="reply-row">
					<div id="idPw" class="idPw">
						<p class="rWriter">${reply.replyId}</p>
						<p id="${reply.replyNo}" style="display:none;">${reply.replyPw}</p>
<!-- 						
						<label for="rPass" class="input-group-addon mr-3 insert-label">비밀번호</label>
 -->						
						<input type="password" class="rPass" id="rPass${reply.replyNo}" name="rPass" placeholder="비밀번호를 입력하세요" /><span style="color:#aaa;" class="SRPCounter" id="SRPCounter${reply.replyNo}"  >(0 / 최대 10자)</span>

						<p class="rDate">&nbsp;&nbsp;&nbsp; 작성일 : <fmt:formatDate value="${reply.createDate }" pattern="yyyy년 MM월 dd일 HH:mm"/></p>
					</div>
	
					<pre class="rContent">${reply.replyContent }</pre>
					
<%-- 					
					<c:if test="${reply.memberNo == loginMember.memberNo}">
 --%>					
						<div class="replyBtnArea">
							<button class="btn btn-primary btn-sm ml-1" id="updateReply" onclick="showUpdateReply(${reply.replyNo}, this)">수정</button>
							<button class="btn btn-primary btn-sm ml-1" id="deleteReply" onclick="deleteReply(${reply.replyNo})">삭제</button>
						</div>
<%-- 						
					</c:if>
 --%>					
				</li>
			</c:forEach>
		</ul>
	</div>

</div>

<script>

// 아람미가 정리해준 ajax
// ajax와 컨트롤러의 관계 이해하기..
// ajax url 안에 적힌 주소로 type에 적힌 전달방식, 그리고 data 값을 가지고 컨트롤러의 if문으로 가서
// 처리한 후 컨트롤러의 response.getWriter().print(result); 안에서 print( )괄호 안에 적힌 값을 가지고
// 다시 ajax 안으로 돌아와서 success 안의 function( )괄호 안으로 담겨서 이 함수가 실행된다 
// 여기서 ( )괄호 안에 적힌 매개변수명은 달라도 상관이 없음 

// 로그인한 회원의 회원 번호, 비로그인 시 "" (빈문자열)
// const loginMemberNo = "${loginMember.memberNo}";

// 현재 게시글 번호
const boardNo = ${board.boardNo};

// 수정 전 댓글 요소를 저장할 변수 (댓글 수정 시 사용)
let beforeReplyRow;



// 댓글등록버튼에 addReply() 버튼이 숨겨져 있음
// 댓글 작성 부분 id replyContent

//-----------------------------------------------------------------------------------------
// 댓글 등록
function addReply()	{
	
	// 작성된 댓글 내용 얻어오기
	const replyContent = $("#replyContent").val();
	const replyId = $("#replyId").val();
	const replyPw = $("#replyPw").val();
	
		if(replyId.trim() == ""){
			swal("작성자를 입력해주세요. 작성자는 한글 2~10글자 내로 입력가능합니다.");
	        $("#replyId").focus();
		}else if(replyPw.trim() == ""){
			swal("비밀번호를 입력해주세요. 비밀번호는 영문자,숫자,특수문자 포함 6~10자리이내 입니다.");
	        $("#replyPw").focus();
		}else if(replyContent.trim() == ""){ // 작성된 댓글 내용이 없을경우
			swal("댓글 작성 후 클릭해주세요. 댓글은 100글자 이내로 입력가능합니다.");
            $("#replyContent").focus();
		}else{ // 로그인이 되어있으면서 작성된 댓글내용이 있을 때
			
			// 욕설필터
/* 			let arr = ['xxx', '야xx', '18'];
			// 정규식으로 조건 작성 
			for(){
				
			} */
			
			// 로그인 0, 댓글 작성0
			// data : 파라미터와 일치하는 것이 vo에 있는지 확인 
			$.ajax({
				url : "${contextPath}/reply/insertReply", // 필수속성!!!!!!!!!! 반드시 ajax안에서 써야하는 코드
				type : "POST", 
				data : {
						"boardNo" : boardNo,
						"replyId" : replyId,
						"replyPw" : replyPw,
						"replyContent" : replyContent
				}, // 비동기 통신을 할 때 전달할 파라미터
				success : function(result){
					if(result > 0){ // 댓글 삽입 성공
						swal({"icon" : "success" , "title" : "댓글 등록 성공"});
						$("#replyId").val(""); 
						$("#replyPw").val("");
						$("#replyContent").val(""); // 댓글 작성 내용 삭제
						selectReplyList(); // 비동기로 댓글 목록 갱신
					}
				}, // 비동기 통신이 성공했을 때
				error : function(){
					console.log("댓글삽입실패");
				} // 비동기 통신이 실패했을 때 
				
			});
		}
		
}	

//-----------------------------------------------------------------------------------------
//해당 게시글 댓글 목록 조회
function selectReplyList(){
	
	$.ajax({
		url : "${contextPath}/reply/list",
		data : {"boardNo" : boardNo},
		// K : 문자열, V : 변수명
		type : "POST",
		dataType : "JSON", // 응답되는 데이터의 형식이 JSON임을 알려줌 -> 자바스크립트 객체로 변환됨
		success : function(rList){
			console.log(rList);
			
	         $("#replyListArea").html(""); // 기존 정보 초기화
	         // 왜? 새로읽어온 댓글 목록으로 다시 만들어서 출력하려고!
	        		 
	         $.each(rList, function(index, item){
	        	// $.each() : jQuery의 반복문
	        	// rList : ajax 결과로 받은 댓글이 담겨있는 객체 배열
	        	// index : 순차 접근 시 현재 인덱스
	        	// item : 순차 접근 시 현재 접근한 배열 요소(댓글 객체 하나)
	        	
	            // console.log(rList[i]);
	            
	            var li = $("<li>").addClass("reply-row");
	         
	            // 작성자, 작성일, 수정일 영역 
	            var div = $("<div>").addClass("idPw");
//				const textColor = $("<input>").attr({"type": "text", "id" : "textColor"}).val(info.event.textColor);
//				const li9 = $("<li>").text( "글자색 : ").append(textColor); // 글자색
	            var rWriter = $("<p>").addClass("rWriter").text( item.replyId);
	            var rPass = $("<input>").attr({"type" : "password", "class" : "rPass", "id" : "rPass"+item.replyNo, "placeholder" : "비밀번호를 입력하세요"});
 	            // var cPass = $("<input>").attr({"type" : "password", "class" : "cPass", "style" : "display:none;", "id" : item.replyNo}).html(item.replyPw);
 	            //var cPass = $("<input>").attr({"type" : "password", "class" : "cPass", "style" : "display:none;", "id" : item.replyNo}).html(item.replyPw);
 	            var cPass = $("<p>").attr({"class" : "cPass", "style":"display:none;", "id" : item.replyNo}).html(item.replyPw);
 	            var SRPCounter = $("<span>").attr({"class" : "SRPCounter", "id" : "SRPCounter"+item.replyNo,  "style" : "color:#aaa"}).text("(0 / 최대 10자)");
 	           /* <span style="color:#aaa;" id="RPCounter"  >(0 / 최대 10자)</span> */
 	            /* <input id="${reply.replyNo}" style="display:none;" value="${reply.replyPw}" /> */
 	            
 	            console.log("cPass:" + JSON.stringify(cPass));
 	            var rDate = $("<p>").addClass("rDate").text("작성일 : " + item.createDate);
	            div.append(rWriter).append(rPass).append(SRPCounter).append(cPass).append(rDate);
	            
	            // 댓글 내용
	            var rContent = $("<pre>").addClass("rContent").html(item.replyContent);
	            
	            // 대댓글, 수정, 삭제 버튼 영역
	            var replyBtnArea = $("<div>").addClass("replyBtnArea");
	            
	            // 현재 댓글의 작성자와 로그인한 멤버의 아이디가 같을 때 버튼 추가
	            //if(item.memberNo == loginMemberNo){
	               
	               // ** 추가되는 댓글에 onclick 이벤트를 부여하여 버튼 클릭 시 수정, 삭제를 수행할 수 있는 함수를 이벤트 핸들러로 추가함. 
	               var showUpdate = $("<button>").addClass("btn btn-primary btn-sm ml-1").text("수정").attr("onclick", "showUpdateReply("+item.replyNo+", this)");
	               var deleteReply = $("<button>").addClass("btn btn-primary btn-sm ml-1").text("삭제").attr("onclick", "deleteReply("+item.replyNo+")");
	               
	               replyBtnArea.append(showUpdate).append(deleteReply);
	            //}
	            
	            // 댓글 요소 하나로 합치기
	            li.append(div).append(rWriter).append(rDate);
	            li.append(div).append(rPass).append(rDate);
	            li.append(div).append(cPass).append(rDate);
	            li.append(div).append(rContent).append(replyBtnArea);
	            
	            
	            // 합쳐진 댓글을 화면에 배치
	            $("#replyListArea").append(li);
	            
	         });
		},
		error : function(){
			console.log("댓글 목록 조회 실패");
		}
		
	});
}

// -----------------------------------------------------------------------------------------
// 일정 시간마다 댓글 목록 갱신
/* const replyInterval = setInterval(function(){
	selectReplyList();
}, 5000);  */// 5초 -> ms단위로 5000
// 여기 코드 주석하면 실시간 업데이트 사라짐 


// -----------------------------------------------------------------------------------------
// 댓글 수정 폼

function showUpdateReply(replyNo, el){
	// el : 수정 버튼 클릭 이벤트가 발생한 요소
	
	// 비밀번호 입력이 안되었을 때
   	// var beforeWriter = $(el).parent().prev().parent().children().children().html();
	// let currentPass = document.getElementById(replyNo).value;
	let currentPass = document.getElementById(replyNo).innerText;
	// let replyPass = $(el).parent().prev().children().prev().html();
	// let replyPass = document.getElementById("rPass"+replyNo).value;
	
	console.log("replyPass : " + replyPw);
    console.log("currentPass : " + currentPass);
    console.log("rPass"+replyNo);
    
	 if ($("#rPass"+replyNo).val().trim().length == 0) {
//	 if ($(el).val().trim().length == 0) {
	    alert("비밀번호를 입력해 주세요.");
	    $("#rPass"+replyNo).focus();
	    return false;
	 }else{
		    if(currentPass == $("#rPass"+replyNo).val()){
		    	//---------------------------------------------------- 여기가 문제 왜 왜왜오애ㅗ애ㅗ애왜오애ㅗ애ㅗ오애오애ㅗ
		 	   // 이미 열려있는 댓글 수정 창이 있을 경우 닫아주기
/* 		 	   
		 	   if($(".replyUpdateWriter").length > 0){
		 		      $(".replyUpdateWriter").eq(0).parent().html(beforeReplyRow);
		 	   }
		 	   if($(".replyUpdatePassword").length > 0){
		 		   $(".replyUpdatePassword").eq(0).parent().html(beforeReplyRow);
		 	   }
 */		 	   
		 	   if($(".replyUpdateContent").length > 0){
		 	      $(".replyUpdateContent").eq(0).parent().html(beforeReplyRow);
		 	   }
		 	   if($(".rPass").length > 0){
		 	      $(".rPass").eq(0).parent().html(beforeReplyRow);
		 	      $(".SRPCounter").remove();
		 	   }
		    	
		  	   // 댓글 수정화면 출력 전 요소를 저장해둠.
		  	   beforeReplyRow = $(el).parent().parent().html();
		  	   
		  	   // 작성되어있던 내용(수정 전 댓글 내용) 
		  	   var beforeWriter = $(el).parent().prev().parent().children().children().html();
		  	   var beforePassword = $(el).parent().prev().parent().children().children().next().next().html();
		  	   var beforeContent = $(el).parent().prev().html();
		  	   
		  	   // 이전 댓글 내용의 크로스사이트 스크립트 처리 해제, 개행문자 변경
		  	   // -> 자바스크립트에는 replaceAll() 메소드가 없으므로 정규 표현식을 이용하여 변경
/* 		  	   
		  	   beforeWriter = beforeWriter.replace(/&amp;/g, "&");   
		  	   beforeWriter = beforeWriter.replace(/&lt;/g, "<");   
		  	   beforeWriter = beforeWriter.replace(/&gt;/g, ">");   
		  	   beforeWriter = beforeWriter.replace(/&quot;/g, "\"");   
		  	   
		  	   beforeWriter = beforeWriter.replace(/<br>/g, "\n");   
		  	   
		  	   beforePassword = beforePassword.replace(/&amp;/g, "&");   
		  	   beforePassword = beforePassword.replace(/&lt;/g, "<");   
		  	   beforePassword = beforePassword.replace(/&gt;/g, ">");   
		  	   beforePassword = beforePassword.replace(/&quot;/g, "\"");   
		  	   
		  	   beforePassword = beforePassword.replace(/<br>/g, "\n");   
 */		  	   
		  	   
		  	   beforeContent = beforeContent.replace(/&amp;/g, "&");   
		  	   beforeContent = beforeContent.replace(/&lt;/g, "<");   
		  	   beforeContent = beforeContent.replace(/&gt;/g, ">");   
		  	   beforeContent = beforeContent.replace(/&quot;/g, "\"");   
		  	   
		  	   beforeContent = beforeContent.replace(/<br>/g, "\n");   
		 	
		        // var rPass = $("<input>").attr({"type" : "password", "class" : "rPass"}).val("");	   
		  	   // 기존 댓글 영역을 삭제하고 textarea를 추가 
/* 		  	   
		 	   $(el).parent().parent().children("#idPw").children(".rWriter").remove();
		  	   var writer = $("<input>").addClass("replyUpdateWriter").attr("rows", "3").val(beforeWriter);
		  	   $(el).parent().parent().children("#idPw").children(".rDate").before(writer);

		  	   var password = $("<input>").attr({"class" : "replyUpdatePassword", "type" : "password", "rows" : "3"}).val(beforePassword);
		  	   $(el).parent().parent().children("#idPw").children(".rDate").before(password);
 */		  	   
		  	   $(el).parent().parent().children("#idPw").children(".rPass").remove();
	     	   $(el).parent().prev().remove();
		  	   var textarea = $("<textarea>").addClass("replyUpdateContent").attr("rows" , "3").val(beforeContent);
		  	   
		  	   var SRPCounter = $("<span>").attr({"class" : "RUCCounter", "id" : "RUCCounter",  "style" : "color:#aaa"}).text("(0 / 최대 100자)");
		  	   $(el).parent().before(textarea).before(SRPCounter);
		  	   
		  	   // 수정 버튼
		  	   var updateReply = $("<button>").addClass("btn btn-primary btn-sm ml-1 mb-4").text("댓글 수정").attr("onclick", "updateReply(" + replyNo + ", this)");
		  	   
		  	   // 취소 버튼
		  	   var cancelBtn = $("<button>").addClass("btn btn-primary btn-sm ml-1 mb-4").text("취소").attr("onclick", "updateCancel(this)");
		  	   
		  	   var replyBtnArea = $(el).parent();
		  	   
		  	   $(replyBtnArea).empty(); 
		  	   $(replyBtnArea).append(updateReply); 
		  	   $(replyBtnArea).append(cancelBtn); 
//		  		ul.append(li6, li7, li8);
//		  		$("#detailBody").append(ul);
		 	   
		    }else{
		        alert("비밀번호가 틀렸습니다. 비밀번호는 영문자,숫자,특수문자 포함 6~10자리이내로 입력해주세요.");
		        $("#rPass"+replyNo).focus();
		        document.getElementById("rPass"+replyNo).value = null;
		        return false;
		        console.log("비밀번호틀림");
		     }
	 }
	/* 
	<ul>
		<li>
			<div>
				<p>작성자</p>
				<input/>비밀번호
				<p>작성일</p>
			</div>
			<p>내용</p>    =>   $(el).parent().before(textarea);
			<div>
				<button></button>
				<button></button>
			</div>
		</li>
	</ul>
	*/
	
	
	   $(document).ready(function(){
		      $("#RUCCounter").html("(" + $(".replyUpdateContent").val().length + " / 최대 100자)");
		   });
	 //-----------------------------------------------------------글 수정 스크립트처리
	 
	 $(".replyUpdateContent").on("input", function(e){
	     var content = $(this).val();
	     $('#RUCCounter').html("("+content.length+" / 최대 100자)");    //글자수 실시간 카운팅
	 
	     if (content.length > 100){
	         alert("최대 100자까지 입력 가능합니다.");
	         $(this).val(content.substring(0, 100));
	         $('#RUCCounter').html("(100 / 최대 100자)");
	     }
	     
	 });

	 $('.replyUpdateContent').keyup(function (e){
	     var content = $(this).val();
	     $('#RUCCounter').html("("+content.length+" / 최대 100자)");    //글자수 실시간 카운팅
	 
	     if (content.length > 100){
	         alert("최대 100자까지 입력 가능합니다.");
	         $(this).val(content.substring(0, 100));
	         $('#RUCCounter').html("(100 / 최대 100자)");
	     }
	 });
	 $(".replyUpdateContent").bind('paste', function(e){
	      var el = $(this);
	      setTimeout(function(){
	          var text = $(el).val();
	      },100);
	 });
	 
	 $(".replyUpdateContent").focusout(function(){
	     var content = $(this).val();
	     $('#RUCCounter"').html("("+content.length+" / 최대 100자)");    //글자수 실시간 카운팅
	 
	     if (content.length > 100){
	         alert("최대 100자까지 입력 가능합니다.");
	         $(this).val(content.substring(0, 100));
	         $('#RUCCounter').html("(100 / 최대 100자)");
	     }
	    $(".replyUpdateContent").bind('paste',function(e){
	         var el = $(this);
	         setTimeout(function(){
	             var text = $(el).val();
	         },100);
	    });
	 });

}

//-----------------------------------------------------------------------------------------
//댓글 수정 취소 시 원래대로 돌아가기
function updateCancel(el){
	$(el).parent().parent().html(beforeReplyRow);
}


//-----------------------------------------------------------------------------------------
// 댓글 수정
function updateReply(replyNo, el){
	
	// 수정된 댓글 내용
	// const replyId = $(el).parent().prev().val();
	// const replyPw = $(el).parent().prev().val();
	//const replyContent = $(el).parent().prev().val();
	const replyContent = $(el).parent().prev().prev().val();
	console.log("replyContent : " + replyContent);
	
	$.ajax({
		url : "${contextPath}/reply/updateReply",
		type : "POST",
		data : {"replyNo" : replyNo,
/* 			
				"replyId" : replyId,
				"replyPw" : replyPw,
 */			
				"replyContent" : replyContent},
		success : function(result){
			if(result > 0){
				swal({"icon" : "success", "title" : "댓글 수정 성공"});
				selectReplyList();
			}
		},
		error : function(){
			console.log("댓글 수정 실패");
		}
	});
}


//-----------------------------------------------------------------------------------------
//댓글 삭제
function deleteReply(replyNo){
	
	let currentPass = document.getElementById(replyNo).value;
	
	 if ($("#rPass"+replyNo).val().trim().length == 0) {
	    alert("비밀번호를 입력해 주세요.");
	    $("#rPass"+replyNo).focus();
	    return false;
	    
	    console.log("비밀번호 미입력");
	 }else{
		 
		 if(currentPass == $("#rPass"+replyNo).val()){
			 
			 if(confirm("정말로 삭제하시겠습니까?")){
			      var url = "${contextPath}/reply/deleteReply";
			      
			      $.ajax({
			         url : url,
			         data : {"replyNo" : replyNo},
			         success : function(result){
			            if(result > 0){
			               selectReplyList(boardNo);
			               swal({"icon" : "success" , "title" : "댓글 삭제 성공"});
			            }
			         }, error : function(){
			            console.log("ajax 통신 실패");
			         }
			      });
			   }
		 }else{
		        alert("비밀번호가 틀렸습니다. 비밀번호는 영문자,숫자,특수문자 포함 6~10자리이내로 입력해주세요.");
		        $("#rPass"+replyNo).focus();
		        document.getElementById("rPass${reply.replyNo}").value = null;
		        return false;
		        console.log("비밀번호틀림");
	     }
	 }
 }
 
 
 
 //-----------------------------------------------------------작성자 스크립트처리     "#replyId"

       $("#replyId").on("input", function(e){
          var inputId = $('#replyId').val();
          var idReg = /[a-z0-9]|[ \[\]{}()<>?|`~!@#$%^&*-_+=,.;:\"'\\]/g;
          
          if(idReg.test(inputId)){
        	  alert("작성자는 한글 2~10자만 입력 가능합니다. 다시 입력해주세요.");
           $("#replyId").focus();
           var insertId = $("#replyId").val().replace(idReg, "");
           $("#replyId").val(insertId);
        	  return false;
          }
          
      });
 
       $('#replyId').keyup(function (e){
           var content = $(this).val();
           $('#RICounter').html("("+content.length+" / 최대 10자)");    //글자수 실시간 카운팅
       
           if (content.length > 10){
               alert("최대 10자까지 입력 가능합니다.");
               $(this).val(content.substring(0, 10));
               $('#RICounter').html("(10 / 최대 10자)");
           }
       });
       $("#replyId").bind('paste', function(e){
            var el = $(this);
            setTimeout(function(){
                var text = $(el).val();
            },10);
       });
       
       $("#replyId").focusout(function(){
           var content = $(this).val();
           $('#RICounter').html("("+content.length+" / 최대 10자)");    //글자수 실시간 카운팅
       
           if (content.length > 10){
               alert("최대 10자까지 입력 가능합니다.");
               $(this).val(content.substring(0, 10));
               $('#RICounter').html("(10 / 최대 10자)");
           }
          $("#replyId").bind('paste',function(e){
               var el = $(this);
               setTimeout(function(){
                   var text = $(el).val();
               },10);
          });
       });
 
//-----------------------------------------------------------비밀번호 스크립트처리    "#replyPw"   
      $("#replyPw").on("input", function(e){
          var boardPass = $('#replyPw').val();
          var idReg = /\s/;
          
          if(idReg.test(boardPass)){
        	alert("비밀번호는 공백이 입력이 불가합니다. 영문,숫자,특수문자를 섞어서 6~10자리내로 입력해주세요");
           $("#replyPw").focus();
           var updatePass = $("#replyPw").val().replace(idReg, "");
           $("#replyPw").val(updatePass);
        	  return false;
          }
          
      });
      
      $('#replyPw').keyup(function (e){
          var content = $(this).val();
          $('#RPCounter').html("("+content.length+" / 최대 10자)");    //글자수 실시간 카운팅
      
          if (content.length > 10){
              alert("비밀번호는 영어대소문자, 숫자, 특수문자 포함하여 최대 10자까지 입력가능합니다.");
              $(this).val(content.substring(0, 10));
              $('#RPCounter').html("(10 / 최대 10자)");
          }
      });
      $("#replyPw").bind('paste', function(e){
           var el = $(this);
           setTimeout(function(){
              var text = $(el).val();
           },10);
      });
      
      $("#replyPw").focusout(function(){
          var content = $(this).val();
          $('#RPCounter').html("("+content.length+" / 최대 10자)");    //글자수 실시간 카운팅   
      
          if (content.length > 10){
              alert("최대 10자까지 입력 가능합니다.");
              $(this).val(content.substring(0, 10));
              $('#RPCounter').html("(10 / 최대 10자)");
          }
         $("#replyPw").bind('paste',function(e){
              var el = $(this);
              setTimeout(function(){
                  var text = $(el).val();
              },10);
         });
      });
      
    //-----------------------------------------------------------글내용 스크립트처리      "#replyContent"   RCCounter
      
      $("#replyContent").on("input", function(e){
          var content = $(this).val();
          $('#RCCounter').html("("+content.length+" / 최대 100자)");    //글자수 실시간 카운팅
      
          if (content.length > 100){
              alert("최대 100자까지 입력 가능합니다.");
              $(this).val(content.substring(0, 100));
              $('#RCCounter').html("(100 / 최대 100자)");
          }
          
      });
    
      $('#replyContent').keyup(function (e){
          var content = $(this).val();
          $('#RCCounter').html("("+content.length+" / 최대 100자)");    //글자수 실시간 카운팅
      
          if (content.length > 100){
              alert("최대 100자까지 입력 가능합니다.");
              $(this).val(content.substring(0, 100));
              $('#RCCounter').html("(100 / 최대 100자)");
          }
      });
      $("#replyContent").bind('paste', function(e){
           var el = $(this);
           setTimeout(function(){
               var text = $(el).val();
           },100);
      });
      
      $("#replyContent").focusout(function(){
          var content = $(this).val();
          $('#RCCounter"').html("("+content.length+" / 최대 100자)");    //글자수 실시간 카운팅
      
          if (content.length > 100){
              alert("최대 100자까지 입력 가능합니다.");
              $(this).val(content.substring(0, 100));
              $('#RCCounter').html("(100 / 최대 100자)");
          }
         $("#replyContent").bind('paste',function(e){
              var el = $(this);
              setTimeout(function(){
                  var text = $(el).val();
              },100);
         });
      });
      
      // ----------------------------------------페이지 로딩 시 글자수 카운팅
/*       
   $(document).ready(function(){
      $("#tCounter").html("(" + $("#boardTitle").val().length + " / 최대 30자)");
      $("#cCounter").html("(" + $("#boardContent").val().length + " / 최대 300자)");
      $("#wCounter").html("(" + $("#boardWriter").val().length + " / 최대 10자)");
      $("#pCounter").html("(" + $("#boardPass").val().length + " / 최대 10자)");
   });
 */	   
 
 
 //-----------------------------------------------------------수정삭제 시 입력 비밀번호 스크립트처리    "#rPass${reply.replyNo}           SRPCounter${reply.replyNo}
 $("#rPass${reply.replyPass}").on("input", function(e){
     var boardPass = $("#rPass${reply.replyPass}").val();
     var idReg = /\s/;
     
     if(idReg.test(boardPass)){
   	alert("비밀번호는 공백이 입력이 불가합니다. 영문,숫자,특수문자를 섞어서 6~10자리내로 입력해주세요");
      $("#rPass${reply.replyPass}").focus();
      var updatePass = $("#rPass${reply.replyPass}").val().replace(idReg, "");
      $("#rPass${reply.replyPass}").val(updatePass);
   	  return false;
     }
 });
 
 $("#rPass${reply.replyPass}").keyup(function (e){
     var content = $(this).val();
     $('#SRPCounter${reply.replyPass}').html("("+content.length+" / 최대 10자)");    //글자수 실시간 카운팅
 
     if (content.length > 10){
         alert("비밀번호는 영어대소문자, 숫자, 특수문자 포함하여 최대 10자까지 입력가능합니다.");
         $(this).val(content.substring(0, 10));
         $('#SRPCounter${reply.replyPass}').html("(10 / 최대 10자)");
     }
 });
 $("#rPass${reply.replyPass}").bind('paste', function(e){
      var el = $(this);
      setTimeout(function(){
         var text = $(el).val();
      },10);
 });
 
 $("#rPass${reply.replyPass}").focusout(function(){
     var content = $(this).val();
     $('#SRPCounter${reply.replyPass}').html("("+content.length+" / 최대 10자)");    //글자수 실시간 카운팅   
 
     if (content.length > 10){
         alert("최대 10자까지 입력 가능합니다.");
         $(this).val(content.substring(0, 10));
         $('#SRPCounter${reply.replyPass}').html("(10 / 최대 10자)");
     }
    $("#rPass${reply.replyPass}").bind('paste',function(e){
         var el = $(this);
         setTimeout(function(){
             var text = $(el).val();
         },10);
    });
 });
 
</script>