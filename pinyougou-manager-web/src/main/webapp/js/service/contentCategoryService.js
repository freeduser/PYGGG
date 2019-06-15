app.service("contentCategoryService", function ($http) {
    this.findAll = function () {
      return  $http.get('../contentCategory/findAll.do');
    }
});