<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${pagination.boardName} 게시판</title>
<style>
/* 게시글 목록 내부 td 태그 */
#list-table td{
	padding : 0; /* td 태그 padding을 없앰 */
  vertical-align: middle; /* td태그 내부 세로 가운데 정렬*/
  /* vertical-align : inline, inline-block 요소에만 적용 가능(td는 inline-block)*/
}


/* 컬럼명 가운데 정렬 */
#list-table th {
	text-align: center;
}

/* 게시글 제목을 제외한 나머지 가운데 정렬 */
#list-table td:not(:nth-of-type(3)) {
	text-align: center;
}

/* 게시글 목록의 높이가 최소 540px은 유지하도록 설정 */
.list-wrapper{
	min-height: 540px;
}

/* 글 제목 영역의 너비를 table의 50% 넓게 설정*/
#list-table td:nth-child(3){
	width: 50%;
}

/* 제목 a태그 색 변경 */
#list-table td:nth-child(3) > a{
	color : black;
}

/* 게시글 제목에 영역 이미지 설정 */
.boardTitle img {
	width: 70px;
	padding: 10px
}

.pagination {
	justify-content: center;
}

#searchForm {
	position: relative;
}

#searchForm>* {
	top: 0;
}


</style>

</head>
<body>
	<jsp:include page="../common/header.jsp"></jsp:include>
	<div class="container my-5">

		<h1>${pagination.boardName} 게시판</h1>
			<div class="list-wrapper">
				<table class="table table-hover table-striped my-5" id="list-table">
					<thead>
						<tr>
							<th>글번호</th>
							<th>카테고리</th>
							<th>제목</th>
							<th>작성자</th>
							<th>조회수</th>
							<th>작성일</th>
						</tr>
					</thead>
					
					
					<%-- 검색 상태 유지를 위한 쿼리스트링용 변수 선언 --%>
					<c:if test="${!empty param.sk}">
					
						<!-- /fin/board/1/list?ct-1&ct=2&sk=writer&sv=유 -->
						<!-- ct가 파라미터에 있다면 -->
						<c:if test="${ !empty paramValues.ct }">
							<!-- &ct=1&ct=2 형식의 문자열을 조합 -->
							<c:forEach items="${ paramValues.ct }" var="code">
								<c:set var="category" value="${category}&ct=${code}" />
							</c:forEach>
						</c:if>
						
						<!-- /fin/board/1/list?ct-1&ct=2&sk=writer -->
						<!-- sv가 파라미터에 있다면 -->
						<c:if test="${ !empty param.sv}">
							<!-- &sv=유 형식의 문자열을 조합 -->
							<c:set var="searchValue" value="&sv=${param.sv}" />
						</c:if>
												
						<%-- 검색은  게시글 목록 조회에 단순히 sk, sv 파라미터를 추가한 것
								-> 목록 조회 결과 화면을 만들기 위해 boardList.jsp로 요청 위임 되기 때문에
									 request객체가 유지되고, 파라미터도 유지된다.
						--%>
						
						<c:set var="searchStr" value="${category}&sk=${param.sk}${searchValue}"  />
					</c:if>
					
					
					<%-- 게시글 목록 출력 --%>
					<tbody>
						
						<c:choose>
							
							<%-- 조회된 게시글 목록이 없는 경우 --%>
							<c:when test="${empty boardList}">
								<tr>
									<td colspan="6">게시글이 존재하지 않습니다.</td>
								</tr>								
							</c:when>
							
							
							<%-- 조회된 게시글 목록이 있을 경우 --%>
							<c:otherwise>
							
								<c:forEach items="${boardList}" var="board">
									<tr>
										<%-- 글 번호 --%>
										<td> ${board.boardNo} </td>
										
										<%-- 카테고리 --%>
										<td> ${board.categoryName} </td>
										
										<%-- 글 제목 --%>
										<td class="boardTitle">                                                         
											<a href="${board.boardNo}?cp=${pagination.currentPage}${searchStr}">                                
