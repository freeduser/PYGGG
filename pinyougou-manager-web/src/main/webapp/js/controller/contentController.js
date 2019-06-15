app.controller("contentController", function ($scope, $controller, contentService, uploadService,contentCategoryService) {
    //为继承
    $controller("baseController", {$scope: $scope});
    /**
     * 搜索
     */
    $scope.search = function () {
        contentService.search($scope.searchEntity, $scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage).success(function (response) {
            $scope.list = response.rows;
            //给分页变量总数赋值
            $scope.paginationConf.totalItems = response.total;
        });
    };
    /**
     *查询分类id
     */
    $scope.findCategoryList = function () {
        contentCategoryService.findAll().success(function (response) {
            $scope.categoryList = response;
        })
    }

    //批量删除
    $scope.dele = function () {
        contentService.dele($scope.ids).success(function (response) {
            if (response.success) {
                //显示所有
                $scope.search();
            } else {
                alert(response.message);
            }
        })
    };

    /**
     * 上传图片到 FASTDFS 图片服务器、调用方法将图片信息绑定到entity对象中
     */
    $scope.upload = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                $scope.Entity.pic = response.message;  //成功的话，message为url地址
            } else {
                alert(response.message)
            }
        })
    }
    //修改,打开模态框
    $scope.update = function (entity) {
        $scope.Entiry = entity;
        $(".modal").modal('show');
    };

    //保存模态框信息
    $scope.save = function () {
        //又id时为修改，每id时为增加
        var url = "/content/add.do";
        if ($scope.Entity.id) {
            url = "/content/update.do";
        }
        contentService.save($scope.Entity, url).success(function (response) {
            if (response.success) {
                $scope.Entity={};
                $scope.search();
            } else {
                alert(response.message);
            }
        })
    }


});