<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<spring:message code="home.title" var="title"/>
<o:header title="${title}" />
<o:topbar pageName="Home" />
<div class="container-fluid main">
	<div class="row-fluid">
		<div class="span12">
			<div class="hero-unit">
				<o:oidcLandingPageWelcome />
			</div>
		</div>
	</div>
</div>
<o:footer />
