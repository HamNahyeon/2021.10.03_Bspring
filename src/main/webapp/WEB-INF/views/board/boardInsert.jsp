<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판</title>
<style>
  .insert-label {
    display: inline-block;
    width: 80px;
    line-height: 40px
  }
   
  .boardImg{
  	cursor : pointer;
		width: 200px;
		height: 200px;
		border : 1px solid #ced4da;
		position : relative;
	}
	
	.thubnail{
		width: 300px;
		height: 300px;
	}
	
	.boardImg > img{
		max-width : 100%;
		max-height : 100%;
		position: absolute;
		top: 0;
		bottom : 0;
		left : 0;
		right : 0;
		margin : auto;
	}
	
	
	#fileArea{
		display : none;
	}
</style>
</head>
<body>
		<jsp:include page="../common/header.jsp"></jsp:include>

		<div class="container my-5">

			<h3>게시글 등록</h3>
			<hr>
			<!-- 파일 업로드를 위한 라이브러리 cos.jar 라이브러리 다운로드(http://www.servlets.com/) -->
			
			<!-- 
				- enctype : form 태그 데이터가 서버로 제출 될 때 인코딩 되는 방법을 지정. (POST 방식일 때만 사용 가능)
				- application/x-www-form-urlencoded : 모든 문자를 서버로 전송하기 전에 인코딩 (form태그 기본값)
				- multipart/form-data : 모든 문자를 인코딩 하지 않음.(원본 데이터가 유지되어 이미지, 파일등을 서버로 전송 할 수 있음.) 
			-->
			<form action="insert" method="post"  enctype="multipart/form-data" role="form" onsubmit="return boardValidate();">
				<c:if test="${!empty category}"> 
					<div class="mb-2">
						<label class="input-group-addon mr-3 insert-label">카테고리</label> 
						<select	class="custom-select" id="categoryCode" name="categoryCode" style="width: 150px;">
							<c:forEach items="${category}" var="c">
								<option value="${c.categoryCode}">${c.categoryName}</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
				
				
				<div class="form-inline mb-2">
					<label class="input-group-addon mr-3 insert-label">제목</label> 
					<input type="text" class="form-control" id="boardTitle" name="boardTitle" size="70"><span style="color:#aaa;" id="tCounter">(0 / 최대 30자)</span>
				</div>

				<div class="form-inline mb-2">
					<label for="boardWriter" class="input-group-addon mr-3 insert-label">작성자</label>
					<input type="text" class="form-control" id="boardWriter" name="boardWriter" size="70" required >
				</div>
				
				<div class="form-inline mb-2">
					<label for="boardPass" class="input-group-addon mr-3 insert-label">비밀번호</label>
					<input type="password" class="form-control" id="boardPass" name="boardPass" size="70" required >
				</div>

				<div class="form-inline mb-2">
					<label class="input-group-addon mr-3 insert-label">작성일</label>
					<h5 class="my-0" id="today"></h5>
				</div>

				<hr>
<!-- 
				<div class="form-inline mb-2">
					<label class="input-group-addon mr-3 insert-label">썸네일</label>
					<div class="boardImg thubnail" id="titleImgArea">
						<img id="titleImg">
					</div>
				</div>

				<div class="form-inline mb-2">
					<label class="input-group-addon mr-3 insert-label">업로드<br>이미지</label>
					<div class="mr-2 boardImg" id="contentImgArea1">
						<img id="contentImg1">
					</div>

					<div class="mr-2 boardImg" id="contentImgArea2">
						<img id="contentImg2">
					</div>

					<div class="mr-2 boardImg" id="contentImgArea3">
						<img id="contentImg3">
					</div>
				</div>


				***** 파일 업로드 하는 부분 *****
				<div id="fileArea">
					name 속성값(images)을 동일하게 지정 
						-> @RequestParam을 이용하여 List로 파라미터를 전달받을 수 있음. 
						
						accept="image/*" 이미지 파일만 선택할 수 있도록하는 속성
						
					<input type="file" id="img0" name="images" onchange="LoadImg(this,0)" accept="image/*"> 
					<input type="file" id="img1" name="images" onchange="LoadImg(this,1)" accept="image/*"> 
					<input type="file" id="img2" name="images" onchange="LoadImg(this,2)" accept="image/*"> 
					<input type="file" id="img3" name="images" onchange="LoadImg(this,3)" accept="image/*">
				</div>
 -->
				<div class="form-group">
					<div>
						<label for="content">내용</label>
					</div>
					<textarea class="form-control" id="boardContent" name="boardContent" rows="15" style="resize: none;"></textarea>
					<span style="color:#aaa;" id="cCounter">(0 / 최대 300자)</span>
				</div>


				<hr class="mb-4">

				<div class="text-center">
					<button type="submit" class="btn btn-primary">등록</button>
					<button type="button" class="btn btn-primary" onclick="location.href='list?type=${param.type}&cp=${param.cp}${searchStr}'">목록으로</button>
				</div>

			</form>
		</div>

		<jsp:include page="../common/footer.jsp"></jsp:include>
		
		
	<script>
		
		(function printToday() {
			// 오늘 날짜 출력 
			var today = new Date();
			var month = (today.getMonth() + 1);
			var date = today.getDate();

			var str = today.getFullYear() + "-"
					+ (month < 10 ? "0" + month : month) + "-"
					+ (date < 10 ? "0" + date : date);
			$("#today").html(str);
		})();

		// 유효성 검사 
		function boardValidate() {
			if ($("#boardTitle").val().trim().length == 0) {
				alert("제목을 입력해 주세요.");
				$("#title").focus();
				return false;
			}

			if ($("#boardContent").val().trim().length == 0) {
				alert("내용을 입력해 주세요.");
				$("#content").focus();
				return false;
			}
		}

		// 이미지 영역을 클릭할 때 파일 첨부 창이 뜨도록 설정하는 함수
		$(function() {
			$(".boardImg").on("click", function() {
				var index = $(".boardImg").index(this);
				// this : 이벤트가 발생한 요소 == 여기서는 클릭된 .boardImg 요소
				// 배열.index("요소") : 매개변수로 작성된 요소가 배열의 몇번째 index요소인지 반환
				
				$("#img" + index).click();
			});

		});

		
		// 각각의 영역에 파일을 첨부 했을 경우 미리 보기가 가능하도록 하는 함수
		function LoadImg(value, num) {
			if (value.files && value.files[0]) {
				var reader = new FileReader();
				// 자바스크립트 FileReader
				// 웹 애플리케이션이 비동기적으로 데이터를 읽기 위하여 읽을 파일을 가리키는 File 혹은 Blob객체를 이용해 파일의 내용을 읽고 사용자의 컴퓨터에 저장하는 것을 가능하게 해주는 객체

				reader.readAsDataURL(value.files[0]);
				// FileReader.readAsDataURL()
				// 지정된의 내용을 읽기 시작합니다. Blob완료되면 result속성 data:에 파일 데이터를 나타내는 URL이 포함 됩니다.

				// FileReader.onload
				// load 이벤트의 핸들러. 이 이벤트는 읽기 동작이 성공적으로 완료 되었을 때마다 발생합니다.
				reader.onload = function(e) {
					// e.target.result
					// -> 파일 읽기 동작을 성공한 객체에(fileTag) 올라간 결과(이미지 또는 파일)

					$(".boardImg").eq(num).children("img").attr("src",e.target.result);
				}

			}
		}
		
		// 글자수 카운팅
		$('#boardContent').keyup(function (e){
		    var content = $(this).val();
		    $('#cCounter').html("("+content.length+" / 최대 300자)");    //글자수 실시간 카운팅
		
		    if (content.length > 300){
		        alert("최대 300자까지 입력 가능합니다.");
		        $(this).val(content.substring(0, 300));
		        $('#cCounter').html("(300 / 최대 300자)");
		    }
		});
		$('#boardTitle').keyup(function (e){
		    var content = $(this).val();
		    $('#tCounter').html("("+content.length+" / 최대 30자)");    //글자수 실시간 카운팅
		
		    if (content.length > 30){
		        alert("최대 30자까지 입력 가능합니다.");
		        $(this).val(content.substring(0, 30));
		        $('#tCounter').html("(30 / 최대 30자)");
		    }
		});
	</script>
</body>
</html>
