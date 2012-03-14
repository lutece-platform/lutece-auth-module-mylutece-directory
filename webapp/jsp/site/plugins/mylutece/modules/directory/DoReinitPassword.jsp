<jsp:useBean id="myLuteceDirectoryApp" scope="request" class="fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.MyLuteceDirectoryApp" />

<%
	response.sendRedirect( myLuteceDirectoryApp.doReinitPassword( request ) );
%>