<%-- 												
												썸네일 출력
												<c:choose>
													썸네일 이미지가 없는 경우
													<c:when test="${ empty board.atList || board.atList[0].fileLevel != 0 }">
														<img src="${contextPath}/resources/images/noimage.png">
													</c:when>
													
													썸네일 이미지가 있는 경우
													<c:otherwise>
														<img src="${contextPath}/${board.atList[0].filePath}${board.atList[0].fileName}">
													</c:otherwise>
												
												</c:choose>
 --%>											
											 	${board.boardTitle }   
											</a>
											
									 	</td>
										
										<%-- 작성자 --%>
										<td> ${board.memberName} </td>
										
										<%-- 조회수 --%>
										<td> ${board.readCount} </td>
										
										<%-- 작성일 --%>
										<td> 
											<fmt:formatDate var="createDate" value="${board.createDate}"  pattern="yyyy-MM-dd"/>                          
											<fmt:formatDate var="today" value="<%= new java.util.Date() %>"  pattern="yyyy-MM-dd"/>                          
											
											<c:choose>
												<%-- 글 작성일이 오늘이 아닐 경우 --%>
												<c:when test="${createDate != today}">
													${createDate}
												</c:when>
												
												<%-- 글 작성일이 오늘일 경우 --%>
												<c:otherwise>
													<fmt:formatDate value="${board.createDate}"  pattern="HH:mm"/>                          
												</c:otherwise>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							
							</c:otherwise>
						
						</c:choose>

					
					</tbody>
				</table>
			</div>

<%-- 
			로그인 되어 있을 경우에만 글쓰기 버튼 노출
			<c:if test="${!empty loginMember }">
 --%>			
				<%-- <button type="button" class="btn btn-primary float-right" id="insertBtn"
				 onclick="location.href='../board2/insertForm?type=${pagination.boardType}';">글쓰기</button> --%>
				<a  class="btn btn-primary float-right" id="insertBtn" href='insert'>글쓰기</a>
<%-- 				
			</c:if>
 --%>			
			
			<%---------------------- Pagination start----------------------%>
			<%-- 페이징 처리 시 주소를 쉽게 작성할 수 있도록 필요한 변수를 미리 선언 --%>
			
			<c:set var="pageURL" value="list"  />
			
			<c:set var="prev" value="${pageURL}?cp=${pagination.prevPage}${searchStr}" />
			<c:set var="next" value="${pageURL}?cp=${pagination.nextPage}${searchStr}" />
			
			
			<div class="my-5">
				<ul class="pagination">
				
					<%-- 현재 페이지가 10페이지 초과인 경우 --%>					
					<c:if test="${pagination.currentPage > pagination.pageSize }">
						<li><a class="page-link" href="${prev}">&lt;&lt;</a></li>
					</c:if>

					
					<%-- 현재 페이지가 2페이지 초과인 경우 --%>
<%-- 					
					<c:if test="${pagination.currentPage > 2 }">
						<li><a class="page-link" href="${pageURL}?cp=${pagination.currentPage - 1}${searchStr}">&lt;</a></li>
					</c:if>
 --%>					
					<c:if test="${pagination.currentPage > 2 }">
						<li><a class="page-link" href="${pageURL}?cp=${pagination.currentPage - pagination.pageSize}${searchStr}">&lt;</a></li>
					</c:if>
					
				
					<%-- 페이지 목록 --%>
					<c:forEach var="p" begin="${pagination.startPage}" end="${pagination.endPage}">
						
							<c:choose>
								<c:when test="${p == pagination.currentPage }">
									<li class="page-item active"><a class="page-link">${p}</a></li>
								</c:when>
								
								<c:otherwise>
									<li><a class="page-link" href="${pageURL}?cp=${p}${searchStr}">${p}</a></li>
								</c:otherwise>
							</c:choose>						
					</c:forEach>
					
					
<%-- 					
					${pagination.maxPage } -- 제일 끝
					${pagination.endPage } -- 현재페이지의 끝
					${pagination.pageSize } -- 잘모르겠음 3나옴...
 --%>					
					
					<%-- 현재 페이지가 마지막 페이지 미만인 경우 --%>
