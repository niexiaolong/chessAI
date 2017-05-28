<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/chess.css"/>
<script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
<script src="js/chess.js"></script>
<title>中国象棋</title>
</head>
<body>
	<div style="background-image: url(./pics/main.gif); width: 558px;height:620px">
		<div id="map" style="padding-top: 22px;padding-left: 18px">
		</div>
	</div>


<!-- 

	<div style="background-image: url(./pics/main.gif); width: 558px;height:620px">
		<div style="padding-top: 22px;padding-left: 18px">
			<c:forEach var="row" items="${data }">
				<div>
					<c:forEach var="cell" items="${row }">
						<c:choose>
							<c:when test="${fn:contains(cell, 'A1')}" >
								<div class="A1"></div>
							</c:when>
							<c:when test="${fn:contains(cell, 'B1')}" >
								<div class="B1"></div>
							</c:when>
							<c:when test="${fn:contains(cell, 'C1')}" >
								<div class="C1"></div>
							</c:when>
							<c:when test="${fn:contains(cell, 'D1')}" >
								<div class="D1"></div>
							</c:when>		
							<c:when test="${fn:contains(cell, 'E1')}" >
								<div class="E1"></div>
							</c:when>	
							<c:when test="${fn:contains(cell, 'F1')}" >
								<div class="F1"></div>
							</c:when>	
							<c:when test="${fn:contains(cell, 'G1')}" >
								<div class="G1"></div>
							</c:when>	
							<c:when test="${fn:contains(cell, 'A2')}" >
								<div class="A2"></div>
							</c:when>
							<c:when test="${fn:contains(cell, 'B2')}" >
								<div class="B2"></div>
							</c:when>
							<c:when test="${fn:contains(cell, 'C2')}" >
								<div class="C2"></div>
							</c:when>
							<c:when test="${fn:contains(cell, 'D2')}" >
								<div class="D2"></div>
							</c:when>		
							<c:when test="${fn:contains(cell, 'E2')}" >
								<div class="E2"></div>
							</c:when>		
							<c:when test="${fn:contains(cell, 'F2')}" >
								<div class="F2"></div>
							</c:when>
							<c:when test="${fn:contains(cell, 'G2')}" >
								<div class="G2"></div>
							</c:when>
							<c:otherwise>
								<div class="empty"></div>
							</c:otherwise>
						</c:choose>
					</c:forEach> 
				</div>					
			</c:forEach>		
		</div>
	</div>
	 -->
</body>
</html>