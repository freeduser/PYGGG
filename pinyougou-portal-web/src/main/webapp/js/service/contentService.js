app.service(    "contentService1", function ($http) {
  this.findByCategoryId=function (id) {
      return $http.get('/content/findByCategoryId.do?id='+id);
  }
});