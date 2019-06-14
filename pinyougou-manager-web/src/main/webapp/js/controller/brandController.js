app.controller("brandController", function ($scope, $controller, brandService) {
    //为继承
    $controller("baseController", {$scope: $scope});
    //条件+分页查询
    $scope.search = function () {
        brandService.search($scope.searchEntity, $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage).success(function (response) {
            //给brans赋值
            $scope.brands = response.rows;
            //给分页变量总数赋值
            $scope.paginationConf.totalItems = response.total;
        });
    };

    //批量删除
    $scope.dele = function () {
        brandService.dele($scope.ids).success(function (response) {
            if (response.success) {
                //显示所有
                $scope.search();
            } else {
                alert(response.message);
            }
        })
    };
    //修改,打开模态框
    $scope.update = function (brand) {
        $scope.brandM = brand;
        $(".modal").modal('show');
    };

    //保存模态框信息
    $scope.save = function () {
        //又id时为修改，每id时为增加
        var url = "/brand/add.do";
        if ($scope.brandM.id) {
            url = "/brand/update.do";
        }
        brandService.save($scope.brandM, url).success(function (response) {
            if (response.success) {
                $scope.search();
            } else {
                alert(response.message);
            }
        })
    }


});