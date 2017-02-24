<%@ page language="java" import="java.util.*" pageEncoding="UTF8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    <title>中文验证码</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <script type="text/javascript">
        function change() {
            document.getElementById("image").src = "${pageContext.request.contextPath}/background?time"
                + new Date().getTime();
        };
    </script>
</head>
<body>
<img src="${pageContext.request.contextPath}/background" id="image" onclick="change()">
<span id="checkcode_span">
   <a href="javascript:void(0)" onclick="change();">看不清，换一张</a>
</span>
    </br>
    </br>
    </br>
    </br>
</body>
</body>
</html>