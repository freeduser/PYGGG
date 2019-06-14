app.service("brandService", function ($http) {
    //条件查询+分页
    this.search = (entity,page, pagesize ) => {
        return $http.post("/brand/search.do?page="+page+"&rows="+pagesize,entity);
    };
    //删除
    this.dele=function(ids){
        return $http.get("/brand/delete.do?ids="+ids);
    };
    //保存信息
    this.save = function (brand,url) {
        return $http.post(url,brand);
    }
});