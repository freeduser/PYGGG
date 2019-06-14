//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        itemCatService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //根据parentId查询寻分类集合
    $scope.findByParentId = function (parentId) {
        itemCatService.findByParentId(parentId).success(function (response) {
            $scope.list = response;
        })
    }

    //设置分类级别变量,初始化为第一级
    $scope.grade = 1;
    $scope.setGrade=function(grade){
        $scope.grade = grade;
    };
    /**
     * 点击查询下一级，更新级别变量，执行当前parentId下的查询
     * @param entity_p 传入的父实体分类变量，给findByParentId(Id)的id赋值
     */
    $scope.selectList = function (entity_p) {
        //根据当前分类级别给父实体对象赋值
        switch ($scope.grade) {
            case 1:      //一级分类的时候,父实体变量为传入之
                // $scope.entity_p1 = entity_p;
                $scope.entity_p2 = null;
                $scope.entity_p3 = null;
                break;
            case 2 :
                $scope.entity_p2 = entity_p;
                $scope.entity_p3 = null;
                break;
            case 3:
                $scope.entity_p3= entity_p;
                break;
        }
       //调用根据父id查询全部分类的方法
        $scope.findByParentId(entity_p.id);
    }

});	
