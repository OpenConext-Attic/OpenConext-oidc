<%@attribute name="pageName" required="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<div class="mod-header">
    <p class="title"><a href="/">${config.topbarTitle}</a></p>

    <div class="meta">
        <security:authorize access="hasRole('ROLE_ADMIN')">
            <div class="name">
                <a href="manage/#user/profile"><span><spring:message code="openconext.header.welcome"/> ${ userInfo.name }</span></a>
            </div>
        </security:authorize>
        <ul class="language">
            <li>
                <a id="header_lang_en" href="#" class="${pageContext.response.locale == "en" || pageContext.response.locale == null ? "selected" : ""}"><spring:message code="openconext.header.lang_en"/></a>
            </li>
            <li>
                <a id="header_lang_nl" href="#" class="${pageContext.response.locale == "nl" ? "selected" : ""}"><spring:message code="openconext.header.lang_nl"/></a>
            </li>
        </ul>
        <ul class="links">
            <li>
                <a href="<spring:message code="openconext.footer.git_link"/>" target="_blank">
                    <img src="resources/images/github.png"/>
                </a>
            </li>
        </ul>
    </div>
</div>
<div class="mod-navigation">
    <security:authorize access="hasRole('ROLE_ADMIN')">
        <ul>
            <li><a href="manage/#admin/clients" data-toggle="collapse" data-target=".nav-collapse"><spring:message
                    code="sidebar.administrative.manage_clients"/></a></li>
            <li><a href="manage/#admin/scope" data-toggle="collapse" data-target=".nav-collapse"><spring:message
                    code="sidebar.administrative.system_scopes"/></a></li>
            <li><a href="manage/#user/profile" data-toggle="collapse" data-target=".nav-collapse"><spring:message
                    code="sidebar.personal.profile_information"/></a></li>
        </ul>
    </security:authorize>
</div>
