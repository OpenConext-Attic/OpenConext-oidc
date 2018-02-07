<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ page import = "java.util.Map" %>
<%@ page import="java.util.Enumeration" %>

<spring:message code="error.title" var="title"/>
<o:header title="${title}" />
<o:topbar pageName="Home" />
<div class="container-fluid main">
	<div class="row-fluid">
		<div class="span12">
			<div class="hero-unit">
                <spring:message code="error.message" var="message"/>
                <p>${message}</p>
                <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
                    <pre>${SPRING_SECURITY_LAST_EXCEPTION}</pre>
                </c:if>
            </div>
		</div>
	</div>
</div>
<o:footer />
