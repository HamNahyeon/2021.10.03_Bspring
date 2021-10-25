<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- 배포된 웹 애플리케이션의 최상위 주소를 간단히 얻어올 수 있도록
	application범위로 변수를 하나 생성 --%>
	<c:set var="contextPath"  scope="application"
    value="${pageContext.servletContext.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>Final Project</title>

<!-- Bootstrap core CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css" integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l" crossorigin="anonymous">

<!-- Bootstrap core JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-Piv4xVNRyMGpqkS2by6br4gNJ7DXjqk09RmUpJ8jgGtD7zP9yug3goQfGII0yAns" crossorigin="anonymous"></script>
 
<!-- sweetalert API 추가 -->
<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>

<style>
body {
	padding-top: 56px;
}
</style>

</head>

<body>

	<!-- Navigation으로 된 header -->
	<div class="header navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
		<div class="container">
		
			<!-- 메인페이지 이동 로고 -->
			<a class="navbar-brand" href="${contextPath}">Final Project</a>
			
			<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="collapse navbar-collapse" id="navbarResponsive">
				<ul class="navbar-nav ml-auto">
					<li class="nav-item"><a class="nav-link" href="${contextPath}/chat/roomList">Chatting</a></li>
					<li class="nav-item"><a class="nav-link" href="${contextPath}/board/1/list">Free Board</a></li>
					<li class="nav-item"><a class="nav-link" href="${contextPath}/board/2/list">Infomation Board</a></li>
					<li class="nav-item"><a class="nav-link" href="${contextPath}/board/3/list">QnA Board</a></li>
															<%-- ?type=1 -> GET방식 요청의 파라미터 : queryString --%>
					<%-- 1) servlet : /semi
						 2) file path : 파일의 경로 WebContent 폴더를 가리킴  --%>

					<%-- 로그인을 했는지 안했는지 구분하는 방법
						-> session에 loginMember 유무 --%>
					<c:choose>
						<%-- 로그인이 되어있지 않을 때
							empty : 비어있거나 null인 경우 true
							loginMember : 별도의 scope설정이 없을 경우
								page -> request -> session -> application
								순서대로 검색하며 loginMember를 찾음 --%>
						<c:when test="${ empty loginMember }" >
							<%-- 로그인 버튼 --%>
							<%-- Modal 동작 버튼은 data-toggle="modal" 속성과 href 속성값으로 보여질 Modal 영역 id를 작성하면된다. --%>
							<li class="nav-item active"><a class="nav-link" data-toggle="modal" href="#modal-container-1">Login</a></li>
						</c:when>
						
						<%-- 로그인이 되어있을 때 --%>
						<c:otherwise >
							<li class="nav-item active"><a class="nav-link" href="${ contextPath }/member/myPage">${ loginMember.memberName }</a></li>
							<li class="nav-item active"><a class="nav-link" href="${ contextPath }/member/logout">Logout</a></li>
						</c:otherwise>
					</c:choose>
					
				</ul>
			</div>
		</div>
	</div>

	<%-- Modal창에 해당하는 html 코드는 페이지 마지막에 작성하는게 좋다 --%>
	<div class="modal fade" id="modal-container-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">

			<div class="modal-content">

				<div class="modal-header">
					<h5 class="modal-title" id="myModalLabel">로그인 모달창</h5>
					<button type="button" class="close" data-dismiss="modal">
					<!-- data-dismiss="modal" 모달창을 닫는 버튼 -->
						<span aria-hidden="true">×</span>
					</button>
				</div>

				<div class="modal-body">
					<form class="form-signin" method="POST" 
						  action="${contextPath}/member/login" onsubmit="return loginValidate();">
						  <!-- onsubmit : form 태그 내부에서 엔터 또는 submit 타입의 
						  				   버튼이 클릭되었을 때를 나타내는 이벤트 리스너 
						  				   
						  	   onsubmit = "return false;"
						  	   			   -> form태그 내부에서 발생하는 submit 이벤트를 제거
						  	   			      == submit 기본 이벤트 제거  -->

						<input type="text" class="form-control" id="memberId" name="memberId" placeholder="아이디" value="${ cookie.saveId.value }">
						<br>
						<input type="password" class="form-control" id="memberPw" name="memberPw" placeholder="비밀번호">
						<br>
						<!-- - -->
						<%-- 이 전에 저장해둔 아이디가 존재한다면 --%>
						<c:if test="${ !empty cookie.saveId.value }">
							<c:set var="ch" value="checked"/>
						</c:if>

						<div class="checkbox mb-3">
							<label> 
								<input type="checkbox" name="save" id="save" ${ch}> 아이디 저장
							</label>
						</div>

						<button class="btn btn-lg btn-primary btn-block" type="submit">로그인</button>
						
																		<%-- servlet요청주소를 적어야 함 --%>
						<a class="btn btn-lg btn-secondary btn-block" href="${contextPath}/member/signUp">회원가입</a>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>

	<%-- 로그인 실패와 같은 메시지가 서버로 부터 전달되어 온 경우 출력 --%>
	<%-- 로그인 실패성공 여부 관계없이 text는 session에 올라가 있음 --%>
	<c:if test="${ !empty title }">
		<script>
			<%-- alert("${text}"); --%>
			swal({
				"icon" :  "${icon}",
				"title" :  "${title}",
				"text" :  "${text}"
			});
		</script>

	</c:if>
	
	<script>
		/* 로그인 수행 시 아이디/비밀번호가 작성되었는지 확인하는 유효성 검사 */
		function loginValidate(){
			// Validate : 유효한지 확인하다
			
			// 아이디가 입력되지 않았을 경우
			// "아이디를 입력해주세요" 경고창을 띄우고
			// 로그인을 수행하지 않음
			if($("#memberId").val().trim().length==0){
				// $("#memberId") : 아이디 속성 값이 memberId인 input태그 선택
				// .val() : input 태그에 작성된 값을 얻어옴
				// .trim() : 얻어온 값 양쪽에 작성된 공백문자를 제거
				// .length : 공백 제거 후 값의 길이
				
				// sweetalert로 경고 출력
				swal({
					"icon" : "warning",
					"title" : "아이디를 입력해주세요"
				}).then(function(){
					// 아이디 입력 안되었을 때 focus를 아이디 입력하는 곳으로 이동
					$("#memberId").focus();
				});
				
				// 제출되지 않도록 submit 기본 이벤트 제거
				return false;
			}
			
			// 비밀번호가 입력되지 않았을 경우
			// "비밀번호를 입력해주세요" 경고창을 띄우고
			// 로그인을 수행하지 않음			
			if($("#memberPw").val().trim().length==""){-
				swal({
					"icon" : "warning",
					"title" : "비밀번호를를 입력해주세요"
				}).then(function(){
					// 아이디 입력 안되었을 때 focus를 아이디 입력하는 곳으로 이동
					$("#memberPw").focus();
				});
				return false;
			}

			// return false; submit 기본이벤트제거
			// return true; 또는 아무것도 반환하지 않으면 submit 수행 
		}
	</script>

</body>

</html>
--