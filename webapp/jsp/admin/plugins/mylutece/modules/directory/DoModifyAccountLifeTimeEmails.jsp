<%@ page errorPage="../../../../ErrorPage.jsp" %>

<jsp:useBean id="DirectoryUser" scope="session" class="fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.MyluteceDirectoryUserJspBean" />

<%
	DirectoryUser.init( request, DirectoryUser.RIGHT_MANAGE_MYLUTECE_DIRECTORY_USERS ) ; 
	response.sendRedirect( DirectoryUser.doModifyAccountLifeTimeEmails( request ) );  
%>

