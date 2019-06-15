app.service("contentService", function ($http) {
    //条件查询+分页
    this.search = (entity,page, pagesize ) => {
        return $http.post("/content/search.do?page="+page+"&rows="+pagesize,entity);
    };
    //删除
    this.dele=function(ids){
        return $http.get("/content/delete.do?ids="+ids);
    };
    //保存信息
    this.save = function (entity,url) {
        return $http.post(url,entity);
    }
});