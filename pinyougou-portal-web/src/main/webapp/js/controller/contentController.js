app.controller("contentController", function ($scope, $controller, contentService1,contentCategoryService) {
    //为继承
    $controller("baseController", {$scope: $scope});
    $scope.contentList = [];
    //根据分类id查询所有广告信息
    $scope.findByCategoryId = function (id) {
        contentService1.findByCategoryId(id).success(function (response) {
            $scope.contentList[id] = response;
        })
    }

});