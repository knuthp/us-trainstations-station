<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Home</title>
</head>
<body>
	<h1>Hello world!</h1>

	<P>The time on the server is ${serverTime}.</P>
	<table>
		<tr>
			<th>PublishedLineName</th>
			<th>Destination</th>
			<th>JourneyId</th>
			<th>ExpArrival</th>
			<th>ExpDepart</th>
			<th>Delay</th>
		</tr>
		<c:forEach items="${arrivalList}" var="rtStop">
			<tr>
				<td><c:out value="${rtStop.publishedLineName}" /></td>
				<td><c:out value="${rtStop.destinationName}" /></td>
				<td><c:out value="${rtStop.journeyId}" /></td>
				<td><c:out value="${rtStop.expectedArrivalTime}" /></td>
				<td><c:out value="${rtStop.expectedDepartureTime}" /></td>
				<td><c:out value="${rtStop.delay}" /></td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>
