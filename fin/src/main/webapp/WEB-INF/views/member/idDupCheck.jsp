<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- jstl c태그를 사용할 수 있게 라이브러리를 연결하는 태그 -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>아이디 중복 검사</title>
</head>
<style>
	body>*{
		margin-left: 75px;
	}
</style>

<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<!-- CDN방식으로 jQuery 라이브러리를 연결함  -->

<body>
	<h4 class="mt-3">아이디 중복 검사</h4>
	<br>
	
	<!-- form 태그 값을 전송 -->
	<form action="${contextPath}/member/idDupCheck" id="idCheckForm" method="post">
		<input type="text" id="id" name="id" >
		<input type="submit" value="중복확인">
	</form>
	<br>
	
	<!-- 사용 가능 여부 메세지 출력 -->
	<span>
	
	<%-- 중복검사를 1회 이상 실행한 경우--%>
	<c:if test="${!empty result }">
		<c:choose>
			<c:when test="${result == 0 }">사용 가능한 아이디입니다.</c:when>
			<c:otherwise>이미 사용중인 아이디입니다.</c:otherwise>
		</c:choose>
	</c:if>
	</span>
	<br>
	<br>
	
	<div>
		<input type="button" id="cancel" value="취소" onclick="window.close();">
		<input type="button" id="confirmId" value="확인" onclick="confirmId();">
	</div>
	
	<script>
		// 회원가입창에 뜨는 것이 부모 중복검사창(팝업)은 자식
	
		let id; // 입력된 아이디를 저장하기 위한 함수
		
		const result = "${result}"; 
		// "0"  -> 중복X
		// "1"  -> 중복O
		// "" (빈 칸) -> 팝업창이 최초로 열려서 검사가 진행되지 않았다.
		
		// 현재 문서가 로드 완료 된 후 동작
		$(function(){
			
			if(result == ""){ // 최초 팝업창 오픈 시
				id = opener.document.signUpForm.id.value;
				// opener : 팝업을 연 창(부모 창)
				// opener.document : 부모 창 내부 HTML 문서
				// opener.document.signUpForm : 부모 창 내부 HTML 문서 중 name 속성이 signUpForm인 요소
				// signUpForm.id : name 속성 값이 signUpForm인 요소 내부에
				// 				   name 속성 값이 id인 요소 선택
				// value : input 태그에 작성된 값을 얻어옴
			}else{	
				id = "${id}"; // 중복 검사를 진행한 아이디
			}
			
			$("#id").val(id);
		});
		
		// 확인 버튼 클릭 시 동작
		function confirmId(){
			// 1. 중복 검사를 진행 한 아이디를 부모창으로 전달		
			// 2. 아이디 중복검사 진행 여부를 확인하기 위한 hidden 타입 태그 값 변경
			// 3. 팝업창 닫기
			// 팝업창에 작성된 아이디를 부모창으로 전달
			opener.document.signUpForm.id.value = $("#id").val();
			
			if(result === "0"){ // 중복검사를 진행했는데 사용 가능한 아이디인 경우
				opener.document.signUpForm.idDup.value = true;
				// 열었던 부모창 문서 내부에 signUp이라는 이름을 가지고 있는 form태그 idDup라는 이름을 가진 값이 있다.
				// signUp.jsp 파일의 <!-- 팝업창 중복체크 여부 판단을 위한 hidden 타입 요소 -->
			}else{
				opener.document.signUpForm.idDup.value = false;
			}
			
			if(opener != null){
				// self : 현재창 -> 팝업 창
				self.close();
			}
		}
		
		// 아이디 정규식 검사
		// 중복확인 버튼을 눌렀다 -> 제출이 되었다.
		$("#idCheckForm").submit(function(){
			
		    // 정규표현식 객체 생성
		    const regExp = /^[a-zA-Z0-9]{6,12}$/;
		    // ^ 시작 $ 끝
		    // 숫자 0-9 또는 /d

		    // 입력된 id(양쪽 공백 제거)를 얻어와  inputId변수에 저장
		    const inputId = $("#id").val().trim();
		    
		    // 입력된 아이디가 유효하지 않을 때
		    if(!regExp.test(inputId)){
		    	alert("유효하지 않은 아이디 형식입니다.");
		    	return false;
		    }
			
		});
		
	</script>
	
	
	<c:remove var="id"/>
	<c:remove var="result"/>
</body>
</html>