app.controller("baseController", function ($scope) {
    //定义分页变量
    $scope.paginationConf = {
        currentPage: 1,					//当前页
        totalItems: 10,					//总记录数
        itemsPerPage: 5,					//每页的记录数
        perPageOptions: [10, 20, 30, 40, 50],  //分页选项
        onChange: function () {				//改变页码时触发此事件
            $scope.search();   //匹配所以查询+分页控制层方法
        }
    };
    //id数组
    $scope.ids = [];
    //跟新id数组
    $scope.selectIds = function (event, id) {
        if (event.target.checked) {
            $scope.ids.push(id);
        } else {
            //在取消选择时
            var index = $scope.ids.indexOf(id);
            if (index != -1) {
                $scope.ids.splice(index, 1);
            }
        }
    };
});