<%-- 					
					<c:if test="${pagination.currentPage < pagination.maxPage }">
						<li><a class="page-link" href="${pageURL}?cp=${pagination.currentPage + 1}${searchStr}">&gt;</a></li>
					</c:if>
 --%>					
					<c:if test="${pagination.currentPage < pagination.maxPage }">
						<li><a class="page-link" href="${next}">&gt;</a></li>
					</c:if>
					
					<%-- 현재 페이지가 마지막 페이지가 아닌 경우 --%>
					<c:if test="${pagination.currentPage - pagination.maxPage + pagination.pageSize < 0}">
						<li><a class="page-link" href="${pageURL}?cp=${pagination.maxPage}${searchStr}">&gt;&gt;</a></li>
					</c:if>
					
					

				</ul>
			</div>
			<%---------------------- Pagination end----------------------%>
		
			<!-- 검색창 -->
			<div class="my-5">
				<form action="list" method="GET" class="text-center" id="searchForm">
				
					<!-- 스프링 추가내용 : 검색조건 체크박스로 선택 -->
<!-- 					
	               <span> 카테고리(다중 선택 가능)<br> 
	                  <label for="talk">잡담</label> <input type="checkbox" name="ct" value="1" id="talk"> &nbsp; 
	                  <label for="question">질문</label> <input type="checkbox" name="ct" value="2" id="question"> &nbsp; 
	                  <label for="news">뉴스</label> <input type="checkbox" name="ct" value="3" id="news"> &nbsp; 
	               </span> 
 -->	               
	               <!-- SELECT * FROM BOARD_LIST
	               		  WHERE CATEGORY_CD IN()
	               		  상황에 따라 SQL이 에러날 상황이면 WHERE절을 삭제 -> 동적 SQL 작성
	               	 -->
				
					<!-- 게시판 타입 유지를 위한 태그 -->
					<%-- <input type="hidden" name="boardType" value="${pagination.boardType}"> --%>
				
					<select name="sk" class="form-control" style="width: 100px; display: inline-block;">
						<option value="title">글제목</option>
						<option value="content">내용</option>
						<option value="titcont">제목+내용</option>
						<option value="writer">작성자</option>
					</select>
					<input type="text" name="sv" class="form-control" style="width: 25%; display: inline-block;">
					<button class="form-control btn btn-primary" style="width: 100px; display: inline-block;">검색</button>
					<!-- 버튼을 누르면 넘어가는 파라미터 값 : 모든 name 속성값
						   ct, boardType, sk, sv 중 여러값을 받는 것은 ct -> ct는 배열
					 -->
				</form>
			</div>
	</div>
	
	<jsp:include page="../common/footer.jsp"></jsp:include>


	<script>
			// 검색 내용이 있을 경우 검색창에 해당 내용을 작성해두는 기능
			(function(){
				var searchKey = "${param.sk}"; 
				// 파라미터 중 sk가 있을 경우   ex)  "abc"
				// 파라미터 중 sk가 없을 경우   ex)  ""
				var searchValue = "${param.sv}";
				
				// 검색창 select의 option을 반복 접근
				$("select[name=sk] > option").each(function(index, item){
					// index : 현재 접근중인 요소의 인덱스
					// item : 현재 접근중인 요소
								// content            content
					if( $(item).val() == searchKey  ){
						$(item).prop("selected", true);
					}
				});		
				
				// 검색어 입력창에 searchValue 값 출력
				$("input[name=sv]").val(searchValue);
				
				// 쿼리스트링에 카테고리가 있을 경우 체크하기
				
				// ** <script> 태그 내부에 EL, JSTL 사용가능
				// 단, 이클립스가 인식을 못함
				/* JSP에서 언어 해석 순서
				   1. EL/JSTL
				   2. HTML
				   3. JavaScript
				*/
				<c:forEach items="${paramValues.ct}" var="code">
					// name 속성값이 ct인 요소들을 반복접근
					$.each( $("[name='ct']"), function(){
						// this : 현재 반복 접근한 요소
						if( $(this).val() == ${code} ){
							$(this).prop("checked", true);
						}
						
					})
				
				</c:forEach>
				
				
				
			})();
			
	</script>
	
</body>
</html>
