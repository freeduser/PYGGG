//服务层
app.service('loginService',function($http){
	//得到当前登录的用户名
	this.loginName=function () {
		return $http.get("../login/name.do");
	}
});
